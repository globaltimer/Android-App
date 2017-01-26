package honkot.gscheduler;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gfx.android.orma.AccessThreadConstraint;

import java.util.ArrayList;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.OrmaDatabase;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<CompareLocale> adapter;
    ArrayList<CompareLocale> worldTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initView();
    }

    private void initView() {


        OrmaDatabase orma = OrmaDatabase.builder(this)
                .writeOnMainThread(BuildConfig.DEBUG ? AccessThreadConstraint.WARNING : AccessThreadConstraint.NONE)
                .build();
        CompareLocaleDao compareLocaleDao = new CompareLocaleDao(orma);
        worldTimes = (ArrayList<CompareLocale>)compareLocaleDao.findAll().toList();
//        worldTimes.add(new Times("Tokyo", "08:00", "AM", "Dec 20th 2017", false));
//        worldTimes.add(new Times("Berlin", "08:00", "AM", "Dec 20th 2017", false));


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

            TextView cityTextView = (TextView)view.findViewById(R.id.text1);
            TextView dateTextView = (TextView)view.findViewById(R.id.text2);
            TextView timeTextView = (TextView)view.findViewById(R.id.text3);


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
