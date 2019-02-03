package junghyun.myapplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class busLine extends AppCompatActivity {

    private static String TAG = "phpquerytest";
    private static final String TAG_RESULT="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_BNAME = "busname";
    private static final String TAG_SNAME = "bustopname";
    private static final String TAG_LONGI = "longitude";
    private static final String TAG_LATI ="latitude";

    ArrayList<HashMap<String, String>> mBusList;
    ListView list;
    String myJSON;
    TextView textview;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line);

        textview =(TextView)findViewById(R.id.textView);
        list = (ListView)findViewById(R.id.listView1);
        editText = (EditText)findViewById(R.id.editBusNum);

        Button goBusID = (Button)findViewById(R.id.goBusID);
        goBusID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//다음화면으로
                Intent intent = new Intent(getApplicationContext(), busID.class);
                startActivity(intent);
            }
        });
        Button searchBusNum = (Button) findViewById(R.id.searchBusNum);
        searchBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBusList.clear();

                GetData searchBusLine = new GetData();
                searchBusLine.execute(editText.getText().toString());


                //Intent intent = new Intent(getApplicationContext(), busID.class);
                //startActivity(intent);
            }
        });
        mBusList = new ArrayList<>();

    }
    /*
        protected void showList(){
            try {
                JSONObject jsonObject = new JSONObject(myJSON);
                JSONArray busArray = jsonObject.getJSONArray(TAG_RESULT);

                for(int i=0;i<busArray.length();i++){

                    JSONObject item = busArray.getJSONObject(i);

                    String id = item.getString(TAG_ID);
                    String busname = item.getString(TAG_BNAME);
                    String bustopname = item.getString(TAG_SNAME);
                    String longitude = item.getString(TAG_LONGI);
                    String latitude = item.getString(TAG_LATI);

                    HashMap<String,String> BusHashMap = new HashMap<>();

                    BusHashMap.put(TAG_ID, id);
                    BusHashMap.put(TAG_BNAME, busname);
                    BusHashMap.put(TAG_SNAME, bustopname);
                    BusHashMap.put(TAG_LONGI, longitude);
                    BusHashMap.put(TAG_LATI, latitude);

                    mBusList.add(BusHashMap);
                }

                ListAdapter adapter = new SimpleAdapter(
                        busLine.this, mBusList, R.layout.list_item,
                        new String[]{TAG_ID, TAG_SNAME,TAG_LONGI,TAG_LATI},
                        new int[]{R.id.id, R.id.bustopname, R.id.longi,R.id.lati}
                );

                list.setAdapter(adapter);

            } catch (JSONException e) {

                Log.d(TAG, "showList : ", e);
            }

        }
    */
    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(busLine.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
           // textview.setText(result);
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

            String serverURL = "http://172.30.1.32/bus.php";
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

                String id = item.getString(TAG_ID);
                String busname = item.getString(TAG_BNAME);
                String bustopname = item.getString(TAG_SNAME);


                HashMap<String,String> BusHashMap = new HashMap<>();

                BusHashMap.put(TAG_ID, id);
                BusHashMap.put(TAG_BNAME, busname);
                BusHashMap.put(TAG_SNAME, bustopname);


                mBusList.add(BusHashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    busLine.this, mBusList, R.layout.list_item,
                    new String[]{TAG_ID, TAG_BNAME,TAG_SNAME},
                    new int[]{R.id.id,R.id.busname, R.id.bustopname}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
/*
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String searchKeyword = params[0];
                String postParameters = "busname = " + searchKeyword;

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(searchKeyword);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }


            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }



*/

}