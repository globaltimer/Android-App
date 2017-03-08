package honkot.gscheduler.activity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import honkot.gscheduler.R;
import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.ActivityTabBinding;
import honkot.gscheduler.fragment.CompareListFragment;
import honkot.gscheduler.fragment.RecordListFragment;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.utils.AdapterGenerater;
import honkot.gscheduler.utils.Debug;

public class TabActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener {

    private final static String PREF = "pref";
    private final static String KEY_FIRST_BOOT = "KEY_FIRST_BOOT";
    private ActivityTabBinding binding;
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] pageTitle;
    private final int PAGE_COMPARE = 0;
    private final int PAGE_RECORD_LIST = 1;

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
        if (pref.getBoolean(KEY_FIRST_BOOT, true)) {
            setDefaultLocale();
            pref.edit().putBoolean(KEY_FIRST_BOOT, false).apply();
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Debug.Log(now.toString() + " " + now.getZone().getId());

        // initialize binder and fragments
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tab);
        pageTitle = getResources().getStringArray(R.array.tab_titles);
        RecordListFragment recordListFragment = new RecordListFragment();
        recordListFragment.setOnItemClickListener(new RecordListFragment.OnItemClickListener() {
            @Override
            public void onItemClick(CompareLocale compareLocale) {
                binding.pager.setCurrentItem(PAGE_COMPARE);
            }
        });
        fragments.add(PAGE_COMPARE, new CompareListFragment());
        fragments.add(PAGE_RECORD_LIST, recordListFragment);

        // fragment pager
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitle[position];
            }

            @Override
            public int getCount() {
                return pageTitle.length;
            }
        };

        // initialize view pager
        binding.pager.setAdapter(adapter);
        binding.pager.addOnPageChangeListener(this);
        binding.pager.setCurrentItem(PAGE_RECORD_LIST); // as default page

        // set view pager to tab
        binding.tabs.setupWithViewPager(binding.pager);
        binding.tabs.setSelectedTabIndicatorColor(
                getResources().getColor(R.color.colorPrimary));     // bar color
        binding.tabs.setSelectedTabIndicatorHeight(10);             // bar height
        binding.tabs.setTabTextColors(
                getResources().getColor(R.color.colorGray),         // normal text color
                getResources().getColor(R.color.colorPrimary));     // selected text color

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        FragmentPagerAdapter adapter =
                (FragmentPagerAdapter) binding.pager.getAdapter();

        if (position == PAGE_COMPARE) {
            // initialize compare page
            CompareListFragment fragment =
                    (CompareListFragment) adapter.getItem(PAGE_COMPARE);
            fragment.initialize();

        } else if (position == PAGE_RECORD_LIST) {
            RecordListFragment fragment =
                    (RecordListFragment) adapter.getItem(PAGE_RECORD_LIST);
            fragment.updateViewIfNeeded();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void setDefaultLocale() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        String defaultId = now.getZone().getId();

        ArrayList<String> defaultIds = new ArrayList<>();
        defaultIds.add(defaultId);
        String[] defineIds = new String[] {
                "Asia/Tokyo",
                "Africa/Nairobi",
                "America/Vancouver",
                "Europe/London",
        };
        for (String id : defineIds) {
            if (!id.equals(defaultId)) {
                defaultIds.add(id);
            }
        }

        HashMap<String, CompareLocale> tmpLocale = new HashMap<>();

        List<HashMap<String, Object>> maps
                = AdapterGenerater.getZones(this);
        for (HashMap<String, Object> map : maps) {
            String name = (String)map.get("name");
            String gmt = (String)map.get("gmt");
            String id = (String)map.get("id");

            if (defaultIds.contains(id)) {
                CompareLocale locale = new CompareLocale(this);
                locale.setLocationName(name);
                locale.setGmtId(id);
                ZonedDateTime newOne = locale.getZonedDateTime().withZoneSameInstant(ZoneId.of(gmt));
                locale.setZonedDateTime(newOne);
                locale.setBasis(id.equals(defaultId));

                tmpLocale.put(id, locale);
            }
        }

        CompareLocale firstLocale = tmpLocale.get(defaultId);
        if (firstLocale != null) {
            compareLocaleDao.insert(firstLocale);
        }

        for (String id : defineIds) {
            if (!id.equals(defaultId)) {
                CompareLocale compareLocale = tmpLocale.get(id);
                compareLocaleDao.insert(compareLocale);
            }
        }
    }
}
