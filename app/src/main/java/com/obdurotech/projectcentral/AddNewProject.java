package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddNewProject extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText projectName;
    EditText projectDesc;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);

        projectName = (EditText) findViewById(R.id.project_name_text);
        projectDesc = (EditText) findViewById(R.id.project_desc_text);
        doneBtn = (Button) findViewById(R.id.project_done_btn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                DatabaseReference childRef =
                        FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).getRef();

                Project project = new Project();
                project.setId(projectName.getText().toString());
                project.setName(projectName.getText().toString());
                project.setDescription(projectDesc.getText().toString());
                project.setTasks(new HashMap<String, Task>());
                project.setReminders(new HashMap<String, Reminder>());
                project.setNotes(new HashMap<String, Note>());
                project.setMedia(new HashMap<String, Medium>());

                childRef.child("projects").child(projectName.getText().toString()).setValue(project);

                Toast.makeText(view.getContext(), "New Project Added: " + project.getName(), Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}
