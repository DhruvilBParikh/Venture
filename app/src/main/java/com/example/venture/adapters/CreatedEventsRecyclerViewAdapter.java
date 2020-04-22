package com.example.venture.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venture.R;
import com.example.venture.models.Event;

import java.util.List;

public class CreatedEventsRecyclerViewAdapter extends RecyclerView.Adapter<CreatedEventsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CreatedEventsRecyclerViewAdapter";

    private List<Event> mEvents;
    private Context mContext;

    public CreatedEventsRecyclerViewAdapter(Context mContext, List<Event> eventList) {
        this.mEvents = eventList;
        this.mContext = mContext;
    }

    public void setmEvents(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

    @NonNull
    @Override
    public CreatedEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        CreatedEventsRecyclerViewAdapter.ViewHolder holder = new CreatedEventsRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(CreatedEventsRecyclerViewAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

//        Log.d(TAG, mEvents.get(position).getImage());

        holder.event_image.setImageResource(R.drawable.sf_trail);

        holder.event_name.setText(mEvents.get(position).getTitle());
        holder.event_location.setText(mEvents.get(position).getLocation());

        holder.event_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mEvents.get(position).getTitle());
                Toast.makeText(mContext, mEvents.get(position).getTitle(), Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public int getItemCount() {
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
