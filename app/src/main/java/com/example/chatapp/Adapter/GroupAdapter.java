package com.example.chatapp.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MesageActivity;
import com.example.chatapp.Model.Group;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.List;
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Group> mGroup;
    private List<String> mImg;
    private String currentUsername;

    FirebaseUser fuser;

    public GroupAdapter(Context mContext, List<Group> mGroup, List<String> mImg, String currentUsername){
        this.mContext=mContext;
        this.mGroup=mGroup;
        this.mImg=mImg;
        this.currentUsername=currentUsername;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_item_right, parent, false);
            return new GroupAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_item_left, parent, false);
            return new GroupAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        Group group = mGroup.get(position);
        String img = mImg.get(position);

        holder.show_message.setText(group.getMessage());
        holder.show_username.setText(group.getSender());

        if(img.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(img).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView show_username;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.group_chat_user_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_username = itemView.findViewById(R.id.username_in_group);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mGroup.get(position).getSender().equals(currentUsername)){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}