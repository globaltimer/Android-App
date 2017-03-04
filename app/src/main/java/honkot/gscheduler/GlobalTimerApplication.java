package honkot.gscheduler;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import honkot.gscheduler.di.AppComponent;
import honkot.gscheduler.di.AppModule;
import honkot.gscheduler.di.DaggerAppComponent;

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

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
