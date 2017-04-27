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
 * Created by Sandesh Ashok Naik on 4/19/2017.
 */

public class TaskClass {
    String uid;
    List<Map<String,?>> tasksList;
    DatabaseReference mRef;
    TaskAdapter myFirebaseRecylerAdapter;
    Context mContext;

    public DatabaseReference getFireBaseRef(){
        return mRef;
    }

    public int getSize(){
        return tasksList.size();
    }

    public void setAdapter(TaskAdapter mAdapter) {
        myFirebaseRecylerAdapter = mAdapter;
    }

    public void removeItemFromServer(Task task){
        if(task!=null){
            String id = task.getTaskId(); //get("id");
            mRef.child(id).removeValue();
        }
    }

    public void addItemToServer(Project project){
        if(project!=null){
            String id = project.getId(); //get("id");
            mRef.child(id).setValue(project);
        }
    }

    public void setContext(Context context){mContext = context;}

    public void onItemRemovedFromCloud(HashMap item){
        int position = -1;
        String id = (String) item.get("taskId");
        for(int i=0;i<tasksList.size();i++){
            HashMap task = (HashMap) tasksList.get(i);
            String mid = (String) task.get("taskId");
            if(mid.equals(id)){
                position= i;
                break;
            }
        }
        if(position != -1){
            tasksList.remove(position);
            Toast.makeText(mContext, "Item Removed:" + id, Toast.LENGTH_SHORT).show();

        }
    }

    public void onItemAddedToCloud(HashMap item){
        int insertPosition = 0;
        String id = (String) item.get("taskId");
        for(int i=0;i<tasksList.size();i++){
            HashMap task = (HashMap) tasksList.get(i);
            String mid = (String) task.get("taskId");
            if(mid.equals(id)){
                return;
            }
            if(mid.compareTo(id)<0){
                insertPosition=i+1;
            }else{
                break;
            }
        }
        tasksList.add(insertPosition,item);
        // Toast.makeText(mContext, "Item added:" + id, Toast.LENGTH_SHORT).show();

    }

    public void onItemUpdatedToCloud(HashMap item){
        String id = (String) item.get("taskId");
        for(int i=0;i<tasksList.size();i++){
            HashMap task = (HashMap) tasksList.get(i);
            String mid = (String) task.get("taskId");
            if(mid.equals(id)){
                tasksList.remove(i);
                tasksList.add(i,item);
                Log.d("My Test: NotifyChanged",id);

                break;
            }
        }

    }

    public void initializeDataFromCloud() {
        tasksList.clear();
        mRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Log.d("MyTest: OnChildAdded", dataSnapshot.toString());
                HashMap<String, ?> task = (HashMap<String,?>) dataSnapshot.getValue();
                onItemAddedToCloud(task);
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Log.d("MyTest: OnChildChanged", dataSnapshot.toString());
                HashMap<String,?> task = (HashMap<String,?>) dataSnapshot.getValue();
                onItemUpdatedToCloud(task);
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Log.d("MyTest: OnChildRemoved", dataSnapshot.toString());
                HashMap<String,?> task = (HashMap<String,?>) dataSnapshot.getValue();
                onItemRemovedFromCloud(task);
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public TaskClass(String userID, String projectName){

        uid = userID;
        tasksList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference().child("userdata").child(uid).child("projects")
                .child(projectName).child("tasks").getRef();
        myFirebaseRecylerAdapter = null;
        mContext = null;

    }


}

