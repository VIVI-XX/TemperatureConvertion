package com.example.tinkpad.temperatureconvertion;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {

    private final String TAG = "Rate";
    Handler handler;
    private float dollarRate = 1 / 6.7f;
    private float euroRate = 1 / 11f;
    private float wonRate = 167f;

    EditText rmb;
    TextView show;
    private String updateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);
        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);//只能自己用
        //获取SharedPreferences的第二种方法，只能获得一个默认的配置文件
        //SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f); //默认值0
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
        updateDate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        final String todayStr = sdf.format(today);


        Log.i(TAG, "onCreate: sp dollarRate= " + dollarRate);
        Log.i(TAG, "onCreate: sp euroRate= " + euroRate);
        Log.i(TAG, "onCreate: sp wonRate= " + wonRate);
        Log.i(TAG, "onCreate: sp updateDate= " + updateDate);
        Log.i(TAG, "onCreate: sp todayStr= " + todayStr);
        //判断时间
        if (!todayStr.equals(updateDate)) {
            Log.i(TAG, "onCreate: 需要更新 ");
            //开启子线程
            Thread t = new Thread(this);
            t.start();

        } else {
            Log.i(TAG, "onCreate: 不需要更新 ");
        }


        //开启子线程
        ///Thread t = new Thread(this);
        //t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 5) {
                    Bundle bdl = (Bundle) msg.obj;//obj需要是一个string/BUNDLE数据才可以强转成功
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");
                    Log.i(TAG, "handleMessage: dollar" + dollarRate);
                    Log.i(TAG, "handleMessage: euro" + euroRate);
                    Log.i(TAG, "handleMessage: won" + wonRate);
                    //保存更新的日期

                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    //一并保存下来，避免日期更改后汇率丢失
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.apply();

                    Toast.makeText(RateActivity.this, "汇率更新", Toast.LENGTH_SHORT).show();


                }
            }

        };
    }


    public void onClick(View btn) {
        float r = 0;

        //获取用户输入内容
        String str = rmb.getText().toString();
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            Toast.makeText(this, "请输入人民币金额！", Toast.LENGTH_SHORT).show();
        }


        if (btn.getId() == R.id.btn_dollar) {
            float val = r * dollarRate;
            show.setText(String.format("%.2f", val));
        } else if (btn.getId() == R.id.btn_euro) {
            float val = r * euroRate;
            show.setText(String.format("%.2f", val));
        } else if (btn.getId() == R.id.btn_won) {
            float val = r * wonRate;
            show.setText(String.format("%.2f", val));

        }
    }

    public void openRate(View btn) {
        openConfig();

    }

    private void openConfig() {
        Log.i("open", "openRate");
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);

        Log.i(TAG, "openRate: " + dollarRate);
        Log.i(TAG, "openRate: " + euroRate);
        Log.i(TAG, "openRate: " + wonRate);
        //访问网页
        //Intent web=new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.baidu.com"));
        //拨打电话,跳转页面
        //Intent tel=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:17361049615"));
        //拨打电话，直接拨出
        //Intent tel=new Intent(Intent.ACTION_CALL,Uri.parse("tel:17361049615"));

        //startActivity(config);
        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openConfig();

        } else if (item.getItemId() == R.id.open_list) {
            //打开列表窗口
            Intent list = new Intent(this, MyList2Activity.class);
            startActivity(list);


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            /* bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);*/
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("key_euro", 0.1f);
            wonRate = bundle.getFloat("key_won", 0.1f);
            Log.i(TAG, "onActivityResult: " + dollarRate);
            Log.i(TAG, "onActivityResult: " + euroRate);
            Log.i(TAG, "onActivityResult: " + wonRate);

            //将新设置汇率写道SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
            //apply()不等待它存完，commit等待它存完

            Log.i(TAG, "onActivityResult: 数据已保存到sharePreferences");


        }
    }

    @Override
    public void run() {
        Log.i(TAG, "run: run……");
        for (int i = 1; i < 6; i++) {
            Log.i(TAG, "run: i=" + i);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //用于保存用户获得的汇率
        Bundle bundle;


        //obj 可以是任何数据 what用于标记当前属性(改变内容)

        //获取网络数据
       /* URL url = null;
        try {
            url = new URL("http://www.boc.cn/sourcedb/whpj/");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);
            Document doc = Jsoup.parse(html);

            Log.i(TAG, "run: html" + html);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        bundle = getFromBOC();
        //bundle中保存所获得的汇率

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage();
        msg.what = 5;
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);


    }
    /*
    从其他的网页获得数据，只用更改网站名和方法名，并对内容做一些处理即可
     */

    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc=Jsoup.parse(html);
            Log.i(TAG, "run: doc title" + doc.title());
            //获取table中的数据
            Elements tables = doc.getElementsByTag("table");
            /*int i=1;
            for(Element table :tables) {
                Log.i(TAG, "run: table["+i+"]"+table);
                i++;
            }
            查询所需内容*/
            Element table2 = tables.get(1);//下标从0开始
            Log.i(TAG, "run: table2" + table2);
            //获取td中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG, "run: text= " + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩国元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }

            }
            /*for(Element td:tds) {
                Log.i(TAG, "run: td="+td);
                Log.i(TAG, "run: text "+td.text());
                Log.i(TAG, "run: html "+td.html());

            }查看td情况*/


        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle getFromUSD() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc=Jsoup.parse(html);
            Log.i(TAG, "run: doc title" + doc.title());
            //获取table中的数据
            Elements tables = doc.getElementsByTag("table");
            /*int i=1;
            for(Element table :tables) {
                Log.i(TAG, "run: table["+i+"]"+table);
                i++;
            }
            查询所需内容*/
            Element table1 = tables.get(0);//下标从0开始
            Log.i(TAG, "run: table1" + table1);
            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG, "run: text= " + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }

            }
            /*for(Element td:tds) {
                Log.i(TAG, "run: td="+td);
                Log.i(TAG, "run: text "+td.text());
                Log.i(TAG, "run: html "+td.html());

            }查看td情况*/


        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    private String inputStream2String(InputStream inputStream) {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = null;
        try {
            in = new InputStreamReader(inputStream, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (; ; ) {
            int rsz = 0;
            try {
                rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return out.toString();


    }
    }
