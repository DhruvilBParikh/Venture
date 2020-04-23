package com.example.venture.adapters;

import android.annotation.SuppressLint;
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

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "HistoryRecyclerViewAdapter";

    private List<Event> mEvents;
    private MainActivity mContext;

    public HistoryRecyclerViewAdapter(Context mContext, List<Event> mEvents) {
        this.mEvents = mEvents;
        this.mContext = (MainActivity)mContext;
    }

    public void setmEvents(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

//        Log.d(TAG, mEvents.get(position).getImage());
        holder.event_image.setImageResource(R.drawable.sf_trail);
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
        return mEvents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView event_image;
        TextView event_name;
        TextView event_location;
        CardView event_layout;

        ViewHolder(@NonNull View itemView) {

            super(itemView);
            event_image = itemView.findViewById(R.id.event_image);
            event_name = itemView.findViewById(R.id.event_name);
            event_location = itemView.findViewById(R.id.event_location);
            event_layout = itemView.findViewById(R.id.cardView);
        }
    }
}