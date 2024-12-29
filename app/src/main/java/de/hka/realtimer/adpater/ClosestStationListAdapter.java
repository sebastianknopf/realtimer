package de.hka.realtimer.adpater;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hka.realtimer.R;
import de.hka.realtimer.databinding.ClosestStationItemBinding;
import de.hka.realtimer.model.StationWithDistance;

public class ClosestStationListAdapter extends RecyclerView.Adapter<ClosestStationListAdapter.ViewHolder>{

    private List<StationWithDistance> closestStationList;
    private OnItemClickListener<StationWithDistance> onItemClickListener;

    public ClosestStationListAdapter() {
        this.closestStationList = new ArrayList<>();
    }

    public ClosestStationListAdapter(List<StationWithDistance> closestStationList) {
        this.closestStationList = closestStationList;
    }

    public void setClosestStationList(List<StationWithDistance> stopTimeList) {
        this.closestStationList = stopTimeList;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<StationWithDistance> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ClosestStationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ClosestStationItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.closest_station_item, parent, false);
        return new ClosestStationListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClosestStationListAdapter.ViewHolder holder, int position) {
        StationWithDistance obj = this.closestStationList.get(position);
        holder.setStopTime(obj);
        holder.setOnItemClickListener(this.onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.closestStationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ClosestStationItemBinding itemBinding;

        public ViewHolder(ClosestStationItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void setStopTime(StationWithDistance obj) {
            this.itemBinding.setStation(obj);
            this.itemBinding.executePendingBindings();
        }

        public void setOnItemClickListener(final OnItemClickListener<StationWithDistance> listener) {
            this.itemBinding.getRoot().setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(this.itemBinding.getStation());
                }
            });
        }
    }
}
