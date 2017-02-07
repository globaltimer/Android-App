package honkot.gscheduler;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.model.CompareLocale;

public class ListActivity extends BaseActivity {

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
            Toast.makeText(getApplicationContext(), getItem(position).getGMT(), Toast.LENGTH_LONG).show();

            //ここで時間とか、listviewの中にあるものを表示させることもできる。
        }
    }

    //TODO menuからMainActivityにとばしてください

}
