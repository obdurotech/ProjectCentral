package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ReminderAdapter extends FirebaseRecyclerAdapter<Reminder, ReminderAdapter.ReminderViewHolder> {

    private Context mContext;
    static private String mProjectName;
    public static ReminderClass reminderClass;

    //public ReminderAdapter(){ };

    public ReminderAdapter(Class<Reminder> modelClass, int modelLayout,
                           Class<ReminderAdapter.ReminderViewHolder> holder, DatabaseReference ref, Context context, String projectName) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
        this.mProjectName = projectName;
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        private CardView myView;
        private TextView txtHeader;
        private TextView txtFooter;
        private ImageView optionsButton;

        FirebaseAuth mAuth;

        public ReminderViewHolder(View v) {

            super(v);

            myView = (CardView) v.findViewById(R.id.reminder_CardView);
            txtHeader = (TextView) v.findViewById(R.id.reminder_header);
            txtFooter = (TextView) v.findViewById(R.id.reminder_footer);
            optionsButton = (ImageView) v.findViewById(R.id.reminder_textViewOptions);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            reminderClass = new ReminderClass(user.getUid(), mProjectName);
        }
    }

    @Override
    protected void populateViewHolder(final ReminderAdapter.ReminderViewHolder reminderViewHolder, final Reminder reminder, final int i) {

        reminderViewHolder.txtHeader.setText(reminder.getRemId());
        reminderViewHolder.txtFooter.setText(reminder.getRemDesc());

        reminderViewHolder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), reminderViewHolder.optionsButton);
                popup.inflate(R.menu.options_menu);
                if (reminderClass.getSize() == 0) {
                    reminderClass.setContext(mContext);
                    reminderClass.initializeDataFromCloud();
                }
                /*popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                break;
                            case R.id.menu2:
                                reminderClass.removeItemFromServer(reminder);
                                break;
                        }
                        return false;
                    }
                });*/

                popup.show();
            }
        });

        reminderViewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DatabaseReference ref = reminderClass.getFireBaseRef();
                String id = reminder.getRemId();
                ref.child(id).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ReminderDetail.class);
                        intent.putExtra("reminderId", reminder.getRemId());
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Test", "Read failed: " + databaseError.getMessage());
                    }
                });
            }
        });
    }
}
