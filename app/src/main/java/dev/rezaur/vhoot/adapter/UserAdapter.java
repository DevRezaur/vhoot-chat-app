package dev.rezaur.vhoot.adapter;

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

import java.util.List;

import dev.rezaur.vhoot.MessageActivity;
import dev.rezaur.vhoot.R;
import dev.rezaur.vhoot.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isOnline;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isOnline) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isOnline = isOnline;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getFullName());
        if(user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.logo);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if(isOnline) {
            if(user.getStatus().equals("online")) {
                holder.img_online.setVisibility(View.VISIBLE);
                holder.img_offline.setVisibility(View.GONE);
            } else {
                holder.img_online.setVisibility(View.GONE);
                holder.img_offline.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_online.setVisibility(View.GONE);
            holder.img_offline.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public ImageView img_online;
        public ImageView img_offline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            profile_image = itemView.findViewById(R.id.iv_profile_image);
            img_online = itemView.findViewById(R.id.img_online);
            img_offline = itemView.findViewById(R.id.img_offline);
        }
    }
}
