package com.grantandishaq.ee461lhomework4;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.location.*;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import java.util.Locale;
import javax.xml.xpath.*;
import org.xml.sax.InputSource;
import java.io.StringReader;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //setUpMapIfNeeded();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    public void goToAddress(LatLng coord, String address){
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = mapFrag.getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 16));
        map.addMarker(new MarkerOptions()
                .title("Your Search")
                .snippet(address)
                .position(coord));
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
                    String lng = xpath.evaluate("/GeocodeResponse/result/geometry/location/long", source2);
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

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 13));

        /*map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/
    }
}
