package com.codecamp.chatapptemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codecamp.chatapptemplate.adapter.MessageAdapter;
import com.codecamp.chatapptemplate.model.Chat;
import com.codecamp.chatapptemplate.model.Messages;
import com.codesgood.views.JustifiedTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPageActivity extends AppCompatActivity {
    private ImageView arrowBackButton,cameraIcon,photoIcon,likeIcon,dictionaryIcon;
    private JustifiedTextView userNameTextView;
    private CircleImageView messageReceiverImage;
    private String userName;
    private String userImage;
    private int requestCode = 1;

    String mFileName = null;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    String ReciverImage, ReciverUID, ReciverName, SenderUID;
    RecyclerView messageAdater;
    ArrayList<Messages> messagesArrayList;
    MessageAdapter adater;
    EditText edtMessage;
    String senderRoom, reciverRoom;
    public static String p_sImage;
    public static String p_rImage;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        arrowBackButton = findViewById(R.id.arrowBackButtonID);
        messageReceiverImage = findViewById(R.id.messageImageViewID);
        userNameTextView = findViewById(R.id.chatsTextID);
        cameraIcon = findViewById(R.id.cameraIconID);
        dictionaryIcon = findViewById(R.id.dictionaryIconID);

        userName = getIntent().getStringExtra("userName");
        userImage = getIntent().getStringExtra("userImage");
        userNameTextView.setText(userName);
        //userImageView.setImageURI(Uri.parse(userImage));
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName = "/recording audio .3gp";
//
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ReciverUID = getIntent().getStringExtra("P_id");
//        ReciverUID = "peifuhgebfgpajer";
//
        messagesArrayList = new ArrayList<Messages>();

        messageAdater = findViewById(R.id.messageAdater);

        messagesArrayList = new ArrayList<Messages>();
        adater = new MessageAdapter(ChatPageActivity.this, messagesArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdater.setLayoutManager(linearLayoutManager);
        messageAdater.setAdapter(adater);
//
        likeIcon = findViewById(R.id.likeIconID);
        edtMessage = findViewById(R.id.messageEdittextID);
        Picasso.get().load(userImage).into(messageReceiverImage);
        SenderUID = firebaseAuth.getUid();
        senderRoom = SenderUID + ReciverUID;
        reciverRoom = ReciverUID + SenderUID;
        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatRefrece = database.getReference().child("chats").child(senderRoom).child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                p_sImage = snapshot.child("p_imageURL").getValue().toString();
                p_rImage = userImage;

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        chatRefrece.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatPageActivity.this, "Please enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                edtMessage.setText("");
                Date date = new Date();
                Messages messages = new Messages(message, SenderUID, date.getTime());
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(reciverRoom)
                                .child("messages")
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });
        dictionaryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dictionaryIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                startActivity(new Intent(ChatPageActivity.this,DictionaryActivity.class));
            }
        });
//        setOnIconClick();
        messageReceiverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageReceiverImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                    Intent intent = new Intent(ChatPageActivity.this,MessageReceiverActivity.class);
//                    intent.putExtra("receiverName",ReciverName);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
            }
        });

        arrowBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowBackButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                startActivity(new Intent(ChatPageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                finish();
            }
        });

    }

    private void setOnIconClick()
    {

//
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
            }
        });
//
//

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
            }
        });
    }


}