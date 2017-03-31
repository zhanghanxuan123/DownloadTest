package com.zhx.downloadtest;

import android.app.Application;
import android.util.Log;

/**
 * Author   :zhx
 * Create at 2017/3/27
 * Description:
 */
public class AppUtil {
    static final Application sApp;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            Log.e(AppUtil.class.getSimpleName(), "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                Log.e(AppUtil.class.getSimpleName(), "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            sApp = app;
        }
    }

    private AppUtil() {
    }
}
