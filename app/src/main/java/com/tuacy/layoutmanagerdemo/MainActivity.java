package com.tuacy.layoutmanagerdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tuacy.layoutmanagerdemo.card.CardActivity;
import com.tuacy.layoutmanagerdemo.table.TableActivity;


public class MainActivity extends AppCompatActivity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		findViewById(R.id.button_card_layout_manager).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CardActivity.startUp(mContext);
			}
		});

		findViewById(R.id.button_table_layout_manager).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TableActivity.startUp(mContext);
			}
		});

	}
}
