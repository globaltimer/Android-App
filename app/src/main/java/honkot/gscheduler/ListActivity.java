package honkot.gscheduler;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.model.CompareLocale;

public class ListActivity extends BaseActivity {

    private static final String TAG = "LIST_ACTIVITY";
    @Inject
    CompareLocaleDao compareLocaleDao;
    ArrayList<CompareLocale> worldTimes;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setContentView(R.layout.activity_list);
        initView();
    }

    private void initView() {

        worldTimes = new ArrayList<>(compareLocaleDao.findAll().toList());
        recyclerView = (RecyclerView)findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyRecAdapter myAdapter = new MyRecAdapter(worldTimes);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setUpItemTouchHelper();

    }

    private void setUpItemTouchHelper(){

    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Log.i(TAG, "onOptionsItemSelected: ");
                return true;

            case R.id.action_edit:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



}
