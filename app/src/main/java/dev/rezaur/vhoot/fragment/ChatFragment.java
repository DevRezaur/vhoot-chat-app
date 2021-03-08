package dev.rezaur.vhoot.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.rezaur.vhoot.R;
import dev.rezaur.vhoot.adapter.UserAdapter;
import dev.rezaur.vhoot.model.Chat;
import dev.rezaur.vhoot.model.ChatList;
import dev.rezaur.vhoot.model.User;
import dev.rezaur.vhoot.notification.Token;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private List<ChatList> userList;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // -> databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userList.clear();
//                for(DataSnapshot ss : snapshot.getChildren()) {
//                    Chat chat = ss.getValue(Chat.class);
//                    assert chat != null;
//                    if(chat.getSender().equals(firebaseUser.getUid())) {
//                        userList.add(chat.getReceiver());
//                    }
//                    if(chat.getReceiver().equals(firebaseUser.getUid())) {
//                        userList.add(chat.getSender());
//                    }
//                }
//
//                Set<String> hashSet = new HashSet<>(userList);
//                userList.clear();
//                userList.addAll(hashSet);
//
//                readChats();
////                chatList();
//            }

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatlist = snapshot.getValue(ChatList.class);
                    userList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }

//    private void readChats() {
//        mUsers = new ArrayList<>();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();
//                for(DataSnapshot ss : snapshot.getChildren()) {
//                    User user = ss.getValue(User.class);
//                    for(String id : userList) {
//                        if(user.getId().equals(id)) {
////                            if(mUsers.size() != 0) {
////                                for(User user1 : mUsers) {
////                                    if(!user.getId().equals(user1.getId())) {
////                                        mUsers.add(user);
////                                    }
////                                }
////                            } else {
////                                mUsers.add(user);
////                            }
//                            mUsers.add(user);
//                        }
//                    }
//                }
//
//                userAdapter = new UserAdapter(getContext(), mUsers, true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void chatList() {
        mUsers = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatlist : userList){
                        if (user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}