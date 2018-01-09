package com.tuacy.layoutmanagerdemo.table;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuacy.layoutmanagerdemo.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ItemViewHolder> {

	private Context      mContext;
	private List<String> mDataList;

	public TableAdapter(Context context, List<String> dataList) {
		mContext = context;
		mDataList = dataList;
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_table_content, parent, false));
	}

	@Override
	public void onBindViewHolder(ItemViewHolder holder, int position) {
		holder.mTextItem.setText(mDataList.get(position));
		if (position / TableActivity.COLUMN_COUNT % 2 == 0) {
			holder.mTextItem.setBackgroundColor(mContext.getResources().getColor(R.color.colorEven));
		} else {
			holder.mTextItem.setBackgroundColor(mContext.getResources().getColor(R.color.colorOdd));
		}
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {

		TextView mTextItem;

		ItemViewHolder(View itemView) {
			super(itemView);
			mTextItem = itemView.findViewById(R.id.text_content_item);
		}
	}

}
