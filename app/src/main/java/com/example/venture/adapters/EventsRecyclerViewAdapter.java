package com.example.venture_v0.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.venture_v0.Fragments.explore.ExploreEventListFragment;
import com.example.venture_v0.R;

import java.util.ArrayList;


public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mEventName = new ArrayList<>();
    private ArrayList<String> mEventLocation = new ArrayList<>();
    private Context mContext;


    public EventsRecyclerViewAdapter(Context mContext, ArrayList<String> mImage,ArrayList<String> mEventName, ArrayList<String> mEventLocation) {
        this.mImage = mImage;
        this.mEventName = mEventName;
        this.mEventLocation = mEventLocation;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Log.d(TAG, mImage.get(position));

//        int id = mContext.getResources().getIdentifier("com.example.venture_v0:drawable/" + mImage.get(position), null, null);
//        holder.event_image.setImageResource(id);
        holder.event_image.setImageResource(R.drawable.sf_trail);

        holder.event_name.setText(mEventName.get(position));
        holder.event_location.setText(mEventLocation.get(position));

        holder.event_layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mEventName.get(position));
                Toast.makeText(mContext, mEventName.get(position), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mEventName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView event_image;
        TextView event_name;
        TextView event_location;
        CardView event_layout;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            event_image = itemView.findViewById(R.id.event_image);
            event_name = itemView.findViewById(R.id.event_name);
            event_location  = itemView.findViewById(R.id.event_location);
            event_layout = itemView.findViewById(R.id.cardView);
        }
    }
}
