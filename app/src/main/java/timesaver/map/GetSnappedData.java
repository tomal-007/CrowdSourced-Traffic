package timesaver.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * Created by tomal on 1/14/2018.
 */

public class GetSnappedData extends AsyncTask<Object,String,String> {
    GoogleMap mMap;
    @Override
    protected String doInBackground(Object... params) {
        String url=(String )params[1];
        mMap=(GoogleMap)params[0];
        DownloadUrl down=new DownloadUrl();
        String data="";
        try {
            data=down.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        String latLngVector;
        DataParser parser = new DataParser();
        Log.d("eta snap e",s);
        latLngVector=parser.parseDirectionsNew(s);
        //showNearbyPlaces(nearbyPlaceList);
        try {
            displayDirection(latLngVector);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void displayDirection(String directionsList) throws ExecutionException, InterruptedException {


            PolylineOptions options = new PolylineOptions();
            options.color(Color.GREEN);
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

            options.addAll(PolyUtil.decode(directionsList));

            mMap.addPolyline(options);
    }
     public void ShowMarker(Vector<LatLng> vector){
         for(int i=0;i<vector.size();i++) {
             MarkerOptions markerOption = new MarkerOptions();
             markerOption.position(vector.get(i));
             markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
             markerOption.draggable(true);
             mMap.addMarker(markerOption);
         }
     }
}
