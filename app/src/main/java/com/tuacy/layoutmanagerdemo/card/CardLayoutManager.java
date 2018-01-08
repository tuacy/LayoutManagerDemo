package com.tuacy.layoutmanagerdemo.card;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 重叠LayoutManager
 */
public class CardLayoutManager extends RecyclerView.LayoutManager {

	private float mSectionScale;
	private float mSectionTranslation;
	private int   mShowViewMax;

	public CardLayoutManager() {
		this(5, 0.075f, 10f);
	}

	public CardLayoutManager(int showViewMax, float sectionScale, float sectionTranslation) {
		mShowViewMax = showViewMax;
		mSectionScale = sectionScale;
		mSectionTranslation = sectionTranslation;
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
		// 防止view过多产生oom情况，这里我们做了view最大个数的限制，他没办法想别的LayoutManager那样通过item是否落在屏幕内判断是否回收
		int viewCount = getItemCount();
		if (getItemCount() > mShowViewMax) {
			viewCount = mShowViewMax;
		}
		//　这里要注意view要反着加，因为adapter position = 0对应的view我们要显示在最上层
		for (int position = viewCount - 1; position >= 0; position--) {
			// 获取到制定位置的view
			final View view = recycler.getViewForPosition(position);
			addView(view);
			// 测量view
			measureChildWithMargins(view, 0, 0);
			// view在RecyclerView里面还剩余的宽度
			int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
			// layout view,水平居中
			layoutDecoratedWithMargins(view, widthSpace / 2, 0, widthSpace / 2 + getDecoratedMeasuredWidth(view),
									   getDecoratedMeasuredHeight(view));
			// 为了让重叠在一起的view，有一个更好的显示效果
			view.setScaleX(getScaleX(position));
			view.setScaleY(getScaleY(position));
			view.setTranslationX(getTranslationX(position));
			view.setTranslationY(getTranslationY(position));
		}

	}

	private float getScaleX(int position) {
		return 1f - position * mSectionScale;
	}

	private float getScaleY(int position) {
		return 1f;
	}

	private float getTranslationX(int position) {
		return 0f;
	}

	private float getTranslationY(int position) {
		return position * mSectionTranslation;
	}
}
