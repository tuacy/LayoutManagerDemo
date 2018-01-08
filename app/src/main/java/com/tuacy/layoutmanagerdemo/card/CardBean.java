package com.tuacy.layoutmanagerdemo.card;


public class CardBean {

	private String mTitle;
	private int    mResourceId;

	public CardBean(String title, int resourceId) {
		mTitle = title;
		mResourceId = resourceId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public int getResourceId() {
		return mResourceId;
	}

	public void setResourceId(int resourceId) {
		mResourceId = resourceId;
	}
}
