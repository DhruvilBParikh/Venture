package com.example.venture.Fragments.addevent;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.dialog.SelectPhotoDialog;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreEventListFragmentViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment implements SelectPhotoDialog.OnPhotoSelectedListener{

    private static final String TAG = "AddEventFragment";

    private static final int defaultImage = R.drawable.sf_trail;

    private static final int REQUEST_CODE = 1;

    private TextInputEditText mTitle;
    private TextInputEditText mDetails;
    private TextInputEditText mDate;
    private TextInputEditText mTime;
    private ImageView mEventPhotoDisplay;
    private TextView mAddPhoto;
    private MaterialButton mAddEvent;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Calendar calendar = Calendar.getInstance();
    private AutocompleteSupportFragment autocompleteFragment;
    private int mMonth = calendar.get(Calendar.MONTH);
    private int mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    private int mYear = calendar.get(Calendar.YEAR);
    private int mHourOfDay = calendar.get(calendar.HOUR_OF_DAY);
    private int mMinute = calendar.get(calendar.MINUTE);
    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

    private Place mPlace;
    private String location="";
    private LatLng latLng;
    private String city;
    private String state;

    private SharedPreferences mPreferences;
    private String userId;
    private String organizer;

    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes;
    private double mProgress=0;
    private String mEventUrl="";
    private String eventId;

    //viewmodels
    private ExploreEventListFragmentViewModel mEventViewModel;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        mTitle = view.findViewById(R.id.editTitle);
        mDetails = view.findViewById(R.id.editDescription);
        mDate = view.findViewById(R.id.editDate);
        mTime = view.findViewById(R.id.editTime);
        mEventPhotoDisplay = view.findViewById(R.id.eventPhotoDisplay);
        mAddPhoto = view.findViewById(R.id.addEventPhotoButton);
        mAddEvent = view.findViewById(R.id.buttonAddEvent);
        calendar = Calendar.getInstance();

        String apiKey = "AIzaSyBAA7PGAMSIVpwIJ478qNdiE8VzjL39hOs";
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint(getString(R.string.add_meeting_point));

        setDate();
        setTime();
        autoSearch();

        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        userId = mPreferences.getString("userId", "");
        organizer = mPreferences.getString("name", "");

        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions();
            }
        });

        mEventViewModel = new ViewModelProvider(this).get(ExploreEventListFragmentViewModel.class);

        addEvent();

        return view;
    }

    private void uploadNewPhoto(Bitmap bitmap) {
        Log.d(TAG, "uploadNewPhoto: uploading a new image bitmap in storage");
        AddEventFragment.BackgroundImageResize resize = new AddEventFragment.BackgroundImageResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath) {
        Log.d(TAG, "uploadNewPhoto: uploading a new image uri in storage");
        AddEventFragment.BackgroundImageResize resize = new AddEventFragment.BackgroundImageResize(null);
        resize.execute(imagePath);
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap){
            if (bitmap!=null){
                mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");

            if(mBitmap==null){
                try{
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uris[0]);
                } catch(IOException e){
                    Log.e(TAG, "doInBackground: "+ e.getMessage());
                }
            }
            byte[] bytes = null;
            Log.d(TAG, "doInBackground: megabytes before compression: "+mBitmap.getByteCount()/1000000);
            bytes = getBytesFromBitmap(mBitmap, 100);
            Log.d(TAG, "doInBackground: megabytes after compression: "+bytes.length/1000000);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes;
            Log.d(TAG, "onPostExecute: Before Executing Upload Task");
            executeUploadtask();
            Log.d(TAG, "onPostExecute: After Executing Upload Task");
        }
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        Log.d(TAG, "getBytesFromBitmap: getting bytes from bitmap to compress");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }

    private void executeUploadtask() {
        Log.d(TAG, "executeUploadtask: Executing upload task");
        Toast.makeText(getActivity(), "uploading an image", Toast.LENGTH_SHORT).show();
        eventId = FirebaseDatabase.getInstance().getReference().push().getKey();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("events/"+eventId+"/"+mTitle.getText().toString());

        Log.d(TAG, "executeUploadtask: upload bytes"+ Arrays.toString(mUploadBytes));
        final UploadTask uploadTask = storageReference.putBytes(mUploadBytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: upload task success");
                Toast.makeText(getActivity(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                mEventUrl = "events/"+eventId+"/"+mTitle.getText().toString();
                setupData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: upload task failure");
                Toast.makeText(getActivity(), "couldn't upload an image", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onProgress: Upload in progress");
                Log.d(TAG, "onProgress: total byte count "+ taskSnapshot.getTotalByteCount());
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if(currentProgress > (mProgress+15)){
                    mProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Toast.makeText(getActivity(), mProgress +"%", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_CALENDAR,Manifest.permission.READ_CALENDAR};
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[0])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[1])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[2])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[3])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[4])== PackageManager.PERMISSION_GRANTED) {
            openDialog();
        } else {
            requestPermissions(permissions,REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private void openDialog(){
        SelectPhotoDialog dialog = new SelectPhotoDialog();
        dialog.show(getActivity().getSupportFragmentManager(),getString(R.string.dialog_select_photo));
        dialog.setTargetFragment(AddEventFragment.this, 1);
    }

    private void autoSearch() {
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "onPlaceSelected: " + place.toString());
                if (!place.getName().isEmpty()) {
                    latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    location = place.getName();
                    Log.d(TAG, "onPlaceSelected: "+latLng);
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addresses.size()>0) {
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        Log.d(TAG, "onPlaceSelected: Geocoder "+addresses.get(0));
                        Log.d(TAG, "onPlaceSelected: Geocoder "+addresses.get(0).getLocality());
                    }
                    Log.d(TAG, "onPlaceSelected: "+place.getName());
                }
            }
            @Override
            public void onError(Status status) {
                Log.d(TAG, "onError: error occurred, status: " + status);
            }
        });

    }

    public void setDate() {
        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int year = mYear;
                int month = mMonth;
                int day = mDayOfMonth;
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );
                dialog.getWindow();
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mMonth = month;
                month = month + 1;
                mDayOfMonth = dayOfMonth;
                mYear = year;
                String date = String.format("%02d", month)+"/"+String.format("%02d", dayOfMonth)+"/"+year;
                mDate.setText(date);
            }
        };
    }

    public void setTime() {
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hours = mHourOfDay;
                int minutes = mMinute;
                Log.d(TAG, "onClick: hours:"+hours);
                Log.d(TAG, "onClick: minutes:"+minutes);
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mTimeSetListener,
                        hours, minutes, false
                );
                timePickerDialog.getWindow();
                timePickerDialog.show();
            }
        });
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                try {
                    mHourOfDay = hourOfDay;
                    mMinute = minute;
                    String _24HourTime = hourOfDay + ":" + minute;
                    Date _24HourDt = _24HourSDF.parse(_24HourTime);
                    Log.d(TAG, "onTimeSet: " + _24HourTime);
                    assert _24HourDt != null;
                    Log.d(TAG, "onTimeSet: " + _12HourSDF.format(_24HourDt));
                    String time = _12HourSDF.format(_24HourDt);
                    mTime.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void addEvent(){
        mAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Latlng"+latLng);
                Log.d(TAG, "onClick: Place Name:"+location);
                String title = mTitle.getText().toString();
                String details = mDetails.getText().toString();
                String date = mDate.getText().toString();
                String time = mTime.getText().toString();
                Log.d(TAG, "onClick: title:::::::::"+title);
                Log.d(TAG, "onClick: details:::::::"+details);
                Log.d(TAG, "onClick: date::::::::::"+date);
                Log.d(TAG, "onClick: time::::::::::"+time);
                Log.d(TAG, "onClick: location::::::"+location);
                if(!title.equals("") && !details.equals("") && (mSelectedBitmap==null || mSelectedUri==null) && !date.equals("") && !time.equals("") && !location.equals("")){
                    if(mSelectedBitmap!=null && mSelectedUri==null){
                        Log.d(TAG, "onClick: bitmap");
                        uploadNewPhoto(mSelectedBitmap);
                    } else if(mSelectedBitmap==null && mSelectedUri!=null) {
                        Log.d(TAG, "onClick: uri");
                        uploadNewPhoto(mSelectedUri);
                    }
                } else {
                    Log.d(TAG, "onClick: fill all the fields");
                    Toast.makeText(getActivity(),"Fill all the fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupData(){

        Event event = new Event();
        event.setTitle(mTitle.getText().toString());
        event.setDetails(mDetails.getText().toString());
        event.setImage(mEventUrl);
        event.setLocation(location);
        event.setLatitude(latLng.latitude);
        event.setLongitude(latLng.longitude);
        event.setDate(mDate.getText().toString());
        event.setTime(mTime.getText().toString());
        event.setOrganizerId(userId);
        event.setCity(city);
        event.setState(state);
        event.setOrganizer(organizer);
        Log.d(TAG, "setupData: city:"+city);
        Log.d(TAG, "setupData: state:"+state);

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues cv = new ContentValues();

        Calendar start = Calendar.getInstance();
        start.set(mYear,mMonth, mDayOfMonth,mHourOfDay,mMinute);
        Calendar end = Calendar.getInstance();
        end.set(mYear,mMonth, mDayOfMonth,mHourOfDay+1,mMinute);

        cv.put(CalendarContract.Events.CALENDAR_ID,3);
        cv.put(CalendarContract.Events.TITLE, mTitle.getText().toString());
        cv.put(CalendarContract.Events.DESCRIPTION, mDetails.getText().toString());
        cv.put(CalendarContract.Events.EVENT_LOCATION, location);
        cv.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);

        ContentValues cv1 = new ContentValues();
        cv1.put(CalendarContract.Reminders.EVENT_ID,eventUri.getLastPathSegment());
        cv1.put(CalendarContract.Reminders.MINUTES,60);

        cr.insert(CalendarContract.Reminders.CONTENT_URI, cv1);
        Log.d(TAG, "onClick: uri "+ eventUri);


        mEventViewModel.addEvent(event, userId, eventId, eventUri.toString());
        ((MainActivity)getActivity()).openFragment("EXPLORE");
    }

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to image button");
        Log.d(TAG, "getImagePath: image path: "+ imagePath.toString());
        mEventPhotoDisplay.setVisibility(View.VISIBLE);
        mAddPhoto.setText(R.string.change_photo);
        Glide.with(this)
                .load(imagePath.toString()) // image url
                .placeholder(defaultImage) // any placeholder to load at start
                .error(defaultImage)  // any image in case of error
                .override(413, 200) // resizing
                .centerCrop()
                .into(mEventPhotoDisplay);
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to image button");
        mEventPhotoDisplay.setVisibility(View.VISIBLE);
        mAddPhoto.setText(R.string.change_photo);
        mEventPhotoDisplay.setImageBitmap(bitmap);
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }

}
