package it.gcantoni.comelit.application;

import android.app.Application;
import com.google.android.material.color.DynamicColors;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Dynamic Colors
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
