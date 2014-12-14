package com.jxhobo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GameListViewAdapter extends BaseAdapter {
	private Context context;
	private String[] gameName;
	private String[] gameDescription;
	private int[] gameLogoId = {R.drawable.patch, R.drawable.patch, R.drawable.icon_appli, R.drawable.longhudou_logo};

	public GameListViewAdapter(Context context) {
		super();
		this.context = context;
		gameName = context.getResources().getStringArray(R.array.game_list);
		gameDescription = context.getResources().getStringArray(R.array.game_description);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gameName.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return gameName[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		GameListItem item = new GameListItem(context);
		item.updateview(gameName[arg0], gameLogoId[arg0], gameDescription[arg0]);
		return item;
	}

}
