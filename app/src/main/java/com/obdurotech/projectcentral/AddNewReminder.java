package com.obdurotech.projectcentral;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simplicityapks.reminderdatepicker.lib.OnDateSelectedListener;
import com.simplicityapks.reminderdatepicker.lib.ReminderDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.text.DateFormat.getDateTimeInstance;

public class AddNewReminder extends AppCompatActivity {

    FirebaseAuth mAuth;
    private ReminderDatePicker datePicker;
    private EditText descText;
    private Button submitButton;
    private Button reminderButton;
    private Date reminderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_reminder);
        datePicker = (ReminderDatePicker) findViewById(R.id.date_picker);
        descText = (EditText) findViewById(R.id.reminderDescription);
        submitButton = (Button) findViewById(R.id.reminder_done_btn);
        reminderButton = (Button) findViewById(R.id.reminder_setAlarm);
        Intent newIntent = getIntent();
        final String projectName = newIntent.getStringExtra("project_name");
        //Listener for date change
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar date) {
                reminderDate = date.getTime();
                Toast.makeText(AddNewReminder.this, "Selected date: "+ getDateTimeInstance().format(date.getTime()), Toast.LENGTH_SHORT).show();
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(reminderDate);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_CALENDAR);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), com.obdurotech.projectcentral.Manifest.permission.READ_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED)
                    {
                        try {
                            GregorianCalendar calDate = new GregorianCalendar(year, month, day, hour, minute);
                            ContentResolver contentResolver = getContentResolver();
                            ContentValues values = new ContentValues();
                            values.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
                            values.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis() + 60 * 60 * 1000);
                            values.put(CalendarContract.Events.TITLE, descText.getText().toString());
                            values.put(CalendarContract.Events.CALENDAR_ID, 1);
                            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                                    .getTimeZone().getID());
                            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
                            long eventId = Long.parseLong(uri.getLastPathSegment());
                            setReminder(contentResolver, eventId, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                final String remId = "reminder" + timeStamp;
                DatabaseReference childRef =
                        FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).child("projects").
                                child(projectName).getRef();
                Reminder reminderObj = new Reminder();
                reminderObj.setRemId(remId);
                reminderObj.setRemDesc(descText.getText().toString());
                reminderObj.setReminderDate(reminderDate);
                reminderObj.setReminderLocation("Unknown");

                childRef.child("reminders").child(reminderObj.getRemDesc()).setValue(reminderObj);

                Toast.makeText(view.getContext(), "New Reminder Added: " + reminderObj.getRemDesc(), Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    public void setReminder(ContentResolver contentResolver, long eventId, int timeBefore)
    {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), com.obdurotech.projectcentral.Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Reminders.MINUTES, timeBefore);
                values.put(CalendarContract.Reminders.EVENT_ID, eventId);
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                Uri uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values);
                Cursor c = CalendarContract.Reminders.query(contentResolver, eventId,
                        new String[]{CalendarContract.Reminders.MINUTES});
                if (c.moveToFirst()) {
                    System.out.println("calendar"
                            + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
