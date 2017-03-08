package jp.lovesalmon.globalclock.activity;

import android.os.Bundle;
import android.view.View;

import jp.lovesalmon.globalclock.fragment.SearchListFragment;

public class AddCompareLocaleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(
                android.R.id.content, new SearchListFragment()).commit();
    }


}
