package com.example.venture.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.Event;

import java.util.List;


public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<Event> mEvents;
    private MainActivity mContext;

    public EventsRecyclerViewAdapter(Context mContext, List<Event> eventList) {
        this.mEvents = eventList;
        this.mContext = (MainActivity)mContext;
    }

    public void setmEvents(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Log.d(TAG, "-------image in recycler view-------"+mEvents.get(position).getTitle());

        if(mEvents.get(position).getImageBitmap()==null) {
            Log.d(TAG, "-----Its Null");
            holder.event_image.setImageResource(R.drawable.default_image);
        }
        else {
            Log.d(TAG, mEvents.get(position).getImageBitmap().toString());
            Log.d(TAG, mEvents.get(position).getImage());
            holder.event_image.setImageBitmap(mEvents.get(position).getImageBitmap());
        }

        holder.event_name.setText(mEvents.get(position).getTitle());
        holder.event_location.setText(mEvents.get(position).getLocation());

        holder.event_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mEvents.get(position).getTitle());
                mContext.openEventFragment(mEvents.get(position).getId(), "");
            }
        });

    }


    @Override
    public int getItemCount() {
        if(mEvents==null)
            return 0;
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView event_image;
        TextView event_name;
        TextView event_location;
        CardView event_layout;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            event_image = itemView.findViewById(R.id.event_image);
            event_name = itemView.findViewById(R.id.event_name);
            event_location = itemView.findViewById(R.id.event_location);
            event_layout = itemView.findViewById(R.id.cardView);
        }
    }
}
