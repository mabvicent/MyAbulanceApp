package com.example.ambulancelocator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AmbulanceLoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_login);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(AmbulanceLoginActivity.this, AmbulanceMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);
        mRegistration = (Button) findViewById(R.id.registration);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(AmbulanceLoginActivity.this, "fill all fields", Toast.LENGTH_SHORT).show();
                }else {
                    final ProgressDialog waitingdialog = new ProgressDialog(AmbulanceLoginActivity.this);
                    waitingdialog.setTitle("CREATING ACCOUNT");
                    waitingdialog.setMessage("please wait.....");
                    waitingdialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(AmbulanceLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                waitingdialog.dismiss();
                                Toast.makeText(AmbulanceLoginActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                            } else {
                                waitingdialog.dismiss();
                                Toast.makeText(AmbulanceLoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Ambulances").child(user_id).child("name");
                                current_user_db.setValue(email);
                            }
                        }
                    });
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(AmbulanceLoginActivity.this, "fill all fields", Toast.LENGTH_SHORT).show();
                }else {

                    final ProgressDialog waitingdialog = new ProgressDialog(AmbulanceLoginActivity.this);
                    waitingdialog.setTitle("SIGNING INN");
                    waitingdialog.setMessage("please wait.....");
                    waitingdialog.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(AmbulanceLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                waitingdialog.dismiss();
                                Toast.makeText(AmbulanceLoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                            } else {
                                waitingdialog.dismiss();
                                Intent intent = new Intent(AmbulanceLoginActivity.this, AmbulanceMapActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
