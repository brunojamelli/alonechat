package com.softwares.jamelli.alone_chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.FragmentPagerAdapter;

import com.softwares.jamelli.alone_chat.fragment.FragmentCamera;
import com.softwares.jamelli.alone_chat.fragment.FragmentChat;
import com.softwares.jamelli.alone_chat.fragment.FragmentMaps;

public class ChatAdapter extends FragmentPagerAdapter {

    public ChatAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentCamera();
            case 1:
                return new FragmentChat();
            case 2:
                return new FragmentMaps();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Camera";
            case 1:
                return "Chat";
            case 2:
                return "Maps";
            default:
                return null;
        }
    }
}
