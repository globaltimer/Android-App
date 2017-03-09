package jp.lovesalmon.globalclock.activity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import jp.lovesalmon.globalclock.R;
import jp.lovesalmon.globalclock.dao.CompareLocaleDao;
import jp.lovesalmon.globalclock.databinding.ActivityTabBinding;
import jp.lovesalmon.globalclock.fragment.CompareListFragment;
import jp.lovesalmon.globalclock.fragment.RecordListFragment;
import jp.lovesalmon.globalclock.model.CompareLocale;
import jp.lovesalmon.globalclock.utils.AdapterGenerater;
import jp.lovesalmon.globalclock.utils.Debug;

public class TabActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener {

    private final static String PREF = "pref";
    private final static String KEY_FIRST_BOOT = "KEY_FIRST_BOOT";
    private ActivityTabBinding binding;
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] pageTitle;
    private final int PAGE_COMPARE = 0;
    private final int PAGE_RECORD_LIST = 1;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

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
        setSupportActionBar(binding.appBarMain.toolbar);
        pageTitle = getResources().getStringArray(R.array.tab_titles);
        RecordListFragment recordListFragment = new RecordListFragment();
        recordListFragment.setOnItemClickListener(new RecordListFragment.OnItemClickListener() {
            @Override
            public void onItemClick(CompareLocale compareLocale) {
                binding.appBarMain.activityMain.pager.setCurrentItem(PAGE_COMPARE);
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
        binding.appBarMain.activityMain.pager.setAdapter(adapter);
        binding.appBarMain.activityMain.pager.addOnPageChangeListener(this);
        binding.appBarMain.activityMain.pager.setCurrentItem(PAGE_RECORD_LIST); // as default page

        // set view pager to tab
        binding.appBarMain.activityMain.tabs.setupWithViewPager(
                binding.appBarMain.activityMain.pager);
        binding.appBarMain.activityMain.tabs.setSelectedTabIndicatorColor(
                getResources().getColor(R.color.colorPrimary));     // bar color
        binding.appBarMain.activityMain.tabs.setSelectedTabIndicatorHeight(10);             // bar height
        binding.appBarMain.activityMain.tabs.setTabTextColors(
                getResources().getColor(R.color.colorGray),         // normal text color
                getResources().getColor(R.color.colorPrimary));     // selected text color



        // initialize drawer menu
        // ホームアイコン横のHomeAsUpアイコンを有効に。HomeAsUpアイコンは後述のドロワートグルで上書き。
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //第三引数でHomeAsUpアイコンを指定。
        //第四・第五引数は、String.xmlで適当な文字列を。
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.appBarMain.toolbar,
                R.string.drawer_menu_open,
                R.string.drawer_menu_close) {

            //閉じた時に呼ばれる
            @Override
            public void onDrawerClosed(View drawerView) {
            }

            //開いた時に呼ばれる
            @Override
            public void onDrawerOpened(View drawerView) {
            }

            //アニメーションの処理。Overrideする場合はスーパークラスの同メソッドを呼ぶ。
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            //状態が変化した時に呼ばれる。
            // 表示/閉じ済み -> 0
            // ドラッグ中 -> 1
            // ドラッグを開放た後のアニメーション中 ->2
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        };
        binding.drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        FragmentPagerAdapter adapter =
                (FragmentPagerAdapter) binding.appBarMain.activityMain.pager.getAdapter();

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
