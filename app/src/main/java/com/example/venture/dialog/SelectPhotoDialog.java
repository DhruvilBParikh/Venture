package com.example.venture.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.venture.R;

public class SelectPhotoDialog extends DialogFragment {

    private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 1235;
    private static final int CAMERA_REQUEST_CODE = 4321;

    public interface OnPhotoSelectedListener {
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoSelectedListener mOnPhotoSelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_photo, container, false);
        TextView selectPhoto = view.findViewById(R.id.choosePhoto);
        TextView takePhoto = view.findViewById(R.id.takePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phone memory");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phone camera");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICKFILE_REQUEST_CODE && resultCode== Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image uri: "+ selectedImageUri);
            mOnPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();
        } else if(requestCode==CAMERA_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: done taking new photo");
//            Bitmap bitmap;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mOnPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getTargetFragment();
        } catch(ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException "+ e.getMessage());
        }
        super.onAttach(context);
    }
}
