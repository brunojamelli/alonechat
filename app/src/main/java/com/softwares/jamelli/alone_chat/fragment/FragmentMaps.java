package com.softwares.jamelli.alone_chat.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softwares.jamelli.alone_chat.R;
import com.softwares.jamelli.alone_chat.model.FriendlyMessage;
import com.softwares.jamelli.alone_chat.util.ToolBox;

import java.util.Calendar;
import java.util.Date;

public class FragmentMaps extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;
    private final int CODE_LOCATION = 58;
    private FusedLocationProviderClient locationClient;
    private Button btnSend;
    private Location myLocation;
    private ChildEventListener listenerDB;
    private FirebaseDatabase fdata;
    private DatabaseReference freference;
    private FirebaseAuth fauth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = inflater.inflate(R.layout.fragment_maps, null, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //pegando a instancia do firebase e da "tabela" de mensagens
        fdata = FirebaseDatabase.getInstance();
        freference = fdata.getReference().child("messages");
        btnSend = view.findViewById(R.id.btn_enviar);
        //pegando a instancia do auth para pegar o usuario
        fauth = FirebaseAuth.getInstance();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = fauth.getCurrentUser();
                Date hoje = ToolBox.currentDate();
                String urlMaps = "https://www.google.com.br/maps/@"+myLocation.getLatitude()+","+myLocation.getLongitude();
                FriendlyMessage fm = new FriendlyMessage(hoje,urlMaps,user.getDisplayName(),null);
                freference.push().setValue(fm);
                Toast.makeText(getActivity(),"Localização enviada com sucesso",Toast.LENGTH_SHORT).show();
            }
        });
        //conteudo do fragment
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_LOCATION);
        }

        locationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.i("opa",location.toString());
                            myLocation = location;
                        }else{
                            LatLng cr = new LatLng(-6.24345, -36.1805);
                            mMap.addMarker(new MarkerOptions().position(cr).title("Marcado em Campo Redondo"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cr));
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CODE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);
                }else{
                    LatLng cr = new LatLng(-6.24345, -36.1805);
                    mMap.addMarker(new MarkerOptions().position(cr).title("Marcado em Campo Redondo"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(cr));
                }
            }

            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(getContext(), "Permissão não concedida, posição padrão marcada no mapa", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onMyLocationClick(@NonNull Location location) {
            String txtLocation = "Localização atual : [" +location.getLongitude()+","+location.getLatitude()+"]";
            Log.i("",txtLocation);
            Toast.makeText(getActivity(), txtLocation, Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean onMyLocationButtonClick() {
            Toast.makeText(getActivity(), "Aproximando a sua localização no mapa", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
