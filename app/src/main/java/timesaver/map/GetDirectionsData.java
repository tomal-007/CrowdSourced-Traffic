package timesaver.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import static timesaver.map.R.id.map;

/**
 * @auth Priyanka
 */

public class GetDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;
    LatLng source;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];
        source=(LatLng)objects[3];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        try {
            displayDirection(directionsList);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void displayDirection(String[] directionsList) throws ExecutionException, InterruptedException {
        Object dataTransfer[] = new Object[2];
        int count = directionsList.length;
        Vector<LatLng> vect=new Vector<>();
        LatLng latll = null;
        Log.d("koto steps:",String.valueOf(count));
        int val=count/2;
        for(int i = 0;i<count;i++)
        {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(15);
            /*List<LatLng> latList=PolyUtil.decode(directionsList[i]);
            if(latList!=null){
                for(int j=0;j<latList.size();j++){
                    dataTransfer=new Object[1];
                    String url=getUrlServer(latList.get(j).latitude,latList.get(j).longitude);
                    dataTransfer[0]=url;
                    AsyncTask as=getServerData.execute(dataTransfer);
                    Log.d("in routes",as.get().toString());
                    if(j==2) break;
                }
            }*/
            List<LatLng> latList=PolyUtil.decode(directionsList[i]);
            if(latList!=null && i<=(val)){
                LatLng llng=new LatLng((latList.get(latList.size()-1).latitude+0.001),(latList.get(latList.size()-1).longitude+0.001));
                vect.add(llng);
            }

            /*if(i==val){
              LatLng laaa=new LatLng(latList.get(0).latitude+0.01,latList.get(0).longitude+0.01);
              latll=laaa;
          }*/

            options.addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
        }

        for(int i=0;i<vect.size()-1;i++){
            MarkerOptions markerOption = new MarkerOptions();
            LatLng latt=vect.get(i);
            markerOption.position(vect.get(i));
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerOption.draggable(true);
            mMap.addMarker(markerOption);
        }

        GetSnappedData getSnapped=new GetSnappedData();
        String url=getUrlSnapped(vect);
        dataTransfer=new Object[2];
        dataTransfer[0]=mMap;
        dataTransfer[1]=url;
        getSnapped.execute(dataTransfer);


    }
    private String getUrlServer(double latitude, double longitude)
    {
        StringBuilder googlePlacesUrl = new StringBuilder("http://192.168.0.105:8000/polls/");
        googlePlacesUrl.append(latitude + "/" + longitude+"/2");
        Log.d("getUrl", googlePlacesUrl.toString());
        //Toast.makeText(MapsActivity.this,googlePlacesUrl.toString(),Toast.LENGTH_LONG).show();
        return (googlePlacesUrl.toString());
    }

    private String getUrlSnapped(Vector<LatLng> vector){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googlePlacesUrl.append("origin="+source.latitude+","+source.longitude);
        googlePlacesUrl.append("&destination="+latLng.latitude+","+latLng.longitude);
        googlePlacesUrl.append("&waypoints=");
        for(int i=0;i<vector.size();i++){
            if(i>0)  googlePlacesUrl.append("|via:");
            double lat=vector.get(i).latitude;
            double longi=vector.get(i).longitude;
            googlePlacesUrl.append(lat+","+longi);

        }
        googlePlacesUrl.append("&key=" + "AIzaSyB2U26UkUbYZqMCxk-Z2wX9P_FB7L5PLb4");
        Log.d("getUrl", googlePlacesUrl.toString());
        //Toast.makeText(MapsActivity.this,googlePlacesUrl.toString(),Toast.LENGTH_LONG).show();
        return (googlePlacesUrl.toString());

    }

    private String getUrlSnapped2(LatLng l){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googlePlacesUrl.append("origin="+source.latitude+","+source.longitude);
        googlePlacesUrl.append("&destination="+latLng.latitude+","+latLng.longitude);
        googlePlacesUrl.append("&waypoints=via:"+l.latitude+","+l.longitude);
        googlePlacesUrl.append("&key=" + "AIzaSyB2U26UkUbYZqMCxk-Z2wX9P_FB7L5PLb4");
        Log.d("getUrl", googlePlacesUrl.toString());
        //Toast.makeText(MapsActivity.this,googlePlacesUrl.toString(),Toast.LENGTH_LONG).show();
        return (googlePlacesUrl.toString());

    }
}

