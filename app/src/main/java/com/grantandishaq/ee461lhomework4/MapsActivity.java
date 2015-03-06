package com.grantandishaq.ee461lhomework4;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.location.*;
import android.content.Context;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import javax.xml.xpath.*;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.*;
import org.w3c.dom.Document;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng searchLoc;
    private Marker userMark;
    private ArrayList<Marker> places;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //setUpMapIfNeeded();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton img = (ImageButton) findViewById(R.id.mapMenuIcon);
        img.setImageResource(R.drawable.menu_list);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }


    public void showPlaces(String param){
        if(searchLoc == null){
            Context context = getApplicationContext();
            CharSequence msg = "Must search for an address before getting Place results";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, msg, duration);
            toast.show();
            return;
        }else{
            MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
            GoogleMap map = mapFrag.getMap();
            map.clear();
            map.addMarker(new MarkerOptions()
                    .title("Your Search")
                    .snippet(userMark.getSnippet())
                    .position(userMark.getPosition()));             //Replace user marker
            String apiKey = (String)getText(R.string.browser_google_maps_key);
            String baseUrl = getString(R.string.places_url);
            baseUrl += String.valueOf(searchLoc.latitude) + "," + String.valueOf(searchLoc.longitude) + "&radius=4000&types="+ param + "&key=" + apiKey;
            RetrievePlacesData getPlaces = new RetrievePlacesData(this);
            getPlaces.execute(baseUrl);
            getPlaces.setMyTaskCompleteListener(new RetrievePlacesData.OnTaskComplete(){
                @Override
                public void setMyTaskComplete(String xml){
                    Log.v("HOPEFULLY REAL XML", xml);

                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();

                    InputSource source1 = new InputSource(new StringReader(xml));

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


                    try{

                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document document = db.parse(source1);
                        boolean morePlaces = true;
                        int resultIndex = 1;
                        if(xpath.evaluate("/PlaceSearchResponse/status", document).equals("ZERO_RESULTS")){
                            Context context = getApplicationContext();
                            CharSequence msg = "No results found";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, msg, duration);
                            toast.show();
                            return;
                        }

                        while(xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]", document) != null){
                            Log.v("PLACE_DATA", "Place Name = "+xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/name/text()", document)+" Place Vicinity = "+xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/vicinity/text()", document) +" Place LatLng= " +xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/geometry/location/lat/text()", document) + " " + xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/geometry/location/lng/text()", document));
                            Marker tmp = mMap.addMarker(new MarkerOptions()
                                    .title(xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/name", document))
                                    .snippet(xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/vicinity", document))
                                    .position(new LatLng(Double.parseDouble(xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/geometry/location/lat", document)),
                                            Double.parseDouble(xpath.evaluate("/PlaceSearchResponse/result["+resultIndex+"]/geometry/location/lng", document))))
                            );
                            //places.add(tmp);
                            resultIndex++;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        String msg = e.getMessage();
                        Log.e("XPATH ERR", ""+msg);
                    }
                }
            });

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.restaurants:
                showPlaces("restaurant");
                return true;
            case R.id.hotels:
                showPlaces("lodging");
                return true;
            case R.id.bars:
                showPlaces("bar");
                return true;
            case R.id.cafes:
                showPlaces("cafe");
                return true;
            case R.id.movie_theaters:
                showPlaces("movie_theater");
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showMapMenu(View view){
        openOptionsMenu();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }


    public void goToAddress(LatLng coord, String address){
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        searchLoc = coord;
        GoogleMap map = mapFrag.getMap();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 15));
        map.clear();
        userMark = map.addMarker(new MarkerOptions()
                .title("Your Search")
                .snippet(address)
                .position(coord));
        userMark.showInfoWindow();
    }


    public void addressLookup(View view){
        EditText addrEdit = (EditText)findViewById(R.id.locEdit);
        String apiKey = (String)getText(R.string.google_maps_key);
        String baseUrl = getString(R.string.geocode_url);
        String address = addrEdit.getText().toString();
        //address = address.replace(' ','+');
        RetrieveAddressData retrieve = new RetrieveAddressData();
        retrieve.execute(baseUrl, address, apiKey);
        retrieve.setMyTaskCompleteListener(new RetrieveAddressData.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(String xml) {
                Log.v("HOPEFULLY REAL XML", xml);

                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();

                InputSource source1 = new InputSource(new StringReader(xml));
                InputSource source2 = new InputSource(new StringReader(xml));

                InputSource source3 = new InputSource(new StringReader(xml));

                try {
                    String addr = xpath.evaluate("/GeocodeResponse/result/formatted_address", source3);
                    Log.v("ADDRESS", addr);
                    String lat = xpath.evaluate("/GeocodeResponse/result/geometry/location/lat", source1);
                    String lng = xpath.evaluate("/GeocodeResponse/result/geometry/location/lng", source2);
                    double latitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lng);
                    LatLng loc = new LatLng(latitude, longitude);
                    goToAddress(loc, addr);
                    Log.v("LATLNG", "Lat= " + lat + " Lng= " + lng );
                }catch (Exception e){
                    e.printStackTrace();
                    String msg = e.getMessage();
                    Log.e("SAXERROR", ""+msg);
                }

            }
        });
        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        /*try{
            List<Address> places = geo.getFromLocationName(address, 1);
            if(places.isEmpty()){
                Log.v("Search","No results found");
            }else{
                double addrLat = places.get(0).getLatitude();
                double addrLong = places.get(0).getLongitude();
                String addrL12 = places.get(0).getAddressLine(0);
                addrL12 += ",\n" + places.get(0).getAddressLine(1);
                LatLng place = new LatLng(addrLat, addrLong);
                goToAddress(place, addrL12);
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/
        //String charset = "UTF-8";
        //address = address.replace(' ','+');
        //Log.v("EditText", address);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }*/

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    /*private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }*/

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        LatLng sydney = new LatLng(-33.867, 151.206);
        LatLng userLoc = new LatLng(latitude, longitude);
        mMap = map;
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 13));

        /*map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/
    }
}
