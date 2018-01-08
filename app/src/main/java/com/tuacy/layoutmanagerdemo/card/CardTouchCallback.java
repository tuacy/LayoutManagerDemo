package com.tuacy.layoutmanagerdemo.card;


import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class CardTouchCallback extends ItemTouchHelper.SimpleCallback {


	private CardItemHelper mCardItemHelper;

	public CardTouchCallback(CardItemHelper helper) {
		super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT | ItemTouchHelper.DOWN);
		mCardItemHelper = helper;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		mCardItemHelper.onSwiped(viewHolder, direction);
	}

	@Override
	public void onChildDraw(Canvas c,
							RecyclerView recyclerView,
							RecyclerView.ViewHolder viewHolder,
							float dX,
							float dY,
							int actionState,
							boolean isCurrentlyActive) {
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}
}
