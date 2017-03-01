package honkot.gscheduler.fragment;


import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import honkot.gscheduler.CompareLocaleListActivity;
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
                // TODO Change to correct. This is temporary program for debug.
                compareLocaleDao.changeBasis(compareLocale);
                Intent intent = new Intent(getActivity(), CompareLocaleListActivity.class);
//                intent.putExtra(MainActivity.EXTRA_ID, compareLocale.getId());
                startActivity(intent);
            }
        });
        binding.recyclerView.setAdapter(myAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        setUpItemTouchHelper();
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


    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback touchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                MyRecAdapter myAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                myAdapter.remove(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;


                Drawable background = new ColorDrawable(Color.RED);
                Drawable binIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_black_24dp);
                binIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                int iconMargin = (int) getActivity().getResources().getDimension(R.dimen.text_margin);

                // color Background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw Bin Icon
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = binIcon.getIntrinsicWidth();
                int intrinsicHeight = binIcon.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - iconMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                binIcon.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                binIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(touchHelper);
        mItemTouchHelper.attachToRecyclerView(binding.recyclerView);
    }
}
