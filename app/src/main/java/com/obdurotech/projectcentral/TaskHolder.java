package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TaskHolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_holder);
        Intent intent = getIntent();
        String projectName = intent.getStringExtra("project_name");

        Bundle bundle = new Bundle();
        bundle.putString("project_name", projectName);
        TaskHomeFragment taskFragment = new TaskHomeFragment();
        taskFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.taskHolder, taskFragment);
        transaction.commit();
    }
}
