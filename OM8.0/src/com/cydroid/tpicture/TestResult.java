package com.cydroid.tpicture;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.cydroid.tpicture.adapter.FireAdapter;
import com.cydroid.tpicture.utils.Singleton;

/**
 * Created by qiang on 4/10/18.
 */
public class TestResult extends BaseActivity {

    private ListView mListView;
    private TextView tvFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_result);

        initView();


    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.test_result_list);
        tvFlag = (TextView) findViewById(R.id.test_nv_flag);
    }


    private void initData() {

        if (getIntent().getBooleanExtra("nvram_flag_501",false)) {
            tvFlag.setTextColor(Color.GREEN);
            tvFlag.setText("TestResult:Pass");;
        }else {
            tvFlag.setTextColor(Color.RED);
            tvFlag.setText("TestResult:Fail");;
        }


        FireAdapter adapter = new FireAdapter(Singleton.getInstance().getPigList(), this.getApplication());
        mListView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
