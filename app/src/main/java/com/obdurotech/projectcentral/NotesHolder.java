package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotesHolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_holder);
        Intent intent = getIntent();
        String projectName = intent.getStringExtra("project_name");

        Bundle bundle = new Bundle();
        bundle.putString("project_name", projectName);
        NotesHomeFragment notesFragment = new NotesHomeFragment();
        notesFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.notesHolder, notesFragment);
        transaction.commit();

    }
}
