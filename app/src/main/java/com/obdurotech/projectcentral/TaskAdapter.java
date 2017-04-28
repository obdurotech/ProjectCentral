package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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

/**
 * Created by Sandesh Ashok Naik on 4/19/2017.
 */

public class TaskAdapter extends FirebaseRecyclerAdapter<Task, TaskAdapter.TaskViewHolder> {

    private Context mContext;
    static private String mProjectName;
    public static TaskClass taskClass;

    //public ReminderAdapter(){ };

    public TaskAdapter(Class<Task> modelClass, int modelLayout,
                           Class<TaskAdapter.TaskViewHolder> holder, DatabaseReference ref, Context context, String projectName) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
        this.mProjectName = projectName;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        private CardView myView;
        private TextView txtHeader;
        private TextView txtFooter;
        private ImageView optionsButton;

        FirebaseAuth mAuth;

        public TaskViewHolder(View v) {

            super(v);

            myView = (CardView) v.findViewById(R.id.task_CardView);
            txtHeader = (TextView) v.findViewById(R.id.task_header);
            txtFooter = (TextView) v.findViewById(R.id.task_footer);
            optionsButton = (ImageView) v.findViewById(R.id.task_textViewOptions);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            taskClass = new TaskClass(user.getUid(), mProjectName);
        }
    }

    @Override
    protected void populateViewHolder(final TaskAdapter.TaskViewHolder taskViewHolder, final Task task, final int i) {


        taskViewHolder.txtHeader.setText(task.getTaskName());
        taskViewHolder.txtFooter.setText(task.getTaskType());

        if (task.getTaskStatus().equals("Complete"))
        {
            taskViewHolder.txtHeader.setPaintFlags(taskViewHolder.txtHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            taskViewHolder.txtFooter.setPaintFlags(taskViewHolder.txtFooter.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        taskViewHolder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), taskViewHolder.optionsButton);
                popup.inflate(R.menu.options_menu);
                if (taskClass.getSize() == 0) {
                    taskClass.setContext(mContext);
                    taskClass.initializeDataFromCloud();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                taskClass.removeItemFromServer(task);
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        taskViewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DatabaseReference ref = taskClass.getFireBaseRef();
                String id = task.getTaskId();
                ref.child(id).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, TaskDetail.class);
                        intent.putExtra("project_name", mProjectName);
                        intent.putExtra("task_name", task);
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

