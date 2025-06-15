package ru.kbuearpov.themarbles.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.TimeUnit;

/** Util, using while game is running.
 * @see Actor
 * @see TimeUnit
 * @see Array
 * **/

public class  GameUtils {

    public static void setActorVisualProps(Actor actor, boolean visible){
        // makes actor visible/invisible
        actor.setVisible(visible);
    }

    public static int checkValue(int value){
        // checks if value < 0
        if (value < 0){
            value = 0;
        }
        return value;
    }

    public static void timedWaiting(TimeUnit unit, int time){
        // stops current thread until timeout is over
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("visualising thread was interrupted");
            System.exit(10);
        }
    }

    public static Array<Integer> computeBetsRange(int start, int end) {
        // calculates max available marbles amount,
        // which can be a bet
        Array<Integer> integerArray = new Array<>();
        for (int i = start; i <= end; i++) {
            integerArray.add(i);
        }
        return integerArray;
    }

    public static boolean isEven(int value){
        // checks if value is even
        return value % 2 == 0;
    }

    public static boolean isOdd(int value){
        // checks if value is odd
        return value % 2 != 0;
    }

}
