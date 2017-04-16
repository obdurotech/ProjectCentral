package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainScreen extends Fragment {
    private static final String TAG = "MainScreen";

    List<Map<String, ?>> myDataset;
    ProjectClass pc;

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected ProjectsAdapter myFirebaseRecylerAdapter;
    protected ProjectClass projectClass;
    //protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SearchView searchView;

    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //if (savedInstanceState != null)
        //{
        //    myDataset = (List<Map<String, ?>>) savedInstanceState.get("movieList");
        //}
//
        //else
        //{
        //    initDataset();
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        rootView.setTag(TAG);

        FloatingActionButton project_fab;
        TextView toolbarText;
        project_fab = (FloatingActionButton) rootView.findViewById(R.id.projects_fab);
        toolbarText = (TextView) rootView.findViewById(R.id.titleText);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.projects_recycler);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference userRef =
                FirebaseDatabase.getInstance().getReference().child("userdata").getRef();

        userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (! dataSnapshot.hasChild(user.getUid()))
                    initializeUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbarText.setText(user.getDisplayName());

        DatabaseReference childRef =
                FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).child("projects").getRef();
        myFirebaseRecylerAdapter = new ProjectsAdapter(Project.class,
                R.layout.project_list_item, ProjectsAdapter.ProjectsViewHolder.class,
                childRef, getContext());
        projectClass = new ProjectClass(user.getUid());

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(myFirebaseRecylerAdapter);
        if (projectClass.getSize() == 0) {
            projectClass.setAdapter(myFirebaseRecylerAdapter);
            projectClass.setContext(getActivity());//getApplicationContext()-activity is used
            projectClass.initializeDataFromCloud();
        }

        project_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getContext();
                Intent intent = new Intent(context, AddNewProject.class);
                context.startActivity(intent);
            }
        });

        return rootView;
    }

    public void initializeUser(FirebaseUser user) {
        UserClass userClass = new UserClass(user.getUid());

        DatabaseReference childRef =
                FirebaseDatabase.getInstance().getReference().child("userdata").getRef();

        Userdatum userdatum = new Userdatum();
        userdatum.setId(user.getUid());
        userdatum.setName(user.getDisplayName());
        userdatum.setEmail(user.getEmail());
        userdatum.setProjects(new HashMap<String, Project>());

        childRef.child(user.getUid()).setValue(userdatum);
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //savedInstanceState.putSerializable("movieList", (Serializable) mAdapter.getMovieList());
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        myDataset = pc.getProjectList();
    }
}
