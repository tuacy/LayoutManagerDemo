package com.tuacy.layoutmanagerdemo.table;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.tuacy.layoutmanagerdemo.R;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {

	public static void startUp(Context context) {
		context.startActivity(new Intent(context, TableActivity.class));
	}

	private Context      mContext;
	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_table);
		mContext = this;
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		mRecyclerView = findViewById(R.id.recycler_table);
		TableLayoutManager layoutManager = new TableLayoutManager(4);
		mRecyclerView.setLayoutManager(layoutManager);
	}

	private void initEvent() {

	}

	private void initData() {
		TableAdapter adapter = new TableAdapter(mContext, obtainDataList());
		mRecyclerView.setAdapter(adapter);
	}

	private List<String> obtainDataList() {
		List<String> dataList = new ArrayList<>();
		dataList.add("1");
		dataList.add("2");
		dataList.add("3");
		dataList.add("4");
		dataList.add("5");
		dataList.add("6");
		dataList.add("7");
		dataList.add("8");
		dataList.add("9");
		dataList.add("10");
		dataList.add("11");
		dataList.add("12");
		dataList.add("13");
		dataList.add("14");
		dataList.add("15");
		dataList.add("16");
		dataList.add("17");
		dataList.add("18");
		dataList.add("19");
		dataList.add("20");
		dataList.add("21");
		dataList.add("22");
		dataList.add("23");
		dataList.add("24");
		dataList.add("25");
		dataList.add("26");
		dataList.add("27");
		dataList.add("28");
		dataList.add("29");
		dataList.add("30");
		dataList.add("31");
		dataList.add("32");
		return dataList;
	}
}
