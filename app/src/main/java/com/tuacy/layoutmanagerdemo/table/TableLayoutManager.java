package com.tuacy.layoutmanagerdemo.table;

import android.graphics.Rect;
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
	 * 整个平铺开来，总宽度
	 */
	private float              mTotalWidth;
	/**
	 * 整个平铺开来，总高度
	 */
	private float              mTotalHeight;

	public TableLayoutManager(int columnCount) {
		this(columnCount, 0, false);
	}

	public TableLayoutManager(int columnCount, int fixedColumnCount, boolean hasTableHeader) {
		mColumnCount = columnCount;
		mIsHasTableHeader = hasTableHeader;
		mFixedColumnCount = fixedColumnCount;
		mItemRectList = new SparseArray<>();
		mItemStateList = new SparseBooleanArray();
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
		calculateChildrenRect(recycler);
		recycle(recycler, state);
		fill(recycler, state);
	}

	/**
	 * 将滑出屏幕的Items回收到Recycle缓存中
	 */
	private void recycle(RecyclerView.Recycler recycler, RecyclerView.State state) {

	}

	private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {

	}


	/**
	 * 计算所有item的位置信息
	 */
	private void calculateChildrenRect(RecyclerView.Recycler recycler) {
		if (getItemCount() <= 0) {
			return;
		}
		Rect rect = new Rect();
		mTotalHeight = 0f;
		mTotalWidth = 0f;
		int rowCount = getItemCount() % mColumnCount == 0 ? getItemCount() / mColumnCount : getItemCount() / mColumnCount + 1;
		for (int row = 0; row < rowCount; row++) {
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

				mItemRectList.put(position, itemRect);
			}
		}
	}

	@Override
	public boolean canScrollHorizontally() {
		return super.canScrollHorizontally();
	}

	@Override
	public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
		return super.scrollHorizontallyBy(dx, recycler, state);
	}

	@Override
	public boolean canScrollVertically() {
		return super.canScrollVertically();
	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		return super.scrollVerticallyBy(dy, recycler, state);
	}


}
