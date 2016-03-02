package com.downloader.austin.mappost_it;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.List;

public class Main extends FragmentActivity implements GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    EditText input;
    Button button;
    PostsDataSource data;
    Marker myMarker;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // List values = data.getAllPosts();
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }




    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d("System out", "onMarkerDragStart..."+marker.getPosition().latitude+"..."+marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                try{
                    PostsDataSource dataSource = new PostsDataSource(Main.this);
                    dataSource.open();
                    dataSource.editPost(Main.this, marker.getTitle(),marker.getPosition().longitude,marker.getPosition().latitude);
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                final View v = getLayoutInflater().inflate(R.layout.message, null);
                LatLng latLng = marker.getPosition();
                String message = marker.getTitle();
                TextView messageView = (TextView) v.findViewById(R.id.textView3);
                messageView.setText(message);

                return v;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                                try{
                                    PostsDataSource dataSource = new PostsDataSource(Main.this);
                                    dataSource.open();
                                    dataSource.deletePost(Main.this, marker.getTitle());
                                    dataSource.close();
                                    marker.hideInfoWindow();
                                    marker.remove();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

        }
        });

        try {
            PostsDataSource dataSource = new PostsDataSource(Main.this);
            dataSource.open();
            if(dataSource.getAllPosts(Main.this)!=null) {
                List<Posts> postsList = dataSource.getAllPosts(Main.this);
                for (int i = 0; i < postsList.size(); i++) {
                    String message = postsList.get(i).getContent();
                    double longitude = postsList.get(i).getLongitude();
                    double latitude = postsList.get(i).getLatitude();
                    LatLng place = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(place).title(message).draggable(true));

                }
            }
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
    }

    public void onMapLongClick(final LatLng point) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
        LayoutInflater inflater =Main.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog, null);
        alertDialogBuilder.setView(layout);
        final EditText input = (EditText)layout.findViewById(R.id.editText);
        alertDialogBuilder.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString();
                        if (countCharacter(message) <= 140) {
                            try {
                                SQLiteDatabase db = new MySQLiteHelper(Main.this).getWritableDatabase();

                                //System.out.println(message + " " + point.latitude);

                                myMarker = mMap.addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(message)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                        .draggable(true)
                                        );
                                double latitude = point.latitude;
                                double longitude = point.longitude;
                                PostsDataSource dataSource = new PostsDataSource(Main.this);
                                dataSource.open();
                                dataSource.addPost(message, longitude, latitude, Main.this);
                                dataSource.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(Main.this, "Message is too long", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
        alertDialogBuilder.create();
        alertDialogBuilder.show();

    }

    public int countCharacter(String message){
        int counter=0;
        for(int i=0;i<message.length();i++){
            if(Character.isLetter(message.charAt(i))){
                counter++;
            }
        }
        return counter;
    }



}