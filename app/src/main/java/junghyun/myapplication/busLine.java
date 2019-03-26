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
import android.os.Looper;
import android.os.Message;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Window.FEATURE_CUSTOM_TITLE;

public class busLine extends AppCompatActivity {

    private CustomDialog customDialog;
    private CustomDialog2 customDialog2;
    int temp; // 임시 전역변수 - singleChoiceItems 에서 선택항목 저장시 사용
    EditText updatepassword;
    private static String TAG = "phpquerytest";
    private static final String TAG_RESULT = "webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_BNAME = "busname";
    private static final String TAG_SNAME = "bustopname";
    private static final String TAG_BUSID = "busid";
    private static final String TAG_BELL = "bell";
    private static final String TAG_BPOS="bpos";
    private static final String TAG_ROAD = "road";
    JSONObject item;
    JSONObject click_postop;
    ArrayList<HashMap<String, String>> mBusList;
    ListAdapter adapter;
    ListView list;
    String myJSON;
    TextView textview;
    EditText editText;
    String clickstop;
    String reservedstop;
    String bell;
    int option_click=0;
    EditText busnamesearch;
    //int []count=new int[7];
    int []count = new int[]{0, 0, 0, 0, 0, 0, 0};
    int err_cnt;
    int clicked_position;
    int bellArray[];
    int busid[];
    String getbusNum;
    String getbusid;
    String getpassword;
    String getupdatepassword;
    String alarm_bell="1";
    public final static int REPEAT_DELAY = 1000;

    public Handler handler= new Handler();

    String   posupdateURL = "http://223.194.154.47/posupdate.php";
    TimerTask tt;
    Timer timer;
    private Boolean isRunning = true;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line);


        textview = (TextView) findViewById(R.id.textView);
        list = (ListView) findViewById(R.id.listView1);
        btn=(Button)findViewById(R.id.btn);
        //  busnamesearch = (EditText) findViewById(R.id.editBusNum);


        Intent intent = getIntent();
        getbusNum = intent.getStringExtra("busNum");//////////////////////////main에서 인텐트로 받은 버스 번호
        getbusid = intent.getStringExtra("busid");
        getpassword = intent.getStringExtra("password");
        if (getbusNum != null && getbusid != null && getpassword != null) {
            Log.i("value", "값을 받았습니다: " + getbusNum + " id값을 받았습니다. " + getbusid + " 비밀번호: " + getpassword);
            //   busNum=intent.getStringExtra("busNum");

        }

        /*다이얼로그에서 버스번호,아이디 입력받은값으로 리스트뷰 불러옴*/
      //  GetData searchBusLine = new GetData();
        //searchBusLine.execute(getbusNum, getbusid, getpassword);
        //mBusList = new ArrayList<>();

      //    GetAlarm alarm = new GetAlarm();
        //alarm.execute(posupdateURL);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        //        GetAlarm alarm=new GetAlarm();
          //      alarm.execute(posupdateURL);
            //    handler.sendEmptyMessage(0);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                option_click=1;
                Toast.makeText(this, "새로고침", Toast.LENGTH_SHORT).show();
                //db내용 불러오고 노티파이
                // list = (ListView) findViewById(R.id.listView1);
                GetData searchBusLine = new GetData();
                searchBusLine.execute(getbusNum, getbusid, getpassword);
                mBusList = new ArrayList<>();
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
            String busname = (String) params[0];
            String busid = (String) params[1];
            String password = (String) params[2];
            String serverURL = "http://223.194.154.47/bus.php";
            String postParameters = "busname=" + busname + "&busid=" + busid + "&password=" + password;

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

                Log.i(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }


    private void showResult() {
        try {
            Log.i("myJSON 값 ", "myJSON값: " + myJSON);
            final JSONObject jsonObject = new JSONObject(myJSON);//////////서버에서 변수는 받았지만 디비값들을 못불러옴
            final JSONArray busArray = jsonObject.getJSONArray(TAG_RESULT);
            HashMap<String, String> BusHashMap;
            HashMap<String, Integer> BellHashMap = new HashMap<>();
            bellArray = new int[7];


            for (int i = 0; i < busArray.length(); i++) {
                item = busArray.getJSONObject(i);

                String busname = item.getString(TAG_BNAME);
                String busid = item.getString(TAG_BUSID);
                String bustopname = item.getString(TAG_SNAME);
                int bell1 = item.getInt(TAG_BELL);

                if (item.getInt(TAG_BELL) == 1) {
                    bell = "예약";//여기에 토스트 알림 띄우기
                } else {
                    bell = " ";
                }

                /* GetData에서 할지 GetBustop에서 할지 결정하기
                 * */

                //    String bell = String.valueOf(item.getInt(TAG_BELL));//int형bell을 list에 나타내기 위해 string로 변환

                BusHashMap = new HashMap<>();
                //     BellHashMap=new HashMap<>();

                //      BusHashMap.put(TAG_ID, id);
                BusHashMap.put(TAG_BNAME, busname);
                BusHashMap.put(TAG_BUSID, busid);
                BusHashMap.put(TAG_SNAME, bustopname);
                BusHashMap.put(TAG_BELL, bell);


                busid = BusHashMap.get(TAG_BUSID);
                Log.i(" 아이디: ", busid);
                BellHashMap.put(TAG_BELL, bell1);

                bellArray[i] = BellHashMap.get(TAG_BELL);
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
                    new String[]{TAG_BNAME, TAG_BUSID, TAG_SNAME, TAG_BELL},
                    new int[]{R.id.busname, R.id.busid, R.id.bustopname, R.id.bell}
            );//여기에 bell=1인걸 따로 표시하는 방법 생각

            list.setAdapter(adapter);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //  int []count = new int[]{0, 0, 0, 0, 0, 0, 0};


                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) {//int position, long id는 클릭된 항목의 id
                    //1. 클릭된 mBusList리스트의 id나 location값으로 클릭된 정류장이름 찾아서  2.php로 보냄
                    clicked_position=position;
                    count[position] += 1;//아이템의 클릭 횟수 카운트
                    err_cnt=count[position];
                    try {
                        clickstop = busArray.getJSONObject(position).getString(TAG_SNAME);//클릭된 아이디로 정류장이름 찾기

                        if (count[position] > 0) {//클릭 발생시
                            if (count[position] >= 2) {//2번 클릭한 사용자는 알림을 수신할 필요 x
                                customDialog = new CustomDialog(busLine.this,
                                        "예약하기", // 제목
                                        "이미 예약되었습니다", // 내용
                                        leftListener// 왼쪽 버튼 이벤트
                                ); // 오른쪽 버튼 이벤트
                                customDialog.show();
                            } else

                            if (bellArray[position] >= 1) {//한 번 클릭하면 알림 수신 가능

                                customDialog = new CustomDialog(busLine.this,
                                        "예약하기", // 제목
                                        "예약되었습니다", // 내용
                                        leftListener// 왼쪽 버튼 이벤트
                                ); // 오른쪽 버튼 이벤트
                                customDialog.show();

                            } else {
                                customDialog = new CustomDialog(busLine.this,
                                        "예약하기", // 제목
                                        "예약되었습니다!", // 내용
                                        rightListener// 오른쪽 버튼 이벤트
                                );
                                customDialog.show();
                                GetBustop searchBustop = new GetBustop();
                                searchBustop.execute(clickstop);

                            }

                            //}




                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "showResult : ", e);
                    }


                }
            });
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//리스트 하나만 선택하도록 하는거*/

        } catch (JSONException e) {
            /*입력한 정보가 하나라도 틀리거나 null일 경우*//*시간이 지나 비밀번호가 바뀐 경우 통과*/
            Log.i("클릭된 포지션", ": "+count[clicked_position] + " "+err_cnt);
            Log.i("클릭된 리스트", ": "+clicked_position);
            if (err_cnt== 1 || option_click==1) {
                customDialog = new CustomDialog(busLine.this,
                        "비밀번호가 바뀌었습니다.", // 제목
                        "다시 입력해주세요", // 내용
                        errorListener// 에러 이벤트
                );
                customDialog.show();
            }else {
                customDialog = new CustomDialog(busLine.this,
                        "정보가 틀렸습니다.", // 제목
                        "다시 입력해주세요", // 내용
                        errorListener// 에러 이벤트
                );

                customDialog.show();
            }
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
            /*알림 받기조건:( bell=1 && clickstop && bpos=7일 떄), (bell=1 && clickstop && bpos=21) 이렇게 각각 하기*/
//alarm_bell에 1인 값 저잗되어있음,
            String serverURL = "http://223.194.154.47/bus.php";

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

    /*알림을 받는 부분*/
    private class GetAlarm extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(busLine.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            Log.d(TAG, "response - " + result);
            try {
                Log.i("result 값: ", result);
                final JSONObject jsonObject1 = new JSONObject(result);//result: 곧 정차합니다->제이슨객체로 바꿔야함
                final JSONArray alarm_array = jsonObject1.getJSONArray(TAG_RESULT);
                //  JSONArray alarm_array=jsonObject1.optJSONArray(TAG_RESULT);
                Log.i("알림메시지", "알림메시지" + alarm_array.getJSONObject(0).toString());
                Toast.makeText(getApplicationContext(), "곧 정차합니다.", Toast.LENGTH_LONG).show();
                //   myJSON1 = result;//clickstop을 php로 보내는거, GetData다음에 GetAlarm이 호출되므로 clickstop은 null값이 아님
                //  showResult2();
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }


        }
        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];
            //  posupdateURL = "http://192.168.0.7/posupdate.php";
            //   String postParameters = "clickstop=" + searchKeyword; //php로 전달하는 매개변수
            try {


                URL url = new URL(posupdateURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

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


        }//doin함수 종료
    }

    /*
        private void showResult2() {
            try {
               Log.i("myJSON ", "myJSON 값: " + myJSON);
                final JSONObject jsonObject1 = new JSONObject(myJSON);//////////서버에서 변수는 받았지만 디비값들을 못불러옴
                final JSONArray alarm_array = jsonObject1.getJSONArray(TAG_RESULT);
                //item=jsonObject1.getJSONObject(p);

                  //  Log.i("php에서 받은 변수: ", item.getJSONObject(TAG_RESULT).toString());
                Log.i("곧 정차합니다.", alarm_array.getJSONObject(0).toString());
                  // click_postop=alarm_array.getJSONObject(0);//php에서 받은 json객체의 문자열을 click_postop에 저장
                    Toast.makeText(getApplicationContext(), "도착합니다 : "+ alarm_array.getJSONObject(0), Toast.LENGTH_LONG).show();

            }

             catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }
        }
    */
    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog.dismiss();//
        }
    };
    private View.OnClickListener errorListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            customDialog.dismiss();
        }
    };


    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            //list=(ListView)findViewById(R.id.listView1);

            GetData searchBusLine = new GetData();
            searchBusLine.execute(getbusNum, getbusid, getpassword);


            mBusList = new ArrayList<>();
            customDialog.dismiss();
        }
    };

    @Override

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, getLocalClassName() + ".onDestroy");
    }




}

