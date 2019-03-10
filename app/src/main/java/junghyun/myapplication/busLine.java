package junghyun.myapplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static android.view.Window.FEATURE_CUSTOM_TITLE;

public class busLine extends AppCompatActivity {


    private CustomDialog customDialog;
    int temp; // 임시 전역변수 - singleChoiceItems 에서 선택항목 저장시 사용


    private static String TAG = "phpquerytest";
    private static final String TAG_RESULT = "webnautes";
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
    String clickstop;
    String reservedstop;

    EditText busnamesearch;
    EditText busidsearch;
    int bellArray[];
    String getbusNum;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line);


        textview = (TextView) findViewById(R.id.textView);
        list = (ListView) findViewById(R.id.listView1);
      //  busnamesearch = (EditText) findViewById(R.id.editBusNum);
        busidsearch = (EditText) findViewById(R.id.editBusID);

        Intent intent= getIntent();
        getbusNum=intent.getStringExtra("busNum");
        if(getbusNum!=null){
            Log.i("value", "값을 받았습니다: "+getbusNum);
        //   busNum=intent.getStringExtra("busNum");
        }



        Button goBusID = (Button) findViewById(R.id.goBusID);
        goBusID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//다음화면으로
                Intent intent = new Intent(getApplicationContext(), LastActivity.class);
                startActivity(intent);
            }
        });

        //다이얼로그에서 입력받은값으로 리스트뷰 불러옴
        GetData searchBusLine = new GetData();
        searchBusLine.execute(getbusNum);

      /*  Button searchBusNum = (Button) findViewById(R.id.searchBusNum);
        searchBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview = (TextView) findViewById(R.id.bus);
                textview = (TextView) findViewById(R.id.stop);
                mBusList.clear();

          //      GetData searchBusLine = new GetData();

            //   searchBusLine.execute(busnamesearch.getText().toString());
          //      searchBusLine.execute(getbusNum);
                //  searchBusLine.execute(busnamesearch.getText().toString());

            }
        });*/
        mBusList = new ArrayList<>();

    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Toast.makeText(this, "선택", Toast.LENGTH_SHORT).show();
//db내용 불러오고 노티파이
//
                list=(ListView)findViewById(R.id.listView1);
                GetData searchBusLine = new GetData();
                searchBusLine.execute(getbusNum);

                mBusList = new ArrayList<>();


                //    adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private class GetData extends AsyncTask<String, Void, String> {

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

            if (result == null) {
                textview.setText(errorString);
            } else {
                myJSON = result;

                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];
            String serverURL = "http://172.30.7.215/bus.php";
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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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


    private void showResult() {
        try {
            final JSONObject jsonObject = new JSONObject(myJSON);
            final JSONArray busArray = jsonObject.getJSONArray(TAG_RESULT);
            HashMap<String, String> BusHashMap;
            HashMap<String, Integer> BellHashMap = new HashMap<>();
            bellArray = new int[7];

            for (int i = 0; i < busArray.length(); i++) {
                JSONObject item = busArray.getJSONObject(i);
                String id = item.getString(TAG_ID);
                String busname = item.getString(TAG_BNAME);
                String bustopname = item.getString(TAG_SNAME);
                //int bell=item.getInt(TAG_BELL);
                String bell = String.valueOf(item.getInt(TAG_BELL));
                Log.i("여기보삼", "호"+bell);
                BusHashMap = new HashMap<>();
                //     BellHashMap=new HashMap<>();

                BusHashMap.put(TAG_ID, id);
                BusHashMap.put(TAG_BNAME, busname);
                BusHashMap.put(TAG_SNAME, bustopname);
                BusHashMap.put(TAG_BELL, bell);
             //   BellHashMap.put(TAG_BELL, bell);

               // bellArray[i] = BellHashMap.get(TAG_BELL);
                //Log.i("벨해쉬멥", "벨값"+BellHashMap.get(TAG_BELL));//BellHashMap에는 벨값 제대로 저장됨


                mBusList.add(BusHashMap);


            }

            for (int i = 0; i < busArray.length(); i++) {
                Log.i("벨해쉬멥", "벨값" + BellHashMap.get(TAG_BELL));//BellHashMap에는 벨값 제대로 저장됨
            }

            for (int j = 0; j < BellHashMap.size(); j++) {

                Log.i("NEW", "bellArray: " + bellArray[j]);
            }


            // ImageView road = (ImageView)findViewById(R.id.road);
            /*ListAdapter*/
            adapter = new SimpleAdapter(
                    // ImageView road = (ImageView)findViewById(R.id.road);
                    busLine.this, mBusList, R.layout.list_item,
                    new String[]{TAG_BNAME, TAG_SNAME, TAG_BELL},
                    new int[]{R.id.busname, R.id.bustopname, R.id.bell}
                    );//여기에 bell=1이면 이미지 정착

            list.setAdapter(adapter);
           final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                int[] count = new int[]{0, 0, 0, 0, 0, 0, 0};

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {//int position, long id는 클릭된 항목의 id
                    //1. 클릭된 mBusList리스트의 id나 location값으로 클릭된 정류장이름 찾아서  2.php로 보냄
                    count[position] += 1;//아이템의 클릭 횟수 카운트
                    try {
                        clickstop = busArray.getJSONObject(position).getString(TAG_SNAME);//클릭된 아이디로 정류장이름 찾기
                        if (count[position] > 0) {


                            if (count[position] >= 2) {
                                customDialog = new CustomDialog(busLine.this,
                                        "예약하기", // 제목
                                        "이미 예약되었습니다", // 내용
                                        leftListener// 왼쪽 버튼 이벤트
                                       ); // 오른쪽 버튼 이벤트
                                customDialog.show();
                            } else {

                                if (bellArray[position] >= 1) {

                                    customDialog = new CustomDialog(busLine.this,
                                            "예약하기", // 제목
                                            "이미 예약되었습니다", // 내용
                                            leftListener// 왼쪽 버튼 이벤트
                                    ); // 오른쪽 버튼 이벤트
                                    customDialog.show();

                                } else {
                                    customDialog = new CustomDialog(busLine.this,
                                            "예약하기", // 제목
                                            "예약되었습니다!", // 내용
                                            leftListener// 왼쪽 버튼 이벤트
                                    ); // 오른쪽 버튼 이벤트
                                    customDialog.show();


                                }

                            }
                            GetBustop searchBustop = new GetBustop();
                            searchBustop.execute(clickstop);

                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "showResult : ", e);
                    }


                }
            });
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//리스트 하나만 선택하도록 하는거*/

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    private class GetBustop extends AsyncTask<String, Void, String> {

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
            clickstop = result;
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];

            String serverURL = "http://172.30.7.215/bus.php";
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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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
private View.OnClickListener leftListener = new View.OnClickListener() {
    public void onClick(View v) {
        customDialog.dismiss();
    }
};

}