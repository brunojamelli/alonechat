package com.softwares.jamelli.alone_chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softwares.jamelli.alone_chat.MessageAdapter;
import com.softwares.jamelli.alone_chat.R;
import com.softwares.jamelli.alone_chat.model.FriendlyMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentChat extends Fragment{
    private FirebaseDatabase fb;
    private DatabaseReference msg;
    private ChildEventListener listener;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b){
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        //conteudo do fragment
        fb = FirebaseDatabase.getInstance();
        msg = fb.getReference().child("messages");
        //pegando o bundle
        Bundle data = getArguments();

        mUsername = ANONYMOUS;
        mUsername = data.getString("user");
        //implementando o lister do banco de dados
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FriendlyMessage fm = dataSnapshot.getValue(FriendlyMessage.class);
                mMessageAdapter.add(fm);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        msg.addChildEventListener(listener);
        // Initialize references to views
        mMessageListView =  v.findViewById(R.id.messageListView);
        mPhotoPickerButton = v.findViewById(R.id.photoPickerButton);
        mMessageEditText =  v.findViewById(R.id.messageEditText);
        mSendButton = v.findViewById(R.id.sendButton);
        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(getContext(), R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date data = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(data);
                Date data_atual = cal.getTime();

                FriendlyMessage fm = new FriendlyMessage(data_atual,mMessageEditText.getText().toString(),mUsername,null);
                msg.push().setValue(fm);
                // Clear input box
                mMessageEditText.setText("");
            }
        });
        return v;
    }
}
