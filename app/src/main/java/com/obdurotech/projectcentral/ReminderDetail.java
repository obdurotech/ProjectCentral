package com.obdurotech.projectcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ReminderDetail extends AppCompatActivity {

    private TextView timeView;
    private TextView locationView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);
        Intent newIntent = getIntent();
        Reminder reminder = (Reminder) newIntent.getSerializableExtra("reminder_name");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        timeView = (TextView) findViewById(R.id.reminderDetail_Time_textView);
        locationView = (TextView) findViewById(R.id.reminderDetail_Place_textView);

        timeView.setText(reminder.getReminderDate().toString());
        locationView.setText(reminder.getReminderLocation());
    }
}
