package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewNote extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference childRef;
    private TextView notesDescription;
    private NotesClass notesClass;
    private Button submitButton;
    private Note noteObj = new Note();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        notesDescription = (EditText) findViewById(R.id.notesDescription);
        submitButton = (Button) findViewById(R.id.notes_done_btn);
        Intent newIntent = getIntent();
        final String projectName = newIntent.getStringExtra("project_name");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                final String notesId = "notes_" + timeStamp;
                if(projectName.equals("none_none"))
                {
                    childRef =
                            FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).
                                    child("quicknotes").getRef();

                }
                else {
                    childRef =
                            FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).child("projects").
                                    child(projectName).getRef();
                }

                noteObj.setNoteId(notesId);
                noteObj.setNoteDesc(notesDescription.getText().toString());
                if(projectName.equals("none_none")) {
                    childRef.child(noteObj.getNoteId()).setValue(noteObj);
                }
                else {
                    childRef.child("notes").child(noteObj.getNoteId()).setValue(noteObj);
                }
                Toast.makeText(view.getContext(), "New Note Added" , Toast.LENGTH_SHORT).show();

                finish();

            }
        });
    }
}
