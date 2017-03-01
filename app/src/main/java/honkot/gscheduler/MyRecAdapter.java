package honkot.gscheduler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.model.CompareLocale;
import honkot.gscheduler.model.CompareLocale_Selector;


public class MyRecAdapter extends RecyclerView.Adapter<MyRecAdapter.MyViewHolder> {

    private List<CompareLocale> items;
    private CompareLocale_Selector selector;
    private OnItemClickListener listener;


    public MyRecAdapter(CompareLocale_Selector selector, OnItemClickListener listener) {
        this.listener = listener;
        this.selector = selector;
        items = new ArrayList<>();
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

    public void remove(int position) {

        CompareLocale item = getItemForPosition(position);
        items.remove(item);
        notifyItemRemoved(position);

    }

}