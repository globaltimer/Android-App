package honkot.gscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.model.CompareLocale;

public class ListActivity extends BaseActivity {

    private static final String TAG = "LIST_ACTIVITY";
    private static final int REQUEST_CODE = 1;
    public static final int RESULT_SUCCESS = 1;

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setContentView(R.layout.activity_list);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_SUCCESS) {
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recylerView);
            ((MyRecAdapter)recyclerView.getAdapter()).setData(compareLocaleDao.findAll());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyRecAdapter myAdapter = new MyRecAdapter(compareLocaleDao.findAll(), new MyRecAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(CompareLocale compareLocale) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, compareLocale.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                Intent intent = new Intent(ListActivity.this, AddCompareLocaleActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
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
