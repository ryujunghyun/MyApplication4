package junghyun.myapplication;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private CustomDialog1 customDialog1;
    private static final int request_code = 0;
    private static final String TAG = "ActivityLifeCycle";
    public String tobusNum;
    public String tobusid;
    public String topassword;
    public TextView epassword;
    private static final String TAG_BNAME = "busname";
    private static final String TAG_BUSID = "busid";
    private static final String TAG_PW = "password";
   // private static String TAG = "phpquerytest";
    private static final String TAG_RESULT = "webnautes";
    EditText ebusNum;
    EditText ebusid;
    EditText ebuspassword;
    String myJSON;
    String busname;
    String busid;
    String password;
    TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout=inflater.inflate(R.layout.activity_custom_dialog1, (ViewGroup)findViewById(R.id.customdialog1));

        ImageView logo = (ImageView) findViewById(R.id.logo);
        TextView text2 = (TextView) findViewById(R.id.textView2);
        TextView text3 = (TextView) findViewById(R.id.textView3);
        Button goBusNum = (Button) findViewById(R.id.reservation);
        goBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog1 = new CustomDialog1(MainActivity.this,  new View.OnClickListener() {
                    public void onClick(View v) {
                        ebusNum=((EditText)customDialog1.findViewById(R.id.busNum));
                        ebusid=((EditText)customDialog1.findViewById(R.id.busid));
                        ebuspassword=((EditText)customDialog1.findViewById(R.id.password));
                        if(!ebusNum.getText().equals(" ") && !ebusid.getText().equals(" ") && !ebuspassword.getText().equals(" ")) {
                            tobusNum = ebusNum.getText().toString();
                            tobusid = ebusid.getText().toString();
                            topassword = ebuspassword.getText().toString();
                            //여기서 서버에 확인
                            Log.i("mainvalue", "메인액티비티에서 입력받은 버스: " + ebusNum);
                            Log.i("mainvalue", "메인액티비티에서 입력받은 버스아이디아이디: " + ebusid);
                            Intent intent = new Intent(getApplicationContext(), busLine.class);
                            intent.putExtra("busNum", tobusNum);
                            intent.putExtra("busid", tobusid);
                            intent.putExtra("password", topassword);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "정보를 입력하세요", Toast.LENGTH_LONG).show();
                        }
                    }
                }, rightListener);
                customDialog1.show();
            }
        });

        Button goReal = (Button) findViewById(R.id.realTime);
        goReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             customDialog1 = new CustomDialog1(MainActivity.this, new View.OnClickListener() {
                 public void onClick(View v) {
                     ebusNum=((EditText)customDialog1.findViewById(R.id.busNum));
                     tobusNum=ebusNum.getText().toString();

                     Intent intent = new Intent(getApplicationContext(), bell.class);
                     intent.putExtra("busNum",tobusNum);
                     startActivity(intent);
                 }
             }, rightListener);
                customDialog1.show();

            }
        });
    }



    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), bell.class);
         //   intent.putExtra("busNum",busNum);
            startActivity(intent);
        }
    };
    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog1.dismiss();
        }
    };

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != request_code || data == null)
            return;

        String msg = data.getStringExtra("ResultString");

        Log.i(TAG, "ActivityResult:" + resultCode + " " + msg);

    }
/*
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
            busname = (String)params[0];
            busid=(String)params[1];
            password=(String)params[2];
            String serverURL = "http://192.168.0.7/bus.php";
            String postParameters = "busname=" + busname + "&busid=" + busid + "&password="+password;

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

            for (int i = 0; i < busArray.length(); i++) {
                JSONObject item = busArray.getJSONObject(i);

                String showbusname = item.getString(TAG_BNAME);
                String showbusid = item.getString(TAG_BUSID);
                String showpassword = item.getString(TAG_PW);
                BusHashMap = new HashMap<>();

                BusHashMap.put(TAG_BNAME, showbusname);
                BusHashMap.put(TAG_BUSID, showbusid);
                BusHashMap.put(TAG_PW, showpassword);
                ///여기서 busname이랑 showbusname, 각각비교

            }
        }catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    */
    @Override

    protected void onStart() {

        super.onStart();

        Log.i(TAG, getLocalClassName() + ".onStart");

    }



    @Override

    protected void onRestart() {

        super.onRestart();

        Log.i(TAG, getLocalClassName() + ".onRestart");

    }



    @Override

    protected void onResume() {

        super.onResume();

        Log.i(TAG, getLocalClassName() + ".onResume");

    }



    @Override

    protected void onPause() {

        super.onPause();

        Log.i(TAG, getLocalClassName() + ".onPause");

    }



    @Override

    protected void onStop() {
        super.onStop();
        Log.i(TAG, getLocalClassName() + ".onStop");
    }

    @Override

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, getLocalClassName() + ".onDestroy");
    }

}
