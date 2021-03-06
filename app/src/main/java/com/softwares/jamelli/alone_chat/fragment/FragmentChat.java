package com.softwares.jamelli.alone_chat.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softwares.jamelli.alone_chat.MessageAdapterN;
import com.softwares.jamelli.alone_chat.R;
import com.softwares.jamelli.alone_chat.model.FriendlyMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.softwares.jamelli.alone_chat.util.ToolBox;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentChat extends Fragment{
    //constantes de configuração do fragment de mensagens
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int LOGIN_CODE = 56;
    private static final int PICTURE_CODE = 57;
    //objetos do firebase para manipulação de arquivos e acesso ao realtime database
    private FirebaseDatabase fdatabase;
    private DatabaseReference dataref;
    private ChildEventListener listener;
    private FirebaseStorage fstorage;
    private StorageReference storageref;
    //objetos da view
    private MessageAdapterN adapter;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private RecyclerView rv;
    //objetos usados para a implementação do login
    private FirebaseAuth fauth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private List<FriendlyMessage> friendlyMessages;
    private String mUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b){
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        FacebookSdk.sdkInitialize(getContext());
        AppEventsLogger.activateApp(getContext());
        initDBandStorage();
        mUsername = ANONYMOUS;
        //implementando o listener do firebase auth
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                //logado
                    onSignInInitialize(user.getDisplayName());
                }else{
                    //não-logado
                    onSignOutCleanUp();

                    //chama o fluxo de login
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.TwitterBuilder().build(),
                            new AuthUI.IdpConfig.FacebookBuilder().setPermissions(Arrays.asList("user_friends")).build());

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            LOGIN_CODE);
                }
            }
        };
        initViewObjects(v);
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
        //listener do botão que envia uma mensagem de texto para o firebase database
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentDate = ToolBox.currentDate();
                FriendlyMessage fm = new FriendlyMessage(currentDate,mMessageEditText.getText().toString(),mUsername,null);
                dataref.push().setValue(fm);
                // Clear input box
                mMessageEditText.setText("");
            }
        });
        //listener do botão que envia uma imagem da galeria para o firebase storage
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICTURE_CODE);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fauth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (authStateListener != null) {
            fauth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
        friendlyMessages.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE){
            if (resultCode == RESULT_OK){
                Toast.makeText(getContext(), "Bem-vindo", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED){
                //finish();

            }
        }else if (requestCode == PICTURE_CODE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference photoref = storageref.child(mUsername + "_" + selectedImageUri.getLastPathSegment());
            //para upload da imagem basta photoref.putFile(selectedImageUri);
            //addOnSuccessListener para saber quando a imagem foi enviada
            photoref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            FriendlyMessage friendlyMessage = new FriendlyMessage(
                                    ToolBox.currentDate(),
                                    null, mUsername,
                                    uri.toString()
                            );
                            dataref.push().setValue(friendlyMessage);
                        }
                    });
                }
            });
        }
    }

    private void attachDatabaseReadListener(){
        if (listener == null) {
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    friendlyMessages.add(friendlyMessage);
                    adapter = new MessageAdapterN(getContext(),friendlyMessages);
                    rv.setAdapter(adapter);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            dataref.addChildEventListener(listener);
        }
    }

    private void detachDatabaseReadListener(){
        if(listener != null) {
            dataref.removeEventListener(listener);
            listener = null;
        }
    }

    private void onSignInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }
    private void onSignOutCleanUp() {
        mUsername = ANONYMOUS;
        friendlyMessages.clear();
        detachDatabaseReadListener();
    }
    public void initDBandStorage(){
        //instanciando os objetos de acesso ao banco
        fdatabase = FirebaseDatabase.getInstance();
        dataref = fdatabase.getReference().child("messages");
        fauth = FirebaseAuth.getInstance();
        //instanciando os objetos para gerenciar arquivos no firebase
        fstorage = FirebaseStorage.getInstance();
        storageref = fstorage.getReference().child("photos_app_jamelli");
    }

    public void initViewObjects(View v){
        // Initialize references to views
        rv = v.findViewById(R.id.screenMessages);
        mPhotoPickerButton = v.findViewById(R.id.photoPickerButton);
        mMessageEditText =  v.findViewById(R.id.messageEditText);
        mSendButton = v.findViewById(R.id.sendButton);
        //Inicializando RecyclerView e o seu adapter
        friendlyMessages = new ArrayList<>();
        adapter = new MessageAdapterN(getContext(),friendlyMessages);
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ((LinearLayoutManager) layout).setStackFromEnd(true);
        ((LinearLayoutManager) layout).setReverseLayout(false);
        rv.setLayoutManager(layout);
    }
}
