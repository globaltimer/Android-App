package honkot.gscheduler;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.model.CompareLocale;

public class ListActivity extends BaseActivity {

    private static final String TAG = "LIST_ACTIVITY";
    @Inject
    CompareLocaleDao compareLocaleDao;

    ListView listView;
    ArrayAdapter<CompareLocale> adapter;
    ArrayList<CompareLocale> worldTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setContentView(R.layout.activity_list);
//        showActionBar();
        initView();
    }

    private void initView() {
        worldTimes = (ArrayList<CompareLocale>)compareLocaleDao.findAll().toList();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MyAdaptor(
                this,
                R.layout.list_row,
                worldTimes
        );

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener)adapter);

    }

    private class MyAdaptor extends ArrayAdapter<CompareLocale> implements AdapterView.OnItemClickListener{


        public MyAdaptor(Context context, int resource, ArrayList<CompareLocale> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListRowBinding binding;
            if (convertView == null) {
                binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.list_row, parent, false);
            } else {
                binding = DataBindingUtil.getBinding(convertView);
            }

            binding.setCompareLocale(getItem(position));

//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
//                    "fonts/Baloo-Regular.ttf");
//
//            cityTextView.setTypeface(tf);

            return binding.getRoot();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getApplicationContext(), getItem(position).getGMT(), Toast.LENGTH_LONG).show();

            //ここで時間とか、listviewの中にあるものを表示させることもできる。
        }
    }


//    private void showActionBar() {
//        LayoutInflater actionBarInflator = (LayoutInflater) this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = actionBarInflator.inflate(R.layout.menu, null);
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayShowHomeEnabled (false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setCustomView(v);
//
//        v.findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "onClick: YAY");
//            }
//        });
//
//        v.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "onClick: YAY2");
//            }
//        });
//    }


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
