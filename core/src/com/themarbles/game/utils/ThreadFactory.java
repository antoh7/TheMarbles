package com.themarbles.game.utils;

import java.util.HashMap;
import java.util.Map;

/** Util, using to quickly create/invoke new threads.
 * @see Thread
 * **/

public class ThreadFactory {

    Map<String, Thread> threadBox = new HashMap<>();

    public void createAndAdd(Runnable task, String name, boolean daemon){
        Thread newThread = new Thread(task, name);
        newThread.setDaemon(daemon);
        threadBox.put(name, newThread);
    }

    public void startThread(String name){
        Thread threadToStart = threadBox.get(name);
        if (!threadToStart.isAlive()) threadToStart.start();
    }
}
