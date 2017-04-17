package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

/**
 * Created by Sandesh Ashok Naik on 4/9/2017.
 */

public class ProjectsAdapter extends FirebaseRecyclerAdapter<Project, ProjectsAdapter.ProjectsViewHolder> {

    private Context mContext;
    public static ProjectClass projectClass;

    public ProjectsAdapter(Class<Project> modelClass, int modelLayout,
                                    Class<ProjectsViewHolder> holder, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
    }

    //TODO: Populate ViewHolder and add listeners.
    public static class ProjectsViewHolder extends RecyclerView.ViewHolder {

        private CardView myView;
        private TextView txtHeader;
        private TextView txtFooter;
        private ImageView iv;
        private ImageView optionsButton;

        FirebaseAuth mAuth;

        public ProjectsViewHolder(View v) {

            super(v);

            myView = (CardView) v.findViewById(R.id.card_view);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            iv = (ImageView) v.findViewById(R.id.icon);
            optionsButton = (ImageView) v.findViewById(R.id.textViewOptions);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            projectClass = new ProjectClass(user.getUid());
        }
    }

    @Override
    protected void populateViewHolder(final ProjectsViewHolder projectsViewHolder, final Project project, final int i) {

        projectsViewHolder.txtHeader.setText(project.getName());
        projectsViewHolder.txtFooter.setText(project.getDescription());
        //Picasso.with(mContext).load(movie.getUrl()).into(movieViewHolder.iv);

        projectsViewHolder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), projectsViewHolder.optionsButton);
                popup.inflate(R.menu.options_menu);
                if (projectClass.getSize() == 0) {
                    //movieData.setAdapter(myFirebaseRecyclerAdapter);
                    projectClass.setContext(mContext);
                    projectClass.initializeDataFromCloud();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                break;
                            case R.id.menu2:
                                projectClass.removeItemFromServer(project);
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        projectsViewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DatabaseReference ref = projectClass.getFireBaseRef();
                String id = project.getId();
                ref.child(id).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ProjectHome.class);
                        intent.putExtra("project_name", project.getName());
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
