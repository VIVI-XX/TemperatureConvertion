package com.example.tinkpad.temperatureconvertion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView out;
    TextView out1;
    EditText edit;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit =findViewById(R.id.inp);
        out =findViewById(R.id.txtout);
        out1 =findViewById(R.id.txtout1);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Log.i("main","onClick msg…");
        String str=edit.getText().toString();
        String str1=out1.getText().toString();
        float num=Float.parseFloat(str);
        num = num*9/5+32 ;
        BigDecimal bd=new BigDecimal(num);
        double num1= bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        out1.setText(str1 + num1 +"℉");

    }
}
