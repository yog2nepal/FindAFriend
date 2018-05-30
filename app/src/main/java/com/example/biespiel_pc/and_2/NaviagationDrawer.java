package com.example.biespiel_pc.and_2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.biespiel_pc.and_2.Model.LocationModel;
import com.example.biespiel_pc.and_2.Model.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import android.os.Vibrator;

public class NaviagationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    SupportMapFragment sMapFragment;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private double latitude, longitude;
    private MarkerOptions marker;
    private Marker m;
    private LocationModel mLocModel;
    private String uid;
    private Bitmap markerPict;
    private Vibrator vibrator;
    private Map<String, Marker> mapMarker;

    // notification

   // NotificationCompat.Builder notification;
  private TextView username,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_naviagation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("Display Picture").child(uid+".JPEG");

        mapMarker = new HashMap<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        this.username = (TextView)headerView.findViewById(R.id.yourName);
        this.email = (TextView)headerView.findViewById(R.id.yourEmail);

        displaySelectedScreen(R.id.nav_MapFragment);
//        FragmentManager supportFragmentManger = getSupportFragmentManager();
//        supportFragmentManger.beginTransaction().replace(R.id.content_navigation_drawer, sMapFragment).commit();

//        getDisplayPict();
//        sMapFragment.getMapAsync(this);

    }

    private void getDisplayPict() {
        //Get Display Picture
        Glide.with(NaviagationDrawer.this)
                .using(new FirebaseImageLoader())
                .load(mStorageRef)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(60, 60){
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        CircularImage ci = new CircularImage();
                        markerPict = ci.setCircularImage(resource);
                    }
                });
    }

    /* LocationModel Update Method */
    private  void LocationUpdate()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 100, 1, new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    // for getting latitude from Locatin Objects
                    latitude = location.getLatitude();
                    // for getting longitude froom location object of type LocationModel
                    longitude = location.getLongitude();
                    final LatLng latLng = new LatLng(latitude, longitude);
                   // Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
//                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
//                        String str = addressList.get(0).getLocality()+ ", ";
//                        str+= addressList.get(0).getCountryName();
//                        marker = new MarkerOptions().position(latLng).title("I am here");
                            if(marker == null) {
                                marker = new MarkerOptions()
                                        .position(latLng)
                                        .title("I am here")
                                        .icon(BitmapDescriptorFactory.fromBitmap(markerPict));

                                m = mMap.addMarker(marker);
                                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            }
                            else
                            {
                                m.setPosition(latLng);
                            }

                        mLocModel = new LocationModel(latitude, longitude);
                        updateLocDatabase(mLocModel);

                        mReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int notNumber=1;
                                for(DataSnapshot locSnapshot : dataSnapshot.getChildren()){
                                    notNumber++;
                                    final User loc = locSnapshot.getValue(User.class);
                                    if(loc.getUid().equals(uid))
                                    {
                                        username.setText(loc.getUserName());
                                        email.setText(loc.getEmail());

                                    }
                                    if(!loc.getUid().equals(uid)){
                                        final LatLng latLngOtherUser = new LatLng(loc.getLatitude(), loc.getLongitude());
                                        Location locOtherUser = new Location("Users");
                                        locOtherUser.setLatitude(loc.getLatitude());
                                        locOtherUser.setLongitude(loc.getLongitude());

                                        StorageReference sr = mStorage.getReferenceFromUrl(loc.getPict());
                                        Glide.with(NaviagationDrawer.this)
                                                .using(new FirebaseImageLoader())
                                                .load(sr)
                                                .asBitmap()
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true)
                                                .into(new SimpleTarget<Bitmap>(60, 60){
                                                    @Override
                                                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                        CircularImage ci = new CircularImage();
                                                        Bitmap userPict = ci.setCircularImage(resource);
                                                        Marker marker = null;
                                                        if(mapMarker.get(loc.getUserName()) == null){
                                                            marker = mMap.addMarker(new MarkerOptions()
                                                                    .position(latLngOtherUser)
                                                                    .title(loc.getUserName())
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(userPict)));;
                                                            mapMarker.put(loc.getUserName(), marker);
                                                        } else {
                                                            marker = mapMarker.get(loc.getUserName());
                                                            marker.setPosition(latLngOtherUser);
                                                        }
//
//                                                        mMap.addMarker(new MarkerOptions()
//                                                                .position(latLngOtherUser)
//                                                                .title(loc.getUserName())
//                                                                .icon(BitmapDescriptorFactory.fromBitmap(userPict)));
                                                    }
                                                });

                                        double distance = location.distanceTo(locOtherUser);
                                        System.out.println(loc.getUserName()+" : "+distance+" m");


                                        if(distance>=0 && distance<=20.02f)
                                        {
                                            vibrator.vibrate(500);
                                            addNotification(loc.getUserName(), notNumber);

                                        }

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.naviagation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager supportFragmentManger = getSupportFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_Logout) {
            finish();
            mAuth.signOut();
            Intent intent = new Intent(NaviagationDrawer.this, LoginActivity.class);
            startActivity(intent);
        }

        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;

        switch (id){
            case R.id.nav_MapFragment:
                fragment = sMapFragment;
                getDisplayPict();
                marker = null;
                sMapFragment.getMapAsync(this);
                break;
            case R.id.Profile:
                fragment = new ProfileFragment();
                break;
        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_navigation_drawer, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        LocationUpdate();
    }

    public void updateLocDatabase(LocationModel locModel){
        Map<String, Object> locUpdate = new HashMap<>();
        locUpdate.put("latitude", locModel.getLatitude());
        locUpdate.put("longitude", locModel.getLongitude());
        mReference.child(uid).updateChildren(locUpdate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!=null)
                    Toast.makeText(NaviagationDrawer.this, "Database Error", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(NaviagationDrawer.this, "Location Changed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Method for Push Notification */


    private void addNotification(String name,int notNumber){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("People near by!");
        builder.setContentText(name+" is near by you");
        //  Intent intent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, 0);
        //builder.setContentIntent(pendingIntent);
        Notification not = builder.build();
        NotificationManager nm = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
        nm.notify(notNumber, not);
    }


}
