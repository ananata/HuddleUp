package OtherClasses;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.example.huddleup.R;

public class VisualUtilities {

    /**
     * Minor animation
     * Adapted from Windless on:
     * https://stackoverflow.com/questions/9448732/shaking-wobble-view-animation-in-android
     * @param view      View that will be animated
     *
     */
    public static void makeMeShake(View view) {
        Animation anim = new TranslateAnimation(-5, 5,0,0);
        anim.setDuration(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(5);
        view.startAnimation(anim);
    }

    /**
     * Generates custom pop-up toast that display information
     * temporarily.
     * @param message String
     * @param act Activity (Likely the activity the UI element is in)
     */
    public void popToast(String message, Activity act) {
        Toast toast = Toast.makeText(act, message, Toast.LENGTH_SHORT);
        View view = toast.getView();

        // Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(act.getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_IN);

        // Show toast
        toast.show();
    }
}
