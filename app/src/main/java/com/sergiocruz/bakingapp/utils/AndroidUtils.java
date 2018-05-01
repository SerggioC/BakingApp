package com.sergiocruz.bakingapp.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.bakingapp.R;

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

    public static void showCustomToast(Context context, String toastText, int icon_RID, int text_color_Res_Id, int duration) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            Toast.makeText(context, toastText, duration).show();
            return;
        }

        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_layout_text);
        text.setText(toastText);
        text.setTextColor(ContextCompat.getColor(context, text_color_Res_Id));
        ImageView imageV = layout.findViewById(R.id.toast_img);
        imageV.setImageResource(icon_RID);
        Toast theCustomToast = new Toast(context);
        theCustomToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        theCustomToast.setDuration(duration);
        theCustomToast.setView(layout);
        theCustomToast.show();
    }

    public static final String capitalize(String string) {
        return TextUtils.isEmpty(string) ? string :
                string.substring(0, 1).toUpperCase() + string.substring(1);
    }


    public static void animateItemViewSlideFromBottom(View viewToAnimate, long timeOffSet) {
        Animation bottomAnimation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_animation_slide_from_bottom);
        bottomAnimation.setStartOffset(timeOffSet);
        viewToAnimate.startAnimation(bottomAnimation);
    }

}
