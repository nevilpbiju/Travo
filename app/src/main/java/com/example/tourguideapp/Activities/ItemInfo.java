package com.example.tourguideapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tourguideapp.Adapters.PlacesAdapter;
import com.example.tourguideapp.Models.GetItem;
import com.example.tourguideapp.Models.VenueModel;
import com.example.tourguideapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ItemInfo extends AppCompatActivity implements OnMapReadyCallback {
    ImageView back,favorite,itemImage;
    TextView itemName;
    TextView description;
    Intent intent;
    Polyline mPolyline;
    Location currentLocation;
    String currentAddress = "", lat = "", longitude = "";
    LocationManager manager;
    EditText editText;
    Marker marker;
    VenueModel venueItem;
    MarkerOptions origin, destination;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        description = findViewById(R.id.imageDescription);

        venueItem = ((PlacesAdapter.ObjectWrapperForBinder)getIntent().getExtras().getBinder("object_value")).getData();
        itemImage = findViewById(R.id.itemImageView1);
//        Picasso.get().load(venueItem.getVenueIcon()).into(itemImage);
        itemName = findViewById(R.id.itemName1);
        itemName.setText(venueItem.getVenueName());
        description.setText(venueItem.getDescription());
        favorite = findViewById(R.id.imageView8);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
//
//        if (checkFavoriteItem(item)) {
//            favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
//            favorite.setTag("favorite");//
//        } else {
//            favorite.setImageResource(R.drawable.ic_baseline_favorite_order_24);
//            favorite.setTag("favorite_empty");
//        }
//        favorite.setOnClickListener(v -> {
//            String tag = favorite.getTag().toString();
//            if (tag.equalsIgnoreCase("favorite_empty")) {
//                SharedPreference.addFavoriteItem(getApplicationContext(), item);
//                Toast.makeText(getApplicationContext(), "Item added to favorite", Toast.LENGTH_SHORT).show();
//                favorite.setTag("favorite");
//                favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
//            }
//            else if(tag.equalsIgnoreCase("favorite")){
//                favorite.setTag("favorite_empty");
//                favorite.setImageResource(R.drawable.ic_baseline_favorite_order_24);
//                SharedPreference.removeFavoriteItem(getApplicationContext(), item);
//                Toast.makeText(getApplicationContext(), "Item removed from favorite", Toast.LENGTH_SHORT).show();
//            }
//        });
        back = findViewById(R.id.backPage);
        back.setOnClickListener(v -> onBackPressed());
    }
    public boolean checkFavoriteItem(GetItem getItem) {
        boolean check = false;

        ArrayList<GetItem> favorites = SharedPreference.getFavoriteItems(getApplicationContext());
        if (favorites != null) {
            for (GetItem item : favorites) {
                //Custom Equal function in Model
                if (item.equals(getItem)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(origin);
        mMap.addMarker(destination);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(), 10));
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                lat = String.valueOf(currentLocation.getLatitude());
                longitude = String.valueOf(currentLocation.getLongitude());
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//                currentAddress = getCompleteAddressString(currentLocation.getLatitude(), currentLocation.getLongitude());
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (supportMapFragment != null) {

                    supportMapFragment.getMapAsync(ItemInfo.this);
                    float lat1 = Float.parseFloat(venueItem.getLatLong().split(",")[0]);
                    float lng = Float.parseFloat(venueItem.getLatLong().split(",")[1]);
                    //Setting marker to draw route between these two points
                    origin = new MarkerOptions().position(new LatLng(Float.parseFloat(lat), Float.parseFloat(longitude))).title("Current Location").snippet("origin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    destination = new MarkerOptions().position(new LatLng(lat1, lng)).title(venueItem.getVenueName()).snippet("destination");


                    String url = getDirectionsUrl(origin.getPosition(), destination.getPosition());
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS to get your current location automatically")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(ItemInfo.this, "Please Enter your location manually", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //    origin = new MarkerOptions().position(new LatLng(12.9121, 77.6446)).title("HSR Layout").snippet("origin");
//destination = new MarkerOptions().position(new LatLng(12.9304, 77.6784)).title("Bellandur").snippet("destination");
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Key
        String key = "key=AIzaSyD_L8g3AcwXBKnEjhvLJwBXwI3L51LjQUU";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb  = new StringBuilder();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            if (iStream != null) {
                iStream.close();
                urlConnection.disconnect();
            }
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }
}