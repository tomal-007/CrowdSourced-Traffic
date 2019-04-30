package timesaver.map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author Priyanka
 */

public class DataParser {


    private HashMap<String,String> getDuration(JSONArray googleDirectionsJson)
    {
        HashMap<String,String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance ="";


        try {

            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration" , duration);
            googleDirectionsMap.put("distance", distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return googleDirectionsMap;
    }


    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        Log.d("getPlace", "Entered");


        try {
            if(!googlePlaceJson.isNull("name"))
            {

                    placeName = googlePlaceJson.getString("name");

            }
            if( !googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");

            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlacesMap.put("place_name" , placeName);
            googlePlacesMap.put("vicinity" , vicinity);
            googlePlacesMap.put("lat" , latitude);
            googlePlacesMap.put("lng" , longitude);
            googlePlacesMap.put("reference" , reference);


            Log.d("getPlace", "Putting Places");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }



    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;
        Log.d("Places", "getPlaces");

        for(int i = 0;i<count;i++)
        {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;

    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");

            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    public String[] parseDirections(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray googleStepsJson )
    {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i = 0;i<count;i++)
        {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }

    public String getPath(JSONObject googlePathJson)
    {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
    public Vector<LatLng> getSnapped(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("snappedPoints");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Vector<LatLng> vector=new Vector<>();
        for(int i=0;i<jsonArray.length();i++){
            try {
                double lat=jsonArray.getJSONObject(i).getJSONObject("location").getDouble("latitude");
                double longi=jsonArray.getJSONObject(i).getJSONObject("location").getDouble("longitude");
                LatLng latLng=new LatLng(lat,longi);
                vector.add(latLng);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vector;


    }
    public String parseDirectionsNew(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        String s="";
        try {
            jsonObject = new JSONObject(jsonData);
       //     jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
             s=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

}
