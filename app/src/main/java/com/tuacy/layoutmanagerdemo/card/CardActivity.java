package com.tuacy.layoutmanagerdemo.card;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.tuacy.layoutmanagerdemo.R;

import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {

	public static void startUp(Context context) {
		context.startActivity(new Intent(context, CardActivity.class));
	}

	private Context        mContext;
	private RecyclerView   mRecyclerView;
	private CardAdapter    mAdapter;
	private List<CardBean> mDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
		mContext = this;
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		mRecyclerView = findViewById(R.id.recycler_card);
		CardLayoutManager layoutManager = new CardLayoutManager();
		mRecyclerView.setLayoutManager(layoutManager);
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CardTouchCallback(mCardItemHelper));
		itemTouchHelper.attachToRecyclerView(mRecyclerView);
	}

	private void initEvent() {

	}

	private void initData() {
		mAdapter = new CardAdapter(mContext, mDataList = obtainDataList());
		mRecyclerView.setAdapter(mAdapter);
	}

	private List<CardBean> obtainDataList() {
		List<CardBean> cardList = new ArrayList<>();
		cardList.add(new CardBean("高安", R.drawable.ic_gaoan));
		cardList.add(new CardBean("萍乡", R.drawable.ic_pingxiang));
		cardList.add(new CardBean("吉安", R.drawable.ic_jian));
		cardList.add(new CardBean("九江", R.drawable.ic_jiujiang));
		cardList.add(new CardBean("南昌", R.drawable.ic_nanc));
		cardList.add(new CardBean("上饶", R.drawable.ic_shangrao));
		cardList.add(new CardBean("宜春", R.drawable.ic_yichun));
		cardList.add(new CardBean("鹰潭", R.drawable.ic_yingtan));
		cardList.add(new CardBean("抚州", R.drawable.ic_fuzhou));
		return cardList;
	}

	private CardItemHelper mCardItemHelper = new CardItemHelper() {
		@Override
		public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
			mDataList.remove(viewHolder.getAdapterPosition());
			mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
		}
	};
}
