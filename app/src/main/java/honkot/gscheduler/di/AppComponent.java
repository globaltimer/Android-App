package honkot.gscheduler.di;

import javax.inject.Singleton;

import dagger.Component;
import honkot.gscheduler.AddCompareLocaleActivity;
import honkot.gscheduler.MainActivity;
import honkot.gscheduler.fragment.RecordListFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(RecordListFragment fragment);

    void inject(AddCompareLocaleActivity activity);
}
