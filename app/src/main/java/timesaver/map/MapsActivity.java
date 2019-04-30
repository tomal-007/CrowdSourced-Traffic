package timesaver.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,OnMyLocationClickListener,GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private Double lat;
    private Double longi;
    private Double c_lat;
    private Double c_long;
    private Double end_latitude;
    private double end_longitude;
    private EditText tf_location;
    private EditText td_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        lat = getIntent().getDoubleExtra("Latitude", 0);
        longi = getIntent().getDoubleExtra("Longitude", 0);
        int count = getIntent().getIntExtra("count", 0);
        c_lat=lat;
        c_long=longi;
        tf_location = (EditText) findViewById(R.id.TF_location);
        td_location=(EditText) findViewById(R.id.TD_location);
        Log.d("lat", String.valueOf(lat));
        Log.d("long", String.valueOf(longi));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myloc = new LatLng(lat, longi);
        MarkerOptions markerOption=new MarkerOptions();
        markerOption.position(myloc);
        markerOption.draggable(true);
        mMap.addMarker(markerOption);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        //showSome();
        //mMap.setOnMyLocationButtonClickListener(this);
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
    public void onClick(View v) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        GetServerData getServerData=new GetServerData();
        switch (v.getId()) {
            case R.id.B_search: {

                String location = tf_location.getText().toString();
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                Log.d("location = ", location);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address myAddress = addressList.get(i);
                            LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            markerOptions.position(latLng);
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                        /*dataTransfer = new Object[1];
                        String url = getUrlServer(lat, longi);
                        dataTransfer[0] = url;
                        AsyncTask as=getServerData.execute(dataTransfer);
                        try {
                            Log.d("accha",as.get().toString());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }*/
                        //Log.d("in here",GetServerData.response);
                        //getNearbyPlacesData.execute(dataTransfer);
                        //Toast.makeText(MapsActivity.this,getServerData.response, Toast.LENGTH_LONG).show();
                    }

                }
                break;
            }
            case R.id.B_hospital:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(lat, longi, hospital);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_LONG).show();
                break;
            case R.id.B_restaurant:
                mMap.clear();
                dataTransfer = new Object[2];
                String restaurant = "restaurant";
                url = getUrl(lat, longi, restaurant);
                getNearbyPlacesData = new GetNearbyPlacesData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_LONG).show();
                break;
            case R.id.B_school:
                mMap.clear();
                String school = "school";
                dataTransfer = new Object[2];
                url = getUrl(lat, longi, school);
                getNearbyPlacesData = new GetNearbyPlacesData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_LONG).show();
                break;
            case R.id.B_to:
                mMap.clear();
                String location = tf_location.getText().toString();
                Log.d("to loc",location);
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                //Log.d("location = ", location);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address myAddress = addressList.get(i);
                            LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            end_latitude=myAddress.getLatitude();
                            end_longitude=myAddress.getLongitude();
                            markerOptions.position(latLng);
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                    }

                }

                String location2 = td_location.getText().toString();
                Log.d("to loc",location2);
                if(!location2.equals("Your location")) {
                    List<Address> addressList2 = null;
                    MarkerOptions markerOptions2 = new MarkerOptions();
                    //Log.d("location = ", location);

                    if (!location2.equals("")) {
                        Geocoder geocoder = new Geocoder(this);
                        try {
                            addressList2 = geocoder.getFromLocationName(location2, 1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (addressList2 != null) {
                            for (int i = 0; i < addressList2.size(); i++) {
                                Address myAddress = addressList2.get(i);
                                LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                                lat = myAddress.getLatitude();
                                longi = myAddress.getLongitude();
                                markerOptions2.position(latLng);
                                mMap.addMarker(markerOptions);
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            }
                        }

                    }
                }
                dataTransfer = new Object[4];
                url = getDirectionsUrl();
                Log.d("url",url);
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude, end_longitude);
                dataTransfer[3]=new LatLng(lat,longi);
                getDirectionsData.execute(dataTransfer);
                break;

        }
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&key=" + "AIzaSyByhw8l5oQFVE0VD8xVRtcYNRtc8y5kVcg");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
    private String getUrlServer(double latitude, double longitude)
    {
        StringBuilder googlePlacesUrl = new StringBuilder("http://192.168.0.106:8000/polls/");
        googlePlacesUrl.append(latitude + "/" + longitude);
        Log.d("getUrl", googlePlacesUrl.toString());
        Toast.makeText(MapsActivity.this,googlePlacesUrl.toString(),Toast.LENGTH_LONG).show();
        return (googlePlacesUrl.toString());
    }
    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+lat+","+longi);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&altenatives=true");
        googleDirectionsUrl.append("&key="+"AIzaSyB2U26UkUbYZqMCxk-Z2wX9P_FB7L5PLb4");

        return googleDirectionsUrl.toString();
    }

             @Override
             public boolean onMarkerClick(Marker marker) {

                 LatLng  latLng=marker.getPosition();
                 Geocoder geo=new Geocoder(this);
                 List<Address> list=null;
                 try {
                     list=geo.getFromLocation(latLng.latitude,latLng.longitude,1);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 String str="";
                 if(list!=null){
                     for(int i=0;i<list.size();i++){
                         Address ad=list.get(i);
                         str=ad.getAddressLine(0);
                     }
                 }
                 tf_location.setText(str);
                 marker.setDraggable(true);

                 return true;
             }

             @Override
             public void onMarkerDragStart(Marker marker) {

             }

             @Override
             public void onMarkerDrag(Marker marker) {

             }

             @Override
             public void onMarkerDragEnd(Marker marker) {
                 end_latitude = marker.getPosition().latitude;
                 end_longitude =  marker.getPosition().longitude;
                 Geocoder geo=new Geocoder(this);
                 List<Address> list=null;
                 try {
                     list=geo.getFromLocation(end_latitude,end_longitude,1);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 if(list!=null){
                     tf_location.setText(list.get(0).getAddressLine(0));
                 }
                 Log.d("end_lat",""+end_latitude);
                 Log.d("end_lng",""+end_longitude);

             }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d("tagLocation","ekhane baaal");
        return true;
    }
}
