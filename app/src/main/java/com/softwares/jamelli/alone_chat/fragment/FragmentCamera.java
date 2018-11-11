package com.softwares.jamelli.alone_chat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softwares.jamelli.alone_chat.R;

import static android.app.Activity.RESULT_OK;

public class FragmentCamera extends Fragment {
    private final int CODE_CAMERA = 66;
    private Button btn_camera;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b){
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        //conteudo do fragment
        //takePicture();
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
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentTakePicture.resolveActivity(getActivity().getPackageManager())!=null){
            startActivityForResult(intentTakePicture,CODE_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_CAMERA && resultCode == RESULT_OK){
            Bundle bdata = data.getExtras();
            Bitmap imB = (Bitmap) bdata.get("data");
            Log.i("foto",imB.toString());
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
}
