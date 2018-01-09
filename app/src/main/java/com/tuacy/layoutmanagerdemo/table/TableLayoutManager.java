package com.tuacy.layoutmanagerdemo.table;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现一个table表格，既可以上下滑动又可以左右滑动
 * 根据item view是否在recycler范围之内做缓存处理
 */
public class TableLayoutManager extends RecyclerView.LayoutManager {

	private static final boolean DEBUG = true;
	private static final String  TAG   = "TableLayoutManager";

	/**
	 * 列的总数
	 */
	private int         mColumnCount;
	/**
	 * 是否有表头
	 */
	private boolean     mIsHasTableHeader;
	/**
	 * 表格中每一行有多少列是固定的
	 */
	private int         mFixedColumnCount;
	/**
	 * state
	 */
	private LayoutState mLayoutState;

	public TableLayoutManager(int columnCount) {
		this(columnCount, 0, false);
	}

	public TableLayoutManager(int columnCount, int fixedColumnCount, boolean hasTableHeader) {
		mColumnCount = columnCount;
		mIsHasTableHeader = hasTableHeader;
		mFixedColumnCount = fixedColumnCount;
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
		mLayoutState.mDisplayRect.set(0, 0, getHorizontalActiveWidth(), getVerticalActiveHeight());
		// 先移除所有view
		detachAndScrapAttachedViews(recycler);
		calculateSpreadSize(recycler);
		fillChildren(recycler, state);
	}

	private void fillChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (getItemCount() <= 0 || state.isPreLayout()) {
			return;
		}
		if (DEBUG) {
			Log.d(TAG, "start fill children");
		}
		int rowCount = getItemCount() % mColumnCount == 0 ? getItemCount() / mColumnCount : getItemCount() / mColumnCount + 1;
		Rect itemRect = new Rect();
		int rowStart = getDisplayRowStart();
		int rowEnd = getDisplayRowEnd();
		int columnStart = getDisplayColumnStart();
		int columnEnd = getDisplayColumnEnd();
		int preRowHeight = getPreRowHeight(rowStart);
		for (int row = rowStart; row <= rowEnd; row++) {
			int preColumnWidth = getPreColumnWidth(columnStart);
			for (int column = columnStart; column <= columnEnd; column++) {
				int position = row * mColumnCount + column;
				if (row == rowCount - 1 && position >= getItemCount()) {
					break;
				}
				itemRect.set(preColumnWidth, preRowHeight, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column),
							 preRowHeight + mLayoutState.mEachRowHeightList.get(row));
				if (Rect.intersects(mLayoutState.mDisplayRect, itemRect)) {
					View view = recycler.getViewForPosition(position);
					addView(view);
					measureChildWithMargins(view, 0, 0);
					layoutDecoratedWithMargins(view, itemRect.left - mLayoutState.mOffsetHorizontal,
											   itemRect.top - mLayoutState.mOffsetVertical, itemRect.right - mLayoutState.mOffsetHorizontal,
											   itemRect.bottom - mLayoutState.mOffsetVertical);
				}
				preColumnWidth += mLayoutState.mEachColumnWidthList.get(column);
			}
			preRowHeight += mLayoutState.mEachRowHeightList.get(row);
		}
		if (DEBUG) {
			Log.d(TAG, "end fill children");
		}
	}

	private int getPreRowHeight(int row) {
		if (mLayoutState.mEachRowHeightList == null || mLayoutState.mEachRowHeightList.size() == 0) {
			return 0;
		}
		int preRowHeight = 0;
		for (int index = 0; index < row; index++) {
			preRowHeight += mLayoutState.mEachRowHeightList.get(index);
		}
		return preRowHeight;
	}

	private int getPreColumnWidth(int column) {
		if (mLayoutState.mEachColumnWidthList == null || mLayoutState.mEachColumnWidthList.size() == 0) {
			return 0;
		}
		int preColumnWidth = 0;
		for (int index = 0; index < column; index++) {
			preColumnWidth += mLayoutState.mEachColumnWidthList.get(index);
		}
		return preColumnWidth;
	}

	private int getDisplayRowStart() {
		int rowHeightPre = 0;
		for (int row = 0; row < mLayoutState.mEachRowHeightList.size(); row++) {
			int itemBottom = rowHeightPre + mLayoutState.mEachRowHeightList.get(row);
			if (itemBottom > mLayoutState.mDisplayRect.top) {
				return row;
			}
			rowHeightPre += mLayoutState.mEachRowHeightList.get(row);
		}
		return 0;
	}

	private int getDisplayRowEnd() {
		int rowCount = getItemCount() % mColumnCount == 0 ? getItemCount() / mColumnCount : getItemCount() / mColumnCount + 1;
		if (rowCount != mLayoutState.mEachRowHeightList.size()) {
			throw new IllegalArgumentException("row count not match");
		}
		int rowHeightPre = 0;
		for (int row = 0; row < mLayoutState.mEachRowHeightList.size(); row++) {
			int itemTop = rowHeightPre;
			if (itemTop >= mLayoutState.mDisplayRect.bottom) {
				return row;
			}
			rowHeightPre += mLayoutState.mEachRowHeightList.get(row);
		}
		return mLayoutState.mEachRowHeightList.size() - 1;
	}

	private int getDisplayColumnStart() {
		int columnPre = 0;
		for (int column = 0; column < mLayoutState.mEachColumnWidthList.size(); column++) {
			int itemRight = columnPre + mLayoutState.mEachColumnWidthList.get(column);
			if (itemRight > mLayoutState.mDisplayRect.left) {
				return column;
			}
			columnPre += mLayoutState.mEachColumnWidthList.get(column);
		}
		return 0;
	}

	private int getDisplayColumnEnd() {
		int columnPre = 0;
		for (int column = 0; column < mLayoutState.mEachColumnWidthList.size(); column++) {
			int itemLeft = columnPre;
			if (itemLeft >= mLayoutState.mDisplayRect.right) {
				return column;
			}
			columnPre += mLayoutState.mEachColumnWidthList.get(column);
		}
		return mLayoutState.mEachColumnWidthList.size() - 1;
	}

	/**
	 * 计算平铺出来的宽度和高度
	 */
	private void calculateSpreadSize(RecyclerView.Recycler recycler) {
		if (getItemCount() <= 0) {
			return;
		}
		if (DEBUG) {
			Log.d(TAG, "start spread size");
		}
		mLayoutState.mSpreadHeight = 0;
		mLayoutState.mSpreadWidth = 0;
		int rowCount = getItemCount() % mColumnCount == 0 ? getItemCount() / mColumnCount : getItemCount() / mColumnCount + 1;
		// 平铺高度
		for (int row = 0; row < rowCount; row++) {
			int position = row * mColumnCount;
			View view = recycler.getViewForPosition(position);
			measureChildWithMargins(view, 0, 0);
			int height = getDecoratedMeasuredHeight(view);
			mLayoutState.mSpreadHeight += height;
			mLayoutState.mEachRowHeightList.put(row, height);
		}
		// 平铺宽度
		if (rowCount > 1) {
			for (int column = 0; column < mColumnCount; column++) {
				View view = recycler.getViewForPosition(column);
				measureChildWithMargins(view, 0, 0);
				int width = getDecoratedMeasuredWidth(view);
				mLayoutState.mSpreadWidth += width;
				mLayoutState.mEachColumnWidthList.put(column, width);
			}
		} else {
			for (int column = 0; column < getItemCount(); column++) {
				View view = recycler.getViewForPosition(column);
				measureChildWithMargins(view, 0, 0);
				int width = getDecoratedMeasuredWidth(view);
				mLayoutState.mSpreadWidth += width;
				mLayoutState.mEachColumnWidthList.put(column, width);
			}
		}
		if (DEBUG) {
			Log.d(TAG, "end spread size");
		}
	}

	@Override
	public boolean canScrollHorizontally() {
		return mLayoutState.mSpreadWidth > getHorizontalActiveWidth();
	}

	@Override
	public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
		// 先移除所有view
		detachAndScrapAttachedViews(recycler);
		if (mLayoutState.mOffsetHorizontal + dx > mLayoutState.mSpreadWidth - getHorizontalActiveWidth()) {
			dx = mLayoutState.mSpreadWidth - getHorizontalActiveWidth() - mLayoutState.mOffsetHorizontal;
		} else if (mLayoutState.mOffsetHorizontal + dx < 0) {
			dx = -mLayoutState.mOffsetHorizontal;
		}
		mLayoutState.mOffsetHorizontal += dx;
		mLayoutState.mDisplayRect.offset(dx, 0);
		fillChildren(recycler, state);
		return dx;
	}

	@Override
	public boolean canScrollVertically() {
		return mLayoutState.mSpreadHeight > getVerticalActiveHeight();
	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		// 先移除所有view
		detachAndScrapAttachedViews(recycler);
		if (mLayoutState.mOffsetVertical + dy > mLayoutState.mSpreadHeight - getVerticalActiveHeight()) {
			dy = mLayoutState.mSpreadHeight - getVerticalActiveHeight() - mLayoutState.mOffsetVertical;
		} else if (mLayoutState.mOffsetVertical + dy < 0) {
			dy = -mLayoutState.mOffsetVertical;
		}
		mLayoutState.mOffsetVertical += dy;
		mLayoutState.mDisplayRect.offset(0, dy);
		fillChildren(recycler, state);
		return dy;
	}

	private int getHorizontalActiveWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}

	private int getVerticalActiveHeight() {
		return getHeight() - getPaddingTop() - getPaddingBottom();
	}

	/**
	 * the layout state of the LayoutManager
	 */
	private class LayoutState {

		/**
		 * 整个平铺开来，总宽度
		 */
		int            mSpreadWidth;
		/**
		 * 整个平铺开来，总高度
		 */
		int            mSpreadHeight;
		/**
		 * 每一行的高度list
		 */
		SparseIntArray mEachRowHeightList;
		/**
		 * 每一列的宽度list
		 */
		SparseIntArray mEachColumnWidthList;
		/**
		 * 水平偏移
		 */
		int            mOffsetHorizontal;
		/**
		 * 垂直偏移
		 */
		int            mOffsetVertical;
		/**
		 * 显示的有效区域
		 */
		Rect           mDisplayRect;

		LayoutState() {
			reset();
		}

		void reset() {
			mSpreadWidth = 0;
			mSpreadHeight = 0;
			mOffsetHorizontal = 0;
			mOffsetVertical = 0;
			mDisplayRect = new Rect();
			mEachRowHeightList = new SparseIntArray();
			mEachColumnWidthList = new SparseIntArray();
		}
	}


}
