package com.okason.diary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Valentine on 5/16/2017.
 */

public class Display {
    public Display() {
    }

    @SuppressLint({"NewApi"})
    public static Point getUsableSize(Context mContext) {
        Point displaySize = new Point();

        try {
            WindowManager e = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
            if(e != null) {
                android.view.Display display = e.getDefaultDisplay();
                if(display != null) {
                    if(Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        } catch (Exception var4) {
            Log.e("checkDisplaySize", "Error checking display sizes", var4);
        }

        return displaySize;
    }

    public static Point getVisibleSize(View view) {
        Point displaySize = new Point();
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        displaySize.x = r.right - r.left;
        displaySize.y = r.bottom - r.top;
        return displaySize;
    }

    public static Point getFullSize(View view) {
        Point displaySize = new Point();
        displaySize.x = view.getRootView().getWidth();
        displaySize.y = view.getRootView().getHeight();
        return displaySize;
    }

    public static int getStatusBarHeight(Context mContext) {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static int getNavigationBarHeightStandard(Context mContext) {
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0?mContext.getResources().getDimensionPixelSize(resourceId):0;
    }

    public static int getNavigationBarHeight(View view) {
        return getFullSize(view).y - getUsableSize(view.getContext()).y;
    }
}
