package honkot.gscheduler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import honkot.gscheduler.di.AppComponent;

public class BaseActivity extends AppCompatActivity {
    private AppComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public AppComponent getComponent() {
        if (activityComponent == null) {
            GlobalTimerApplication hackApplication = (GlobalTimerApplication) getApplication();
            activityComponent = hackApplication.getComponent();
        }
        return activityComponent;
    }

    public void updateActionBar(String title) {
//        int stringId = getApplicationInfo().labelRes;
//        String name = getString(stringId);
//        if (title != null) {
//            setTitle(title);
//        } else {
//            setTitle(name);
//        }
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            if (this instanceof GoBackToHomeInterface) {
//                actionBar.setDisplayHomeAsUpEnabled(((GoBackToHomeInterface) this).shouldShowGoBackButton());
//                actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
//            } else {
//                actionBar.setDisplayHomeAsUpEnabled(false);
//            }
//
//            Session session = SessionManager.getInstance().getSession();
//            if (session == null) {
//                actionBar.setSubtitle(null);
//                actionBar.setDisplayShowHomeEnabled(false);
//            } else {
//                String userAccount = session.getUserAccount();
//                Company company = sessionDao.findCompany(session);
//                String companyName = company == null ? "" : company.getName();
//                actionBar.setSubtitle(userAccount + " @ " + companyName);
//
//                actionBar.setDisplayShowHomeEnabled(true);
//            }
//        }
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
