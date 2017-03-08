package jp.lovesalmon.globalclock.di;

import javax.inject.Singleton;

import dagger.Component;
import jp.lovesalmon.globalclock.activity.AddCompareLocaleActivity;
import jp.lovesalmon.globalclock.activity.TabActivity;
import jp.lovesalmon.globalclock.fragment.CompareListFragment;
import jp.lovesalmon.globalclock.fragment.RecordListFragment;
import jp.lovesalmon.globalclock.fragment.SearchListFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(TabActivity activity);

    void inject(RecordListFragment fragment);

    void inject(SearchListFragment fragment);

    void inject(CompareListFragment fragment);

    void inject(AddCompareLocaleActivity activity);
}
