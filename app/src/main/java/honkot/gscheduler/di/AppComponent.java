package honkot.gscheduler.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import honkot.gscheduler.AddCompareLocaleActivity;
import honkot.gscheduler.ListActivity;
import honkot.gscheduler.MainActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(Application application);

    void inject(ListActivity listActivity);

    void inject(MainActivity mainActivity);

    void inject(AddCompareLocaleActivity activity);
}
