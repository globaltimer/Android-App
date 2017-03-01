package honkot.gscheduler.di;

import javax.inject.Singleton;

import dagger.Component;
import honkot.gscheduler.activity.AddCompareLocaleActivity;
import honkot.gscheduler.fragment.CompareListFragment;
import honkot.gscheduler.fragment.RecordListFragment;
import honkot.gscheduler.fragment.SearchListFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(RecordListFragment fragment);

    void inject(SearchListFragment fragment);

    void inject(CompareListFragment fragment);

    void inject(AddCompareLocaleActivity activity);
}
