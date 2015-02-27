package com.example.sarmad.sunshine.app;

import android.content.Intent;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private String POST_CODE;
  //  public static final String Weather_info_String = null;
   public final static String Weather_info_String = "com.example.sarmad.sunshine.app.Weather_info_String";
    ArrayAdapter<String> adapter  ;
    public PlaceholderFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    class FetchWeatherTask extends AsyncTask<String,Void,String[]>{



        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
            return format.format(date).toString();
        }




        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


            private String[] getWeatherDataFromJson( String forecastJSonStr, int Days)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJSonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[Days];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;

            }
                for (String result : resultStrs){

                    Log.v(LOG_Tag,"preparing data" + result);
                }



            return resultStrs;
        }


        public final String LOG_Tag=FetchWeatherTask.class.getSimpleName();



        @Override
        protected String[] doInBackground(String... params) {
         // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;



             String  format="json";
             String Unit="metric";
             int  days=7;


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
                        .appendQueryParameter(DAYS_Param,String.valueOf(days))
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

            try {

              return getWeatherDataFromJson(forecastJsonStr, days);

            }

            catch (JSONException e){

                Log.e(LOG_Tag,e.getMessage(),e);
                e.printStackTrace();
            }

            return  null;

        }



        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
        // * @param strings The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String[] result) throws RuntimeException{
            //super.onPostExecute(strings);

            adapter.clear();
          //  adapter.addAll(result);



            for(String s : result){
                Log.v(LOG_Tag," in on post execute" + s);
                adapter.add(s);

            }


        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> list = new ArrayList<String>();

        //String[] s = new String[10];



                adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                list
                );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p/>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView , View view, int position, long id) {
                String Result=adapter.getItem(position);
                Intent detailActivity_intent= new Intent(getActivity(),DetailActivity.class);
                detailActivity_intent.putExtra(Intent.EXTRA_TEXT,Result);
                startActivity(detailActivity_intent);
                //Toast.makeText(getActivity(),Result,position).show();


            }
        });
        listView.setAdapter(adapter);



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.forecastfragement,menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // int id=item.getItemId();

        switch(item.getItemId()){

            case R.id.action_refresh :
                FetchWeatherTask task = new FetchWeatherTask();
                task.execute("Karachi");
                return true;
            case R.id.action_settings_main:

                 startActivity(new Intent(getActivity(),SettingsActivity.class));
                 return true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }



}
