package com.codecamp.chatapptemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codecamp.chatapptemplate.fragments.ChatsFragment;
import com.codesgood.views.JustifiedTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth Auth ;
    private EditText EmailView,passwordView;
    private RelativeLayout loginButton,parentLayout;
    private JustifiedTextView signUpTextView,signUpTextView_student;
    private FirebaseFirestore fStore;
    public static String type;
    Bundle bundle ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Auth = FirebaseAuth.getInstance();
        EmailView = findViewById(R.id.emailEditTextID);
        passwordView = findViewById(R.id.passwordEditTextID);
        loginButton = findViewById(R.id.loginButtonID);
        parentLayout = findViewById(R.id.loginRelativeLayoutID);
        signUpTextView = findViewById(R.id.signupTextID);
        signUpTextView_student = findViewById(R.id.signupTextID_Student);
        fStore = FirebaseFirestore.getInstance();
        bundle = new Bundle();
        signUpTextView_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                 startActivity(new Intent(LoginActivity.this,SignUpStudentActivity.class));
                //  finish();
            }
        });
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                //  finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                attemptLogin();

            }
        });
    }
    private void attemptLogin() {


        String email = EmailView.getText().toString();
        String password = passwordView.getText().toString();
        if (email.equals("") || password.equals("")){
            Toast.makeText(this,"fields empty ..",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,LoginActivity.class));
        }else{
            Toast.makeText(this,"login in progress ..",Toast.LENGTH_SHORT).show();
        }

        // TODO: Use FirebaseAuth to sign in with email & password
        Auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FlashChat", "onComplete:signin  " + task.isSuccessful());

                if (task.isSuccessful()){
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    CollectionReference df = fStore.collection("Users");
                    showErrorDialog("log in is succesful");
                    DocumentReference docref= df.document(user1.getUid());
                    docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    Intent intent = new Intent(LoginActivity.this, ChatsFragment.class);
                                    intent.putExtra("Type",document.get("type").toString());
                                    type=document.get("type").toString();
                                    Toast.makeText(LoginActivity.this, type, Toast.LENGTH_SHORT).show();
                                    docref.update("isActive","true");
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                }
                            }else{
                                showErrorDialog("verification failed");

                            }

                        }
                    });
                }
                else{
                    showErrorDialog("your email or password incorrect" +
                            "");
                }

            }
        });
            }
    private  void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Message :")
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