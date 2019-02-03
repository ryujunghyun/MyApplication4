package junghyun.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class busPW extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_pw);

        Button goBell = (Button) findViewById(R.id.goBell);
        goBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText editBusID = (EditText)findViewById(R.id.editBusID);
                //String busNum = editBusID.toString();
                Intent intent = new Intent(getApplicationContext(), bell.class);
                // intent.putExtra("busNum",busNum);
                startActivity(intent);
            }
        });
   /*     Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
