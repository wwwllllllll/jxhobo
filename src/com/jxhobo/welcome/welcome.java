package com.jxhobo.welcome;


import com.jxhobo.LoginActivity;
import com.jxhobo.R;
import com.jxhobo.RegisterActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;

public class welcome extends Activity {
	private TextView wel_register,wel_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.welcome_activity);
	    AVOSCloud.initialize(this, "tkxdapbvfxo0dtt3ry74gflfeohviyjfbduu11s0xgjnqqbl", "78rr0z1ozdeo8l3vfnpxm7aqx5fz7ptg8zvszihy3xdpdv7t");
	    AVAnalytics.enableCrashReport(this, true);
	    AVAnalytics.trackAppOpened(getIntent());
	    
	    wel_register=(TextView) findViewById(R.id.wel_register);
	    wel_login=(TextView) findViewById(R.id.wel_login);
	    wel_register.setClickable(true);
	    wel_register.setFocusable(true);
	    
	    wel_login.setClickable(true);
	    wel_login.setFocusable(true);
	    
	    wel_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toRegister=new Intent(welcome.this,RegisterActivity.class);
				startActivity(toRegister);
				finish();
			}
		});
	    
	    wel_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toLogin=new Intent(welcome.this,LoginActivity.class);
				startActivity(toLogin);
				finish();
			}
		});
	}
}
