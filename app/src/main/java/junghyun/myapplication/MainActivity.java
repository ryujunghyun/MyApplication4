package junghyun.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text=(TextView)findViewById(R.id.textView2);
        Button goBusNum = (Button) findViewById(R.id.reservation);
        goBusNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), busPW.class);
                //intent.putExtra("busNum",busNum);
                startActivity(intent);
            }
        });

        Button goReal = (Button) findViewById(R.id.realTime);
        goReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pwForRealtime.class);
                //intent.putExtra("busNum",busNum);
                startActivity(intent);
            }
        });
    }



}