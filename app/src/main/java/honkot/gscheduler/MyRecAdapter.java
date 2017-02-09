package honkot.gscheduler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import honkot.gscheduler.databinding.ListRowBinding;
import honkot.gscheduler.model.CompareLocale;

/**
 * Created by ayako_sayama on 2017/02/08.
 */
public class MyRecAdapter extends RecyclerView.Adapter<MyRecAdapter.MyViewHolder> {

    private ArrayList<CompareLocale> mData;
    private OnItemClickListener mListener;

    public MyRecAdapter(ArrayList<CompareLocale> compareLocale, OnItemClickListener listener) {
        mListener = listener;
        mData = compareLocale;
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
            if (mListener != null) {
                mListener.onItemClicked(
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
        return mData.size();
    }

    private CompareLocale getItemForPosition(int position) {
        return mData.get(position);
    }

    public interface OnItemClickListener {
        void onItemClicked(CompareLocale compareLocale);
    }

}
