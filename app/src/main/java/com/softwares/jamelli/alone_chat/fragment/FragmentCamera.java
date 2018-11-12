package com.softwares.jamelli.alone_chat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softwares.jamelli.alone_chat.R;
import com.softwares.jamelli.alone_chat.model.FriendlyMessage;
import com.softwares.jamelli.alone_chat.util.ToolBox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.media.MediaRecorder.VideoSource.CAMERA;

public class FragmentCamera extends Fragment {
    private final int CODE_CAMERA = 55;
    private Button btn_camera;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseDatabase fdata;
    private DatabaseReference freference;
    private FirebaseAuth fauth;
    private String userName;
    private File arquivoFoto = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b){
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("photos_app_jamelli");
        fdata = FirebaseDatabase.getInstance();
        freference = fdata.getReference().child("messages");
        //pegando a instancia do auth para pegar o usuario
        fauth = FirebaseAuth.getInstance();

        btn_camera = v.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        return v;
    }
    private void takePicture() {
        FirebaseUser user = fauth.getCurrentUser();
        userName = user.getDisplayName();
        /*Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentTakePicture.resolveActivity(getActivity().getPackageManager())!=null){
            startActivityForResult(intentTakePicture,CODE_CAMERA);
        }*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                arquivoFoto = createFile();
            } catch (IOException ex) {
                // Manipulação em caso de falha de criação do arquivo
            }
            if (arquivoFoto != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() +
                                ".provider", arquivoFoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CODE_CAMERA);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_CAMERA && resultCode == RESULT_OK){
            getActivity().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(arquivoFoto))
            );
            Uri image = Uri.fromFile(arquivoFoto);
            Log.i("foto",image.toString());
            //Log.i("uri",selectedImageUri.toString());
            StorageReference photoref = mStorageReference.child(userName + "_" + ToolBox.currentDate());
            //para upload da imagem basta photoref.putFile(selectedImageUri);
            //addOnSuccessListener para saber quando a imagem foi enviada
            photoref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            FriendlyMessage friendlyMessage = new FriendlyMessage(
                                    ToolBox.currentDate(),
                                    null, userName,
                                    uri.toString()
                            );
                            freference.push().setValue(friendlyMessage);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onResume() {
        //takePicture();
        super.onResume();
    }

    @Override
    public void onPause() {
        //takePicture();
        super.onPause();
    }

    private File createFile() throws IOException {
        String timeStamp = new
                SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pasta = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imagem = new File(pasta.getPath() + File.separator
                + "JPG_" + timeStamp + ".jpg");
        return imagem;
    }
    //public void permissions
}
