package junghyun.myapplication;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private CustomDialog customDialog;
    private CustomDialog1 customDialog1;
    private CustomDialog2 customDialog2;
    private static final int request_code = 0;
    private static final String TAG = "ActivityLifeCycle";
    public String tobusNum;
    public String tobusid;
    public String topassword;
    public TextView epassword;
    private static final String TAG_BNAME = "busname";
    private static final String TAG_BUSID = "busid";
    private static final String TAG_PW = "password";
    private static final String TAG_RESULT = "webnautes";

    EditText ebusNum;
    EditText ebusid;
    EditText ebuspassword;
    EditText ebuspassword1;
    String myJSON;
    String busname;
    String busid;
    String password;
    TextView textview;
    String topassword1;
    Toolbar toolbar;
    String serverURL = "http://172.20.10.2/realpw.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout=inflater.inflate(R.layout.activity_custom_dialog1, (ViewGroup)findViewById(R.id.customdialog1));


    // 검색에 사용될 데이터를 리스트에 추가한다.


       // ImageView logo = (ImageView) findViewById(R.id.logo);
       // TextView text2 = (TextView) findViewById(R.id.textView2);
        //TextView text3 = (TextView) findViewById(R.id.textView3);
        ImageButton goBusNum = (ImageButton) findViewById(R.id.reservation);
        goBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog1 = new CustomDialog1(MainActivity.this, new View.OnClickListener() {

                    public void onClick(View v) {
                        ebusNum = ((EditText) customDialog1.findViewById(R.id.busNum));
                        ebusid = ((EditText) customDialog1.findViewById(R.id.busid));
                        ebuspassword = ((EditText) customDialog1.findViewById(R.id.password));

                        // if(!ebusNum.getText().equals(" ") && !ebusid.getText().equals(" ") && !ebuspassword.getText().equals(" ")) {
                        //    if(ebusNum!=null && ebusid!=null && ebuspassword!=null){

                        if (ebusNum.getText().toString() != " " && ebusid.getText().toString() != "" && ebuspassword.getText().toString() != "") {
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

                        } else {
                            Toast.makeText(MainActivity.this, "정보를 입력하세요", Toast.LENGTH_LONG).show();
                        }
                    }
                }, rightListener);
                customDialog1.show();
            }
        }
        );

        ImageButton goReal = (ImageButton) findViewById(R.id.realTime);
        goReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             customDialog2 = new CustomDialog2(MainActivity.this, new View.OnClickListener() {
                 public void onClick(View v) {
                    ebuspassword1=((EditText) customDialog2.findViewById(R.id.password1));
                     if (ebuspassword1.getText().toString() != " ") {
                         topassword1=ebuspassword1.getText().toString();
                         //여기서 검사
                         GetPW getpw = new GetPW();
                         getpw.execute(topassword1);
                    //     Intent intent = new Intent(getApplicationContext(), bell.class);
                      //   startActivity(intent);
                     }
                 }
             }, rightListener2);
                customDialog2.show();

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater1 = getMenuInflater();
        inflater1.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_descript:

                customDialog=new CustomDialog(MainActivity.this,
                        "사용 안내",
                        "하차 정류장을 예약하시려면 '예약'버튼을" +
                        "   바로 하차하시려면 'BELL'을 누르십시오.",
                            normalListener
                );
                customDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (ebusNum.getText().toString() == " " && ebusid.getText().toString() == "" && ebuspassword.getText().toString() == ""){
                customDialog = new CustomDialog(MainActivity.this,
                        "입력해주세요.", // 제목
                        "다시 입력해주세요", // 내용
                        errorListener// 에러 이벤트
                );
            customDialog.show();}
            else {
                Intent intent = new Intent(getApplicationContext(), bell.class);
                //   intent.putExtra("busNum",busNum);
                startActivity(intent);
            }
        }
    };
    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog1.dismiss();
        }
    };

    private View.OnClickListener rightListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog2.dismiss();
        }
    };

    private View.OnClickListener normalListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

    private View.OnClickListener errorListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            customDialog.dismiss();
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

    private class GetPW extends AsyncTask<String, Void, String> {
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
                Toast.makeText(MainActivity.this, "Null입니다.", Toast.LENGTH_LONG).show();
            } else {
                myJSON = result;
                Log.d(TAG, "response - " + result);
                try {

                        Log.i("result 값: ", result);
                        final JSONObject jsonObject1 = new JSONObject(myJSON);//result: 곧 정차합니다->제이슨객체로 바꿔야함
                        JSONArray valid = jsonObject1.getJSONArray(TAG_RESULT);
                        //  JSONArray alarm_array=jsonObject1.optJSONArray(TAG_RESULT);
                        Log.i("비밀번호", "비밀번호" + valid.getJSONObject(0).toString());
                        Intent intent1= new Intent(getApplicationContext(), bell.class);
                        startActivity(intent1);

                } catch (JSONException e) {
                    customDialog = new CustomDialog(MainActivity.this,
                            "정보가 틀렸습니다.", // 제목
                            "다시 입력해주세요", // 내용
                            errorListener// 에러 이벤트
                    );
                    customDialog.show();
                    Log.d(TAG, "showResult : ", e);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            topassword1 = (String)params[0];

          //  String serverURL = "http://192.168.0.7/realpw.php";
            String postParameters = "&password1="+topassword1;

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
