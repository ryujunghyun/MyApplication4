package junghyun.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private CustomDialog1 customDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        TextView text2 = (TextView) findViewById(R.id.textView2);
        TextView text3 = (TextView) findViewById(R.id.textView3);
        Button goBusNum = (Button) findViewById(R.id.reservation);
        goBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     Intent intent = new Intent(getApplicationContext(), busPW.class);
                //intent.putExtra("busNum",busNum);
           //     startActivity(intent);
                customDialog1 = new CustomDialog1(MainActivity.this, "비밀번호를 입력하세요", pListener, rightListener);
                customDialog1.show();
            }
        });

        Button goReal = (Button) findViewById(R.id.realTime);
        goReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             customDialog1 = new CustomDialog1(MainActivity.this, "비밀번호를 입력하세요", leftListener, rightListener);
                customDialog1.show();


                //    Intent intent = new Intent(getApplicationContext(), pwForRealtime.class);
                //intent.putExtra("busNum",busNum);
                //  startActivity(intent);

            }
        });
    }
    private View.OnClickListener pListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), busLine.class);
            //intent.putExtra("busNum",busNum);
            startActivity(intent);
        }
    };

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), bell.class);
            //intent.putExtra("busNum",busNum);
            startActivity(intent);
        }
    };
    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog1.dismiss();
        }
    };
}
