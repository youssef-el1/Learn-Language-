package com.codecamp.chatapptemplate;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    Button changeButton;
    EditText currentPassword,newPassword,ConfirmPassword;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.newpassword);
        ConfirmPassword = findViewById(R.id.confirmpassword);
        auth = FirebaseAuth.getInstance();
        changeButton = findViewById(R.id.change_button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }
    public void changePassword(){
        if((!currentPassword.getText().toString().isEmpty())&&
                (!newPassword.getText().toString().isEmpty()) &&
                (!ConfirmPassword.getText().toString().isEmpty())){
            if(newPassword.getText().toString().equals(ConfirmPassword.getText().toString())){
                FirebaseUser user = auth.getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), currentPassword.getText().toString());


                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete( Task<Void> task) {
                                if(task.isSuccessful()) {
                                    showmessah("reauthentifcation completed");
                                    user.updatePassword(newPassword.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showmessah("Password changed");
//                                                        auth.signOut();
//                                                        startActivity(new Intent(ChangePassword.this,LoginActivity.class));
//                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        }
    }
    public void showmessah(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    }
