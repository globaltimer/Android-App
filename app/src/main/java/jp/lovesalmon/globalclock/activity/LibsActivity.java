package jp.lovesalmon.globalclock.activity;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

public class LibsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LibsSupportFragment fragment = new LibsBuilder()
                .withLibraries("Android Orma")
                .withLibraries("Three Ten ABP")
                .supportFragment();

        getSupportFragmentManager().beginTransaction().replace(
                android.R.id.content, fragment).commit();
    }
}
