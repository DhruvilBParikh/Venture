package com.example.venture.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class JoinedEventsRecyclerViewAdapter extends RecyclerView.Adapter<JoinedEventsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "JoinedEventsRecyclerViewAdapter";

    private List<Event> mEvents;
    private Context mContext;

    public JoinedEventsRecyclerViewAdapter(Context mContext, List<Event> eventList) {
        this.mEvents = eventList;
        this.mContext = mContext;
    }

    public void setmEvents(List<Event> mEvents) {
            iterateThroughEvents(mEvents, 0);
    }

    public void iterateThroughEvents(final List<Event> eventList, final int position) {
        final String TAG = "setFirebaseImages";
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final long ONE_MEGABYTE = 1024 * 1024;

        if (position >= eventList.size()) {

            this.mEvents = eventList;
            this.notifyDataSetChanged();
            return;
        }


        StorageReference imagesRef = mStorageRef.child(eventList.get(position).getImage());

        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                eventList.get(position).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Log.d(TAG, "success-------------" + eventList.get(position).getImageBitmap());
                iterateThroughEvents(eventList, position + 1);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    @NonNull
    @Override
    public JoinedEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        JoinedEventsRecyclerViewAdapter.ViewHolder holder = new JoinedEventsRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(JoinedEventsRecyclerViewAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if(mEvents.get(position).getImageBitmap()==null) {
            Log.d(TAG, "-----Its Null");
            holder.event_image.setImageResource(R.drawable.default_image);
        }
        else {
            Log.d(TAG, mEvents.get(position).getImageBitmap().toString());
            holder.event_image.setImageBitmap(mEvents.get(position).getImageBitmap());
        }
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
