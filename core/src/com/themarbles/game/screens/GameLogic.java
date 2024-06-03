package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.graphics;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.scenes.scene2d.Touchable.disabled;
import static com.badlogic.gdx.scenes.scene2d.Touchable.enabled;
import static com.themarbles.game.constants.Constants.EVEN;
import static com.themarbles.game.constants.Constants.GAME_FINISHED;
import static com.themarbles.game.constants.Constants.GAME_RUNNING;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.ODD;
import static com.themarbles.game.constants.Constants.RANDOMIZING_TURN;
import static com.themarbles.game.constants.Constants.SERVER;
import static com.themarbles.game.constants.Constants.WAITING_FOR_PLAYER_CONNECT;
import static com.themarbles.game.constants.Constants.WAITING_FOR_START;
import static com.themarbles.game.constants.Constants.WIDGET_PREFERRED_HEIGHT;
import static com.themarbles.game.constants.Constants.WIDGET_PREFERRED_WIDTH;
import static com.themarbles.game.constants.Constants.WIDTH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.Player;
import com.themarbles.game.myImpls.SerializableImage;
import com.themarbles.game.networking.DataPacket;
import com.themarbles.game.networking.Receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class GameLogic implements Screen {
    private final EntryPoint entryPoint;
    private final Stage stage;
    private final Image background;
    private final TextArea tokenArea;
    private final List<Integer> chooseBet;
    private final List<String> chooseStmt;
    private SerializableImage[] opponentHandInstances;
    private SerializableImage [] ourHandInstances;
    private final Thread acceptingThread, netUpdateListener, eventUpdateListener;
    private final TextButton startButton;
    private Player we, opponent;


    private String game_state;
    private Receiver receiver;
    private boolean ourTurn, endOfStage,weReady, opponentReady;

    private boolean canBeShown;

    //TODO delete
    private long logNum = 0;

    public GameLogic(EntryPoint entryPoint){
        this.entryPoint = entryPoint;
        stage = new Stage();

        game_state = WAITING_FOR_PLAYER_CONNECT;

        weReady = false;
        opponentReady = false;
        endOfStage = false;
        canBeShown = true;

        acceptingThread = initAcceptingThread();
        netUpdateListener = initNetUpdateListener();
        eventUpdateListener = initEventUpdateManager();

        startButton = new TextButton("S T A R T", new Skin(files.internal("buttons/startbuttonassets/startbuttonskin.json")));
        chooseBet = new List<>(new Skin(files.internal("labels/chooselist/chooselistskin.json")));
        chooseStmt = new List<>(new Skin(files.internal("labels/chooselist/chooselistskin.json")));

        background = new Image(new Texture(files.internal("textures/game_background.jpg")));

        tokenArea = new TextArea(null, new Skin(files.internal("labels/tokenfield/tokenfieldskin.json")));

        SerializableImage ourHandClosed = new SerializableImage((new Texture(files.internal("textures/ou_h_c.png"))));
        SerializableImage opponentHandClosed = new SerializableImage((new Texture(files.internal("textures/op_h_c.png"))));

        loadImages();

        we = new Player(ourHandClosed, ourHandInstances[0], WIDTH - Player.getDefaultWidth(), 0);
        opponent = new Player(opponentHandClosed, opponentHandInstances[0], 0, HEIGHT - Player.getDefaultHeight());


    }
    @Override
    public void show() {

        initBackground();
        initStartButton();
        initTokenArea();
        initChooseBet();
        initChooseStmt();

        stage.addActor(background);

        if (entryPoint.playerState.equals(SERVER)) {
            stage.addActor(startButton);
            stage.addActor(tokenArea);
        }

        stage.addActor(we.getPlayerHandClosed());
        stage.addActor(opponent.getPlayerHandClosed());
        stage.addActor(chooseBet);
        stage.addActor(chooseStmt);
        input.setInputProcessor(stage);

        acceptingThread.start();
    }

    @Override
    public void render(float delta){
        entryPoint.batch.begin();

        stage.act(graphics.getDeltaTime());
        stage.draw();

        // hand rendering
        if (game_state.equals(GAME_RUNNING)) {

            if(!eventUpdateListener.isAlive()) eventUpdateListener.start();

            if (weReady) {
                //System.out.println("[INFO]: weReady " + weReady);
                we.setHandVisible(we.getPlayerHandClosed(), true);
            }
            if (opponentReady) {
                //System.out.println("[INFO]: opponentReady " + opponentReady);
                opponent.setHandVisible(opponent.getPlayerHandClosed(), true);
            }
            if (endOfStage){
                we.setHandVisible(we.getPlayerHandOpened(), true);
                opponent.setHandVisible(opponent.getPlayerHandOpened(), true);
            }


        }
        entryPoint.batch.end();

    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        receiver.disable();
    }

    //################## private methods #########################

    private Thread initAcceptingThread(){
        Thread t = new Thread(() -> {
            try {
                entryPoint.client = entryPoint.server.accept();
            } catch (NullPointerException ignore){
            } catch (IOException e) {
                e.printStackTrace();
            }
            receiver = new Receiver(entryPoint.client);
            game_state = WAITING_FOR_START;
            if(!netUpdateListener.isAlive()) netUpdateListener.start();
        });
        t.setName("accepting_thread");
        t.setDaemon(true);
        return t;
    }

    private Thread initNetUpdateListener(){
        Thread t  = new Thread(() -> {

            DataPacket currPacket;

            Player abstractPlayer;

            while (!game_state.equals(GAME_FINISHED)) {

                currPacket = receiver.getData();

                //if (currPacket != null) {
                abstractPlayer = currPacket.getPlayerData();
                //System.out.println(abstractPlayer.getPlayerSecret());

                opponent.setBet(abstractPlayer.getBet());
                opponent.setMarblesAmount(abstractPlayer.getMarblesAmount());
                opponent.setStatement(abstractPlayer.getStatement());
                opponent.setPlayerHandOpened(opponentHandInstances[abstractPlayer.getMarblesAmount()]);

                synchronized (this) {
                    game_state = currPacket.getGameState();
                    ourTurn = !currPacket.getTurnOrder();
                    opponentReady = currPacket.getPlayerReady();
                }

                System.out.printf("*********RECEIVED(%d)***********\n", logNum);
                //System.out.println("[" + logNum + "]  " + "game_state: " + currPacket.getGameState());
                //System.out.println("[" + logNum + "]  " + "ourTurn: " + currPacket.getTurnOrder());
                //System.out.println("[" + logNum + "]  " + "weReady: " + weReady);
                //System.out.println("[" + logNum + "]  " + "opponentReady: " + currPacket.getPlayerReady());
                System.out.println("[" + logNum + "]  " + "bet: " + abstractPlayer.getBet());
                System.out.println("[" + logNum + "]  " + "statement: " + abstractPlayer.getStatement());
                System.out.println("[" + logNum + "]  " + "marbles amount: " + abstractPlayer.getMarblesAmount());
                System.out.println("[" + logNum + "]  " + "*****************************");
                logNum++;
                //System.out.printf("datapacket was received at %d:%d:%d\n",currPacket.getTimestamp().getHours(),
                //                                                        currPacket.getTimestamp().getMinutes(),
                //
                //
                //                                                      currPacket.getTimestamp().getSeconds());
                //System.out.println(abstractPlayer.getPlayerSecret());

                //}
                timedWaiting(MILLISECONDS, 100);
            }
        });
        t.setName("net_update_listener_thread");
        t.setDaemon(true);
        return t;
    }

    private Thread initEventUpdateManager(){
        Thread t = new Thread(() -> {
            while (!game_state.equals(GAME_FINISHED)) {
                synchronized (this) {

                    //TODO place if(weReady){return;} in setActorVisualProps();
                    if (ourTurn && canBeShown) {
                        setActorVisualProps(chooseBet, true, enabled);
                    }

                }

                synchronized (this) {

                    if (!ourTurn && opponentReady && canBeShown) {
                        setActorVisualProps(chooseBet, true, enabled);
                    }

                }

                if (weReady && opponentReady) {

                    timedWaiting(SECONDS, 2);

                    weReady = false;
                    opponentReady = false;
                    endOfStage = true;

                    final int ourMarblesAmount;
                    final int opponentMarblesAmount;
                    final int ourBet;
                    final int opponentBet;

                    //synchronized (this){
                        ourMarblesAmount = we.getMarblesAmount();
                        opponentMarblesAmount = opponent.getMarblesAmount();
                        ourBet = we.getBet();
                        opponentBet = opponent.getBet();

                   // }

                    we.setPlayerHandOpened(ourHandInstances[we.getMarblesAmount()]);

                    timedWaiting(SECONDS, 3);

                    if (ourTurn){
                        if (opponent.getStatement().equals(EVEN) && isEven(ourBet)){
                            opponent.setMarblesAmount(opponentMarblesAmount + ourBet);
                            we.setMarblesAmount(ourMarblesAmount - opponentBet);
                        } else if (opponent.getStatement().equals(ODD) && isOdd(ourBet)) {
                            opponent.setMarblesAmount(opponentMarblesAmount + ourBet);
                            we.setMarblesAmount(ourMarblesAmount - opponentBet);
                        } else{
                            we.setMarblesAmount(ourMarblesAmount + ourBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - ourBet);
                        }
                    }

                    if (!ourTurn){
                        if (we.getStatement().equals(EVEN) && isEven(opponentBet)){
                            we.setMarblesAmount(ourMarblesAmount + opponentBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - ourBet);
                        } else if (we.getStatement().equals(ODD) && isOdd(opponentBet)) {
                            we.setMarblesAmount(ourMarblesAmount + opponentBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - ourBet);
                        } else {
                            we.setMarblesAmount(ourMarblesAmount - opponentBet);
                            opponent.setMarblesAmount(opponentMarblesAmount + opponentBet);
                        }
                    }
                    we.setPlayerHandOpened(ourHandInstances[we.getMarblesAmount()]);

                    timedWaiting(SECONDS, 3);

                    reset();

                }


            }

        });
        t.setName("event_update_manager");
        t.setDaemon(true);
        return t;
    }

    private void initStartButton(){
        startButton.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT + 35);
        startButton.setPosition((float) WIDTH/2 - WIDGET_PREFERRED_WIDTH,
                (float) HEIGHT/2 - WIDGET_PREFERRED_HEIGHT);
        startButton.addListener(new ChangeListener() {
            @Override
            @SuppressWarnings("deprecated")
            public void changed(ChangeEvent event, Actor actor) {
                if (game_state.equals(WAITING_FOR_START)) {
                    choosePlayerTurn();

                    setActorVisualProps(startButton, false, disabled);
                    setActorVisualProps(tokenArea, false, disabled);

                    acceptingThread.stop();
                }
            }
        });
    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }

    private void choosePlayerTurn(){
        game_state = RANDOMIZING_TURN;
        Random random = new Random();
        boolean[] turnVariants = new boolean[]{true, false};

        ourTurn = turnVariants[random.nextInt(turnVariants.length)];

        game_state = GAME_RUNNING;

        System.out.println("turn: " + ourTurn);
        receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));
    }

    private void reset(){
        //System.out.println("reset");
        ourTurn = !ourTurn;

        canBeShown = true;
        weReady = false;
        opponentReady = false;
        endOfStage = false;

        we.setHandVisible(we.getPlayerHandClosed(), false);
        opponent.setHandVisible(opponent.getPlayerHandClosed(), false);

        if (entryPoint.playerState.equals(SERVER)){
            receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));
        }

    }

    private void timedWaiting(TimeUnit unit, int time){
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("visualising thread was interrupted");
            app.exit();
        }
    }

    private void initTokenArea(){
        String text = "INVITE TOKEN: " + "\n" + entryPoint.inviteToken;
        tokenArea.setPosition((float) WIDTH/2 - WIDGET_PREFERRED_WIDTH,
                (float) HEIGHT/2 - WIDGET_PREFERRED_HEIGHT - 80);
        tokenArea.setSize(400, 70);
        tokenArea.setText(text);
        tokenArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tokenArea.setText(text);
            }
        });

    }

    private void initChooseBet(){
        chooseBet.setPosition((float) WIDTH/3, (float) HEIGHT/3);
        chooseBet.setSize((float) WIDTH/3, (float) HEIGHT/3);
        chooseBet.setItems(converter(IntStream.rangeClosed(1, 5).toArray()));
        chooseBet.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                canBeShown = false;
                int ma = chooseBet.getSelected();

                we.setBet(ma);
                we.setPlayerHandOpened(ourHandInstances[ma]);
                //we.setStatement("test stmt");

                if (ourTurn) {
                    weReady = true;
                    receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));
                    setActorVisualProps(chooseBet, false, disabled);
                    return;
                }

                setActorVisualProps(chooseBet, false, disabled);

                setActorVisualProps(chooseStmt, true, enabled);

            }
        });

        setActorVisualProps(chooseBet, false, disabled);
    }

    private void initChooseStmt() {
        chooseStmt.setPosition((float) WIDTH / 3, (float) HEIGHT / 3);
        chooseStmt.setSize((float) WIDTH / 3, (float) HEIGHT / 3);
        chooseStmt.setItems(Array.with("ODD", "EVEN"));
        chooseStmt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                we.setStatement(chooseStmt.getSelected());

                weReady = true;

                receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));

                setActorVisualProps(chooseStmt, false, disabled);

            }
        });

        setActorVisualProps(chooseStmt, false, disabled);
    }

    private void loadImages(){
        opponentHandInstances = new SerializableImage[11];
        ourHandInstances = new SerializableImage[11];
        for(int i = 0; i < 11; i++){
            opponentHandInstances[i] = new SerializableImage(new Texture(files.internal("textures/op_h_" + i + "_o.png")));
            ourHandInstances[i] = new SerializableImage(new Texture(files.internal("textures/ou_h_" + i + "_o.png")));
        }

    }

    private void gameFinished(){
        //TODO remake
        if (we.getMarblesAmount() <= 0){
            System.out.println("DEFEAT");
            game_state = GAME_FINISHED;
        }
        if (opponent.getMarblesAmount() <= 0){
            System.out.println("VICTORY");
            game_state = GAME_FINISHED;

        }
        reset();
    }

    private void setActorVisualProps(Actor actor, boolean visible, Touchable touchable){
        actor.setVisible(visible);
        actor.setTouchable(touchable);
    }

    private Integer[] converter(int[] toConvert){
        Integer[] converted = new Integer[toConvert.length];
        for (int item = 0; item < toConvert.length; item++){
            converted[item] = toConvert[item];
        }
        return converted;
    }

    private boolean isEven(int value){
        return value % 2 == 0;
    }

    private boolean isOdd(int value){
        return value % 2 != 0;
    }
}
