package com.obdurotech.projectcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class MediaHome extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;

    FragmentPagerAdapter adapterViewPager;
    private Uri filePath;

    private Button buttonUpload;
    private StorageReference storageReference;

    private final int NUM_ITEMS = 5;

    FirebaseAuth mAuth;
    String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_home);

        Intent intent = getIntent();
        projectName = intent.getStringExtra("project_name");

        buttonUpload = (Button) findViewById(R.id.uploadBtn);
        storageReference = FirebaseStorage.getInstance().getReference();

        ViewPager vpPager = (ViewPager) findViewById(R.id.view_Pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
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

            StorageReference riversRef = storageReference.child("images/" + filename);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference childRef =
                                    FirebaseDatabase.getInstance().getReference().child("userdata")
                                            .child(user.getUid()).child("projects").child(projectName).getRef();

                            Medium media = new Medium();

                            media.setMediaId(filename);
                            @SuppressWarnings("VisibleForTests")
                            String link = taskSnapshot.getDownloadUrl().toString();
                            media.setMediaLink(link);
                            media.setMediaName(filename);

                            childRef.child("media").child(filename).setValue(media);

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            return ImageFragment.newInstance(projectName, position);

            //return  movie_info.newInstance("movie", position);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            //final HashMap movieInfo = md.getItem(position);
            //return movieInfo.get("name").toString();
            return "Page " + position;
        }

    }
}
