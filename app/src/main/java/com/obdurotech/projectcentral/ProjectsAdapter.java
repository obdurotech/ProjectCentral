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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

/**
 * Created by Sandesh Ashok Naik on 4/9/2017.
 */

public class ProjectsAdapter extends FirebaseRecyclerAdapter<Project, ProjectsAdapter.ProjectsViewHolder> {

    private Context mContext;
    private UserData userData = new UserData();

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

        public ProjectsViewHolder(View v) {

            super(v);

            myView = (CardView) v.findViewById(R.id.card_view);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            iv = (ImageView) v.findViewById(R.id.icon);
            optionsButton = (ImageView) v.findViewById(R.id.textViewOptions);
        }
    }

    @Override
    protected void populateViewHolder(final ProjectsViewHolder projectsViewHolder, final Project project, final int i) {

        //TODO: Populate viewHolder by setting the movie attributes to cardview fields

        projectsViewHolder.txtHeader.setText(project.getName());
        projectsViewHolder.txtFooter.setText(project.getDescription());
        //Picasso.with(mContext).load(movie.getUrl()).into(movieViewHolder.iv);


    }

}
