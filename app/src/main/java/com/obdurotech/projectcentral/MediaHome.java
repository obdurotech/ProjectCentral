package com.obdurotech.projectcentral;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MediaHome extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 101;
    boolean mediaChanged = false;

    ViewPager vpPager;
    FragmentPagerAdapter adapterViewPager;
    private Uri filePath;

    private String mediaName;
    private Button buttonUpload;
    private StorageReference storageReference;

    DatabaseReference childRef;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String projectName;
    List<Medium> mediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        projectName = intent.getStringExtra("project_name");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mediaList = new ArrayList<>();

        setContentView(R.layout.activity_media_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.media_toolbar);
        setSupportActionBar(toolbar);

        vpPager = (ViewPager) findViewById(R.id.view_Pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

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
                    if (adapterViewPager == null)
                        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
                    vpPager.setAdapter(adapterViewPager);
                    vpPager.getAdapter().notifyDataSetChanged();
                }
                else
                    vpPager.setBackground(getDrawable(R.drawable.no_media));

                //setContentView(R.layout.activity_media_home);
                //vpPager = (ViewPager) findViewById(R.id.view_Pager);
                //adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
                //vpPager.setAdapter(adapterViewPager);

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

                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                        alertDialogAndroid.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadFile("image");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadFile("video");

            } catch (IOException e) {
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

            Calendar c = Calendar.getInstance();
            String yr = String.valueOf(c.get(Calendar.YEAR));
            String mm = String.valueOf(c.get(Calendar.MONTH));
            String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            String hr = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            String min = String.valueOf(c.get(Calendar.MINUTE));
            String sec = String.valueOf(c.get(Calendar.SECOND));

            final String filename = "media" + yr + mm + day + hr + min + sec;

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
                            if (mediaName.length() > 0)
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

    }
}
