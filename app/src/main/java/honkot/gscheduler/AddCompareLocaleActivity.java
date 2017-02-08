package honkot.gscheduler;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import honkot.gscheduler.dao.TmpTimeZoneDao;
import honkot.gscheduler.databinding.ActivityAddCompareLocaleBinding;
import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.model.TmpTimeZone;
import honkot.gscheduler.model.TmpTimeZone_Selector;
import honkot.gscheduler.utils.AdapterGenerater;

public class AddCompareLocaleActivity extends BaseActivity {

    ActivityAddCompareLocaleBinding mBinding;
    CustomAdapter mAdapter;

    @Inject
    TmpTimeZoneDao tmpTimeZoneDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_compare_locale);
        mAdapter = new CustomAdapter();
        mAdapter.initialize();
        mBinding.listView.setAdapter(mAdapter);
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.search(newText);
                return true;
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {

        TmpTimeZone_Selector selector;

        private void initialize() {
            // delete all
            tmpTimeZoneDao.deleteAll();

            List<HashMap<String, Object>> maps
                    = AdapterGenerater.getZones(getApplicationContext());
            ArrayList<TmpTimeZone> tmpTimeZones = new ArrayList<>();
            for (HashMap<String, Object> map : maps) {
                String name = (String)map.get("name");
                String gmt = (String)map.get("gmt");
                String id = (String)map.get("id");
                int offset = (int)map.get("offset");

                tmpTimeZones.add(new TmpTimeZone(name, gmt, id, offset));
            }

            // insert all
            tmpTimeZoneDao.insert(tmpTimeZones);
            selector = tmpTimeZoneDao.findAll();
        }

        private void search(String value) {
            if (value.isEmpty()) {
                initialize();
            } else {
                selector = tmpTimeZoneDao.likeQuery(value);
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListRowBinding binding;
            if (convertView == null) {
                binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.list_row, parent, false);
            } else {
                binding = DataBindingUtil.getBinding(convertView);
            }

            binding.setCompareLocale(getItem(position));

            return binding.getRoot();
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public TmpTimeZone getItem(int position) {
            return selector.get(position);
        }

        @Override
        public int getCount() {
            return selector.count();
        }
    }
}
