package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotesDetail extends AppCompatActivity {

    FirebaseAuth mAuth;
    private EditText noteDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Intent newIntent = getIntent();
        Note note = (Note) newIntent.getSerializableExtra("note_name");

        noteDescription = (EditText) findViewById(R.id.notesDetail_description);
        noteDescription.setText(note.getNoteDesc());
        noteDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                noteDescription.setFocusable(true);
                noteDescription.setFocusableInTouchMode(true);
            }
        });

    }
}
