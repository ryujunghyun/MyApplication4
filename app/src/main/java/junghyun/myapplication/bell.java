package junghyun.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import static java.lang.Double.parseDouble;

public class bell extends AppCompatActivity implements OnMapReadyCallback {
/*지연이코드*/
  private static String TAG = "phpquerytest";
    private static final String TAG_RESULT="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_BNAME = "busname";
    private static final String TAG_SNAME = "bustopname";

    private static final String TAG_LONGI = "longitude";
    private static final String TAG_LATI ="latitude";
       String myJSON;
    TextView textview;//버스이름입력
    EditText editText;//버스이름입력
  //  Double latitude, longitude;
    GoogleMap mGoogleMap;
    Marker marker;
    Address bestResult;
    String longitude, latitude, bustopname, busname;
    /////////////////

    final private int REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell);
        textview =(TextView)findViewById(R.id.busName);
        editText = (EditText)findViewById(R.id.editBusName);

        Button drop = (Button) findViewById(R.id.drop);
        Button cancel = (Button) findViewById(R.id.cancel);

        Button curLoc = (Button) findViewById(R.id.curLoc);
        curLoc.setOnClickListener(new View.OnClickListener() {//버튼 누르면 현재위치 받아옴
            @Override
            public void onClick(View view) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(bell.this);

                if (!checkLocationPermissions()) {
                    requestLocationPermissions(REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION);
                } else {
                    getLastLocation();
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
     /*   mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!checkLocationPermissions()) {
            requestLocationPermissions(REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION);
        } else {
            getLastLocation();
        }

*/
        Button showMap = (Button) findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mBusList.clear()
                bell.GetName searchBusStop = new bell.GetName();//버스이름얻어
                searchBusStop.execute(editText.getText().toString());
            }
        });
    }




    /*지연이코드*/
    private class GetName extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(bell.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            textview.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){
                textview.setText(errorString);
            }
            else {
                myJSON = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];

            String serverURL = "http://192.168.0.7/bus.php";
            String postParameters = "busname=" + searchKeyword;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }

    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            JSONArray busArray = jsonObject.getJSONArray(TAG_RESULT);

            for(int i=0;i<busArray.length();i++){
                JSONObject item = busArray.getJSONObject(i);

               // String id = item.getString(TAG_ID);
                 busname = item.getString(TAG_BNAME);
                 bustopname = item.getString(TAG_SNAME);
                  longitude = item.getString(TAG_LONGI);
                  latitude = item.getString(TAG_LATI);
               // longitude=item.getDouble(TAG_LONGI);
                //latitude=item.getDouble(TAG_LATI);

                HashMap<String,String> BusHashMap = new HashMap<>();

               // BusHashMap.put(TAG_ID, id);
               BusHashMap.put(TAG_BNAME, busname);
                BusHashMap.put(TAG_SNAME, bustopname);
                BusHashMap.put(TAG_LONGI, longitude);
                BusHashMap.put(TAG_LATI, latitude);
                getAllMarker();
            //    mBusList.add(BusHashMap);
            }

/*
            ListAdapter adapter = new SimpleAdapter(
                    busLine.this, mBusList, R.layout.list_item,
                    new String[]{TAG_ID, TAG_BNAME,TAG_SNAME,TAG_LONGI,TAG_LATI},
                    new int[]{R.id.id,R.id.busname, R.id.bustopname, R.id.longi,R.id.lati}
            );

            list.setAdapter(adapter);
*/
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions(int requestCode) {
        ActivityCompat.requestPermissions(
                bell.this,            // bell 액티비티의 객체 인스턴스를 나타냄
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},        // 요청할 권한 목록을 설정한 String 배열
                requestCode    // 사용자 정의 int 상수. 권한 요청 결과를 받을 때
        );
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        Task task = mFusedLocationClient.getLastLocation();       // Task<Location> 객체 반환
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    mCurrentLocation = location;
                    LatLng curlocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curlocation, 15));
                    //updateUI();
                } else
                    Toast.makeText(getApplicationContext(),
                            "Unavailable!",
                            Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {//마커표시하는함수
        mGoogleMap = googleMap;
     //   LatLng stops = new LatLng(latitude, longitude);
       // googleMap.addMarker(
         //       new MarkerOptions().
           //             position(stops).
             //           title("한성대학교"));
        // move the camera
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hansung,15));

    }

    //모든 마커 표시
    public void getAllMarker(){
        //bell.GetID searchBusStop = new bell.GetID();//버스아이디얻어
        //searchBusStop.execute(editText.getText().toString());
        LatLng stops = new LatLng(parseDouble(latitude), parseDouble(longitude));
   //     LatLng stops = new LatLng(latitude, longitude);
        marker=mGoogleMap.addMarker(
                new MarkerOptions().
                        position(stops).
                     title(bustopname).
                alpha(0.8f));
            }

        }


