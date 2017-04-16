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
 * Created by Sandesh Ashok Naik on 4/11/2017.
 */

public class UserClass {
    List<Map<String,?>> projectList;
    DatabaseReference mRef;
    Context mContext;

    public void removeItemFromServer(Userdatum userdatum){
        if(userdatum!=null){
            String id = userdatum.getId(); //get("id");
            mRef.child(id).removeValue();
        }
    }

    public void addItemToServer(Userdatum userdatum){
        if(userdatum!=null){
            String id = userdatum.getId(); //get("id");
            mRef.child(id).setValue(userdatum);
        }
    }

    public DatabaseReference getFireBaseRef(){
        return mRef;
    }
    public void setContext(Context context){mContext = context;}

    public List<Map<String,?>> getProjectList() {
        return projectList;
    }

    public int getSize(){
        return projectList.size();
    }

    public HashMap getItem(int i){
        if (i >=0 && i < projectList.size()){
            return (HashMap) projectList.get(i);
        } else return null;
    }


    public void onItemRemovedFromCloud(HashMap item){
        int position = -1;
        String id = (String) item.get("id");
        for(int i=0;i<projectList.size();i++){
            HashMap project = (HashMap) projectList.get(i);
            String mid = (String) project.get("id");
            if(mid.equals(id)){
                position= i;
                break;
            }
        }
        if(position != -1){
            projectList.remove(position);
            Toast.makeText(mContext, "Item Removed:" + id, Toast.LENGTH_SHORT).show();

        }
    }

    public void onItemAddedToCloud(HashMap item){
        int insertPosition = 0;
        String id = (String) item.get("id");
        for(int i=0;i<projectList.size();i++){
            HashMap project = (HashMap) projectList.get(i);
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
        projectList.add(insertPosition,item);
        // Toast.makeText(mContext, "Item added:" + id, Toast.LENGTH_SHORT).show();

    }

    public void onItemUpdatedToCloud(HashMap item){
        String id = (String) item.get("id");
        for(int i=0;i<projectList.size();i++){
            HashMap project = (HashMap) projectList.get(i);
            String mid = (String) project.get("id");
            if(mid.equals(id)){
                projectList.remove(i);
                projectList.add(i,item);
                Log.d("My Test: NotifyChanged",id);

                break;
            }
        }

    }
    public void initializeDataFromCloud() {
        projectList.clear();
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

    public UserClass(String uid){

        projectList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference().child("userdata").child(uid).getRef();
        mContext = null;

    }


    public int findFirst(String query){

        for(int i=0;i<projectList.size();i++){
            HashMap project = (HashMap) getProjectList().get(i);
            if(((String) project.get("name")).toLowerCase().contains(query.toLowerCase())){
                return i;
            }
        }
        return 0;

    }
}
