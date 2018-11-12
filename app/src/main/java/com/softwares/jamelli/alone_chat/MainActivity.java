package com.softwares.jamelli.alone_chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager vp;
    PagerAdapter pa;
    private final int PERMISSION_REQUEST = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askingPermissions();
        initTabLayout();
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        //tabLayout.getTabAt(0);
                    case 1:
                        //tabLayout.getTabAt(1).setIcon(R.drawable.chat);
                    case 2:
                        //tabLayout.getTabAt(2).setIcon(R.drawable.maps);
                    default:
                        return;
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:{
//logout
                AuthUI.getInstance().signOut(this);
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void initTabLayout(){
        vp = findViewById(R.id.pager);
        pa = new ChatAdapter(getSupportFragmentManager());
        vp.setAdapter(pa);
        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(vp);
        tabLayout.getTabAt(0).setIcon(R.drawable.camera2);
        vp.setCurrentItem(1);
    }

    public void askingPermissions(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            }
        }
    }
}