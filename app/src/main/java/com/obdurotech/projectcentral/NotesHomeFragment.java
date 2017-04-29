package com.obdurotech.projectcentral;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesHomeFragment extends Fragment {
    private String projectName;
    private static final String TAG = "NotesHomeScreen";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth mAuth;
    private OnFragmentInteractionListener mListener;
    protected RecyclerView mRecyclerView;
    protected NotesAdapter myFirebaseRecylerAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected NotesClass notesClass;

    public NotesHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesHomeFragment newInstance(String param1, String param2) {
        NotesHomeFragment fragment = new NotesHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final View rootView = inflater.inflate(R.layout.fragment_notes_home, container, false);
        rootView.setTag(TAG);

        FloatingActionButton notes_fab;
        TextView toolbarText;
        DatabaseReference childRef;
        notes_fab = (FloatingActionButton) rootView.findViewById(R.id.notes_fab);
        toolbarText = (TextView) rootView.findViewById(R.id.notes_titleText);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notes_recycler);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if(projectName.equals("none_none"))
        {
            DatabaseReference userRef =
                    FirebaseDatabase.getInstance().getReference().child("userdata").getRef();
        }
        else {
            DatabaseReference userRef =
                    FirebaseDatabase.getInstance().getReference().child("userdata").child("projects").getRef();
        }
        notes_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getContext();
                Intent intent = new Intent(context, AddNewNote.class);
                intent.putExtra("project_name", projectName);
                context.startActivity(intent);
            }
        });

        if (projectName.equals("none_none"))
            toolbarText.setText(user.getDisplayName() + " - Quick Notes");
        else
            toolbarText.setText(projectName + " - Notes");
        if(projectName.equals("none_none"))
        {
            childRef =
                    FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid())
                            .child("quicknotes").getRef();
        }
        else {
            childRef =
                    FirebaseDatabase.getInstance().getReference().child("userdata").child(user.getUid()).
                            child("projects").child(projectName).child("notes").getRef();
        }
        myFirebaseRecylerAdapter = new NotesAdapter(Note.class,
                R.layout.activity_notes_adapter, NotesAdapter.NotesViewHolder.class,
                childRef, getContext(), projectName);
        notesClass = new NotesClass(user.getUid(), projectName);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(myFirebaseRecylerAdapter);
        if (notesClass.getSize() == 0) {
            notesClass.setAdapter(myFirebaseRecylerAdapter);
            notesClass.setContext(getActivity());
            notesClass.initializeDataFromCloud();
        }


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
