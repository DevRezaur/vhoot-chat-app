package dev.rezaur.vhoot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import dev.rezaur.vhoot.R;
import dev.rezaur.vhoot.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChats;
    private String imageUrl;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChats, String imageUrl) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.txt_message.setText(chat.getMessage());
        if(imageUrl.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.logo);
        } else {
            Glide.with(mContext).load(imageUrl).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_message;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_message = itemView.findViewById(R.id.txt_message);
            profile_image = itemView.findViewById(R.id.iv_profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return  MSG_TYPE_LEFT;
        }
    }
}
