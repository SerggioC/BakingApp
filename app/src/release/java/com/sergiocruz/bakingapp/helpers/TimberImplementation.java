package com.sergiocruz.bakingapp.helpers;


import timber.log.Timber;

public class TimberImplementation {
    public static void init() {
        Timber.plant(new ReleaseTree());
    }
}