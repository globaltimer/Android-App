package jp.lovesalmon.globalclock.utils;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;

import jp.lovesalmon.globalclock.R;
import jp.lovesalmon.globalclock.databinding.ListRowBinding;
import jp.lovesalmon.globalclock.fragment.CompareListFragment;
import jp.lovesalmon.globalclock.model.CompareLocale;
import jp.lovesalmon.globalclock.model.CompareLocale_Selector;

public class MyRecAdapter extends RecyclerView.Adapter<MyRecAdapter.MyViewHolder> {

    private CompareLocale_Selector selector;
    private OnItemClickListener listener;
    private OnItemClickListener deleteButtonListener;
    private CompareListFragment.OffsetMinsGetter offsetMinsGetter;
    private int mSelectorCount;
    private ZonedDateTime startTime;
    private ArrayList<MyViewHolder> mViewHolders = new ArrayList<>();
    private SparseArray<CompareLocale> mDataCash = new SparseArray<>();


    public MyRecAdapter(CompareLocale_Selector selector, OnItemClickListener listener,
                        OnItemClickListener deleteButtonListener, CompareListFragment.OffsetMinsGetter offsetMinsGetter) {
        this.listener = listener;
        this.deleteButtonListener = deleteButtonListener;
        this.offsetMinsGetter = offsetMinsGetter;
        setSelector(selector, true);
        startTime = ZonedDateTime.now(ZoneId.systemDefault()).withSecond(0).withNano(0);
    }

    public MyRecAdapter(CompareLocale_Selector selector, OnItemClickListener listener
            , CompareListFragment.OffsetMinsGetter offsetMinsGetter) {
        this(selector, listener, null, offsetMinsGetter);
        this.offsetMinsGetter = offsetMinsGetter;
    }

    public void setDataAndUpdateList(CompareLocale_Selector selector) {
        setSelector(selector, true);
        notifyDataSetChanged();
    }

    public void switchBasis(CompareLocale_Selector newSelector, CompareLocale newBasis) {
        innerSwitchBasis(newSelector, newBasis, false);
    }

    public void switchBasisWithClearCash(CompareLocale_Selector newSelector, CompareLocale newBasis) {
        innerSwitchBasis(newSelector, newBasis, true);
    }

    private void innerSwitchBasis(CompareLocale_Selector newSelector, CompareLocale newBasis, boolean cashClear) {
        for (MyViewHolder holder : mViewHolders) {
            holder.compareLocale.setBasis(holder.compareLocale.equals(newBasis));
            holder.binding.setCompareLocale(holder.compareLocale);
        }
        // mDataCashはそのまま使いたいので、selectorだけ変更するようにする
        setSelector(newSelector, cashClear);
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
        private int position;

        private MyViewHolder(final ListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.rowClickView.setOnClickListener(this);
            this.binding.editRow.setOnClickListener(this);
            this.binding.deleteBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.editRow:
                case R.id.rowClickView:
                    if (listener != null) {
                        if (position < mSelectorCount) {
                            listener.onItemClicked(
                                    getItemForPosition(getLayoutPosition()), position);
                        }
                    }
                    break;
                case R.id.deleteBtn:
                    if (deleteButtonListener != null) {
                        if (position < mSelectorCount) {
                            deleteButtonListener.onItemClicked(
                                    getItemForPosition(getLayoutPosition()), position);
                        }
                    }
                    break;

            }
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CompareLocale item = getItemForPosition(position);
        holder.compareLocale = item;
        holder.position = position;
        if (offsetMinsGetter != null) {
            item.setOffsetMins(offsetMinsGetter.getOffsetMins());
        }

        holder.binding.setCompareLocale(item);

    }

    public void changeRow(boolean isEdit) {
        for (MyViewHolder holder : mViewHolders) {
            holder.binding.rowClickView.setVisibility(
                    isEdit ? View.GONE : View.VISIBLE);
            holder.binding.editRow.setVisibility(
                    isEdit ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSelectorCount;
    }

    public CompareLocale getItemForPosition(int position) {
        CompareLocale compareLocale = mDataCash.get(position);
        if (compareLocale == null) {
            compareLocale = selector.get(position);
            mDataCash.put(position, compareLocale);
        }
        compareLocale.setZonedDateTime(
                startTime.withZoneSameInstant(compareLocale.getZonedDateTime().getZone()));
        return compareLocale;
    }

    public interface OnItemClickListener {
        void onItemClicked(CompareLocale compareLocale, int position);
    }


    /**
     * ここに入る前に該当レコードはDBから削除されていることを大前提とする。
     * そのままnotifyDataSetChangedをするとアニメーションがされなくなってしまう。
     * それを防ぐために、新しいselectorの差し替えと、キャッシュだけを差し替えるようにする。
     * キャッシュの差し替えとは具体的に、position以降のCompareLocaleのpositionをズラすだけ。
     * @param newSelector
     * @param position
     */
    public void remove(CompareLocale_Selector newSelector, int position) {
        // ViewHolderがもつpositionをズラす
        for (MyViewHolder holder : mViewHolders) {
            if (holder.position > position) {
                holder.position--;
            }
        }

        // 削除animation
        notifyItemRemoved(position);

        // selector更新
        setSelector(newSelector, false);

        // Cash更新
        SparseArray<CompareLocale> newCash = new SparseArray<>();
        for (int i = 0; i < mDataCash.size(); i++) {
            CompareLocale tmpLocale = mDataCash.get(i);
            if (i < position) {
                if (tmpLocale != null)
                    newCash.append(i, tmpLocale);
            } else if (i > position) {
                if (tmpLocale != null)
                    newCash.append(i - 1, tmpLocale);
            }
        }

        mDataCash = newCash;
    }
}
