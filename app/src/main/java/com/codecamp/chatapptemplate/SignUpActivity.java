package com.codecamp.chatapptemplate;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codecamp.chatapptemplate.model.Parents;
import com.codesgood.views.JustifiedTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameEditText,emailEditText,passwordEditText;
    private RelativeLayout signupButton,parentLayout;
    private JustifiedTextView loginTextView;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.nameEditTextID);
        emailEditText = findViewById(R.id.emailEditTextID);
        passwordEditText = findViewById(R.id.passwordEditTextID);
        signupButton = findViewById(R.id.signupButtonID);
        parentLayout = findViewById(R.id.signupRelativeLayoutID);
        loginTextView = findViewById(R.id.loginTextID);

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                SignUp(v);
            }
        });
    }
    public void SignUp(View v) {
        attemptRegistration();

    }
    private void attemptRegistration() {

        // Reset errors displayed in the form.
        emailEditText.setError(null);
        passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
            progressDialog.dismiss();
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
            progressDialog.dismiss();
        } else if (!isEmailValid(email)) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
            progressDialog.dismiss();
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            progressDialog.dismiss();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();
        }
    }
    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        String confirmPassword = passwordEditText.getText().toString();
        return confirmPassword.equals(password) && password.length() > 5;
    }
    private void createFirebaseUser(){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FlashChat", "CreateUser onComplete: " + task.isSuccessful());
                if (!task.isSuccessful()){
                    progressDialog.dismiss();
                    Log.d("FlashChat", "CreateUser Failed ");
                    ShowErrorDialog("rejestration attmpt failed");
                }else{
                    DatabaseReference reference=database.getReference().child("user").child(mAuth.getUid());
                    StorageReference storageReference=storage.getReference().child("uplod").child(mAuth.getUid());
                    saveDisplayNmae();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(SignUpActivity.this, "Registration Completed", Toast.LENGTH_SHORT).show();
                    DocumentReference df = fStore.collection("Users").document(user.getUid());
                    Map<String, Object> userinfo = new HashMap<>();
                    userinfo.put("fullName", nameEditText.getText().toString());
                    userinfo.put("email", emailEditText.getText().toString());
                    userinfo.put("imageURL","default");
                    userinfo.put("type","isTeacher");
                    userinfo.put("isActive","false");
                    userinfo.put("id",user.getUid());


                    df.set(userinfo);
                    imageURI="https://firebasestorage.googleapis.com/v0/b/quickchatapp-a181b.appspot.com/o/profile_image.png?alt=media&token=a6748395-5c27-492d-b73b-d6859d4b5d15";
                    Parents users =new Parents(emailEditText.getText().toString(),nameEditText.getText().toString(),mAuth.getUid(),imageURI);
                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                            }else {
                                Toast.makeText(SignUpActivity.this, "Error in Creating a New user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
            }
    private  void saveDisplayNmae(){
        String displayNmae =nameEditText.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,0);
        prefs.edit().putString(DISPLAY_NAME_KEY,displayNmae).apply();
    }

    // TODO: Create an alert dialog to show in case registration failed
    private void ShowErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        parentLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.signup_anim));
    }
}