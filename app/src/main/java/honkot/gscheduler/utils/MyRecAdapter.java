package honkot.gscheduler.utils;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.fragment.CompareListFragment;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.CompareLocale_Selector;

public class MyRecAdapter extends RecyclerView.Adapter<MyRecAdapter.MyViewHolder> {

    private CompareLocale_Selector selector;
    private OnItemClickListener listener;
    private CompareListFragment.OffsetMinsGetter offsetMinsGetter;
    private int mSelectorCount;
    private ArrayList<MyViewHolder> mViewHolders = new ArrayList<>();
    private SparseArray<CompareLocale> mDataCash = new SparseArray<>();

    public MyRecAdapter(CompareLocale_Selector selector, OnItemClickListener listener) {
        this.listener = listener;
        setSelector(selector, true);
    }

    public MyRecAdapter(CompareLocale_Selector selector, OnItemClickListener listener
            , CompareListFragment.OffsetMinsGetter offsetMinsGetter) {
        this(selector, listener);
        this.offsetMinsGetter = offsetMinsGetter;
    }

    public void setDataAndUpdateList(CompareLocale_Selector selector) {
        setSelector(selector, true);
        notifyDataSetChanged();
    }

    public void switchBasis(CompareLocale_Selector newSelector, CompareLocale newBasis) {
        for (MyViewHolder holder : mViewHolders) {
            holder.compareLocale.setBasis(holder.compareLocale.equals(newBasis));
            holder.binding.setCompareLocale(holder.compareLocale);
        }
        // mDataCashはそのまま使いたいので、selectorだけ変更するようにする
        setSelector(newSelector, false);
    }

    private void setSelector(CompareLocale_Selector selector, boolean needCashClean) {
        this.selector = selector;
        mSelectorCount = selector.count();
        if (needCashClean) {
            mDataCash = new SparseArray<>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListRowBinding itemBinding = ListRowBinding.inflate(layoutInflater, parent, false);
        MyViewHolder holder = new MyViewHolder(itemBinding);
        mViewHolders.add(holder);
        return holder;
    }

    // inner class to hold a reference to each item of RecyclerView
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ListRowBinding binding;
        private CompareLocale compareLocale;

        private MyViewHolder(ListRowBinding binding) {
            super(binding.getRoot());
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
        holder.compareLocale = item;
        if (offsetMinsGetter != null) {
            item.setOffsetMins(offsetMinsGetter.getOffsetMins());
        }

        holder.binding.setCompareLocale(item);
    }

    @Override
    public int getItemCount() {
        return mSelectorCount;
    }

    private CompareLocale getItemForPosition(int position) {
        if (mDataCash.get(position) == null) {
            mDataCash.append(position, selector.get(position));
        }
        return mDataCash.get(position);
    }

    public interface OnItemClickListener {
        void onItemClicked(CompareLocale compareLocale);
    }

    public void remove(int position) {
        notifyItemRemoved(position);
    }
}