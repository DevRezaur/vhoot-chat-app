package dev.rezaur.vhoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private EditText edt_full_name;
    private EditText edt_email;
    private EditText edt_password;
    private EditText edt_confirm_password;
    private Button btn_signup;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edt_full_name = findViewById(R.id.edt_full_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);
        btn_signup = findViewById(R.id.btn_signup);

        auth = FirebaseAuth.getInstance();
    }

    public void doSignup(View view) {
        String fullName = edt_full_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String confirmPassword = edt_confirm_password.getText().toString().trim();

        if(fullName.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Fill all the information", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
        } else if(password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password length must be greater than 6", Toast.LENGTH_SHORT).show();
        } else {
            btn_signup.setEnabled(false);
            register(fullName, email, password);
        }
    }

    private void register(final String fullName, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("id", userid);
                            map.put("fullName", fullName);
                            map.put("imageURL", "default");
                            map.put("status", "offline");
                            map.put("search", fullName.toLowerCase());

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(SignUp.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            btn_signup.setEnabled(true);
                            edt_email.setError("!");
                            Toast.makeText(getApplicationContext(), "This email is already used", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}