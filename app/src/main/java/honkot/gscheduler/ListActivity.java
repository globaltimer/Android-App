package honkot.gscheduler;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
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
                android.R.id.text1,
                worldTimes
        );

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener)adapter);

    }

    private class MyAdaptor extends ArrayAdapter<CompareLocale> implements AdapterView.OnItemClickListener{


        public MyAdaptor(Context context, int resource, int row, ArrayList<CompareLocale> objects) {
            super(context, resource, row, objects);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = super.getView(position, convertView, parent);

            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.list_row, parent, false);
            } else {
                view = convertView;
            }

            TextView cityTextView = (TextView)view.findViewById(R.id.cityTextView);
            TextView dateTextView = (TextView)view.findViewById(R.id.dateTextView);
            TextView timeTextView = (TextView)view.findViewById(R.id.timeTextView);


            cityTextView.setText(getItem(position). getDisplayName());
            dateTextView.setText(getItem(position).getGMT());
            timeTextView.setText(Integer.toString(getItem(position).getOffset()));


//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
//                    "fonts/Baloo-Regular.ttf");
//
//            cityTextView.setTypeface(tf);

            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), getItem(position).getGMT(), Toast.LENGTH_LONG).show();

            //ここで時間とか、listviewの中にあるものを表示させることもできる。
        }
    }



}
