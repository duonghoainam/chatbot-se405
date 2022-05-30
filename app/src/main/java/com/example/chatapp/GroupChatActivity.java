package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
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
    private ImageButton btn_send;
    private EditText text_send;
    private RecyclerView displayTextMessages;
    private DatabaseReference userRef, groupIdRef, groupMessageKeyRef;

    GroupAdapter groupAdapter;
    List<GroupMessage> mGroupMessages;
    List<String> mImgs;
    List<Chatlist> mUsers;

    private FirebaseAuth mAuth;
    private String currentGroupName, currentGroupId, currentUserID, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        currentGroupId = getIntent().getExtras().get("groupId").toString();
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
                DisplayMessages(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot usersSnapshot) {
        mGroupMessages = new ArrayList<>();
        mUsers = new ArrayList<>();
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