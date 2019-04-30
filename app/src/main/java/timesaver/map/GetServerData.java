package timesaver.map;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;

/**
 * Created by tomal on 1/12/2018.
 */

public class GetServerData extends AsyncTask<Object,String,String> {
    String googlePlacesData;
    GoogleMap mMap;
    String url;
    public static String response;
    @Override
    protected String doInBackground(Object... params) {

        url = (String)params[0];
        //url = (String)params[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }
    @Override
    protected void onPostExecute(String s) {
        response=s;
        Log.d("response on post",s);
        //directionsList = parser.parseDirections(s);
        //displayDirection(directionsList);

    }
}
