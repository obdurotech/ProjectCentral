package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReminderHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReminderHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String projectName;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "RemindersHomeScreen";

    List<Map<String, ?>> myDataset;
    ProjectClass pc;

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected ReminderHomeFragment.LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected ReminderAdapter myFirebaseRecylerAdapter;
    protected ReminderClass reminderClass;
    //protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    FirebaseAuth mAuth;

    private OnFragmentInteractionListener mListener;

    public ReminderHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderHomeFragment newInstance(String param1, String param2) {
        ReminderHomeFragment fragment = new ReminderHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //projectName = getActivity().getIntent().getExtras().getString("project_name");
        Bundle bundle = this.getArguments();
        projectName = bundle.getString("project_name");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        projectName = bundle.getString("project_name");
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_reminder_home, container, false);
        rootView.setTag(TAG);

        FloatingActionButton reminder_fab;
        TextView toolbarText;
        reminder_fab = (FloatingActionButton) rootView.findViewById(R.id.reminders_fab);
        toolbarText = (TextView) rootView.findViewById(R.id.reminders_titleText);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.reminders_recycler);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference userRef =
                FirebaseDatabase.getInstance().getReference().child("userdata").child("projects").getRef();

        reminder_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getContext();
                Intent intent = new Intent(context, AddNewReminder.class);
                intent.putExtra("project_name", projectName);
                context.startActivity(intent);
            }
        });

        /*userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (! dataSnapshot.hasChild(user.getUid()))
                    initializeUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        toolbarText.setText(projectName + " - Reminders");

        DatabaseReference childRef =
                FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).
                        child("projects").child(projectName).child("reminders").getRef();
        myFirebaseRecylerAdapter = new ReminderAdapter(Reminder.class,
                R.layout.activity_reminder_adapter, ReminderAdapter.ReminderViewHolder.class,
                childRef, getContext(), projectName);
        reminderClass = new ReminderClass(user.getUid(), projectName);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(myFirebaseRecylerAdapter);
        if (reminderClass.getSize() == 0) {
            reminderClass.setAdapter(myFirebaseRecylerAdapter);
            reminderClass.setContext(getActivity());
            reminderClass.initializeDataFromCloud();
        }

        /*project_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getContext();
                Intent intent = new Intent(context, AddNewProject.class);
                context.startActivity(intent);
            }
        });*/

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    /*private void initDataset() {
        myDataset = pc.getProjectList();
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
