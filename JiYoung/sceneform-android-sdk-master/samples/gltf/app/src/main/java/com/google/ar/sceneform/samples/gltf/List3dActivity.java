package com.google.ar.sceneform.samples.gltf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class List3dActivity extends AppCompatActivity {

    ArrayList<SampleData> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list3d);

        this.InitializeData();

        ListView listView = (ListView) findViewById(R.id.listview);
        final ListAdapter listadapter = new ListAdapter(this, dataList);

        listView.setAdapter(listadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long l) {

                String key = listadapter.getItem(position).getKey();
//                String newUri = "http://www.hanssem.store/" + key;
                String newUri = "http://www.hanssem.store/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUri));

                startActivity(intent);

//                Toast.makeText(getApplicationContext(), listadapter.getItem(position).getFullName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void InitializeData(){
        dataList = new ArrayList<SampleData>();

        dataList.add(new SampleData(R.drawable.i668317, "668317", "한샘 하이 엠마 천연가죽 3인용 소파", 211));
        dataList.add(new SampleData(R.drawable.i668318, "668318", "한샘 하이 엠마 천연가죽 4인용 소파", 301));
        dataList.add(new SampleData(R.drawable.i681946, "681946", "한샘 하이 모먼트 헤드무빙 천연가죽 소파 4인용", 285));
        dataList.add(new SampleData(R.drawable.i681947, "681947", "한샘 하이 모먼트 헤드무빙 천연가죽 소파 3인용", 210));
        dataList.add(new SampleData(R.drawable.i737686, "737686", "한샘 하이 브리오 이태리 천연가죽 4인용 소파", 285));
        dataList.add(new SampleData(R.drawable.i737687, "737687", "한샘 하이 브리오 이태리 천연가죽 3.5인용 소파", 240));
        dataList.add(new SampleData(R.drawable.i746525, "746525", "한샘 프라임 노블 천연면피가죽 전동 리클라이너 소파 4인용", 263));
        dataList.add(new SampleData(R.drawable.i746526, "746526", "한샘 프라임 노블 천연면피가죽 전동 리클라이너 소파 3인용", 192));
        dataList.add(new SampleData(R.drawable.i746540, "746540", "한샘 프라임 리츠 천연면피가죽 전동 리클라이너 소파 4인용", 263));
        dataList.add(new SampleData(R.drawable.i746541, "746541", "한샘 프라임 리츠 천연면피가죽 전동 리클라이너 소파 3인용", 192));
        dataList.add(new SampleData(R.drawable.i772973, "772973", "한샘 클로즈 침대 SS 슈퍼싱글 코튼그레이", 116));
        dataList.add(new SampleData(R.drawable.i777039, "777039", "한샘 밀로 패브릭소파 3인용", 200));
        dataList.add(new SampleData(R.drawable.i777040, "777040", "한샘 밀로 패브릭소파 4인용", 280));
        dataList.add(new SampleData(R.drawable.i786840, "786840", "한샘 모아 모듈형 패브릭소파 3인용", 252));
        dataList.add(new SampleData(R.drawable.i786841, "786841", "한샘 모아 모듈형 패브릭소파 3인 카우치", 252));
        dataList.add(new SampleData(R.drawable.i786842, "786842", "한샘 모아 모듈형 패브릭소파 4인 오픈형", 280));
        dataList.add(new SampleData(R.drawable.i787819, "787819", "한샘 듀이 아르떼 패브릭소파 4인 카우치", 301));
        dataList.add(new SampleData(R.drawable.i787823, "787823", "한샘 듀이 아르떼 패브릭소파 4인용", 301));
        dataList.add(new SampleData(R.drawable.i796379, "796379", "한샘 엠마 테일러 천연면피가죽 4인용 소파", 278));
        dataList.add(new SampleData(R.drawable.i796416, "796416", "한샘 엠마 테일러 천연면피가죽 3인용 소파", 185));
        dataList.add(new SampleData(R.drawable.i799215, "799215", "한샘 엠마 컴포트 천연면피가죽 3인용 소파", 200));
        dataList.add(new SampleData(R.drawable.i799220, "799220", "한샘 엠마 컴포트 천연면피가죽 4인용 소파", 268));

    }
}