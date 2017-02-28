package honkot.gscheduler;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import honkot.gscheduler.dao.CompareLocaleDao;
import honkot.gscheduler.databinding.ActivityListBinding;
import honkot.gscheduler.model.CompareLocale;

public class ListActivity extends BaseActivity {

    private static final String TAG = "LIST_ACTIVITY";
    private static final int REQUEST_CODE = 1;
    public static final int RESULT_SUCCESS = 1;
    private ActivityListBinding binding;
    MyRecAdapter myAdapter;

    @Inject
    public CompareLocaleDao compareLocaleDao;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_SUCCESS) {
            MyRecAdapter adapter = (MyRecAdapter) binding.recyclerView.getAdapter();
            adapter.setDataAndUpdateList(compareLocaleDao.findAll());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyRecAdapter(compareLocaleDao.findAll(), new MyRecAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(CompareLocale compareLocale) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, compareLocale.getId());
                startActivity(intent);
            }
        });
        binding.recyclerView.setAdapter(myAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        setUpItemTouchHelper();

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
                myAdapter = (MyRecAdapter) binding.recyclerView.getAdapter();
                myAdapter.remove(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;


                Drawable background = new ColorDrawable(Color.RED);
                Drawable binIcon = ContextCompat.getDrawable(ListActivity.this, R.drawable.ic_delete_black_24dp);
                binIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                int iconMargin = (int) ListActivity.this.getResources().getDimension(R.dimen.text_margin);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Log.i(TAG, "onOptionsItemSelected: ");
                Intent intent = new Intent(ListActivity.this, AddCompareLocaleActivity.class);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("List Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}