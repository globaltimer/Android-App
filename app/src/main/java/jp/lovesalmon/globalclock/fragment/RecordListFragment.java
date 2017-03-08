package jp.lovesalmon.globalclock.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import jp.lovesalmon.globalclock.R;
import jp.lovesalmon.globalclock.activity.AddCompareLocaleActivity;
import jp.lovesalmon.globalclock.activity.BaseActivity;
import jp.lovesalmon.globalclock.dao.CompareLocaleDao;
import jp.lovesalmon.globalclock.databinding.FragmentRecordListBinding;
import jp.lovesalmon.globalclock.model.CompareLocale;
import jp.lovesalmon.globalclock.utils.Debug;
import jp.lovesalmon.globalclock.utils.MyRecAdapter;

public class RecordListFragment extends Fragment {

    private static final String TAG = "RecordListFragment";
    protected static final int REQUEST_CODE = 1;
    public static final int RESULT_SUCCESS = 1;
    private FragmentRecordListBinding binding;
    private OnItemClickListener tabListener;
    private boolean editMode = false;
    private MenuItem addMenu;
    private MenuItem editMenu;
    private MenuItem doneMenu;
    private long lastBasisId = 0;
    private long lastRecordCount = 0;

    private int offsetMins = 0;
    private ZonedDateTime startTime = ZonedDateTime.now(ZoneId.systemDefault()).withSecond(0).withNano(0);
    private Handler timeCountHandler;

    public interface OnItemClickListener {
        void onItemClick(CompareLocale compareLocale);
    }

    @Inject
    CompareLocaleDao compareLocaleDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((BaseActivity) getActivity()).getComponent().inject(this);

        binding = FragmentRecordListBinding.inflate(
                getActivity().getLayoutInflater(), null, false);
        initView();
        initHandler();

        CompareLocale compareLocale = compareLocaleDao.getBasisLocale();
        if (compareLocale != null) {
            lastBasisId = compareLocale.getId();
            lastRecordCount = compareLocaleDao.findAll().count();
        }

        return binding.getRoot();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.tabListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateTime();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        long nowSec = now.toEpochSecond();

        timeCountHandler.sendEmptyMessageDelayed(0,
                (now.withSecond(0).withNano(0).plusMinutes(1).toEpochSecond() - nowSec ) * 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeCountHandler.removeMessages(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeCountHandler = null;
    }

    private void initView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyRecAdapter myAdapter = new MyRecAdapter(compareLocaleDao.findAll(), new MyRecAdapter.OnItemClickListener() {

            /**
             * When the row is clicked
             */
            @Override
            public void onItemClicked(CompareLocale compareLocale, int position) {
                if (compareLocale.isBasis() && !editMode) {
                    if (tabListener != null) {
                        tabListener.onItemClick(compareLocale);

                    } else {
                        Log.e(TAG, "onItemClick can not catch event " + compareLocale.toString());
                    }
                } else {
                    compareLocaleDao.changeBasis(compareLocale);
                    MyRecAdapter myAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                    myAdapter.switchBasis(compareLocaleDao.findAll(), compareLocale);
                }
            }

        }, new MyRecAdapter.OnItemClickListener() {

            /**
             * When the delete button in row is clicked
             */
            @Override
            public void onItemClicked(CompareLocale compareLocale, int position) {
                MyRecAdapter myAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                CompareLocale removeLocale = myAdapter.getItemForPosition(position);
                compareLocaleDao.remove(removeLocale);
                myAdapter.remove(compareLocaleDao.findAll(), position);

                showEmptyIfNeeded();
            }
        }, new CompareListFragment.OffsetMinsGetter() {

            /**
             * just callback the time offset
             */
            @Override
            public int getOffsetMins() {
                return offsetMins;
            }

        });
        binding.recyclerView.setAdapter(myAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        setUpItemTouchHelper();
        showEmptyIfNeeded();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_SUCCESS) {
            MyRecAdapter adapter = (MyRecAdapter)binding.recyclerView.getAdapter();
            adapter.setDataAndUpdateList(compareLocaleDao.findAll());
            showEmptyIfNeeded();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        addMenu = menu.findItem(R.id.action_add);
        editMenu = menu.findItem(R.id.action_edit);
        doneMenu = menu.findItem(R.id.action_done);

        updateMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getActivity(), AddCompareLocaleActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;

            case R.id.action_edit:
            case R.id.action_done:
                editMode = !editMode;
                MyRecAdapter adapter = (MyRecAdapter)binding.recyclerView.getAdapter();
                adapter.changeRow(editMode);

                attachItemTouchHelper();
                updateMenu();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMenu() {
        boolean emptyRecord = compareLocaleDao.findAll().isEmpty();

        if (!emptyRecord) {
            addMenu.setVisible(!editMode);
            editMenu.setVisible(!editMode);
            doneMenu.setVisible(editMode);

        } else {
            addMenu.setVisible(true);
            editMenu.setVisible(false);
            doneMenu.setVisible(false);
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
                CompareLocale removeLocale = myAdapter.getItemForPosition(swipedPosition);
                Debug.Log("p:" + swipedPosition + ", id:" + removeLocale.getId() + ", c:" + removeLocale.getDisplayCity());

                compareLocaleDao.remove(removeLocale);
                myAdapter.remove(compareLocaleDao.findAll(), swipedPosition);

                showEmptyIfNeeded();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                int rouge = ContextCompat.getColor(getContext(), R.color.colorRouge);
                Drawable background = new ColorDrawable(rouge);
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
        mItemTouchHelper = new ItemTouchHelper(touchHelper);
        attachItemTouchHelper();
    }

    ItemTouchHelper mItemTouchHelper;

    private void attachItemTouchHelper() {
        mItemTouchHelper.attachToRecyclerView(editMode ? null : binding.recyclerView);
    }

    private void initHandler() {
        HandlerThread handlerThread = new HandlerThread("time_counter");
        handlerThread.start();
        timeCountHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    updateTime();
                    timeCountHandler.sendEmptyMessageDelayed(0, 60 * 1000);
                }
            }
        };
    }

    private void updateTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        offsetMins = (int)((now.toEpochSecond() - startTime.toEpochSecond()) / 60);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyRecAdapter myAdapter = (MyRecAdapter)binding.recyclerView.getAdapter();
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updateViewIfNeeded() {
        if (compareLocaleDao != null) {
            CompareLocale compareLocale = compareLocaleDao.getBasisLocale();
            long tmpBasisId = 0;
            if (compareLocale != null) {
                tmpBasisId = compareLocale.getId();
            }

            int recordCount = compareLocaleDao.findAll().count();
            if (tmpBasisId != lastBasisId
                    || lastRecordCount != recordCount) {
                lastBasisId = tmpBasisId;
                showEmptyIfNeeded();
                MyRecAdapter myRecAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                myRecAdapter.setDataAndUpdateList(compareLocaleDao.findAll());
            }

        }
    }

    private void showEmptyIfNeeded() {
        lastRecordCount = compareLocaleDao.findAll().count();
        boolean emptyRecord = lastRecordCount == 0;

        binding.emptyView.emptyViewRoot.setVisibility(emptyRecord ? View.VISIBLE : View.GONE);
        binding.recyclerView.setVisibility(emptyRecord ? View.GONE : View.VISIBLE);
        if (emptyRecord) {
            binding.emptyView.addFirstLocaleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AddCompareLocaleActivity.class);
                    startActivityForResult(intent, RESULT_SUCCESS);
                }
            });

            editMode = false;
            if (editMenu != null) {
                updateMenu();
            }
        }
    }
}
