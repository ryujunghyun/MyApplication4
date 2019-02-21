package junghyun.myapplication;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class busLine extends AppCompatActivity {

    private static String TAG = "phpquerytest";
    private static final String TAG_RESULT="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_BNAME = "busname";
    private static final String TAG_SNAME = "bustopname";
    private static final String TAG_BELL = "bell";
    private static final String TAG_ROAD = "road";

    ArrayList<HashMap<String, String>> mBusList;
    ListAdapter adapter;
    ListView list;
    String myJSON;
    TextView textview;
    EditText editText;
    Cursor c;
    String clickstop;
    EditText busnamesearch;
    EditText busidsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line);

        textview =(TextView)findViewById(R.id.textView);
        list = (ListView)findViewById(R.id.listView1);
        busnamesearch = (EditText)findViewById(R.id.editBusNum);
        busidsearch = (EditText)findViewById(R.id.editBusID);


        Button goBusID = (Button)findViewById(R.id.goBusID);
        goBusID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//다음화면으로
                Intent intent = new Intent(getApplicationContext(), LastActivity.class);
                startActivity(intent);
            }
        });

        Button searchBusNum = (Button) findViewById(R.id.searchBusNum);
        searchBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBusList.clear();

                GetData searchBusLine = new GetData();

                searchBusLine.execute(busnamesearch.getText().toString());

                //  searchBusLine.execute(busnamesearch.getText().toString());

            }
        });
        mBusList = new ArrayList<>();

    }

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
            String serverURL = "http://172.30.58.78/bus.php";
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
            final JSONObject jsonObject = new JSONObject(myJSON);
            final JSONArray busArray = jsonObject.getJSONArray(TAG_RESULT);
            HashMap<String,String> BusHashMap;

            for(int i=0;i<busArray.length();i++){

                JSONObject item = busArray.getJSONObject(i);
                String id=item.getString(TAG_ID);
                String busname = item.getString(TAG_BNAME);
                String bustopname = item.getString(TAG_SNAME);
                // String bell = item.getString(TAG_BELL);

                BusHashMap = new HashMap<>();

                BusHashMap.put(TAG_ID, id);
                BusHashMap.put(TAG_BNAME, busname);
                BusHashMap.put(TAG_SNAME, bustopname);
                //     BusHashMap.put(TAG_BELL, bell);

                mBusList.add(BusHashMap);

            }

            // ImageView road = (ImageView)findViewById(R.id.road);
            /*ListAdapter*/
            adapter = new SimpleAdapter(

                    // ImageView road = (ImageView)findViewById(R.id.road);


                    busLine.this, mBusList, R.layout.list_item,
                    new String[]{TAG_ID , TAG_BNAME,TAG_SNAME/*,TAG_BELL*/},
                    new int[]{R.id.id, R.id.busname, R.id.bustopname/*,R.id.bell*/}
            );

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                int[] count=new int[]{0, 0, 0, 0, 0, 0, 0};
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {//int position, long id는 클릭된 항목의 id
                    //1. 클릭된 mBusList리스트의 id나 location값으로 클릭된 정류장이름 찾아서  2.php로 보냄
                    count[position]+=1;//아이템의 클릭 횟수 카운트
                    try {
                        clickstop = busArray.getJSONObject(position).getString(TAG_SNAME);//클릭된 아이디로 정류장이름 찾기

                        if(count[position]>0) {
                            if(count[position]>=2){
                                Toast.makeText(
                                        getApplicationContext(),
                                        "이미 예약되었습니다",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                            else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "예약된 정류장: " + clickstop + " 클릭 횟수: " + count[position],
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                        GetBustop searchBustop = new GetBustop();
                        searchBustop.execute(clickstop);



                    }
                    catch (JSONException e) {
                        Log.d(TAG, "showResult : ", e);
                    }


                }
            });
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//리스트 하나만 선택하도록 하는거*/

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    private class GetBustop extends AsyncTask<String, Void, String>{

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
            clickstop=result;
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];

            String serverURL = "http://172.30.58.78/bus.php";
            String postParameters = "clickstop=" + searchKeyword; //php로 전달하는 매개변수

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

