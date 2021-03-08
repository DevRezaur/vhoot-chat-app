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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText edt_email;
    private Button btn_reset_password;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edt_email = findViewById(R.id.edt_email);
        btn_reset_password = findViewById(R.id.btn_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void resetPassword(View view) {
        String email = edt_email.getText().toString().trim();

        if(email.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a email address", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPassword.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}