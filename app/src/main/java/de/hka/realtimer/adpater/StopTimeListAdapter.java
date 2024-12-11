package de.hka.realtimer.adpater;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.hka.realtimer.R;
import de.hka.realtimer.databinding.StopTimeItemBinding;
import de.hka.realtimer.model.StopTimeWithStop;

public class StopTimeListAdapter extends RecyclerView.Adapter<StopTimeListAdapter.ViewHolder> {

    private List<StopTimeWithStop> stopTimeList;
    private OnItemSelectListener<StopTimeWithStop> onItemSelectListener;
    private int selectedItemPosition;

    public StopTimeListAdapter() {
        this.stopTimeList = new ArrayList<>();
        this.selectedItemPosition = -1;
    }

    public StopTimeListAdapter(List<StopTimeWithStop> stopTimeList) {
        this.stopTimeList = stopTimeList;
    }

    public void setStopTimeList(List<StopTimeWithStop> stopTimeList) {
        this.stopTimeList = stopTimeList;
        this.notifyDataSetChanged();
    }

    public void setOnItemSelectListener(OnItemSelectListener<StopTimeWithStop> onItemClickListener) {
        this.onItemSelectListener = onItemClickListener;
    }
    
    public void selectItem(int position) {
        this.selectedItemPosition = position;

        if (this.onItemSelectListener != null) {
            this.onItemSelectListener.onItemSelect(this.stopTimeList.get(position));
        }

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StopTimeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StopTimeItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.stop_time_item, parent, false);
        return new StopTimeListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StopTimeListAdapter.ViewHolder holder, int position) {
        StopTimeWithStop obj = this.stopTimeList.get(position);
        holder.setStopTime(obj);
    }

    @Override
    public int getItemCount() {
        return this.stopTimeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public StopTimeItemBinding itemBinding;

        public ViewHolder(StopTimeItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void setStopTime(StopTimeWithStop obj) {
            this.itemBinding.setStopTime(obj);
            this.itemBinding.executePendingBindings();

            this.itemBinding.getRoot().setOnClickListener(view -> {
                selectItem(this.getAdapterPosition());
            });

            this.itemBinding.layoutStopTimeItem.setSelected(this.getAdapterPosition() == selectedItemPosition);
        }
    }
}
