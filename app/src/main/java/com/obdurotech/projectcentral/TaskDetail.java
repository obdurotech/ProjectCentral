package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskDetail extends AppCompatActivity {

    private TextView taskName, taskDesc,
            taskType, taskStatus;
    private ImageView statusImage, taskLine;
    private Button doneBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        setupWindowAnimations();

        Intent intent = getIntent();
        final String projectName = intent.getStringExtra("project_name");
        final Task task = (Task) intent.getSerializableExtra("task_name");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final DatabaseReference childRef = FirebaseDatabase.getInstance().getReference()
                .child("userdata").child(user.getUid()).child("projects").child(projectName).getRef();

        taskName = (TextView) findViewById(R.id.taskName_text);
        taskDesc = (TextView) findViewById(R.id.taskDesc_text);
        taskType = (TextView) findViewById(R.id.taskType_text);
        taskStatus = (TextView) findViewById(R.id.taskStatus_text);

        statusImage = (ImageView) findViewById(R.id.taskStatus_image);
        taskLine = (ImageView) findViewById(R.id.task_line);

        doneBtn = (Button) findViewById(R.id.taskDone_button);

        taskName.setText(task.getTaskName());
        taskDesc.setText(task.getTaskDesc());
        taskType.setText(task.getTaskType());

        if (task.getTaskStatus().equals("Pending"))
            setView("orange");

        else if (task.getTaskStatus().equals("Complete"))
            setView("green");

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (task.getTaskStatus().equals("Pending"))
                {
                    task.setTaskStatus("Complete");
                    childRef.child("tasks").child(task.getTaskId()).child("taskStatus").setValue("Complete");
                    setView("green");
                    Snackbar.make(v, "Task Complete! Good job!", Snackbar.LENGTH_LONG).show();
                }

                //if (task.getTaskStatus().equals("Complete"))
                else
                {
                    task.setTaskStatus("Pending");
                    childRef.child("tasks").child(task.getTaskId()).child("taskStatus").setValue("Pending");
                    setView("orange");
                    Snackbar.make(v, "Get to work and complete it!", Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }

    private void setView(String status)
    {
        if (status.equals("orange"))
        {
            statusImage.setImageResource(R.mipmap.ic_pending);
            doneBtn.setText("Mark as Done");
            doneBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            taskStatus.setText("Pending");

            taskDesc.setBackground(getDrawable(R.drawable.task_bg_pending));
            taskType.setBackground(getDrawable(R.drawable.task_bg_pending));
            taskStatus.setBackground(getDrawable(R.drawable.task_bg_pending));

            taskLine.setImageResource(R.color.colorAccent);
        }

        else if (status.equals("green"))
        {
            statusImage.setImageResource(R.mipmap.ic_done);
            doneBtn.setText("Not Done? Undo!");
            doneBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            taskStatus.setText("Complete");

            taskDesc.setBackground(getDrawable(R.drawable.task_bg_complete));
            taskType.setBackground(getDrawable(R.drawable.task_bg_complete));
            taskStatus.setBackground(getDrawable(R.drawable.task_bg_complete));

            taskLine.setImageResource(R.color.colorPrimary);
        }
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }
}
