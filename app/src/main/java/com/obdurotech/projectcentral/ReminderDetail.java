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

import java.text.Format;
import java.text.SimpleDateFormat;

public class ReminderDetail extends AppCompatActivity {

    private TextView remTitle;
    private TextView timeView, dateView;
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

        remTitle = (TextView) findViewById(R.id.reminderDetail_TitleText);
        dateView = (TextView) findViewById(R.id.reminderDetail_DateDetail);
        timeView = (TextView) findViewById(R.id.reminderDetail_TimeDetail);
        locationView = (TextView) findViewById(R.id.reminderDetail_Place_textView);

        Format dateFormat = new SimpleDateFormat("EEE MMM/dd/yyyy");
        String date = dateFormat.format(reminder.getReminderDate());

        Format timeFormat = new SimpleDateFormat("HH:mm");
        String time = timeFormat.format(reminder.getReminderDate());

        remTitle.setText(reminder.getRemDesc());
        dateView.setText(date);
        timeView.setText(time);
        locationView.setText(reminder.getReminderLocation());
    }
}
