package dev.rezaur.vhoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.rezaur.vhoot.adapter.MessageAdapter;
import dev.rezaur.vhoot.fragment.APIService;
import dev.rezaur.vhoot.model.Chat;
import dev.rezaur.vhoot.model.User;
import dev.rezaur.vhoot.notification.Client;
import dev.rezaur.vhoot.notification.Data;
import dev.rezaur.vhoot.notification.Sender;
import dev.rezaur.vhoot.notification.Token;
import dev.rezaur.vhoot.notification.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private Intent intent;
    private ImageButton btn_send;
    private EditText edt_message;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userid;

    MessageAdapter messageAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;
    private APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, HomeActivity.class));
                //finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.iv_profile_image);
        username = findViewById(R.id.txt_full_name);
        btn_send = findViewById(R.id.btn_send);
        edt_message = findViewById(R.id.edt_message);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getFullName());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.logo);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                readMessage(firebaseUser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadMessage(String sender, final String receiver, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);

        databaseReference.child("Chats").push().setValue(map);

        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    chatReference.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(firebaseUser.getUid());
        chatReceiver.child("id").setValue(firebaseUser.getUid());

        final String msg = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify) {
                    sendNotification(receiver, user.getFullName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String sender, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ss : snapshot.getChildren()) {
                    Token token = ss.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.logo, "", "New Message", userid);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<UserResponse>() {
                                @Override
                                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                    if(response.code() == 200) {
                                        if(response.body().success != 1) {
                                            Toast.makeText(getApplicationContext(), "Failed..", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendMessage(View view) {
        notify = true;
        String message = edt_message.getText().toString().trim();
        if(!message.equals("")) {
            uploadMessage(firebaseUser.getUid(), userid, message);
        }
        edt_message.setText("");
    }

    private void readMessage(final String myid, final String userid, final String imageUrl) {
        mChats = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for(DataSnapshot ss : snapshot.getChildren()) {
                    Chat chat = ss.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                        || chat.getReceiver().equals(userid) && chat.getSender().equals(myid))
                    {
                        mChats.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        databaseReference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}