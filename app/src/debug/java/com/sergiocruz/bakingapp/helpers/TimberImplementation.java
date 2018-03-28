package com.sergiocruz.bakingapp.helpers;


import timber.log.Timber;

public class TimberImplementation {
    public static void init() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return String.format("Sergio> %s; Method %s; Line %s",
                        super.createStackElementTag(element),
                        element.getMethodName(),
                        element.getLineNumber());
            }
        });
    }
}