package com.sergiocruz.bakingapp.helpers;


import timber.log.Timber;

public class ReleaseTree extends Timber.Tree{
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        // No debugging Tree
        return;
    }
}
