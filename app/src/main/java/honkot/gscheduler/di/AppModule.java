package honkot.gscheduler.di;

import android.app.Application;
import android.content.Context;

import com.github.gfx.android.orma.AccessThreadConstraint;
import com.github.gfx.android.orma.migration.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import honkot.gscheduler.model.OrmaDatabase;

@Module
public class AppModule {

    public static final String DATABASE_NAME = "globaltimer.db";

    private Context context;

    public AppModule(Application app) {
        context = app;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public OrmaDatabase provideOrmaDatabase(Context context) {
        // TODO: メインスレッドでの読み書きは適切に変更する

        return OrmaDatabase.builder(context)
                .readOnMainThread(AccessThreadConstraint.NONE)
                .writeOnMainThread(BuildConfig.DEBUG ? AccessThreadConstraint.WARNING : AccessThreadConstraint.NONE)
                .name(AppModule.DATABASE_NAME)
                .build();
    }
}