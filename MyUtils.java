package foodies.gui.lab.clientfoodies;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

/**
 * Created by Guillaume Labbe on 27/05/2015.
 */
public class MyUtils {

    private static Activity mActivity = null;
    private static DisplayMetrics   mDm = null;

    public static void setActivity(Activity activity) {
        mActivity = activity;
        Display d = activity.getWindowManager().getDefaultDisplay();
        mDm = new DisplayMetrics();
        d.getMetrics(mDm);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return mDm;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getHeightRatio_16_9() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(MyUtils.pxToDp(mDm.widthPixels) / (16f / 9f)), mActivity.getResources().getDisplayMetrics());
    }

    public static int getHeightRatio_16_9(int width) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(MyUtils.pxToDp(width) / (16f / 9f)), mActivity.getResources().getDisplayMetrics());
    }
}