package com.sergiocruz.bakingapp.utils;

import android.webkit.MimeTypeMap;

public class AndroidUtils {

    public enum MimeType {
        IMAGE, AUDIO, VIDEO, OTHER, INVALID
    }

    public static MimeType getMymeTypeFromString(String uri) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri));
        if (mimeType == null) {
            return MimeType.INVALID;
        } else {
            if (mimeType.contains("image")) {
                return MimeType.IMAGE;
            } else if (mimeType.contains("audio")) {
                return MimeType.AUDIO;
            } else if (mimeType.contains("video")) {
                return MimeType.VIDEO;
            } else {
                return MimeType.OTHER;
            }
        }
    }

}
