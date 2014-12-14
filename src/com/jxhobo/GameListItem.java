package com.jxhobo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameListItem extends LinearLayout {
	private ImageView imageView;
	private TextView name;
	private TextView description;
		
	public GameListItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		View view = LayoutInflater.from(context).inflate(R.layout.game_list_item, null);
		imageView = (ImageView) view.findViewById(R.id.game_logo);
		name = (TextView) view.findViewById(R.id.game_name);
		description = (TextView) view.findViewById(R.id.game_description);
		addView(view);
	}
	public void updateview (String gameName, int gameLogoId, String gameDescription) {
		imageView.setImageResource(gameLogoId);
		name.setText(gameName);
		description.setText(gameDescription);
	}
}
