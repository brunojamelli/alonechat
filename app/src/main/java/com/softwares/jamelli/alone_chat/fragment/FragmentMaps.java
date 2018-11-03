package com.softwares.jamelli.alone_chat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softwares.jamelli.alone_chat.R;

public class FragmentMaps extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b){
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        //conteudo do fragment
        return v;
    }
}
