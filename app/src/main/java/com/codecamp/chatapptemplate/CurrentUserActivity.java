package com.codecamp.chatapptemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.codecamp.chatapptemplate.model.Parents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentUserActivity extends AppCompatActivity {
    private ImageView arrowBackButton,save2;
    public static CircleImageView setting_image2;
    EditText setting_name2, setting_email;
    public static final int CAMERA_CODE = 200;
    public static final int GALLERY_CODE = 100;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selctedImageUri= null;
    public static String image;
    String email;
    DatabaseReference reference;
    FirebaseUser user;
    ProgressDialog progressDialog;
    Button change_password_teacher,logout_btn;
    Uri imageUri = null;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user);
        arrowBackButton = findViewById(R.id.arrowBackButtonID);
        setting_image2 =findViewById(R.id.setting_image2);
        setting_email =findViewById(R.id.setting_email);
        setting_name2 =findViewById(R.id.setting_name2);
        logout_btn =findViewById(R.id.logout);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        change_password_teacher =findViewById(R.id.change_password_teacher);
        save2 = findViewById(R.id.save2);
        DocumentReference df = fStore.collection("Users").document(auth.getUid());
        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("uplod").child(auth.getUid());
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                df.update("isActive","false");
                auth.signOut();
                startActivity(new Intent(CurrentUserActivity.this,LoginActivity.class));
                finish();
            }
        });
        arrowBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowBackButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                startActivity(new Intent(CurrentUserActivity.this,MainActivity.class));
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);



//

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("p_email").getValue().toString();
                String fullname = snapshot.child("p_fullName").getValue().toString();
                image = snapshot.child("p_imageURL").getValue().toString();
                df.update("imageURL",image);
                setting_name2.setText(fullname);
                setting_email.setText(email);
                Picasso.get().load(image).into(setting_image2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String fullname = setting_name2.getText().toString();
                String email = setting_email.getText().toString();
//                uploadImage();
                if (selctedImageUri != null)
                {
                    storageReference.putFile(selctedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(CurrentUserActivity.this, "youssef", Toast.LENGTH_SHORT).show();
                                    String finalImageUri=uri.toString();
                                    Parents users=new Parents(email,fullname,auth.getUid(),finalImageUri);
                                    Toast.makeText(CurrentUserActivity.this, "youssef", Toast.LENGTH_SHORT).show();
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(CurrentUserActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
//                                                startActivity(new Intent(CurrentUserActivity.this,Home_Teacher.class));
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(CurrentUserActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri=uri.toString();
                            Parents users=new Parents(email,fullname,auth.getUid(),finalImageUri);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(CurrentUserActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(CurrentUserActivity.this,Home_Teacher.class));
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(CurrentUserActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
                setting_image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();

                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
//                        SelectImage();
                    }
                });
        change_password_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentUserActivity.this,ChangePassword.class);
                startActivity(intent);
            }
        });

//
//
            }
            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10)
        {
            if(data!=null)
            {
                selctedImageUri =data.getData();
                setting_image2.setImageURI(selctedImageUri);
            }
        }
//

    }
}