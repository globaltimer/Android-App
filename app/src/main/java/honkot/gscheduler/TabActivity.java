package honkot.gscheduler;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import honkot.gscheduler.databinding.ActivityTabBinding;
import honkot.gscheduler.fragment.CompareListFragment;
import honkot.gscheduler.fragment.PageFragment;
import honkot.gscheduler.fragment.RecordListFragment;
import honkot.gscheduler.model.CompareLocale;

public class TabActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener, PageFragment.OnFragmentInteractionListener {

    private ActivityTabBinding binding;
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] pageTitle;
    private final int PAGE_COMPARE = 0;
    private final int PAGE_RECORD_LIST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize binder and fragments
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tab);
        pageTitle = getResources().getStringArray(R.array.tab_titles);
        RecordListFragment recordListFragment = new RecordListFragment();
        recordListFragment.setOnItemClickListener(new RecordListFragment.OnItemClickListener() {
            @Override
            public void onItemClick(CompareLocale compareLocale) {
                FragmentPagerAdapter adapter =
                        (FragmentPagerAdapter) binding.pager.getAdapter();
                CompareListFragment fragment =
                        (CompareListFragment) adapter.getItem(PAGE_COMPARE);
                fragment.initialize();
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
                getResources().getColor(R.color.colorGray),      // normal text color
                getResources().getColor(R.color.colorPrimary));     // selected text color

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
