package com.example.sarmad.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.URI;


public class MainActivity extends ActionBarActivity {

    private String LOG_TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(LOG_TAG,"in oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


        else if(id==R.id.action_map){
            openPreferedLocationMap();
          return true;
        }

        return super.onOptionsItemSelected(item);
    }




private void openPreferedLocationMap(){

    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    String Location=sharedPrefs.getString(getString(R.string.pref_location_key),
            getString(R.string.pref_location_key));

    Uri geoLocation= Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",Location).build();

    Intent mapIntent= new Intent(Intent.ACTION_VIEW);
    mapIntent.setData(geoLocation);

    if(mapIntent.resolveActivity(getPackageManager())!=null){
        startActivity(mapIntent);

    }

    else{
        Log.d(LOG_TAG, "Couldn't call " + Location + ", no receiving apps installed!");
    }


}

    @Override
    protected void onStart() {
        Log.v(LOG_TAG,"in  onstart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG,"in onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG,"in onStop");
        super.onStop();
    }


    @Override
    protected void onResume() {
        Log.v(LOG_TAG,"in onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG,"in onDestroy");
        super.onDestroy();
    }
}
