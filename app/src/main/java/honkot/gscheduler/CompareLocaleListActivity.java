package honkot.gscheduler;

import android.os.Bundle;

import honkot.gscheduler.fragment.CompareListFragment;

public class CompareLocaleListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(
                android.R.id.content, new CompareListFragment()).commit();
    }
}
