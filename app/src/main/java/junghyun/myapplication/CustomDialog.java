package junghyun.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {
    private TextView title;
    private TextView content;
    private Button mPositiveButton;
    private String mTitle;
    private String mContent;

    private View.OnClickListener mPositiveListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_custom_dialog);
        title=(TextView)findViewById(R.id.title) ;
        content=(TextView)findViewById(R.id.content);
//제목과 내용은 생성자에서 설정
        title.setText(mTitle);
       content.setText(mContent);


        //셋팅
        mPositiveButton=(Button)findViewById(R.id.pbutton);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(mPositiveListener);

    }

    //생성자 생성
    public CustomDialog(@NonNull Context context,String mTitle,
                        String mContent, View.OnClickListener positiveListener) {
        super(context);
        this.mTitle=mTitle;
        this.mContent=mContent;
        this.mPositiveListener = positiveListener;

    }
}


