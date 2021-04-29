package com.example.beatme.ui.tournaments.utility;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class BracketsUtility{
    public static int dpToPx(int dp) {

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
