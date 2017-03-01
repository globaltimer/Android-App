package honkot.gscheduler.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import honkot.gscheduler.GlobalTimerApplication;
import honkot.gscheduler.di.AppComponent;

public class BaseActivity extends AppCompatActivity {
    private AppComponent activityComponent;
    private boolean calledUpdateActionBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!calledUpdateActionBar) {
            updateActionBar(null, null);
        }
    }

    @NonNull
    public AppComponent getComponent() {
        if (activityComponent == null) {
            GlobalTimerApplication hackApplication = (GlobalTimerApplication) getApplication();
            activityComponent = hackApplication.getComponent();
        }
        return activityComponent;
    }

    public void updateActionBar(String title, String subTitle) {
        calledUpdateActionBar = true;

        int stringId = getApplicationInfo().labelRes;
        String name = getString(stringId);
        if (title != null) {
            setTitle(title);
        } else {
            setTitle(name);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // setSubtitleはNullを許容する
            actionBar.setSubtitle(subTitle);
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        event.startTracking();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()) {
            if (!onBackKey()) {
                return false;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * バックしたくなければfalseを返すこと
     * @return
     */
    protected boolean onBackKey() {
        return true;
    }
}
