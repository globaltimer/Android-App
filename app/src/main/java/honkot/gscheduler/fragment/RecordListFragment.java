package honkot.gscheduler.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import honkot.gscheduler.AddCompareLocaleActivity;
import honkot.gscheduler.BaseActivity;
import honkot.gscheduler.MainActivity;
import honkot.gscheduler.MyRecAdapter;
import honkot.gscheduler.R;
import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.FragmentRecordListBinding;
import honkot.gscheduler.model.CompareLocale;

/**
 * Created by hiroki on 2017-02-24.
 */
public class RecordListFragment extends Fragment {

    private static final String TAG = "RecordListFragment";
    private static final int REQUEST_CODE = 1;
    public static final int RESULT_SUCCESS = 1;
    private FragmentRecordListBinding binding;

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getComponent().inject(this);

        binding = FragmentRecordListBinding.inflate(
                getActivity().getLayoutInflater(), null, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyRecAdapter myAdapter = new MyRecAdapter(compareLocaleDao.findAll(), new MyRecAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(CompareLocale compareLocale) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, compareLocale.getId());
                startActivity(intent);
            }
        });
        binding.recyclerView.setAdapter(myAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_SUCCESS) {
            MyRecAdapter adapter = (MyRecAdapter)binding.recyclerView.getAdapter();
            adapter.setDataAndUpdateList(compareLocaleDao.findAll());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Log.i(TAG, "onOptionsItemSelected: ");
                Intent intent = new Intent(getActivity(), AddCompareLocaleActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;

            case R.id.action_edit:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
