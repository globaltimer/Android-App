package honkot.gscheduler;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.CompareLocale_Selector;

/**
 * Created by ayako_sayama on 2017/02/08.
 */
public class MyRecAdapter extends RecyclerView.Adapter<MyRecAdapter.MyViewHolder> {


    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    boolean undoOn;
    List<String> items;
    List<String> itemsPendingRemoval;
    private CompareLocale_Selector selector;
    private OnItemClickListener listener;
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    public MyRecAdapter(CompareLocale_Selector selector, OnItemClickListener listener) {
        this.listener = listener;
        this.selector = selector;
    }

    public void setDataAndUpdateList(CompareLocale_Selector selector) {
        this.selector = selector;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListRowBinding itemBinding = ListRowBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(itemBinding);
    }

    // inner class to hold a reference to each item of RecyclerView
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ListRowBinding binding;

        private MyViewHolder(ListRowBinding binding) {
            super(binding.rowRoot);
            this.binding = binding;
            this.binding.rowClickView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClicked(
                        getItemForPosition(getLayoutPosition()));
            }
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CompareLocale item = getItemForPosition(position);
        holder.binding.setCompareLocale(item);
    }

    @Override
    public int getItemCount() {
        return selector.count();
    }

    private CompareLocale getItemForPosition(int position) {
        return selector.get(position);
    }

    public interface OnItemClickListener {
        void onItemClicked(CompareLocale compareLocale);
    }


    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final String item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        String item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        String item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }


}
