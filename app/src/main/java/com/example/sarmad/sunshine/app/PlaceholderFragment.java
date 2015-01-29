package com.example.sarmad.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private String POST_CODE;
    public PlaceholderFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    class FetchWeatherTask extends AsyncTask<String,Void,Void>{

        public final String LOG_Tag=FetchWeatherTask.class.getSimpleName();
        @Override
        protected Void doInBackground(String... params) {
         // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;



             String  format="json";
             String Unit="metric";
             String  days="7";


            try {


                final  String Forecast_Base="http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String Query_param="q";
                final String Mode_param="mode";
                final String Unit_Param="units";
                final String DAYS_Param="cnt";

                Uri ForecastUriBuilder=Uri.parse(Forecast_Base).buildUpon()
                        .appendQueryParameter(Query_param,params[0])
                        .appendQueryParameter(Mode_param,format)
                        .appendQueryParameter(Unit_Param,Unit)
                        .appendQueryParameter(DAYS_Param,days)
                        .build();
                Log.v(LOG_Tag,"URI Builder"+ForecastUriBuilder.toString());
// Construct the URL for the OpenWeatherMap query
// Possible parameters are avaiable at OWM's forecast API page, at
// http://openweathermap.org/API#forecast
                URL url = new URL(ForecastUriBuilder.toString());
// Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
// Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
// Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
// Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
// But it does make debugging a *lot* easier if you print out the completed
// buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
// Stream was empty. No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_Tag,"Forecast Jason String " + forecastJsonStr);
            }
            catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
// If the code didn't successfully get the weather data, there's no point in attemping
// to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> list = new ArrayList<String>();

        list.add("Today - Sunny - 88/63");
        list.add("Tomorow - Hot - 88/63");
        list.add("Random - Dizzle - 88/63");
        list.add("Today - Coll - 68/63");
        list.add("Today - Coll - 68/63");
        list.add("Today - Coll - 68/63");
        list.add("Today - Coll - 68/63");
        list.add("Today - Coll - 68/63");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                list);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.forecastfragement,menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.action_refresh){
            FetchWeatherTask task = new FetchWeatherTask();
            task.execute("94043");
            return true;
        }
        else{

            return super.onOptionsItemSelected(item);
        }
    }



}
