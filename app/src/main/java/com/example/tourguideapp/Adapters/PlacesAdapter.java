package com.example.tourguideapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguideapp.Activities.ItemInfo;
import com.example.tourguideapp.Models.VenueModel;
import com.example.tourguideapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.viewHolder> {

    private final ArrayList<VenueModel> arrayList;
    Context context;
    public PlacesAdapter(ArrayList<VenueModel> arrayList) { this.arrayList = arrayList; }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_places, parent, false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        VenueModel model = arrayList.get(position);
        holder.desc.setText(model.getDescription());
        holder.name.setText(model.getVenueName());
        String dist = model.getDistance() + context.getString(R.string.dist_unit);
        holder.distance.setText(dist);
        holder.cardView.setOnClickListener(v ->{
           final Bundle bundle = new Bundle();
            bundle.putBinder("object_value", new ObjectWrapperForBinder(model));
            v.getContext().startActivity(new Intent(v.getContext(), ItemInfo.class).putExtras(bundle));
        });
        //Setting image
        Picasso.get().load(model.getVenueIcon()).into(holder.image);
    }
    @Override
    public int getItemCount() { return arrayList.size(); }
    public static class viewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, desc, distance;
        CardView cardView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.place_desc);
            image = itemView.findViewById(R.id.place_view);
            name = itemView.findViewById(R.id.place_name);
            distance = itemView.findViewById(R.id.place_ratingBar);
            cardView = itemView.findViewById(R.id.place_card);
        }
    }

    public static class ObjectWrapperForBinder extends Binder {
        private final VenueModel mData;
        public ObjectWrapperForBinder(VenueModel data) {
            mData = data;
        }
        public VenueModel getData() {
            return mData;
        }
    }
}
