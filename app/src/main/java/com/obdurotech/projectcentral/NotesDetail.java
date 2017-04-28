package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesDetail extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText noteDescription;
    TextView notesTopText;
    FloatingActionButton note_fab;
    Button doneBtn;

    DatabaseReference projectRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Intent newIntent = getIntent();
        String projectName = newIntent.getStringExtra("project_name");
        final Note note = (Note) newIntent.getSerializableExtra("note_name");

        noteDescription = (EditText) findViewById(R.id.notesDetail_description);
        note_fab = (FloatingActionButton) findViewById(R.id.notesDetail_fab);
        doneBtn = (Button) findViewById(R.id.notesDetail_button);
        notesTopText = (TextView) findViewById(R.id.notesDetail_toptext);

        projectRef = FirebaseDatabase.getInstance().getReference().child("userdata")
                .child(user.getUid()).child("projects").child(projectName).getRef();

        notesTopText.setText(projectName);
        noteDescription.setText(note.getNoteDesc());

        note_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                note_fab.setEnabled(false);

                noteDescription.setEnabled(true);
                noteDescription.setFocusable(true);
                noteDescription.setFocusableInTouchMode(true);

                doneBtn.setEnabled(true);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                note.setNoteDesc(noteDescription.getText().toString());

                projectRef.child("notes").child(note.getNoteId()).setValue(note);

                doneBtn.setEnabled(false);
                note_fab.setEnabled(true);

                noteDescription.setEnabled(false);
                noteDescription.setFocusable(false);
                noteDescription.setFocusableInTouchMode(false);

            }
        });

    }
}
