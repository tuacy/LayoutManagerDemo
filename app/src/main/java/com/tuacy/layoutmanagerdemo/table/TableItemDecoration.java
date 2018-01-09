package com.tuacy.layoutmanagerdemo.table;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TableItemDecoration extends RecyclerView.ItemDecoration {

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (parent.getLayoutManager() instanceof TableLayoutManager) {

		}
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		if (parent.getLayoutManager() instanceof TableLayoutManager) {
			TableLayoutManager layoutManager = (TableLayoutManager) parent.getLayoutManager();
		}
		super.getItemOffsets(outRect, view, parent, state);
	}
}
