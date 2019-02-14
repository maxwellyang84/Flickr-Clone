package com.mendozae.teamflickr;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.provider.DocumentsContract.isDocumentUri;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCamera .OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyCamera# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCamera extends Fragment implements SurfaceHolder.Callback{
    final int  CAMERA_REQUEST_CODE = 1;
    final int GALLERY_REQUEST_CODE = 2;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    ImageView capture;
    Camera.PictureCallback pictureCallback;

    //Image page
    TextView next;
    ImageView cancel;
    ImageView imageHolder;
    Bitmap rotateBitmap;
    ImageView cameraRoll;

    String[] projection = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };


    private void captureImage() {
        camera.takePicture(null, null, pictureCallback);
    }
    private void deleteImage() {
        surfaceView.setVisibility(View.VISIBLE);
        capture.setVisibility(View.VISIBLE);

        next.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        imageHolder.setVisibility(View.GONE);
    }

    //Camera roll ==================================================================================
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)    {
            switch (requestCode) {
                case 0:
                    Uri galleryImageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), galleryImageUri);
                        String pathName = saveToInternalStorage(bitmap);
                        Intent intent = new Intent (getContext(), ShareImage.class);
                        intent.putExtra("filename", pathName);
                        startActivity(intent);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    //==============================================================================================

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
        } else {
            final Cursor cursor = getContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            // Put it in the image view
            if (cursor.moveToFirst()) {
                String imageLocation = cursor.getString(1);
                File imageFile = new File(imageLocation);
                if (imageFile.exists()) {   // TODO: is there a better way to do this?
                    Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                    cameraRoll.setImageBitmap(bm);
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        surfaceView = view.findViewById(R.id.surfaceView);

        cameraRoll = view.findViewById(R.id.cameraRoll);

        // Find the last picture
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
        } else {
            final Cursor cursor = getContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            // Put it in the image view
            if (cursor.moveToFirst()) {
                String imageLocation = cursor.getString(1);
                File imageFile = new File(imageLocation);
                if (imageFile.exists()) {   // TODO: is there a better way to do this?
                    Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                    cameraRoll.setImageBitmap(bm);
                }
            }
        }

        cameraRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                Intent chooser = Intent.createChooser(intent, "Choose a Picture");
                startActivityForResult(chooser, 0);
            }
        });

        capture = view.findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        //Image display elements
        next = view.findViewById(R.id.nextText);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pathName = saveToInternalStorage(rotateBitmap);
                Intent intent = new Intent(getContext(), ShareImage.class);
                intent.putExtra("filename", pathName);

                startActivity(intent);
                deleteImage();
            }
        });
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });
        imageHolder = view.findViewById(R.id.imgHolder);
        next.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        imageHolder.setVisibility(View.GONE);

        surfaceHolder = surfaceView.getHolder();
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        pictureCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                surfaceView.setVisibility(View.GONE);
                capture.setVisibility(View.GONE);

                next.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                imageHolder.setVisibility(View.VISIBLE);

                if (data != null){
                    // convert byte to bitmap then set image to bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // bitmap is rotated landscape so rotate image.
                    rotateBitmap = rotate(bitmap);

                    imageHolder.setImageBitmap(rotateBitmap);


                }
            }
        };

        return view;
    }



    private String saveToInternalStorage(Bitmap rotateBitmap) {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            rotateBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    private Bitmap rotate(Bitmap bitmap) {
        int width = bitmap.getWidth(), height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        camera.setDisplayOrientation(90);
        params.setPreviewFrameRate(30);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size bestSize = sizeList.get(0);
        for (int i = 1; i < sizeList.size(); i++){
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height))
                bestSize = sizeList.get(i);
        }
        params.setPreviewSize(bestSize.width ,bestSize.height);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    surfaceHolder.addCallback(this);
                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                } else {
                    Toast.makeText(getContext(), "Access to camera needed", Toast.LENGTH_SHORT).show();
                }
            case GALLERY_REQUEST_CODE:
                final Cursor cursor = getContext().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
                // Put it in the image view
                if (cursor.moveToFirst()) {
                    String imageLocation = cursor.getString(1);
                    File imageFile = new File(imageLocation);
                    if (imageFile.exists()) {   // TODO: is there a better way to do this?
                        Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                        cameraRoll.setImageBitmap(bm);
                    }
                }
        }
    }

}
