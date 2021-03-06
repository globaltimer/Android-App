package jp.lovesalmon.globalclock.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import jp.lovesalmon.globalclock.R;
import jp.lovesalmon.globalclock.activity.AddCompareLocaleActivity;
import jp.lovesalmon.globalclock.activity.BaseActivity;
import jp.lovesalmon.globalclock.dao.CompareLocaleDao;
import jp.lovesalmon.globalclock.databinding.FragmentCompareListBinding;
import jp.lovesalmon.globalclock.model.CompareLocale;
import jp.lovesalmon.globalclock.model.CompareLocale_Selector;
import jp.lovesalmon.globalclock.utils.MyRecAdapter;

import static jp.lovesalmon.globalclock.fragment.RecordListFragment.REQUEST_CODE;
import static jp.lovesalmon.globalclock.fragment.RecordListFragment.RESULT_SUCCESS;

/**
 * Created by hiroki on 2017-02-25.
 */
public class CompareListFragment extends Fragment {

    private static final String TAG = "CompareListFragment";
    private static final int SHIFT_MINS = 30;
    private FragmentCompareListBinding binding;
    private int offsetMinutes;
    private CompareLocale basisLocale;
    private ZonedDateTime startZonedDateTime;
    private long lastBasisId = 0;

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getComponent().inject(this);

        binding = FragmentCompareListBinding.inflate(
                getActivity().getLayoutInflater(), null, false);
        initialize();

        return binding.getRoot();
    }

    public void initialize() {
        basisLocale = compareLocaleDao.getBasisLocale();
        CompareLocale_Selector expectBasisSelector = compareLocaleDao.findAllExceptBasis();

        boolean basisAvailable = basisLocale != null;
        boolean emptyRecord = !basisAvailable && expectBasisSelector.isEmpty();

        binding.emptyView.emptyViewRoot.setVisibility(emptyRecord ? View.VISIBLE : View.GONE);
        binding.mainView.setVisibility(emptyRecord ? View.GONE : View.VISIBLE);

        if (basisAvailable) {
            if (lastBasisId != basisLocale.getId()) {
                // 初回 or basisが変わったら全て描画し直す
                lastBasisId = basisLocale.getId();
                basisLocale.setZonedDateTimeNow();
                startZonedDateTime = basisLocale.getZonedDateTime();
                offsetMinutes = 0;
                initView();
                return;

            } else {
                MyRecAdapter myAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                if (myAdapter.getItemCount() != expectBasisSelector.count()) {
                    // CompareLocaleが追加/削除されていたらリスト更新
                    myAdapter.setDataAndUpdateList(expectBasisSelector);
                }
            }

        } else {
            if (!emptyRecord) {
                Toast.makeText(getContext(),
                        R.string.compare_fragment_toast_tap_locale,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        R.string.compare_fragment_toast_empty_record,
                        Toast.LENGTH_SHORT).show();
                binding.emptyView.addFirstLocaleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AddCompareLocaleActivity.class);
                        startActivityForResult(intent, RESULT_SUCCESS);
                    }
                });
            }
            initView();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_SUCCESS) {
            initialize();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyRecAdapter myAdapter = new MyRecAdapter(compareLocaleDao.findAllExceptBasis(), new MyRecAdapter.OnItemClickListener() {

            /**
             * When the row is clicked
             */
            @Override
            public void onItemClicked(CompareLocale compareLocale, int position) {
                // update basis and recyclerView
                compareLocaleDao.changeBasis(compareLocale);
                MyRecAdapter myAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                myAdapter.switchBasisWithClearCash(
                        compareLocaleDao.findAllExceptBasis(), compareLocale);
                myAdapter.notifyDataSetChanged();

                // update basis view
                basisLocale = compareLocaleDao.getBasisLocale();
                basisLocale.setZonedDateTime(
                        startZonedDateTime.withZoneSameInstant(
                                basisLocale.getZonedDateTime().getZone()));
                basisLocale.setOffsetMins(offsetMinutes);
                lastBasisId = basisLocale.getId();
                updateTime();
            }
        }, new OffsetMinsGetter() {

            /**
             * just callback the time offset
             */
            @Override
            public int getOffsetMins() {
                return offsetMinutes;
            }
        });
        binding.recyclerView.setAdapter(myAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.shiftLeftButton.setOnClickListener(shiftListener);
        binding.shiftRightButton.setOnClickListener(shiftListener);
        binding.editDateButton.setOnClickListener(dateListener);
        binding.editTimeButton.setOnClickListener(timeListener);
        updateTime();
    }

    private View.OnClickListener shiftListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.shiftLeftButton:
                    offsetMinutes -= SHIFT_MINS;
                    break;
                case R.id.shiftRightButton:
                    offsetMinutes += SHIFT_MINS;
                    break;
            }
            basisLocale.setOffsetMins(offsetMinutes);
            updateTime();
        }
    };

    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Calculate current offset time
            ZonedDateTime basisDateTime = basisLocale.getZonedDateTime().plusMinutes(offsetMinutes);

            new DatePickerDialog(getContext(), R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, final int year, final int monthOfYear, final int dayOfMonth) {
                    ZonedDateTime selectDate = basisLocale.getZonedDateTime()
                            .plusMinutes(offsetMinutes)
                            .withYear(year).withMonth(monthOfYear + 1).withDayOfMonth(dayOfMonth);
                    long offsetSeconds = selectDate.toEpochSecond() - basisLocale.getZonedDateTime().toEpochSecond();
                    offsetMinutes = (int)(offsetSeconds / 60);
                    basisLocale.setOffsetMins(offsetMinutes);
                    updateTime();
                }
            }, basisDateTime.getYear(), basisDateTime.getMonthValue() - 1, basisDateTime.getDayOfMonth()).show();
        }
    };

    private View.OnClickListener timeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Calculate current offset time
            ZonedDateTime basisDateTime = basisLocale.getZonedDateTime().plusMinutes(offsetMinutes);

            new TimePickerDialog(getContext(), R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, final int hour, final int minute) {
                    ZonedDateTime selectTime = basisLocale.getZonedDateTime()
                            .withHour(hour).withMinute(minute);
                    int tmpDay = offsetMinutes / (60 * 24);
                    long offsetSeconds = selectTime.toEpochSecond() - basisLocale.getZonedDateTime().toEpochSecond();
                    offsetMinutes = (int)(offsetSeconds / 60) + (tmpDay * (60 * 24));
                    basisLocale.setOffsetMins(offsetMinutes);
                    updateTime();
                }
            }, basisDateTime.getHour(), basisDateTime.getMinute(), true).show();
        }
    };

    /**
     * update time on all displayed views along offsetMinutes.
     */
    private void updateTime() {

        if (basisLocale != null) {
            binding.setCompareLocale(basisLocale);

            // Calculate current offset time
            ZonedDateTime basisDateTime = basisLocale.getZonedDateTime().plusMinutes(offsetMinutes);

            if (offsetMinutes == 0) {
                binding.futureMsgTextView.setText(R.string.now);
            } else {
                int absOffsetMinutes = Math.abs(offsetMinutes);
                int day = absOffsetMinutes / (60 * 24);
                int hour = (absOffsetMinutes - day * (60 * 24)) / 60;
                int minutes = (absOffsetMinutes - day * (60 * 24)) % 60;

                StringBuilder sb = new StringBuilder();
                if (day != 0) {
                    sb.append(day).append(" day");
                    if (day > 1) {
                        sb.append("s");
                    }
                    sb.append(" ");
                }

                sb.append(hour).append(":").append(String.format("%02d", minutes));
                sb.append(" hours in the ");
                sb.append(offsetMinutes > 0 ? "future" : "past");
                binding.futureMsgTextView.setText(sb.toString());
            }

            // Basis Current Date and Time
            binding.basisCurrentTimeTextView.setText(
                    basisDateTime.toLocalDateTime().toString());

            // 30 mins before
            binding.basisPreviousTimeTextView.setText(
                    basisDateTime.minusMinutes(SHIFT_MINS).toLocalTime().toString());

            // 30 mins after
            binding.basisNextTimeTextView.setText(
                    basisDateTime.plusMinutes(SHIFT_MINS).toLocalTime().toString());

        }

        // update list
        binding.recyclerView.getAdapter().notifyDataSetChanged();
    }

    public interface OffsetMinsGetter {
        int getOffsetMins();
    }
}
