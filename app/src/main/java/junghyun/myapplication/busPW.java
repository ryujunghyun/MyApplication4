package junghyun.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                Intent intent = new Intent(getApplicationContext(), busLine.class);
                // intent.putExtra("busNum",busNum);
                startActivity(intent);
            }
        });

    }

}
