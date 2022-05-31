package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.GroupAdapter;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.Group;
import com.example.chatapp.Model.GroupMessage;
import com.example.chatapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private TextView groupName;
    private ImageButton btn_send, btn_addmember;
    private EditText text_send;
    private RecyclerView displayTextMessages;
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference userRef, groupIdRef, groupMessageKeyRef;

    private ArrayList<String> list_of_members = new ArrayList<>();
    private ArrayList<String> list_of_members_id = new ArrayList<>();
    private ArrayList<String> list_of_members_choosed = new ArrayList<>();


    GroupAdapter groupAdapter;
    List<GroupMessage> mGroupMessages;
    List<String> mImgs;
    List<String> mUsers;

    private FirebaseAuth mAuth;
    private String currentGroupName, currentGroupId, currentGroupAdmin, currentUserID, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        currentGroupId = getIntent().getExtras().get("groupId").toString();
        currentGroupAdmin = getIntent().getExtras().get("groupAdmin").toString();

        groupIdRef = FirebaseDatabase.getInstance().getReference().child("Groupss").child(currentGroupId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        groupName = findViewById(R.id.groupname);
        groupName.setText(currentGroupName);

        displayTextMessages = findViewById(R.id.group_chat_text_display);
        displayTextMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        displayTextMessages.setLayoutManager(linearLayoutManager);

        btn_send = findViewById(R.id.btn_send);
        btn_addmember = findViewById(R.id.add_member);

        text_send = findViewById(R.id.text_send);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupChatActivity.this, HomeActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        getUserInfo();

        btn_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFormAddMember();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageGroupInfo();
                text_send.setText("");
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (currentGroupAdmin.equals(currentUserName)) btn_addmember.setVisibility(View.VISIBLE);

                DisplayMessages(snapshot);
                LoadUserToAdd(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadUserToAdd(DataSnapshot usersSnapshot) {
        groupIdRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_of_members.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chatlist userChatlist = dataSnapshot.getValue(Chatlist.class);
                    for (DataSnapshot userSnapshot : usersSnapshot.getChildren()){
                        User user = userSnapshot.getValue(User.class);
                        if (!user.getId().equals(userChatlist.getId())){
                            list_of_members.add(user.getUsername());
                            list_of_members_id.add(user.getId());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openFormAddMember() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this, R.style.AlertDialog);
        builder.setTitle("Choose members name: ");

        final ListView ListViewMembers = new ListView(GroupChatActivity.this);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_members);
        ListViewMembers.setAdapter(arrayAdapter);
        ListViewMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewMembers.getChildAt(position).setEnabled(false);
                list_of_members_choosed.add(list_of_members_id.get(position));
            }
        });

        builder.setView(ListViewMembers);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (list_of_members_choosed.size()==0){
                    Toast.makeText(GroupChatActivity.this, "Please choose member", Toast.LENGTH_SHORT).show();
                } else {
                    AddToGroup(list_of_members_choosed);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void AddToGroup(ArrayList<String> list_of_members_choosed) {
        for (int i=0; i<list_of_members_choosed.size(); i++){
            groupIdRef.child("users").child(list_of_members_choosed.get(i)).child("id").setValue(list_of_members_choosed.get(i));
        }
    }

    private void DisplayMessages(DataSnapshot usersSnapshot) {
        mGroupMessages = new ArrayList<>();
        mImgs = new ArrayList<>();

        groupIdRef.child("groupMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mGroupMessages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GroupMessage groupMessage = dataSnapshot.getValue(GroupMessage.class);
                    mGroupMessages.add(groupMessage);

                    for (DataSnapshot userSnapshot : usersSnapshot.getChildren()){
                        User user = userSnapshot.getValue(User.class);
                        if (user.getUsername().equals(groupMessage.getSender())){
                            mImgs.add(user.getImageURL());
                            break;
                        }
                    }
                }

                groupAdapter = new GroupAdapter(GroupChatActivity.this, mGroupMessages, mImgs, currentUserName);
                displayTextMessages.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    currentUserName = user.getUsername();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveMessageGroupInfo() {
        String message = text_send.getText().toString();
        String messageKey = groupIdRef.child("groupMessages").push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please enter message!", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupIdRef.child("groupMessages").updateChildren(groupMessageKey);

            groupMessageKeyRef = groupIdRef.child("groupMessages").child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("sender", currentUserName);
            messageInfoMap.put("message", message);
            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }
}