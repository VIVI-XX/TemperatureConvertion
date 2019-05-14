package com.example.tinkpad.temperatureconvertion;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {
    private final String TAG = "RateList";
    String data[] = {"wait..."};
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);
        //父类里已经包含布局
        List<String> list1 = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            list1.add("item" + i);


        }
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        setListAdapter(adapter);

        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this, android.R.layout.simple_list_item_1, list2);
                    setListAdapter(adapter);

                }
                super.handleMessage(msg);


            }

        };
    }

    @Override
    public void run() {
        //获取网络数据，放入list
        List<String> retList = new ArrayList<String>();
        Document doc = null;
        try {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc=Jsoup.parse(html);
            Log.i(TAG, "run: doc title" + doc.title());
            //获取table中的数据
            Elements tables = doc.getElementsByTag("table");

            Element table2 = tables.get(1);//下标从0开始
            Log.i(TAG, "run: table2" + table2);
            //获取td中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);

                String str1 = td1.text();
                String val = td2.text();

                Log.i(TAG, "run: " + td1.text() + "==>" + td2.text());
                retList.add(str1 + "==>" + val);


            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);


    }
}




