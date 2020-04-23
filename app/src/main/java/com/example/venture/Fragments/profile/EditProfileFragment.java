package com.example.venture.Fragments.profile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.dialog.SelectPhotoDialog;
import com.example.venture.models.User;
import com.example.venture.viewmodels.explore.UsersViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class EditProfileFragment extends Fragment implements SelectPhotoDialog.OnPhotoSelectedListener {

    private static final String TAG = "EditProfileFragment";
    private static final int defaultImage = R.drawable.profile_pic;

    private ImageView profileImage;
    private EditText userName;
    private EditText bioDescription;
    private MaterialButton uploadImage;
    private MaterialButton saveChanges;
    private UsersViewModel usersViewModel;
    private SharedPreferences mPreferences;

    private static final int REQUEST_CODE=1;
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes;
    private double mProgress=0;

    private String name;
    private String bio;
    private String userId;
    private String email;
    private String profilePic;
    private Uri downloadUri;


    public EditProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileImage = view.findViewById(R.id.editProfileImage);
        userName = view.findViewById(R.id.editUserName);
        bioDescription = view.findViewById(R.id.editBioDescription);
        uploadImage = view.findViewById(R.id.uploadProfileImageButton);
        saveChanges = view.findViewById(R.id.editSaveButton);

        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        userId = mPreferences.getString("userId", "");
        name = mPreferences.getString("name", "" );
        bio = mPreferences.getString("bio", "");
        email = mPreferences.getString("email", "");
        profilePic = mPreferences.getString("profilePic", "");
        userName.setText(name);
        bioDescription.setText(bio);

        //Setting Image
        Glide.with(this)
                .load(profilePic) // image url
                .placeholder(defaultImage) // any placeholder to load at start
                .error(defaultImage)  // any image in case of error
                .override(100, 100) // resizing
                .centerCrop()
                .into(profileImage);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes");
                if(!TextUtils.isEmpty(userName.getText())){
                    if(mSelectedBitmap!=null && mSelectedUri==null){
                        uploadNewPhoto(mSelectedBitmap);
                    } else if(mSelectedBitmap==null && mSelectedUri!=null){
                        uploadNewPhoto(mSelectedUri);
                    } else {
                        setupData();
                    }
                } else {
                    Toast.makeText(getActivity(), "You must fill Name", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void uploadNewPhoto(Bitmap bitmap) {
        Log.d(TAG, "uploadNewPhoto: uploading a new image bitmap in storage");
        BackgroundImageResize resize = new BackgroundImageResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath) {
        Log.d(TAG, "uploadNewPhoto: uploading a new image uri in storage");
        BackgroundImageResize resize = new BackgroundImageResize(null);
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

    private void executeUploadtask() {
        Log.d(TAG, "executeUploadtask: Executing upload task");
        Toast.makeText(getActivity(), "uploading an image", Toast.LENGTH_SHORT).show();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("users/"+userId+"/profile_pic");

        Log.d(TAG, "executeUploadtask: upload bytes"+ Arrays.toString(mUploadBytes));
        final UploadTask uploadTask = storageReference.putBytes(mUploadBytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: upload task success");
                Toast.makeText(getActivity(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
//                            Uri profilePic = task.getResult();
                            downloadUri = task.getResult();
                            profilePic = downloadUri.toString();
                            setupData();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

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

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        Log.d(TAG, "getBytesFromBitmap: getting bytes from bitmap to compress");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }

    private void setupData(){
        User user = new User();
        user.setEmail(email);
        user.setName(userName.getText().toString());
        user.setBio(bioDescription.getText().toString());
        user.setProfilePic(profilePic.toString());
        user.setId(userId);
        usersViewModel.editUser(user);
        ((MainActivity) getActivity()).setPreferences(user);
        ((MainActivity) getActivity()).openFragment("PROFILE");
    }

    private void openDialog(){
        SelectPhotoDialog dialog = new SelectPhotoDialog();
        dialog.show(getActivity().getSupportFragmentManager(),getString(R.string.dialog_select_photo));
        dialog.setTargetFragment(EditProfileFragment.this, 1);
    }

    private void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[0])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[1])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[2])== PackageManager.PERMISSION_GRANTED) {
            openDialog();
        } else {
            requestPermissions(permissions,REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to image button");
        Log.d(TAG, "getImagePath: image path: "+ imagePath.toString());
        Glide.with(this)
                .load(imagePath.toString()) // image url
                .placeholder(defaultImage) // any placeholder to load at start
                .error(defaultImage)  // any image in case of error
                .override(100, 100) // resizing
                .centerCrop()
                .into(profileImage);
//        UniversalImageLoader.setImage(imagePath.toString(), profileImage, (MainActivity)getActivity().this);
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to image button");
        profileImage.setImageBitmap(bitmap);
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }

}
