package com.obdurotech.projectcentral;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
    private Button locationButton;
    private Date reminderDate;
    Place place;
    private Reminder reminderObj = new Reminder();
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_reminder);
        datePicker = (ReminderDatePicker) findViewById(R.id.date_picker);
        descText = (EditText) findViewById(R.id.reminderDescription);
        submitButton = (Button) findViewById(R.id.reminder_done_btn);
        reminderButton = (Button) findViewById(R.id.reminder_setAlarm);
        locationButton = (Button) findViewById(R.id.reminder_setLocation);
        Intent newIntent = getIntent();
        final String projectName = newIntent.getStringExtra("project_name");

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewReminder.this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOACTION }, 0);
        }

        //Listener for date change
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar date) {
                reminderDate = date.getTime();
                Toast.makeText(AddNewReminder.this, "Selected date: "+ getDateTimeInstance().format(date.getTime()), Toast.LENGTH_SHORT).show();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent = null;
                try {
                    intent = builder.build(AddNewReminder.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
                catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(reminderDate);

                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra(CalendarContract.Events.TITLE, descText.getText().toString());
                if (place != null)
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, place.getName() + " , " + place.getAddress());
                startActivity(intent);
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

                reminderObj.setRemId(remId);
                reminderObj.setRemDesc(descText.getText().toString());
                reminderObj.setReminderDate(reminderDate);

                childRef.child("reminders").child(reminderObj.getRemId()).setValue(reminderObj);

                Toast.makeText(view.getContext(), "New Reminder Added: " + reminderObj.getRemDesc(), Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getApplicationContext(), data);
                String address = place.getName() + " , " + place.getAddress();
                reminderObj.setReminderLocation(address);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


}
