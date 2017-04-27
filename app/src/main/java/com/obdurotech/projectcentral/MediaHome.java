package com.obdurotech.projectcentral;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MediaHome extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    boolean mediaChanged = false;

    ViewPager vpPager;
    Button btnNext;
    Button btnPrev;
    FragmentPagerAdapter adapterViewPager;
    private Uri filePath;

    private String mediaName;
    private Button buttonUpload;
    private StorageReference storageReference;

    Uri mCameraUri;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String projectName;
    List<Medium> mediaList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        projectName = intent.getStringExtra("project_name");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mediaList = new ArrayList<>();

        setContentView(R.layout.activity_media_home);
        btnNext = (Button) findViewById(R.id.nextBtn);
        btnPrev = (Button) findViewById(R.id.prevBtn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.media_toolbar);
        setSupportActionBar(toolbar);

        vpPager = (ViewPager) findViewById(R.id.view_Pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new CubeOutTransformer());

        mRef = FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).
                child("projects").child(projectName).child("media").getRef();

        mRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            public static final String TAG = "";

            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                mediaList.clear();
                int i = 0;

                for (com.google.firebase.database.DataSnapshot mediaSnapshot: dataSnapshot.getChildren()) {
                    Medium mediaItem = new Medium();
                    mediaItem.setMediaName((String) mediaSnapshot.child("mediaName").getValue());
                    mediaItem.setMediaId((String) mediaSnapshot.child("mediaId").getValue());
                    mediaItem.setMediaLink(((String) mediaSnapshot.child("mediaLink").getValue()));
                    mediaItem.setMediaType(((String) mediaSnapshot.child("mediaType").getValue()));

                    mediaList.add(i, mediaItem);
                    i++;
                }

                if (mediaList.size() > 0 )
                {
                    vpPager.setBackgroundResource(R.color.tw__transparent);
                    vpPager.setAdapter(adapterViewPager);
                    vpPager.setPageTransformer(true, new CubeOutTransformer());
                    vpPager.getAdapter().notifyDataSetChanged();

                    if (savedInstanceState != null)
                        vpPager.setCurrentItem(savedInstanceState.getInt("currentItem", 0));
                }
                else
                    vpPager.setBackground(getDrawable(R.drawable.no_media));

                buttonUpload = (Button) findViewById(R.id.uploadBtn);
                storageReference = FirebaseStorage.getInstance().getReference();

                buttonUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(v.getContext());
                        View mView = layoutInflaterAndroid.inflate(R.layout.media_input_dialog, null);
                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(v.getContext());
                        alertDialogBuilderUserInput.setView(mView);

                        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.media_NameInput);
                        final Button cameraBtn = (Button) mView.findViewById((R.id.cameraButton));

                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(MediaHome.this,
                                    new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                        }

                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("Browse for Image", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        mediaName = userInputDialogEditText.getText().toString();
                                        Intent intent = new Intent();
                                        intent.setType("image/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST );
                                        dialogBox.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                })
                                .setNegativeButton("Browse for Video", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                mediaName = userInputDialogEditText.getText().toString();
                                                Intent intent = new Intent();
                                                intent.setType("video/*");
                                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO );
                                                dialogBox.dismiss();
                                            }
                                        });

                        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                        alertDialogAndroid.show();

                        cameraBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mediaName = userInputDialogEditText.getText().toString();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                mCameraUri = FileProvider.getUriForFile(getApplicationContext(),
                                        getApplicationContext().getPackageName() + ".provider", getOutputMediaFile());
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
                                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                                alertDialogAndroid.dismiss();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vpPager.setCurrentItem(vpPager.getCurrentItem() + 1, true);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vpPager.setCurrentItem(vpPager.getCurrentItem() - 1, true);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ProjectCentral");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + "image.jpg");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.media_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete_item: {

                if (mediaList .size() > 0)
                {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MediaHome.this);
                    View dView = layoutInflaterAndroid.inflate(R.layout.delete_dialog, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MediaHome.this);
                    alertDialogBuilderUserInput.setView(dView);

                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    String ID = mediaList.get(vpPager.getCurrentItem()).getMediaId(); //project.getId(); //get("id");
                                    mRef.child(ID).removeValue();
                                    mediaList.remove(vpPager.getCurrentItem());
                                    adapterViewPager.notifyDataSetChanged();

                                    if (mediaList.size() == 0)
                                        initializeViewPager();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    dialogBox.cancel();
                                }
                            });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No media to delete!", Toast.LENGTH_LONG).show();
                }
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initializeViewPager() {
        setContentView(R.layout.activity_media_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.media_toolbar);
        setSupportActionBar(toolbar);
        vpPager = (ViewPager) findViewById(R.id.view_Pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new CubeOutTransformer());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                uploadFile("image");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                uploadFile("video");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            filePath = mCameraUri;
            try {
                uploadFile("image");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(final String type) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            final String filename = "media" + timeStamp;

            StorageReference storageRef = storageReference.child(type + "s/" + filename);
            storageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Medium media = new Medium();

                            media.setMediaId(filename);
                            @SuppressWarnings("VisibleForTests")
                            String link = taskSnapshot.getDownloadUrl().toString();
                            media.setMediaLink(link);
                            if (mediaName != null && mediaName.length() > 0)
                                media.setMediaName(mediaName);
                            else
                                media.setMediaName(type + vpPager.getCurrentItem());
                            media.setMediaType(type);

                            mRef.child(filename).setValue(media);
                            //mediaChanged = true;
                            vpPager.getAdapter().notifyDataSetChanged();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }

        else {
            //
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return mediaList.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            Medium media = mediaList.get(position);

            return ImageFragment.newInstance(projectName, media);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            return mediaList.get(position).getMediaName();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try
            {
                super.finishUpdate(container);
            }
            catch (NullPointerException nullPointerException)
            {
                System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageItem", vpPager.getCurrentItem());
    }
}
