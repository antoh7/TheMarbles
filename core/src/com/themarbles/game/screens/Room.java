package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.utils.Align.center;
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
import static com.themarbles.game.utils.FontGenerator.generateFont;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.Player;
import com.themarbles.game.myImpls.SerializableImage;
import com.themarbles.game.networking.DataPacket;
import com.themarbles.game.networking.Receiver;
import com.themarbles.game.utils.ThreadFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Room implements Screen {

    private final EntryPoint entryPoint;
    private final Stage stage;
    private final Image background;
    private final TextArea tokenArea;
    private final SelectBox<Integer> betSelection;
    private final SelectBox<String> statementSelection;
    private final BitmapFont indicatorFont;
    private final ThreadFactory threadFactory;
    private final TextButton startButton;

    private Map<Integer, SerializableImage> opponentHandInstances;
    private Map<Integer, SerializableImage> ourHandInstances;
    private Player we, opponent;
    private String game_state;
    private Receiver receiver;
    private boolean ourTurn, endOfStage,weReady, opponentReady;

    public Room(EntryPoint entryPoint){
        this.entryPoint = entryPoint;
        stage = new Stage();

        game_state = WAITING_FOR_PLAYER_CONNECT;

        weReady = false;
        opponentReady = false;
        endOfStage = false;

        threadFactory = new ThreadFactory();

        threadFactory.createAndAdd(this::initAcceptingThread, "accepting_thread", true);
        threadFactory.createAndAdd(this::initNetUpdateListener, "net_update_listener_thread", true);
        threadFactory.createAndAdd(this::initEventUpdateManager, "event_update_manager_thread", true);


        indicatorFont = generateFont(files.internal("fonts/font.ttf"), 50, Color.ROYAL);

        startButton = new TextButton("S T A R T", new Skin(files.internal("buttons/startbuttonassets/startbuttonskin.json")));
        betSelection = new SelectBox<>(new Skin(files.internal("labels/selectlist/selectlist.json")));
        statementSelection = new SelectBox<>(new Skin(files.internal("labels/selectlist/selectlist.json")));

        background = new Image(new Texture(files.internal("textures/game_background.jpg")));

        tokenArea = new TextArea(null, new Skin(files.internal("labels/tokenfield/tokenfieldskin.json")));

        SerializableImage ourHandClosed = new SerializableImage((new Texture(files.internal("textures/ou_h_c.png"))));
        SerializableImage opponentHandClosed = new SerializableImage((new Texture(files.internal("textures/op_h_c.png"))));

        loadImages();

        we = new Player(ourHandClosed, new SerializableImage(), WIDTH - Player.getDefaultHandWidth(), 0);
        opponent = new Player(opponentHandClosed, new SerializableImage(), 0, HEIGHT - Player.getDefaultHandHeight());
    }

    @Override
    public void show() {

        initBackground();
        initStartButton();
        initTokenArea();
        initBetSelectionWindow();
        initStatementSelectionWindow();

        stage.addActor(background);

        stage.addActor(we.getPlayerHandClosed());
        stage.addActor(opponent.getPlayerHandClosed());
        stage.addActor(we.getPlayerHandOpened());
        stage.addActor(opponent.getPlayerHandOpened());

        stage.addActor(betSelection);
        stage.addActor(statementSelection);

        if (entryPoint.deviceState.equals(SERVER)) {
            stage.addActor(startButton);
            stage.addActor(tokenArea);
        }

        input.setInputProcessor(stage);

        threadFactory.startThread("accepting_thread");
    }

    @Override
    public void render(float delta){
        entryPoint.batch.begin();

        stage.act(delta);
        stage.draw();

        // hand rendering
        if (game_state.equals(GAME_RUNNING)) {
            String text;

            synchronized (this) {
                text = ourTurn ? "Turn: yours" : "Turn: opponent`s";
            }

            indicatorFont.draw(entryPoint.batch, text, 75, 55);
            indicatorFont.draw(entryPoint.batch, "marbles remain: " + we.getMarblesAmount(), 405, 55);

            threadFactory.startThread("event_update_manager_thread");

            if (weReady) {
                we.setHandVisible(we.getPlayerHandClosed(), true);
            }
            if (opponentReady) {
                opponent.setHandVisible(opponent.getPlayerHandClosed(), true);
            }
            if (endOfStage){
                we.setHandVisible(we.getPlayerHandClosed(), false);
                opponent.setHandVisible(opponent.getPlayerHandClosed(), false);
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
        indicatorFont.dispose();
    }


    // ################################## init methods #################################

    private void initAcceptingThread(){
        try {
            entryPoint.client = entryPoint.server.accept();
        } catch (NullPointerException ignore){
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiver = new Receiver(entryPoint.client);
        game_state = WAITING_FOR_START;
        threadFactory.startThread("net_update_listener_thread");
    }

    private void initNetUpdateListener(){

        DataPacket currPacket;
        Player abstractPlayer;

        while (!game_state.equals(GAME_FINISHED)) {

            currPacket = receiver.getData();
            abstractPlayer = currPacket.getPlayerData();

            opponent.setBet(abstractPlayer.getBet());
            opponent.setMarblesAmount(abstractPlayer.getMarblesAmount());
            opponent.setStatement(abstractPlayer.getStatement());

            synchronized (this) {
                game_state = currPacket.getGameState();
                ourTurn = !currPacket.getTurnOrder();
                opponentReady = currPacket.getPlayerReady();
            }
        }
    }

    private void initEventUpdateManager(){

        while (!game_state.equals(GAME_FINISHED)) {

            synchronized (this) {
                if (ourTurn && !weReady) {
                    setActorVisualProps(betSelection, true);
                }

            }

            synchronized (this) {
                if (!ourTurn && opponentReady && !weReady) {
                    setActorVisualProps(betSelection, true);
                }
            }

            if (weReady && opponentReady) {

                timedWaiting(SECONDS, 2);

                weReady = false;
                opponentReady = false;
                endOfStage = true;

                final int ourMarblesAmount = we.getMarblesAmount();
                final int opponentMarblesAmount = opponent.getMarblesAmount();
                final String ourStmt = we.getStatement();
                final String opponentStmt = opponent.getStatement();

                int ourBet = we.getBet();
                int opponentBet = opponent.getBet();
                int opponentMarblesAmountOnHand = 0, ourMarblesAmountOnHand = 0;

                we.setPlayerHandOpened(ourHandInstances.get(ourBet));
                opponent.setPlayerHandOpened(opponentHandInstances.get(opponentBet));

                timedWaiting(SECONDS, 3);

                //we make bet,
                //opponent guesses it
                if (ourTurn){

                    if ((opponentStmt.equals(EVEN) && isEven(ourBet)) || (opponentStmt.equals(ODD) && isOdd(ourBet))){
                        if (ourBet <= opponentBet) {
                            opponent.setMarblesAmount(opponentMarblesAmount + ourBet);
                            we.setMarblesAmount(ourMarblesAmount - ourBet);
                        } else {
                            opponent.setMarblesAmount(opponentMarblesAmount + opponentBet);
                            we.setMarblesAmount(ourMarblesAmount - opponentBet);
                        }
                        ourMarblesAmountOnHand = ourBet - opponentBet;
                        opponentMarblesAmountOnHand = opponentBet + ourBet;

                    } else{

                        if (ourBet <= opponentBet) {
                            we.setMarblesAmount(ourMarblesAmount + ourBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - ourBet);
                        } else {
                            we.setMarblesAmount(ourMarblesAmount + opponentBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - opponentBet);
                        }
                        ourMarblesAmountOnHand = ourBet + opponentBet;
                        opponentMarblesAmountOnHand = opponentBet - ourBet;

                    }
                }

                //opponent makes bet,
                //we guess it
                if (!ourTurn){

                    if ((ourStmt.equals(EVEN) && isEven(opponentBet)) || (ourStmt.equals(ODD) && isOdd(opponentBet))){
                        if (ourBet <= opponentBet) {
                            we.setMarblesAmount(ourMarblesAmount + ourBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - ourBet);
                        } else {
                            we.setMarblesAmount(ourMarblesAmount + opponentBet);
                            opponent.setMarblesAmount(opponentMarblesAmount - opponentBet);
                        }
                        ourMarblesAmountOnHand = ourBet + opponentBet;
                        opponentMarblesAmountOnHand = opponentBet - ourBet;

                    } else {

                        if (ourBet <= opponentBet) {
                            we.setMarblesAmount(ourMarblesAmount - ourBet);
                            opponent.setMarblesAmount(opponentMarblesAmount + ourBet);
                        } else {
                            we.setMarblesAmount(ourMarblesAmount - opponentBet);
                            opponent.setMarblesAmount(opponentMarblesAmount + opponentBet);
                        }
                        ourMarblesAmountOnHand = ourBet - opponentBet;
                        opponentMarblesAmountOnHand = opponentBet + ourBet;

                    }
                }

                we.setPlayerHandOpened(ourHandInstances.get(checkValue(ourMarblesAmountOnHand)));
                opponent.setPlayerHandOpened(opponentHandInstances.get(checkValue(opponentMarblesAmountOnHand)));

                timedWaiting(SECONDS, 3);

                reset();
            }
        }

    }

    private void initStartButton(){
        startButton.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT + 35);
        startButton.setPosition((float) WIDTH/2 - startButton.getWidth() / 2,
                (float) HEIGHT/2 - startButton.getHeight() / 2);

        startButton.addListener(new ChangeListener() {
            @Override
            @SuppressWarnings("deprecated")
            public void changed(ChangeEvent event, Actor actor) {
                if (game_state.equals(WAITING_FOR_START)) {
                    choosePlayerTurn();

                    setActorVisualProps(startButton, false);
                    setActorVisualProps(tokenArea, false);

                    threadFactory.stopThread("accepting_thread");
                }
            }
        });
    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }

    private void initTokenArea(){
        String text = """
                                  INVITE TOKEN:
                                       %s
                      """.formatted(entryPoint.inviteToken).replaceAll("\n", "");

        tokenArea.setPosition((float) WIDTH/2 - startButton.getWidth() / 2,
                (float) HEIGHT/2 - startButton.getHeight() * 2 + 30);
        tokenArea.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT + 35);

        tokenArea.setAlignment(center);
        tokenArea.setText(text);
        tokenArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tokenArea.setText(text);
            }
        });

    }

    private void initBetSelectionWindow(){
        betSelection.setAlignment(center);
        betSelection.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT + 35);
        betSelection.setPosition((float) WIDTH / 2 - betSelection.getWidth() / 2,
                (float) HEIGHT / 2 - betSelection.getHeight() / 2);

        betSelection.setItems(computeBetsRange(1, 5));
        betSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                int ma = betSelection.getSelected();

                we.setBet(ma);
                we.setPlayerHandOpened(ourHandInstances.get(ma));

                if (ourTurn) {
                    weReady = true;
                    setActorVisualProps(betSelection, false);
                    receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));
                    return;
                }

                setActorVisualProps(statementSelection, true);

            }
        });

        setActorVisualProps(betSelection, false);
    }

    private void initStatementSelectionWindow() {
        statementSelection.setAlignment(center);
        statementSelection.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT + 35);
        statementSelection.setPosition((float) WIDTH / 2 - statementSelection.getWidth() / 2,
                (float) HEIGHT / 2 - statementSelection.getHeight() / 2);

        statementSelection.setItems(Array.with("ODD", "EVEN"));
        statementSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                we.setStatement(statementSelection.getSelected());

                weReady = true;

                setActorVisualProps(betSelection, false);

                setActorVisualProps(statementSelection, false);

                receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));

            }
        });

        setActorVisualProps(statementSelection, false);
    }


    // ******************************* util methods *************************************

    private void setActorVisualProps(Actor actor, boolean visible){
        // makes actor visible/invisible
        actor.setVisible(visible);
    }

    private int checkValue(int value){
        // checks if value < 0
        if (value < 0){
            value = 0;
        }
        return value;
    }

    private void timedWaiting(TimeUnit unit, int time){
        // stops current thread until timeout is over
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("visualising thread was interrupted");
            System.exit(10);
        }
    }

    private Array<Integer> computeBetsRange(int start, int end) {
        // calculates max available marbles amount,
        // which can be a bet
        int[] intArray = IntStream.rangeClosed(start, end).toArray();
        Array<Integer> integerArray = new Array<>();
        for (int element: intArray) {
            integerArray.add(element);
        }
        return integerArray;
    }

    private boolean isEven(int value){
        // checks if value is even
        return value % 2 == 0;
    }

    private boolean isOdd(int value){
        // checks if value is odd
        return value % 2 != 0;
    }


    // &&&&&&&&&&&&&&&&&&&&&&&&&& another methods &&&&&&&&&&&&&&&&&&&&&&&&

    private void loadImages(){
        // loads hand textures
        opponentHandInstances = new HashMap<>();
        ourHandInstances = new HashMap<>();
        for(int i = 0; i < 11; i++){
            opponentHandInstances.put(i, new SerializableImage(new Texture(files.internal("textures/op_h_" + i + "_o.png"))));
            ourHandInstances.put(i, new SerializableImage(new Texture(files.internal("textures/ou_h_" + i + "_o.png"))));
        }
    }

    private void isGameFinished(){
        // checks if a game must be finished
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

    private void choosePlayerTurn(){
        // at start, once selects whose turn to make bet
        game_state = RANDOMIZING_TURN;
        Random random = new Random();
        boolean[] turnVariants = new boolean[]{true, false};

        ourTurn = turnVariants[random.nextInt(turnVariants.length)];

        game_state = GAME_RUNNING;

        receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));
    }

    private void reset(){
        // resets current stage, inverts turn order
        endOfStage = false;
        weReady = false;
        opponentReady = false;

        we.setHandVisible(we.getPlayerHandOpened(), false);
        opponent.setHandVisible(opponent.getPlayerHandOpened(), false);
        betSelection.setItems(computeBetsRange(1, we.getMarblesAmount()));

        if (entryPoint.deviceState.equals(SERVER)){
            if (we.getMarblesAmount() == 1) ourTurn = false;
            else if (opponent.getMarblesAmount() == 1) ourTurn = true;
            else ourTurn = !ourTurn;

            receiver.sendData(new DataPacket(game_state, ourTurn, weReady, we));
        } else {
            // waiting for net update listener thread
            // receive a packet with changed turn order
            timedWaiting(SECONDS, 1);
        }
    }
    
}