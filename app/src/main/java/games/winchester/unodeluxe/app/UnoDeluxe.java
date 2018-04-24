package games.winchester.unodeluxe.app;

import android.app.Application;
import android.content.Context;

public class UnoDeluxe extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        UnoDeluxe.context = getApplicationContext();
    }

    public static Context getContext() {
        return UnoDeluxe.context;
    }
}
