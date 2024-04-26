package com.themarbles.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.Player;
import com.themarbles.game.myImpls.SerializableImage;
import com.themarbles.game.networking.DataPacket;
import com.themarbles.game.networking.Receiver;
import com.themarbles.game.utils.FontGenerator;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.scenes.scene2d.Touchable.disabled;
import static com.badlogic.gdx.scenes.scene2d.Touchable.enabled;
import static com.themarbles.game.constants.Constants.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class GameLogic implements Screen {
    private EntryPoint entryPoint;
    private Stage stage;
    private Image background;
    private TextArea tokenArea;
    private List<Integer> chooseBet;
    private List<String> chooseStmt;
    private SerializableImage[] opHandInstances;
    private SerializableImage [] ouHandInstances;
    private Player we, opponent;
    private Thread acceptingThread, netUpdateListener, eventUpdateListener;
    private TextButton startButton;

    private String game_state;
    private Receiver receiver;
    private boolean ourTurn, weReady, opponentReady, endOfStage;

    public GameLogic(EntryPoint entryPoint){
        this.entryPoint = entryPoint;
        stage = new Stage();

        game_state = WAITING_FOR_PLAYER_CONNECT;
        weReady = false;
        opponentReady = false;
        endOfStage = false;

        acceptingThread = initAcceptingThread();
        netUpdateListener = initNetUpdateListener();
        eventUpdateListener = initEventUpdateListener();

        startButton = new TextButton("S T A R T", new Skin(files.internal("buttons/startbuttonassets/startbuttonskin.json")));
        chooseBet = new List<>(new Skin(files.internal("labels/chooselist/chooselistskin.json")));
        chooseStmt = new List<>(new Skin(files.internal("labels/chooselist/chooselistskin.json")));

        background = new Image(new Texture(files.internal("textures/game_background.jpg")));

        FontGenerator.initParameter(40, Color.RED);
        tokenArea = new TextArea(null, new Skin(files.internal("labels/tokenfield/tokenfieldskin.json")));

        SerializableImage handW_C = new SerializableImage((new Texture(files.internal("textures/ou_h_c.png"))));
        SerializableImage handO_C = new SerializableImage((new Texture(files.internal("textures/op_h_c.png"))));

        loadImages();

        we = new Player(handW_C, ouHandInstances[0], WIDTH - Player.getDefaultWidth(), 0);
        opponent = new Player(handO_C, opHandInstances[0], 0, HEIGHT - Player.getDefaultHeight());


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

        //main game loop
        if (game_state.equals(GAME_RUNNING)) {
            if(!eventUpdateListener.isAlive()) eventUpdateListener.start();

            if (weReady) {
                we.setHandVisible(we.getPlayerHandClosed(), true);
            }
            if (opponentReady) {
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
        receiver.release();
    }

    //################## private methods #########################

    private Thread initAcceptingThread(){
        Thread t = new Thread(() -> {
            try {
                entryPoint.me = entryPoint.server.accept();
            } catch (NullPointerException ignore){
            } catch (IOException e) {
                e.printStackTrace();
            }
            receiver = new Receiver(entryPoint.me);
            game_state = WAITING_FOR_START;
            netUpdateListener.start();
        });
        t.setDaemon(true);
        return t;
    }

    private Thread initNetUpdateListener(){
        Thread t  = new Thread(() -> {
            while (!game_state.equals(GAME_FINISHED)) {
                DataPacket currPacket = receiver.get_data();
                if (currPacket != null) {
                    Player p = currPacket.getPlayerData();
                    synchronized (this) {

                        opponent.setBet(p.getBet());
                        opponent.setMarblesAmount(p.getMarblesAmount());
                        opponent.setStatement(p.getStatement());
                        opponent.setPlayerHandOpened(opHandInstances[p.getMarblesAmount()]);

                        game_state = currPacket.getGameState();
                        ourTurn = currPacket.getTurnOrder();
                        opponentReady = currPacket.getPlayerReady();
                        System.out.println("*********RECEIVED*********");
                        System.out.println("statement: " + p.getStatement());
                        System.out.println("bet: " + p.getBet());
                        System.out.println("marbles amount: " + p.getMarblesAmount());
                        System.out.println("*****************************");

                    }

                }
            }
        });
        t.setDaemon(true);
        return t;
    }

    private Thread initEventUpdateListener(){
        Thread t = new Thread(() -> {
            while (!game_state.equals(GAME_FINISHED)) {
                synchronized (this) {

                    if (ourTurn && !weReady) {

                        enableChooseBet();
                    }
                    if (!ourTurn) {
                        if (opponentReady && !weReady) {
                            enableChooseBet();
                        }
                    }
                }

                if (weReady && opponentReady) {
                    weReady = false;
                    opponentReady = false;
                    endOfStage = true;

                    int maW = we.getMarblesAmount();
                    int maO = opponent.getMarblesAmount();
                    int betW = we.getBet();
                    int betO = opponent.getBet();
                    wait(SECONDS, 3);

                    if (ourTurn){
                        if (opponent.getStatement().equals(EVEN) && isEven(betW)){
                            opponent.setMarblesAmount(maO + betW);
                            we.setMarblesAmount(maW - betO);
                        } else if (opponent.getStatement().equals(ODD) && isOdd(betW)) {
                            opponent.setMarblesAmount(maO + betW);
                            we.setMarblesAmount(maW - betO);
                        }else{
                            we.setMarblesAmount(maW + betW);
                            opponent.setMarblesAmount(maO - betW);
                        }
                    }
                    if (!ourTurn){
                        if (we.getStatement().equals(EVEN) && isEven(betO)){
                            we.setMarblesAmount(maW + betO);
                            opponent.setMarblesAmount(maO - betW);
                        } else if (we.getStatement().equals(ODD) && isOdd(betO)) {
                            we.setMarblesAmount(maW + betO);
                            opponent.setMarblesAmount(maO - betW);
                        }else {
                            we.setMarblesAmount(maW - betO);
                            opponent.setMarblesAmount(maO + betO);
                        }
                    }
                    we.setPlayerHandOpened(ouHandInstances[we.getMarblesAmount()]);

                    if(entryPoint.playerState.equals(CLIENT)) {
                        commitUpdate(receiver, new DataPacket(game_state, !ourTurn, weReady, we));
                    }

                    System.out.println("*********COMMITTED (from: stageEnded) *********");
                    System.out.println("statement: " + we.getStatement());
                    System.out.println("bet: " + we.getBet());
                    System.out.println("marbles amount: " + we.getMarblesAmount());
                    System.out.println("*****************************");

                    wait(SECONDS, 3);
                    reset();
                }
            }

        });
        t.setDaemon(true);
        return t;
    }

    private void initStartButton(){
        startButton.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT + 35);
        startButton.setPosition((float) WIDTH/2 - WIDGET_PREFERRED_WIDTH,
                (float) HEIGHT/2 - WIDGET_PREFERRED_HEIGHT);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game_state.equals(WAITING_FOR_START)) {
                    choosePlayerTurn();

                    startButton.setVisible(false);
                    startButton.setDisabled(true);
                    tokenArea.setVisible(false);

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

        commitUpdate(receiver, new DataPacket(game_state, !ourTurn, weReady, we));

        System.out.println("********* COMMITTED (from: choosePlayerTurn) *********");
        System.out.println("statement: " + we.getStatement());
        System.out.println("bet: " + we.getBet());
        System.out.println("marbles amount: " + we.getMarblesAmount());
        System.out.println("*****************************");
    }

    private void commitUpdate(Receiver receiver, DataPacket packet){
        receiver.send_data(packet);
    }

    private void reset(){
        ourTurn = !ourTurn;
        weReady = false;
        opponentReady = false;
        endOfStage = false;
        we.setHandVisible(we.getPlayerHandClosed(), false);
        opponent.setHandVisible(opponent.getPlayerHandClosed(), false);
        if (entryPoint.playerState.equals(SERVER)) {
            commitUpdate(receiver, new DataPacket(game_state, !ourTurn, weReady, we));
            System.out.println("COMMITTED (from: reset)");
            System.out.println("********* COMMITTED (from: chooseBet) *********");
            System.out.println("statement: " + we.getStatement());
            System.out.println("bet: " + we.getBet());
            System.out.println("marbles amount: " + we.getMarblesAmount());
            System.out.println("*****************************");
        }
    }

    private void wait(TimeUnit unit, int time){
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
                int ma = chooseBet.getSelected();
                weReady = true;

                we.setBet(ma);
                we.setPlayerHandOpened(ouHandInstances[ma]);

                chooseBet.setVisible(false);
                chooseBet.setTouchable(disabled);


                if (ourTurn) {
                    commitUpdate(receiver, new DataPacket(game_state, !ourTurn,
                            weReady, we));
                    System.out.println("********* COMMITTED (from: chooseBet) *********");
                    System.out.println("statement: " + we.getStatement());
                    System.out.println("bet: " + we.getBet());
                    System.out.println("marbles amount: " + we.getMarblesAmount());
                    System.out.println("*****************************");
                    return;
                }

                enableChooseStmt();
            }
        });

        chooseBet.setVisible(false);
        chooseBet.setTouchable(disabled);
    }

    private void initChooseStmt() {
        chooseStmt.setPosition((float) WIDTH / 3, (float) HEIGHT / 3);
        chooseStmt.setSize((float) WIDTH / 3, (float) HEIGHT / 3);
        chooseStmt.setItems(Array.with("ODD", "EVEN"));
        chooseStmt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                weReady = true;
                we.setStatement(chooseStmt.getSelected());
                System.out.println("changeddddddddddddddddd");

                chooseStmt.setVisible(false);
                chooseStmt.setTouchable(disabled);

                System.out.println("********* COMMITTED (from: chooseStmt) *********");
                System.out.println("statement: " + we.getStatement());
                System.out.println("bet: " + we.getBet());
                System.out.println("marbles amount: " + we.getMarblesAmount());
                System.out.println("*****************************");

                commitUpdate(receiver, new DataPacket(game_state, !ourTurn,
                        weReady, we));

            }
        });

        chooseStmt.setVisible(false);
        chooseStmt.setTouchable(disabled);
    }

    private void loadImages(){
        opHandInstances = new SerializableImage[11];
        ouHandInstances = new SerializableImage[11];
        for(int i = 0; i < 11; i++){
            opHandInstances[i] = new SerializableImage(new Texture(files.internal("textures/op_h_" + i + "_o.png")));
            ouHandInstances[i] = new SerializableImage(new Texture(files.internal("textures/ou_h_" + i + "_o.png")));
        }

    }

    private void gameFinished(){
        if (we.getMarblesAmount() <= 0){
            System.out.println("DEFEAT");
            game_state = GAME_FINISHED;
            reset();
        }
        if (opponent.getMarblesAmount() <= 0){
            System.out.println("VICTORY");
            game_state = GAME_FINISHED;
            reset();
        }
    }

    private synchronized void enableChooseBet(){
        chooseBet.setVisible(true);
        chooseBet.setTouchable(enabled);

    }

    private synchronized void enableChooseStmt(){
        chooseStmt.setVisible(true);
        chooseStmt.setTouchable(enabled);
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
