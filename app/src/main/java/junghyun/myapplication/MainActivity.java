package junghyun.myapplication;

import android.content.ContentValues;
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

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    private CustomDialog1 customDialog1;
    private static final int request_code = 0;
    private static final String TAG = "ActivityLifeCycle";
    public String busNum;
    public String password;

    public TextView epassword;
    EditText ebusNum;

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
                        busNum=ebusNum.getText().toString();
                        Log.i("mainvalue", "메인액티비티에서 입력받은 버스: "+ebusNum);
                        Intent intent = new Intent(getApplicationContext(), busLine.class);
                        intent.putExtra("busNum",busNum);
                        startActivity(intent);
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
                     busNum=ebusNum.getText().toString();
                     Intent intent = new Intent(getApplicationContext(), bell.class);
                     intent.putExtra("busNum",busNum);
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
