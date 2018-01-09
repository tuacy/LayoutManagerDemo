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
		TableLayoutManager layoutManager = new TableLayoutManager(10);
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
		for (int index = 1; index < 1000; index++) {
			dataList.add(index + "");
		}
		return dataList;
	}
}
