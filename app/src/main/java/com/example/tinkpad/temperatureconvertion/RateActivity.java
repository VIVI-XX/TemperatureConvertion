package com.example.tinkpad.temperatureconvertion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {
    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);
    }

    public void onClick(View btn) {
        float r=0;

        //获取用户输入内容
        String str = rmb.getText().toString();
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        }else{
            Toast.makeText(this, "请输入人民币金额！", Toast.LENGTH_SHORT).show();
        }


        if (btn.getId() == R.id.btn_dollar) {
            float val = r/6.7f;
            show.setText(String.format("%.2f",val));
            }else if(btn.getId() == R.id.btn_euro) {
                float val = r /7.5f;
                show.setText(String.format("%.2f",val));
            }else if(btn.getId() == R.id.btn_won){
                float val = r *170;
                show.setText(String.format("%.2f",val));

        }
        }
    public void openRate (View btn){
        Log.i("open","openRate");
        Intent rate=new Intent(this,MainActivity.class);
        //访问网页
        //Intent web=new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.baidu.com"));
        //拨打电话
        //Intent tel=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:17361049615"));
        startActivity(rate);

    }
    }
