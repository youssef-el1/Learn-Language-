package com.codecamp.chatapptemplate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codecamp.chatapptemplate.LoginActivity;
import com.codecamp.chatapptemplate.R;
import com.codecamp.chatapptemplate.SignUpActivity;
import com.codecamp.chatapptemplate.adapter.ActiveChatAdapter;
import com.codecamp.chatapptemplate.adapter.ChatAdapter;
import com.codecamp.chatapptemplate.model.ActiveChat;
import com.codecamp.chatapptemplate.model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.codecamp.chatapptemplate.LoginActivity.*;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {
    private RecyclerView chatRecyclerView,activeChatRecyclerView;
    private ArrayList<Chat> chatList_s,chatList_t;
    private ChatAdapter chatAdapter_s,chatAdapter_t;
    private ArrayList<ActiveChat> activeChatList,activeChatList_s;
    private ActiveChatAdapter activeChatAdapter,activeChatAdapter_s;
    private RelativeLayout createRoomButton;
    private String ReceiverImage;

    private FirebaseAuth Auth ;
    private FirebaseFirestore fStore;
    private String uid;
    private  String Type;
    private String idcoll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats_layout,container,false);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerViewID);
        activeChatRecyclerView = view.findViewById(R.id.activeChatRecyclerViewID);

        chatList_s = new ArrayList<>();
        chatAdapter_s = new ChatAdapter(getContext(),chatList_s);
        chatList_t = new ArrayList<>();
        chatAdapter_t = new ChatAdapter(getContext(),chatList_t);
        fStore = FirebaseFirestore.getInstance();

//        createRoomButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createRoomButton.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
//                Toast.makeText(getContext(),"edit profile",Toast.LENGTH_SHORT).show();
//            }
//        });
        CollectionReference docref = fStore.collection("Users");
        docref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (queryDocumentSnapshot.get("type").toString().equals("isTeacher")) {
                            chatList_t.add(new Chat(queryDocumentSnapshot.get("imageURL").toString(), queryDocumentSnapshot.get("fullName").toString(),
                                    "hey " + queryDocumentSnapshot.get("fullName").toString() + " whats going on",
                                    queryDocumentSnapshot.get("id").toString()));
                        } else {
                            chatList_s.add(new Chat(queryDocumentSnapshot.get("imageURL").toString(), queryDocumentSnapshot.get("fullName").toString(),
                                    "hey " + queryDocumentSnapshot.get("fullName").toString() + " whats going on",
                                    queryDocumentSnapshot.get("id").toString()));
                        }
                    }
                    if(LoginActivity.type.equals("isTeacher")) {
                        chatList_t.clear();
                        chatRecyclerView.setHasFixedSize(true);
                        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        chatRecyclerView.setAdapter(chatAdapter_s);
                        createActiveChat();
                    }
//                    }
                    else if (LoginActivity.type.equals("isStudent")){
                        chatList_s.clear();
                        chatRecyclerView.setHasFixedSize(true);
                        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        chatRecyclerView.setAdapter(chatAdapter_t);
                        createActiveChat();
                    }
                }
                else{
                    Log.d("hkkn","error",task.getException());

                }

            }
        });
        return view;
    }
 // a faire ulterieurement avec un champs IsActif in the database firestore -----------------------------------------------------
    private void createActiveChat()
    {
        activeChatList = new ArrayList<>();
        activeChatAdapter = new ActiveChatAdapter(getContext(),activeChatList);
        CollectionReference docref = fStore.collection("Users");
        docref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (queryDocumentSnapshot.get("isActive").toString().equals("true") ) {
                            activeChatList.add(new ActiveChat(queryDocumentSnapshot.get("imageURL").toString()));
                        }
                    }
//                    activeChatList.clear();
                    activeChatRecyclerView.setHasFixedSize(true);
                    activeChatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                    activeChatRecyclerView.setAdapter(activeChatAdapter);
                }
                else{
                    Log.d("hkkn","error",task.getException());

                }

            }
        });
    }
}
