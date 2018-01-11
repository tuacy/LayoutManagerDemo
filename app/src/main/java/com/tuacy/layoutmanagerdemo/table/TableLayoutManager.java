package com.tuacy.layoutmanagerdemo.table;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.tuacy.layoutmanagerdemo.utils.DensityUtils;

/**
 * 实现表格布局
 */
public class TableLayoutManager extends RecyclerView.LayoutManager {

	private static final boolean DEBUG = true;
	private static final String  TAG   = "TableLayoutManager";

	/**
	 * 构建器
	 */
	private Build       mBuild;
	/**
	 * state
	 */
	private LayoutState mLayoutState;

	private TableLayoutManager(Build build) {
		mBuild = build;
		mLayoutState = new LayoutState();
	}

	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		super.onLayoutChildren(recycler, state);
		if (getItemCount() <= 0 || state.isPreLayout() || mBuild.mColumnCount <= 0) {
			return;
		}
		mLayoutState.reset();
		mLayoutState.mColumnCount = mBuild.mColumnCount;
		//确定总共有多少行
		mLayoutState.mRowCount = getItemCount() % mBuild.mColumnCount == 0 ? getItemCount() / mBuild.mColumnCount :
								 getItemCount() / mBuild.mColumnCount + 1;
		//内容显示区域(可滑动的区域)
		mLayoutState.mSlideAreaRect.set(0, 0, getHorizontalActiveWidth(), getVerticalActiveHeight());
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
		Rect itemRect = new Rect();
		int rowStart = getDisplayRowStart();
		int rowEnd = getDisplayRowEnd();
		int columnStart = getDisplayColumnStart();
		int columnEnd = getDisplayColumnEnd();
		int preRowHeight = getPreRowHeight(rowStart);
		for (int row = rowStart; row <= rowEnd; row++) {
			int preColumnWidth = getPreColumnWidth(columnStart);
			for (int column = columnStart; column <= columnEnd; column++) {
				boolean skip = false;
				int position = row * mBuild.mColumnCount + column;
				if (row == mLayoutState.mRowCount - 1 && position >= getItemCount()) {
					break;
				}
				if (mBuild.mFixHeader && row == 0) {
					itemRect.set(preColumnWidth, preRowHeight, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column),
								 preRowHeight + mBuild.mHeadHeight);
				} else {
					itemRect.set(preColumnWidth, preRowHeight, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column),
								 preRowHeight + mBuild.mRowHeight);
				}

				if (mBuild.mFixHeader && row == 0) {
					skip = true;
				}
				if (column < mBuild.mFixColumnCount) {
					skip = true;
				}
				if (!skip && Rect.intersects(mLayoutState.mSlideAreaRect, itemRect)) {
					View view = recycler.getViewForPosition(position);
					addView(view);
					final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
					if (mBuild.mFixHeader) {
						layoutParams.height = row == 0 ? mBuild.mHeadHeight : mBuild.mRowHeight;
					} else {
						layoutParams.height = mBuild.mRowHeight;
					}
					measureChildWithMargins(view, 0, 0);
					layoutDecoratedWithMargins(view, itemRect.left - mLayoutState.mOffsetHorizontal,
											   itemRect.top - mLayoutState.mOffsetVertical, itemRect.right - mLayoutState.mOffsetHorizontal,
											   itemRect.bottom - mLayoutState.mOffsetVertical);
				}
				preColumnWidth += mLayoutState.mEachColumnWidthList.get(column);
			}
			if (mBuild.mFixHeader && row == 0) {
				preRowHeight += mBuild.mHeadHeight;
			} else {
				preRowHeight += mBuild.mRowHeight;
			}
		}
		/**
		 * 处理列固定
		 */
		preRowHeight = getPreRowHeight(rowStart);
		if (mBuild.mFixColumnCount > 0) {
			for (int row = rowStart; row <= rowEnd; row++) {
				int preColumnWidth = 0;
				for (int column = 0; column < mBuild.mFixColumnCount; column++) {
					boolean skip = false;
					int position = row * mBuild.mColumnCount + column;
					if (row == mLayoutState.mRowCount - 1 && position >= getItemCount()) {
						break;
					}
					if (mBuild.mFixHeader && row == 0) {
						itemRect.set(preColumnWidth, preRowHeight, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column),
									 preRowHeight + mBuild.mHeadHeight);
					} else {
						itemRect.set(preColumnWidth, preRowHeight, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column),
									 preRowHeight + mBuild.mRowHeight);
					}
					if (mBuild.mFixHeader && row == 0) {
						skip = true;
					}
					if (!skip) {
						View view = recycler.getViewForPosition(position);
						addView(view);
						final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
						if (mBuild.mFixHeader) {
							layoutParams.height = row == 0 ? mBuild.mHeadHeight : mBuild.mRowHeight;
						} else {
							layoutParams.height = mBuild.mRowHeight;
						}
						measureChildWithMargins(view, 0, 0);
						layoutDecoratedWithMargins(view, itemRect.left, itemRect.top - mLayoutState.mOffsetVertical, itemRect.right,
												   itemRect.bottom - mLayoutState.mOffsetVertical);
					}
					preColumnWidth += mLayoutState.mEachColumnWidthList.get(column);
				}
				if (mBuild.mFixHeader && row == 0) {
					preRowHeight += mBuild.mHeadHeight;
				} else {
					preRowHeight += mBuild.mRowHeight;
				}
			}
		}
		/**
		 * 处理行固定，有固定的时候也是固定第一行
		 */
		if (mBuild.mFixHeader) {
			int preColumnWidth = getPreColumnWidth(columnStart);
			for (int column = columnStart; column <= columnEnd; column++) {
				boolean skip = false;
				if (column >= getItemCount()) {
					break;
				}
				itemRect.set(preColumnWidth, 0, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column), mBuild.mHeadHeight);
				if (column < mBuild.mFixColumnCount) {
					skip = true;
				}
				if (!skip) {
					View view = recycler.getViewForPosition(column);
					addView(view);
					final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
					layoutParams.height = mBuild.mHeadHeight;
					measureChildWithMargins(view, 0, 0);
					layoutDecoratedWithMargins(view, itemRect.left - mLayoutState.mOffsetHorizontal, itemRect.top,
											   itemRect.right - mLayoutState.mOffsetHorizontal, itemRect.bottom);
				}
				preColumnWidth += mLayoutState.mEachColumnWidthList.get(column);
			}
		}

		/**
		 * 处理固定行固定列相交的部分
		 */
		if (mBuild.mFixHeader && mBuild.mFixColumnCount > 0) {
			int preColumnWidth = 0;
			for (int column = 0; column < mBuild.mFixColumnCount; column++) {
				if (column >= getItemCount()) {
					break;
				}
				itemRect.set(preColumnWidth, 0, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column), mBuild.mHeadHeight);
				View view = recycler.getViewForPosition(column);
				addView(view);
				final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
				layoutParams.height = mBuild.mHeadHeight;
				measureChildWithMargins(view, 0, 0);
				layoutDecoratedWithMargins(view, itemRect.left, itemRect.top, itemRect.right, itemRect.bottom);
				preColumnWidth += mLayoutState.mEachColumnWidthList.get(column);
			}
		}

		if (DEBUG) {
			Log.d(TAG, "end fill children");
		}
	}

	/**
	 * 获取指定行前面的高度
	 */
	private int getPreRowHeight(int row) {
		int preRowHeight = 0;
		for (int index = 0; index < row; index++) {
			if (mBuild.mFixHeader && index == 0) {
				preRowHeight += mBuild.mHeadHeight;
			} else {
				preRowHeight += mBuild.mRowHeight;
			}
		}
		return preRowHeight;
	}

	/**
	 * 获取指定列前面的宽度
	 */
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

	/**
	 * 获取要显示行开始的位置
	 */
	private int getDisplayRowStart() {
		int rowHeightPre = 0;
		for (int row = 0; row < mLayoutState.mRowCount; row++) {
			int itemBottom;
			if (mBuild.mFixHeader && row == 0) {
				itemBottom = rowHeightPre + mBuild.mHeadHeight;
			} else {
				itemBottom = rowHeightPre + mBuild.mRowHeight;
			}
			if (itemBottom > mLayoutState.mSlideAreaRect.top) {
				return row;
			}
			if (mBuild.mFixHeader && row == 0) {
				rowHeightPre += mBuild.mHeadHeight;
			} else {
				rowHeightPre += mBuild.mRowHeight;
			}
		}
		return 0;
	}

	/**
	 * 获取要显示行结束的位置
	 */
	private int getDisplayRowEnd() {
		int rowHeightPre = 0;
		for (int row = 0; row < mLayoutState.mRowCount; row++) {
			int itemTop = rowHeightPre;
			if (itemTop >= mLayoutState.mSlideAreaRect.bottom) {
				return row;
			}
			if (mBuild.mFixHeader && row == 0) {
				rowHeightPre += mBuild.mHeadHeight;
			} else {
				rowHeightPre += mBuild.mRowHeight;
			}
		}

		return mLayoutState.mRowCount - 1;
	}

	/**
	 * 获取显示列开始的位置
	 */
	private int getDisplayColumnStart() {
		int columnPre = 0;
		for (int column = 0; column < mLayoutState.mEachColumnWidthList.size(); column++) {
			int itemRight = columnPre + mLayoutState.mEachColumnWidthList.get(column);
			if (itemRight > mLayoutState.mSlideAreaRect.left) {
				return column;
			}
			columnPre += mLayoutState.mEachColumnWidthList.get(column);
		}
		return 0;
	}

	/**
	 * 获取显示列结束的位置
	 */
	private int getDisplayColumnEnd() {
		int columnPre = 0;
		for (int column = 0; column < mLayoutState.mEachColumnWidthList.size(); column++) {
			int itemLeft = columnPre;
			if (itemLeft >= mLayoutState.mSlideAreaRect.right) {
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
		mLayoutState.mSpreadHeight = 0;
		mLayoutState.mSpreadWidth = 0;
		// 平铺高度
		if (mBuild.mFixHeader) {
			mLayoutState.mSpreadHeight = mBuild.mHeadHeight;
			if (mLayoutState.mRowCount > 1) {
				mLayoutState.mSpreadHeight += (mLayoutState.mRowCount - 1) * mBuild.mRowHeight;
			}
		} else {
			mLayoutState.mSpreadHeight = mLayoutState.mRowCount * mBuild.mRowHeight;
		}

		// 平铺宽度
		if (mLayoutState.mRowCount > 1) {
			for (int column = 0; column < mBuild.mColumnCount; column++) {
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
	}

	/**
	 * 平铺宽度大于内容宽度，水平可以滑动
	 */
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
		mLayoutState.mSlideAreaRect.offset(dx, 0);
		fillChildren(recycler, state);
		return dx;
	}

	/**
	 * 平铺的高度大于内容区域的高度，垂直可以滑动
	 */
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
		mLayoutState.mSlideAreaRect.offset(0, dy);
		fillChildren(recycler, state);
		return dy;
	}

	private int getHorizontalActiveWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}

	private int getVerticalActiveHeight() {
		return getHeight() - getPaddingTop() - getPaddingBottom();
	}

	public boolean isColumnStart(int position) {
		return position % mLayoutState.mColumnCount == 0;
	}

	public boolean isColumnEnd(int position) {
		return position % mLayoutState.mColumnCount == mLayoutState.mColumnCount - 1;
	}

	public boolean isRowStart(int position) {
		return position < mLayoutState.mColumnCount;
	}

	public boolean isRowEnd(int position) {
		return position >= (mLayoutState.mRowCount - 1) * mLayoutState.mColumnCount;
	}

	/**
	 * the layout state of the LayoutManager
	 */
	private static class LayoutState {

		/**
		 * 总行数
		 */
		int            mRowCount;
		/**
		 * 总列数
		 */
		int            mColumnCount;
		/**
		 * 整个平铺开来，总宽度
		 */
		int            mSpreadWidth;
		/**
		 * 整个平铺开来，总高度
		 */
		int            mSpreadHeight;
		/**
		 * 每一列对应的宽度list
		 */
		SparseIntArray mEachColumnWidthList;
		/**
		 * 水平偏移量
		 */
		int            mOffsetHorizontal;
		/**
		 * 垂直偏移量
		 */
		int            mOffsetVertical;
		/**
		 * 内容区域(可滑动的区域),绘制的时候只绘制内容区域内的item
		 */
		Rect           mSlideAreaRect;

		LayoutState() {
			reset();
		}

		void reset() {
			mSpreadWidth = 0;
			mSpreadHeight = 0;
			mOffsetHorizontal = 0;
			mOffsetVertical = 0;
			mSlideAreaRect = new Rect();
			mEachColumnWidthList = new SparseIntArray();
		}
	}


	public static class Build {

		/**
		 * 表格列数
		 */
		int     mColumnCount;
		/**
		 * 是否固定表头
		 */
		boolean mFixHeader;
		/**
		 * 表头行高度
		 */
		int     mHeadHeight;
		/**
		 * 表每一行的高度
		 */
		int     mRowHeight;
		/**
		 * 每一行的前多少列固定不动
		 */
		int     mFixColumnCount;

		Build(Context context) {
			mColumnCount = 1;
			mFixHeader = false;
			mHeadHeight = DensityUtils.dp2px(context, 48);
			mRowHeight = DensityUtils.dp2px(context, 48);
			mFixColumnCount = 0;
		}

		public Build setColumnCount(int columnCount) {
			mColumnCount = columnCount;
			return this;
		}

		public Build setFixHeader(boolean fixed) {
			mFixHeader = fixed;
			return this;
		}

		public Build setHeadHeight(int height) {
			mHeadHeight = height;
			return this;
		}

		public Build setRowHeight(int height) {
			mRowHeight = height;
			return this;
		}

		public Build setFixColumnCount(int count) {
			mFixColumnCount = count;
			return this;
		}

		TableLayoutManager build() {
			return new TableLayoutManager(this);
		}

	}


}
