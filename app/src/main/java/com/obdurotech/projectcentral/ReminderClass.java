package com.obdurotech.projectcentral;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Varun on 4/16/2017.
 */

public class ReminderClass {
    String uid;
    List<Map<String,?>> remindersList;
    DatabaseReference mRef;
    ReminderAdapter myFirebaseRecylerAdapter;
    Context mContext;

    public DatabaseReference getFireBaseRef(){
        return mRef;
    }

    public int getSize(){
        return remindersList.size();
    }

    public void setAdapter(ReminderAdapter mAdapter) {
        myFirebaseRecylerAdapter = mAdapter;
    }

    public void setContext(Context context){mContext = context;}

    public void onItemRemovedFromCloud(HashMap item){
        int position = -1;
        String id = (String) item.get("id");
        for(int i=0;i<remindersList.size();i++){
            HashMap project = (HashMap) remindersList.get(i);
            String mid = (String) project.get("id");
            if(mid.equals(id)){
                position= i;
                break;
            }
        }
        if(position != -1){
            remindersList.remove(position);
            Toast.makeText(mContext, "Item Removed:" + id, Toast.LENGTH_SHORT).show();

        }
    }

    public void onItemAddedToCloud(HashMap item){
        int insertPosition = 0;
        String id = (String) item.get("id");
        for(int i=0;i<remindersList.size();i++){
            HashMap project = (HashMap) remindersList.get(i);
            String mid = (String) project.get("id");
            if(mid.equals(id)){
                return;
            }
            if(mid.compareTo(id)<0){
                insertPosition=i+1;
            }else{
                break;
            }
        }
        remindersList.add(insertPosition,item);
        // Toast.makeText(mContext, "Item added:" + id, Toast.LENGTH_SHORT).show();

    }

    public void onItemUpdatedToCloud(HashMap item){
        String id = (String) item.get("id");
        for(int i=0;i<remindersList.size();i++){
            HashMap project = (HashMap) remindersList.get(i);
            String mid = (String) project.get("id");
            if(mid.equals(id)){
                remindersList.remove(i);
                remindersList.add(i,item);
                Log.d("My Test: NotifyChanged",id);

                break;
            }
        }

    }

    public void initializeDataFromCloud() {
        remindersList.clear();
        mRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Log.d("MyTest: OnChildAdded", dataSnapshot.toString());
                HashMap<String, ?> project = (HashMap<String,?>) dataSnapshot.getValue();
                onItemAddedToCloud(project);
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Log.d("MyTest: OnChildChanged", dataSnapshot.toString());
                HashMap<String,?> project = (HashMap<String,?>) dataSnapshot.getValue();
                onItemUpdatedToCloud(project);
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Log.d("MyTest: OnChildRemoved", dataSnapshot.toString());
                HashMap<String,?> project = (HashMap<String,?>) dataSnapshot.getValue();
                onItemRemovedFromCloud(project);
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public ReminderClass(String userID, String projectName){

        uid = userID;
        remindersList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference().child("userdata").child(uid).child("projects").child(projectName).getRef();
        myFirebaseRecylerAdapter = null;
        mContext = null;

    }


}

