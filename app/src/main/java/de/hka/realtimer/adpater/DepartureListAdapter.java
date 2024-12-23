package de.hka.realtimer.adpater;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.hka.realtimer.R;
import de.hka.realtimer.databinding.DepartureItemBinding;
import de.hka.realtimer.model.Departure;

public class DepartureListAdapter extends RecyclerView.Adapter<DepartureListAdapter.ViewHolder> {

    private List<Departure> departureList;
    private OnItemClickListener<Departure> onItemClickListener;

    public DepartureListAdapter() {
        this.departureList = new ArrayList<>();
    }

    public DepartureListAdapter(List<Departure> departureList) {
        this.departureList = departureList;
    }

    public void setDepartureList(List<Departure> departureList) {
        this.departureList = departureList;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<Departure> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DepartureItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.departure_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Departure obj = this.departureList.get(position);
        holder.setDeparture(obj);
        holder.setOnItemClickListener(this.onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.departureList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public DepartureItemBinding itemBinding;

        public ViewHolder(DepartureItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void setDeparture(Departure obj) {
            this.itemBinding.setDeparture(obj);
            this.itemBinding.executePendingBindings();

            /*switch (obj.getTrip().getRoute().getRouteType()) {
                case TRAM:
                    this.itemBinding.imgRouteType.setImageResource(R.drawable.ic_route_tram);
                    break;

                case SUBWAY:
                    this.itemBinding.imgRouteType.setImageResource(R.drawable.ic_route_subway);
                    break;

                case RAIL:
                    if (obj.getTrip().getRoute().getRouteName().trim().toLowerCase().startsWith("s")) {
                        this.itemBinding.imgRouteType.setImageResource(R.drawable.ic_route_lightrail);
                    } else {
                        this.itemBinding.imgRouteType.setImageResource(R.drawable.ic_route_railway);
                    }
                    break;

                case BUS:
                    this.itemBinding.imgRouteType.setImageResource(R.drawable.ic_route_bus);
                    break;

                case FERRY:
                    this.itemBinding.imgRouteType.setImageResource(R.drawable.ic_route_ferry);
                    break;

                default:

                    break;
            }*/
        }

        public void setOnItemClickListener(final OnItemClickListener<Departure> listener) {
            this.itemBinding.getRoot().setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(this.itemBinding.getDeparture());
                }
            });
        }
    }
}
