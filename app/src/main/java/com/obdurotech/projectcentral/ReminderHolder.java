package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ReminderHolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_holder);
        Intent intent = getIntent();
        String projectName = intent.getStringExtra("project_name");

        //ReminderHomeFragment fragment = ReminderHomeFragment.newInstance("movie", projectName);
        //getSupportFragmentManager().beginTransaction().add(R.id.reminderHolder, fragment).commit();

        Bundle bundle = new Bundle();
        bundle.putString("project_name", projectName);
        ReminderHomeFragment reminderFragment = new ReminderHomeFragment();
        reminderFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.reminderHolder, reminderFragment);
        transaction.commit();


        /*Fragment fragment = new ReminderHomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.reminderHolder, fragment);
        ft.commit();*/
    }
}
