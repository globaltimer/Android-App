package honkot.gscheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.ActivityMainBinding;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.utils.AdapterGenerater;

public class MainActivity extends BaseActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private CompareLocale mCompareLocale;
    private ActivityMainBinding binding;
    private boolean isDebug = true;

    public static final String EXTRA_ID = "EXTRA_ID";
    private static final String TAG = "MAIN_ACTIVITY";

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        // initialize data
        long id = getIntent().getLongExtra(EXTRA_ID, 0);
        if (id != 0) {
            // start as edit mode
            mCompareLocale = compareLocaleDao.findById(id);
        }
        if (mCompareLocale == null) {
            // start as New
            mCompareLocale = new CompareLocale(this);
        }

        // initialize views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.gmtSpinner.setAdapter(AdapterGenerater.getAdapter(this, false));
        binding.gmtSpinner.setOnItemSelectedListener(this);
        binding.dateButton.setOnClickListener(this);
        binding.timeButton.setOnClickListener(this);
        binding.insertOrUpdateButton.setOnClickListener(this);
        binding.deleteButton.setOnClickListener(this);
        binding.taskEditText.addTextChangedListener(mTaskWatcher);
        binding.taskEditText.setText(mCompareLocale.getTaskName());
        binding.cityEditText.addTextChangedListener(mCityWatcher);
        binding.cityEditText.setText(mCompareLocale.getLocationName());
        updateDisplayInfo();
        updateButtonState();
        printAll();

    }



    private void updateDisplayInfo() {
        binding.gmtSpinner.setSelection(AdapterGenerater.getPositionByGMTId(
                this, mCompareLocale.getGmtId()));
        binding.dateButton.setText(mCompareLocale.getDisplayDate());
        binding.timeButton.setText(mCompareLocale.getDisplayTime());
    }

    private void updateButtonState() {
        binding.insertOrUpdateButton.setEnabled(
                !mCompareLocale.getLocationName().isEmpty() && !mCompareLocale.getTaskName().isEmpty()
        );
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

    private TextWatcher mTaskWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // nothing to do
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // nothing to do
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mCompareLocale.setTaskName(editable.toString());
            updateButtonState();
        }
    };

    private TextWatcher mCityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // nothing to do
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // nothing to do
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mCompareLocale.setLocationName(editable.toString());
            updateButtonState();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap<String, Object> map = (HashMap)binding.gmtSpinner.getSelectedItem();
        Set<String> keys = map.keySet();

        if (isDebug) {
            for (String key : keys) {
                Log.e("test", "### " + key + ":" + map.get(key).getClass().getSimpleName());
            }
        }

        String name = (String)map.get("name");
        String gmt = (String)map.get("gmt");
        String id = (String)map.get("id");
        int offset = (int)map.get("offset");

        // Zoneだけ変えると、設定した時間もそれに応じて変わる
//        mCompareLocale.setZonedDateTime(mCompareLocale.getZonedDateTime()
//                        .withZoneSameInstant(ZoneId.of(gmt)));
        // なので、同じ時間でzoneだけ組み直す
        mCompareLocale.setGmtId(id);
        ZonedDateTime tmp = mCompareLocale.getZonedDateTime();
        ZonedDateTime neo = ZonedDateTime.of(
                tmp.getYear(),
                tmp.getMonthValue(),
                tmp.getDayOfMonth(),
                tmp.getHour(),
                tmp.getMinute(),
                0,
                0,
                ZoneId.of(gmt));
        mCompareLocale.setZonedDateTime(neo);

        if (isDebug) {
            Log.e("test", "### " + mCompareLocale.toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Impossible case. Do not consider this case.
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.dateButton: showDatePicker(); break;
            case R.id.timeButton: showTimePicker(); break;
            case R.id.insertOrUpdateButton:
                // TODO 試験用！消して下のと置き換える！
                mCompareLocale.setBasis(binding.basisCheckBox.isChecked());
                compareLocaleDao.insert(mCompareLocale);

//                if (mCompareLocale.getId() > 0) {
//                    compareLocaleDao.update(mCompareLocale);
//                } else {
//                    compareLocaleDao.insert(mCompareLocale);
//                }
                break;
            case R.id.deleteButton: compareLocaleDao.remove(mCompareLocale); break;
        }

        if (isDebug) {
            Log.e("test", "### " + mCompareLocale.toString());
        }

        printAll();
    }

    private void printAll() {
        StringBuffer buf = new StringBuffer();
        buf.append(mCompareLocale.getTaskName()).append(":").append(mCompareLocale.getLocationName()).append(":")
                .append(mCompareLocale.getZonedDateTime()).append(":").append(mCompareLocale.getZonedDateTime());
        for (CompareLocale favorite: compareLocaleDao.findAll()) {
            buf.append(System.getProperty("line.separator")).append("### ");
            buf.append(favorite.getTaskName()).append(":").append(favorite.getLocationName()).append(":")
                    .append(favorite.getZonedDateTime()).append(":").append(favorite.getGmtId());
        }
        binding.resultTextView.setText(buf.toString());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                this,
                mCompareLocale.getZonedDateTime().getYear(),
                mCompareLocale.getZonedDateTime().getMonthValue(),
                mCompareLocale.getZonedDateTime().getDayOfMonth());
        datePickerDialog.show();

    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                this,
                mCompareLocale.getZonedDateTime().getHour(),
                mCompareLocale.getZonedDateTime().getMinute(),
                true);
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
        mCompareLocale.setZonedDateTime(mCompareLocale.getZonedDateTime()
                .withYear(year)
                .withMonth(monthOfYear)
                .withDayOfMonth(dayOfMonth)
        );
        updateDisplayInfo();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        mCompareLocale.setZonedDateTime(mCompareLocale.getZonedDateTime()
                .withHour(hour)
                .withMinute(minute)
        );
        updateDisplayInfo();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);

    }


}
