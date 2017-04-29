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

public class NotesAdapter extends FirebaseRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> {
    private Context mContext;
    static private String mProjectName;
    public static NotesClass notesClass;

    public NotesAdapter(Class<Note> modelClass, int modelLayout,
                           Class<NotesAdapter.NotesViewHolder> holder, DatabaseReference ref, Context context, String projectName) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
        this.mProjectName = projectName;
    }
    public static class NotesViewHolder extends RecyclerView.ViewHolder {

        private CardView myView;
        //private TextView txtHeader;
        private TextView txtFooter;
        private ImageView optionsButton;

        FirebaseAuth mAuth;

        public NotesViewHolder(View v) {

            super(v);

            myView = (CardView) v.findViewById(R.id.notes_CardView);
            //txtHeader = (TextView) v.findViewById(R.id.notes_header);
            txtFooter = (TextView) v.findViewById(R.id.notes_footer);
            optionsButton = (ImageView) v.findViewById(R.id.notes_textViewOptions);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            notesClass = new NotesClass(user.getUid(), mProjectName);
        }
    }

    @Override
    protected void populateViewHolder(final NotesAdapter.NotesViewHolder notesViewHolder, final Note note, final int i) {


        //notesViewHolder.txtHeader.setText("Note " + String.valueOf(i+1));
        notesViewHolder.txtFooter.setText(note.getNoteDesc());

        notesViewHolder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), notesViewHolder.optionsButton);
                popup.inflate(R.menu.options_menu);
                if (notesClass.getSize() == 0) {
                    notesClass.setContext(mContext);
                    notesClass.initializeDataFromCloud();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                notesClass.removeItemFromServer(note);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        notesViewHolder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DatabaseReference ref = notesClass.getFireBaseRef();
                String id = note.getNoteId();
                ref.child(id).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, NotesDetail.class);
                        intent.putExtra("project_name", mProjectName);
                        intent.putExtra("note_name", note);
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
