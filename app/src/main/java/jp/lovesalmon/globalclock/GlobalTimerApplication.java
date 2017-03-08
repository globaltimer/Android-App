package jp.lovesalmon.globalclock;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;

import jp.lovesalmon.globalclock.di.AppComponent;
import jp.lovesalmon.globalclock.di.AppModule;
import jp.lovesalmon.globalclock.di.DaggerAppComponent;

public class GlobalTimerApplication extends Application {
    AppComponent appComponent;

    @NonNull
    public AppComponent getComponent() {
        return appComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize to use ZonedDateTime
        AndroidThreeTen.init(this);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
