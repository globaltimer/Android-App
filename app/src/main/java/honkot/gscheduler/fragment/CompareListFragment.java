package honkot.gscheduler.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import honkot.gscheduler.BaseActivity;
import honkot.gscheduler.MainActivity;
import honkot.gscheduler.MyRecAdapter;
import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.FragmentCompareListBinding;
import honkot.gscheduler.model.CompareLocale;

/**
 * Created by hiroki on 2017-02-25.
 */
public class CompareListFragment extends Fragment {

    private static final String TAG = "CompareListFragment";
    private FragmentCompareListBinding binding;

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getComponent().inject(this);

        binding = FragmentCompareListBinding.inflate(
                getActivity().getLayoutInflater(), null, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    private void initView() {
        CompareLocale basisLocale = compareLocaleDao.getBasisLocale();
        binding.basisCurrentTimeTextView.setText(basisLocale.getDisplayDate() + " " + basisLocale.getDisplayTime());

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
}
