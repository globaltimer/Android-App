package honkot.gscheduler;

import android.os.Bundle;

import honkot.gscheduler.fragment.SearchListFragment;

public class AddCompareLocaleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(
                android.R.id.content, new SearchListFragment()).commit();
    }
}
