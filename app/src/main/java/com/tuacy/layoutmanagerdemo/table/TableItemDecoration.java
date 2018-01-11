package com.tuacy.layoutmanagerdemo.table;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TableItemDecoration extends RecyclerView.ItemDecoration {

	private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

	private Drawable mDivider;

	public TableItemDecoration(Context context) {
		final TypedArray a = context.obtainStyledAttributes(ATTRS);
		mDivider = a.getDrawable(0);
		a.recycle();
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (parent.getLayoutManager() instanceof TableLayoutManager) {

			int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int left = child.getLeft() - params.leftMargin;
				final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
				final int top = child.getBottom() + params.bottomMargin;
				final int bottom = top + mDivider.getIntrinsicHeight();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);

				final int top1 = child.getTop() - params.topMargin;
				final int bottom1 = child.getBottom() + params.bottomMargin;
				final int left1 = child.getRight() + params.rightMargin;
				final int right1 = left1 + mDivider.getIntrinsicWidth();

				mDivider.setBounds(left1, top1, right1, bottom1);
				mDivider.draw(c);
			}
		}
	}

	public void drawHorizontal(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int left = child.getLeft() - params.leftMargin;
			final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
			final int top = child.getBottom() + params.bottomMargin;
			final int bottom = top + mDivider.getIntrinsicHeight();
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	public void drawVertical(Canvas c, RecyclerView parent) {
		final int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);

			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int top = child.getTop() - params.topMargin;
			final int bottom = child.getBottom() + params.bottomMargin;
			final int left = child.getRight() + params.rightMargin;
			final int right = left + mDivider.getIntrinsicWidth();

			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		if (parent.getLayoutManager() instanceof TableLayoutManager) {
			TableLayoutManager layoutManager = (TableLayoutManager) parent.getLayoutManager();
			int position = parent.getChildAdapterPosition(view);
			outRect.right = layoutManager.isColumnEnd(position) ? 0 : mDivider.getIntrinsicWidth();
			outRect.bottom = layoutManager.isRowEnd(position) ? 0 : mDivider.getIntrinsicHeight();
			return;
		}
		super.getItemOffsets(outRect, view, parent, state);
	}
}
