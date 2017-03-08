package jp.lovesalmon.globalclock.di;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    final Activity activity;


    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    public Activity activity() {
        return activity;
    }

    @Provides
    public Context context() {
        return activity;
    }

    @Provides
    LayoutInflater layoutInflater() {
        return activity.getLayoutInflater();
    }
}
