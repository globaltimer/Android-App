package jp.lovesalmon.globalclock.fragment;


import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.SectionIndexer;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import jp.lovesalmon.globalclock.R;
import jp.lovesalmon.globalclock.activity.BaseActivity;
import jp.lovesalmon.globalclock.dao.CompareLocaleDao;
import jp.lovesalmon.globalclock.dao.TmpTimeZoneDao;
import jp.lovesalmon.globalclock.databinding.FragmentSearchListBinding;
import jp.lovesalmon.globalclock.databinding.ListRowBinding;
import jp.lovesalmon.globalclock.model.CompareLocale;
import jp.lovesalmon.globalclock.model.TmpTimeZone;
import jp.lovesalmon.globalclock.model.TmpTimeZone_Schema;
import jp.lovesalmon.globalclock.model.TmpTimeZone_Selector;
import jp.lovesalmon.globalclock.utils.AdapterGenerater;

/**
 * Created by hiroki on 2017-02-24.
 */
public class SearchListFragment extends Fragment {

    private FragmentSearchListBinding binding;

    @Inject
    TmpTimeZoneDao tmpTimeZoneDao;

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getComponent().inject(this);

        binding = FragmentSearchListBinding.inflate(
                getActivity().getLayoutInflater(), null, false);
        initView();
        return binding.getRoot();
    }

    public void initView() {
        CustomAdapter adapter = new CustomAdapter();
        adapter.initialize();
        binding.listView.setAdapter(adapter);
        binding.listView.setOnItemClickListener(adapter);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CustomAdapter adapter = (CustomAdapter) binding.listView.getAdapter();
                adapter.search(newText);
                return true;
            }
        });

        // remove focus from EditText
        binding.listView.requestFocus();
    }

    private class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, SectionIndexer {

        TmpTimeZone_Selector selector;
        Cursor cursor;
        AlphabetIndexer mAlphabetIndexer;

        private void initialize() {
            // delete all
            tmpTimeZoneDao.deleteAll();

            List<HashMap<String, Object>> maps
                    = AdapterGenerater.getZones(getActivity());
            List<String> registerdCities = compareLocaleDao.registerdCities();
            ArrayList<TmpTimeZone> tmpTimeZones = new ArrayList<>();
            for (HashMap<String, Object> map : maps) {
                String name = (String)map.get("name");
                String gmt = (String)map.get("gmt");
                String id = (String)map.get("id");
                int offset = (int)map.get("offset");

                if (!registerdCities.contains(name)) {
                    tmpTimeZones.add(new TmpTimeZone(name, gmt, id, offset));
                }
            }

            // insert all
            tmpTimeZoneDao.insert(tmpTimeZones);
            setSelector(tmpTimeZoneDao.findAll());

            mAlphabetIndexer = new AlphabetIndexer(
                    cursor,
                    cursor.getColumnIndex(TmpTimeZone_Schema.INSTANCE.name.name),
                    " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        private void setSelector(TmpTimeZone_Selector selector) {
            this.selector = selector;
            cursor = selector.execute();
        }

        private void search(String value) {
            if (value.isEmpty()) {
                initialize();
            } else {
                setSelector(tmpTimeZoneDao.likeQuery(value));
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListRowBinding tmpBinding;
            if (convertView == null) {
                tmpBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.list_row, parent, false);
                tmpBinding.rowClickView.setFocusable(false);
                tmpBinding.rowClickView.setClickable(false);
            } else {
                tmpBinding = DataBindingUtil.getBinding(convertView);
            }

            tmpBinding.setCompareLocale(getItem(position));

            return tmpBinding.getRoot();
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

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            // set locale.
            TmpTimeZone tmpTimeZone = getItem(position);
            CompareLocale locale = CompareLocale.getInstance();
            locale.setLocationName(tmpTimeZone.getName());
            locale.setGmtId(tmpTimeZone.getLocaleId());
            ZonedDateTime newOne = locale.getZonedDateTime().withZoneSameInstant(ZoneId.of(tmpTimeZone.getGmt()));
            locale.setZonedDateTime(newOne);

            compareLocaleDao.insert(locale);

            getActivity().setResult(RecordListFragment.RESULT_SUCCESS);
            getActivity().finish();
        }

        @Override
        public Object[] getSections() {
            return mAlphabetIndexer == null ? new Object[0]
                    : mAlphabetIndexer.getSections();
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return mAlphabetIndexer == null ? 0
                    : mAlphabetIndexer.getPositionForSection(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            return mAlphabetIndexer == null ? 0
                    : mAlphabetIndexer.getSectionForPosition(position);
        }
    }
}
