package junghyun.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CustomDialog1 extends Dialog implements View.OnClickListener {
    private TextView title;
    public EditText epassword;
    public EditText ebusNum;
    private Button mPositiveButton;
    private Button mNegativeButton;
    public String mTitle;
    public String mPassword;
    public View.OnClickListener mPositiveListener;
    public View.OnClickListener mNegativeListener;
    public CustomDialogListener1 customDialogListener1;



    //인터페이스 설정
    interface CustomDialogListener1{
        void onPositiveClicked(String busNum,String password);
        void onNegativeClicked();
    }

    //액티비티에서호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener1 customDialogListener1){
        this.customDialogListener1 = customDialogListener1;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_custom_dialog1);
        title=(TextView)findViewById(R.id.title) ;
        epassword=(EditText) findViewById(R.id.password);
        ebusNum=(EditText) findViewById(R.id.busNum);


//제목과 내용은 생성자에서 설정

       // epassword.setText(mPassword);


        //셋팅
        mPositiveButton=(Button)findViewById(R.id.pbutton);
        mNegativeButton=(Button)findViewById(R.id.nbutton);
        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(mNegativeListener);

    } ;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pbutton: //확인 버튼을 눌렀을 때
                //변수에 EidtText에서 가져온 값을 저장
                String password = epassword.getText().toString();
                String busNum=ebusNum.getText().toString();
                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달

                customDialogListener1.onPositiveClicked(busNum, password);
                dismiss();
                break;
            case R.id.nbutton: //취소 버튼을 눌렀을 때
                cancel();
                break;
        }
    }



    public CustomDialog1(@NonNull Context context
                        /*String mPassword*/, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);

   //     this.mPassword=mPassword;
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;

    }
}
