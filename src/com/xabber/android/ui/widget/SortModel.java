package com.xabber.android.ui.widget;

import android.graphics.drawable.Drawable;

public class SortModel {

	private String name;
	private String sortLetters;
	private String userc;
	private Drawable image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getUserc() {
		return userc;
	}

	public void setUserc(String userc) {
		this.userc = userc;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}
}
