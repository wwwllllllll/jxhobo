package com.jxhobo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ResultPagerAdapter extends FragmentPagerAdapter {
	private Context context;
	private int index1;
	private int index2;
	public ResultPagerAdapter(FragmentManager fm, Context context, int index1, int index2) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.index1 = index1;
		this.index2 = index2;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			return new FragmentResult(context, index1);
		case 1:
			return new FragmentResult(context, index2);
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;	//card1 and card2
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "第一张牌";
		case 1:
			return "第二张牌";
		}
		return null;
	}
}
