package honkot.gscheduler;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.gfx.android.orma.AccessThreadConstraint;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.inject.Inject;

import honkot.gscheduler.adapter.LocaleAdapter;
import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.dao.CompareLocaleDao_Factory;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.OrmaDatabase;
import honkot.gscheduler.utils.AdapterGenerater;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Inject
    OrmaDatabase orma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(AdapterGenerater.getAdapter(this, false));

        findViewById(R.id.insert).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);

        orma = OrmaDatabase.builder(this)
                .writeOnMainThread(BuildConfig.DEBUG ? AccessThreadConstraint.WARNING : AccessThreadConstraint.NONE)
                .build();
        compareLocaleDao = new CompareLocaleDao(orma);

        TextView tv = (TextView)findViewById(R.id.result);
        tv.setText(TimeZone.getDefault().getDisplayName() + ":" + TimeZone.getDefault().getID() + ":" + getGMT(TimeZone.getDefault()));
    }

    private String getGMT(TimeZone tz) {
//        final TimeZone tz = TimeZone.getTimeZone(id);
        final int HOURS_1 = 60 * 60000;

        long date = Calendar.getInstance().getTimeInMillis();
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        return name.toString();
    }

    @Override
    public void onClick(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        HashMap<String, Object> map = (HashMap)spinner.getSelectedItem();
        Set<String> keys = map.keySet();
        for (String key: keys) {
            Log.e("test", "### " + key + ":" + map.get(key).getClass().getSimpleName());
        }

        String name = (String)map.get("name");
        String gmt = (String)map.get("gmt");
        String id = (String)map.get("id");
        int offset = (int)map.get("offset");
        Log.e("test", "### " + name + ":" + gmt + ":" + id + ":" + offset);

        switch (view.getId()) {
            case R.id.insert:
                CompareLocale insertData = new CompareLocale();
                insertData.id = id;
                insertData.displayName = name;
                insertData.GMT = gmt;
                insertData.offset = offset;
                compareLocaleDao.insert(insertData);
                break;
            case R.id.delete:
                CompareLocale deleteData = new CompareLocale();
                deleteData.id = id;
                deleteData.displayName = name;
                deleteData.GMT = gmt;
                deleteData.offset = offset;
                compareLocaleDao.remove(deleteData);
                break;
        }

        StringBuffer buf = new StringBuffer();
        TextView tv = (TextView)findViewById(R.id.result);
        buf.append(TimeZone.getDefault().getDisplayName() + ":" + TimeZone.getDefault().getID() + ":" + getGMT(TimeZone.getDefault()));
        for (CompareLocale favorite: compareLocaleDao.findAll()) {
            buf.append(System.getProperty("line.separator"));
            buf.append("### " + favorite.displayName + ":" + favorite.GMT + ":" + favorite.id);
        }
        tv.setText(buf.toString());
    }
}
