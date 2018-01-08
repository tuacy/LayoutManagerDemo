package com.tuacy.layoutmanagerdemo.table;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现一个table表格，既可以上下滑动又可以左右滑动
 * 根据item view是否在recycler范围之内做缓存处理
 */
public class TableLayoutManager extends RecyclerView.LayoutManager {

	/**
	 * 列的总数
	 */
	private int                mColumnCount;
	/**
	 * 是否有表头
	 */
	private boolean            mIsHasTableHeader;
	/**
	 * 表格中每一行有多少列是固定的
	 */
	private int                mFixedColumnCount;
	/**
	 * 垂直scroll offset
	 */
	private float              mVerticalScrollOffset;
	/**
	 * 水平scroll offset
	 */
	private float              mHorizontalScrollOffset;
	/**
	 * 用于保存所有item的位置信息
	 */
	private SparseArray<Rect>  mItemRectList;
	/**
	 * 用于保存所有item的状态(是否可见)
	 */
	private SparseBooleanArray mItemStateList;
	/**
	 * state
	 */
	private LayoutState        mLayoutState;

	public TableLayoutManager(int columnCount) {
		this(columnCount, 0, false);
	}

	public TableLayoutManager(int columnCount, int fixedColumnCount, boolean hasTableHeader) {
		mColumnCount = columnCount;
		mIsHasTableHeader = hasTableHeader;
		mFixedColumnCount = fixedColumnCount;
		mItemRectList = new SparseArray<>();
		mItemStateList = new SparseBooleanArray();
		mLayoutState = new LayoutState();
	}

	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		super.onLayoutChildren(recycler, state);
		if (getItemCount() <= 0 || state.isPreLayout()) {
			return;
		}
		// 先移除所有view
		detachAndScrapAttachedViews(recycler);
		mLayoutState.mContentRect.set(0, 0, getWidth(), getHeight());
		calculateChildrenRect(recycler);
		fill(recycler, state);
	}

	private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (getItemCount() <= 0) {
			return;
		}
		// 先移除所有view
		detachAndScrapAttachedViews(recycler);
		int rowCount = getItemCount() % mColumnCount == 0 ? getItemCount() / mColumnCount : getItemCount() / mColumnCount + 1;
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < mColumnCount; column++) {
				int position = row * mColumnCount + column;
				Rect itemRect = mItemRectList.get(position);
				itemRect.offset(-mLayoutState.mOffsetX, -mLayoutState.mOffsetY);
				if (Rect.intersects(itemRect, mLayoutState.mContentRect)) {
					View view = recycler.getViewForPosition(position);
					addView(view);
					measureChildWithMargins(view, 0, 0);
					layoutDecoratedWithMargins(view, itemRect.left, itemRect.top, itemRect.right, itemRect.bottom);
				}
			}
		}
	}


	/**
	 * 计算所有item的位置信息
	 */
	private void calculateChildrenRect(RecyclerView.Recycler recycler) {
		if (getItemCount() <= 0) {
			return;
		}
		Rect rect = new Rect();
		mLayoutState.mTotalHeight = 0;
		mLayoutState.mTotalWidth = 0;
		int rowCount = getItemCount() % mColumnCount == 0 ? getItemCount() / mColumnCount : getItemCount() / mColumnCount + 1;
		int rowHeightPre = 0;
		for (int row = 0; row < rowCount; row++) {
			int columnWidthPre = 0;
			int oneRowHeightMax = 0;
			for (int column = 0; column < mColumnCount; column++) {
				int position = row * mColumnCount + column;
				View view = recycler.getViewForPosition(position);
				measureChildWithMargins(view, 0, 0);
				calculateItemDecorationsForChild(view, rect);
				int width = getDecoratedMeasuredWidth(view);
				int height = getDecoratedMeasuredHeight(view);
				Rect itemRect = mItemRectList.get(position);
				if (itemRect == null) {
					itemRect = new Rect();
				}
				itemRect.set(columnWidthPre, rowHeightPre, columnWidthPre + width, rowHeightPre + height);
				mItemRectList.put(position, itemRect);
				columnWidthPre = columnWidthPre + width;
				oneRowHeightMax = Math.max(height, oneRowHeightMax);
			}
			mLayoutState.mTotalWidth = Math.max(mLayoutState.mTotalWidth, columnWidthPre);
			rowHeightPre = rowHeightPre + oneRowHeightMax;
		}
		mLayoutState.mTotalHeight = rowHeightPre;
	}

	@Override
	public boolean canScrollHorizontally() {
		return mLayoutState.mTotalWidth > getWidth();
	}

	@Override
	public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (mLayoutState.mOffsetX + dx > mLayoutState.mTotalWidth - getWidth()) {
			dx = mLayoutState.mTotalWidth - getWidth() - mLayoutState.mOffsetX;
		} else if (mLayoutState.mOffsetX + dx < 0) {
			dx = -mLayoutState.mOffsetX;
		}
		Log.d("tuacy", "dx = " + dx);
		mLayoutState.mOffsetX += dx;
		Log.d("tuacy", "offset = " + mLayoutState.mOffsetX);
		fill(recycler, state);
		return dx;
	}

	@Override
	public boolean canScrollVertically() {
		return mLayoutState.mTotalHeight > getHeight();
	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (mLayoutState.mOffsetY + dy > mLayoutState.mTotalHeight - getHeight()) {
			dy = mLayoutState.mTotalHeight - getHeight() - mLayoutState.mOffsetY;
		} else if (mLayoutState.mOffsetY + dy < 0) {
			dy = -mLayoutState.mOffsetY;
		}
		mLayoutState.mOffsetY += dy;
		fill(recycler, state);
		return dy;
	}

	/**
	 * the layout state of the LayoutManager
	 */
	private class LayoutState {

		/**
		 * 整个平铺开来，总宽度
		 */
		int mTotalWidth;
		/**
		 * 整个平铺开来，总高度
		 */
		int mTotalHeight;
		int mOffsetX;
		int mOffsetY;

		final Rect mContentRect = new Rect();
	}


}
