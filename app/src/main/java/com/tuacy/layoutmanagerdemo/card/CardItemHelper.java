package com.tuacy.layoutmanagerdemo.card;


import android.support.v7.widget.RecyclerView;

public interface CardItemHelper {

	void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

}
