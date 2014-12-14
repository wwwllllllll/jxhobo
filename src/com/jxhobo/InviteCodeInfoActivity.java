package com.jxhobo;

import com.jxhobo.bean.User;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class InviteCodeInfoActivity extends Activity {

	TextView inviteCode;
	TextView memberCount;
	TextView memberPay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_code_info);
		inviteCode = (TextView) findViewById(R.id.invite_code);
		memberCount = (TextView) findViewById(R.id.member_count);
		memberPay = (TextView) findViewById(R.id.member_pay);
		
		inviteCode.setText(User.getInstance().getBuiltRegCode());
		memberCount.setText(User.getInstance().getMemberCount() + "");
		memberPay.setText(User.getInstance().getMemberPay() + "元");
	}
}
