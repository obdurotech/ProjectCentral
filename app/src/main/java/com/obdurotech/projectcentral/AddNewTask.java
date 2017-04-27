package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simplicityapks.reminderdatepicker.lib.ReminderDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddNewTask extends AppCompatActivity {

    FirebaseAuth mAuth;
    private EditText nameText, descText;
    private Button submitButton;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        nameText = (EditText) findViewById(R.id.taskName_input);
        descText = (EditText) findViewById(R.id.taskDesc_input);
        typeSpinner = (Spinner) findViewById(R.id.taskType_spinner);
        submitButton = (Button) findViewById(R.id.task_done_btn);

        populateTypes();

        Intent newIntent = getIntent();
        final String projectName = newIntent.getStringExtra("project_name");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                final String taskId = "task" + timeStamp;
                DatabaseReference childRef = FirebaseDatabase.getInstance().getReference()
                        .child("userdata").child(user.getUid()).child("projects").child(projectName).getRef();

                Task task = new Task();
                task.setTaskId(taskId);
                task.setTaskName(nameText.getText().toString());
                task.setTaskDesc(descText.getText().toString());
                task.setTaskType(String.valueOf(typeSpinner.getSelectedItem()));
                task.setTaskStatus("Pending");

                childRef.child("tasks").child(taskId).setValue(task);

                Toast.makeText(v.getContext(), "New Task Added: " + task.getTaskName(), Toast.LENGTH_SHORT).show();

                finish();
            }
        });


    }

    public void populateTypes() {

        List<String> typeList = new ArrayList<String>();

        typeList.add(0, "Deadline");
        typeList.add(1,"Draft");
        typeList.add(2,"Follow-up");
        typeList.add(3,"Meeting");
        typeList.add(4,"Milestone");
        typeList.add(5,"Project Review");
        typeList.add(6,"Rollout");
        typeList.add(7,"Scheduled Task");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(dataAdapter);
    }
}
