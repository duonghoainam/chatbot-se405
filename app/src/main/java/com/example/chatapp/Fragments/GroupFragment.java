package com.example.chatapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chatapp.GroupChatActivity;
import com.example.chatapp.Model.Group;
import com.example.chatapp.Model.GroupMessage;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupFragment extends Fragment {
    private  View groupFramentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private ArrayList<String> list_of_idGroups = new ArrayList<>();
    private ArrayList<String> list_of_adminGroups = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference GroupRef;
    private String currentUserID;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFramentView = inflater.inflate(R.layout.fragment_group, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groupss");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        inializeFields();
        
        RetrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                String currentGroupId = list_of_idGroups.get(position).toString();
                String currentGroupAdmin = list_of_adminGroups.get(position).toString();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                groupChatIntent.putExtra("groupId", currentGroupId);
                groupChatIntent.putExtra("groupAdmin", currentGroupAdmin);
                startActivity(groupChatIntent);
            }
        });
        
        return groupFramentView;
    }

    private void inializeFields() {
        listView = (ListView) groupFramentView.findViewById(R.id.list_group);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        listView.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> set = new ArrayList<>();
                List<String> set_id = new ArrayList<>();
                List<String> set_addmin = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.child("users").child(currentUserID).exists()){
                        set.add(dataSnapshot.child("name").getValue().toString());
                        set_id.add(dataSnapshot.getKey().toString());
                        set_addmin.add(dataSnapshot.child("admin").getValue().toString());
                    }
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);

                list_of_idGroups.clear();
                list_of_idGroups.addAll(set_id);

                list_of_adminGroups.clear();
                list_of_adminGroups.addAll(set_addmin);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}