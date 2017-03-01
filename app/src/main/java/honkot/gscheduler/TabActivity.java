package honkot.gscheduler;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;

import honkot.gscheduler.fragment.RecordListFragment;
import honkot.gscheduler.fragment.SearchListFragment;

public class TabActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.content);

        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("Time Now");
        Bundle bundle1 = new Bundle();
        bundle1.putString("name", "timeNow");
        tabHost.addTab(tabSpec1, RecordListFragment.class, bundle1);

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("Set Time");
        Bundle bundle2 = new Bundle();
        bundle2.putString("name", "setTime");
        tabHost.addTab(tabSpec2, SearchListFragment.class, bundle2);
    }

}
