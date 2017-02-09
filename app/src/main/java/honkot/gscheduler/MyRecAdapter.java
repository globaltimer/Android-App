package honkot.gscheduler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import honkot.gscheduler.model.CompareLocale;

/**
 * Created by ayako_sayama on 2017/02/08.
 */
public class MyRecAdapter extends RecyclerView.Adapter<MyRecAdapter.ViewHolder> {


    public ArrayList<CompareLocale> compareLocale;

    public MyRecAdapter(ArrayList<CompareLocale> compareLocale) {
        this.compareLocale = compareLocale;
    }



//    public MyRecAdapter  r(Context applicationContext, int list_row, ArrayList<CompareLocale> worldTimes) {
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return (ViewHolder) viewHolder;
    }


    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewDate;
        public TextView txtViewTime;
        public TextView txtViewCity;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            txtViewDate = (TextView) itemLayoutView.findViewById(R.id.dateTextView);
            txtViewTime = (TextView) itemLayoutView.findViewById(R.id.timeTextView);
            txtViewCity = (TextView) itemLayoutView.findViewById(R.id.cityTextView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.txtViewDate.setText(compareLocale.get(position).getDisplayCity());
        viewHolder.txtViewTime.setText(compareLocale.get(position).getDisplayTime());
        viewHolder.txtViewDate.setText(compareLocale.get(position).getDisplayDate());

    }

    @Override
    public int getItemCount() {
        return compareLocale.size();
    }



}
