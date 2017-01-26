package com.chernenkovit.tasker;

import android.app.Application;

/** Application class. */
public class App extends Application {

    private static boolean activityVisible;

    public static boolean isActivityVisible(){
        return activityVisible;
    }

    public static void activityResumed(){
        activityVisible=true;
    }

    public static void activityPaused(){
        activityVisible=false;
    }
}
