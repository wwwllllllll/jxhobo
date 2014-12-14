package com.jxhobo;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.jxhobo.bean.User;
import com.jxhobo.broad.Constants;
import com.jxhobo.http.dataservice.RequestHandler;
import com.jxhobo.http.dataservice.http.HttpRequest;
import com.jxhobo.http.dataservice.http.HttpResponse;
import com.jxhobo.http.util.*;
import com.jxhobo.welcome.welcome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends Activity implements RequestHandler<HttpRequest, HttpResponse> {
    private List<Integer> newCodeList1 = new ArrayList<Integer>();
    private EditText zmobile;
    private EditText zkey;
    private EditText zcode;
    private Button back;
    private Button ok;
    private SQLiteDatabase db;
    private SharedPreferences ps;
    private String mobile, key, code;
    private HttpRequest registerReq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        zmobile = (EditText) findViewById(R.id.zmobile);
        zkey = (EditText) findViewById(R.id.zkey);
        zcode = (EditText) findViewById(R.id.zcode);
        back = (Button) findViewById(R.id.back);
        ok = (Button) findViewById(R.id.ok);
        ps = getSharedPreferences("config", MODE_PRIVATE);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent it = new Intent(RegisterActivity.this, welcome.class);
                startActivity(it);
                finish();
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = zmobile.getText().toString();
                key = zkey.getText().toString();
                code = zcode.getText().toString();
                if (mobile.length() != 11) {
                	Toast.makeText(RegisterActivity.this, "手机号长度11位", Toast.LENGTH_SHORT).show();
                } else if (key.length() == 0 || code.length() == 0) {
                    Log.i("mobile--", mobile);
                    Log.i("key", key);
                    Log.i("code", code);
                    Toast.makeText(RegisterActivity.this, "不可为空", Toast.LENGTH_SHORT).show();
                } else {
                    registerReq = HttpUtil.register(RegisterActivity.this, mobile, key, code, RegisterActivity.this);
                }
            }
        });
    }


    private void updateUserData(int userType, Long uId, String token) {
        Account.getInstance().setPhone(mobile);
        Account.getInstance().setToken(token);
        Account.getInstance().setUserId(uId);
        Account.getInstance().setUserType(userType);
        
        User.getInstance().setPhoneNumber(mobile);
        User.getInstance().setRegCode(code);
        Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dateFormat.format(date);
		User.getInstance().setMembership(strDate);
		User.getInstance().setRegDate(strDate);
		User.getInstance().setBuiltRegCode("");
		User.getInstance().setPay(0);
		User.getInstance().setMemberCount(0);
		User.getInstance().setMemberPay(0);
		if (userType == 1) {
		    //non agent, update for according agent
			User.getInstance().saveToCloud(this);
			AVQuery<AVObject> query = new AVQuery<AVObject>("GameUser");
			query.whereEqualTo("builtRegCode", code);
			query.findInBackground(new FindCallback<AVObject>() {
			    public void done(List<AVObject> avObjects, AVException e) {
			        if (e == null) {
			            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
			            final AVObject gameUser = avObjects.get(0);
			            int memberCount = gameUser.getInt("memberCount");
			            gameUser.put("memberCount", memberCount + 1);
			            gameUser.saveInBackground(new SaveCallback() {

			    			@Override
			    			public void done(AVException e) {
			    				// TODO Auto-generated method stub
			    				if (e == null) {
			    					Log.w("Parse", "object id =" + gameUser.getObjectId());
			    				} else {
			    					Toast.makeText(getApplicationContext(), "个人数据存取失败，请检查网络！", Toast.LENGTH_SHORT).show();
			    				}
			    			}
			    		});
			        } else {
			        	Toast.makeText(getApplicationContext(), "个人数据获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
			        }
			    }
			});
		}
		else if (userType == 2) {//agent, get invite code
			HttpUtil.generateInviteCode(this, new GenerateInviteCodeHandler(this));
		}
		Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
        Intent it = new Intent(RegisterActivity.this, SelectPage.class);
        startActivity(it);
        finish();
    }

    @Override
    public void onRequestFailed(HttpRequest request, HttpResponse response) {
        Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRequestFinish(HttpRequest request, HttpResponse response) {
        CommonMsg commonMsg = CommonMsg.deSerializeJson((byte[]) response.result());
        if (commonMsg == null) {
            Toast.makeText(RegisterActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (request == registerReq) {
            if (!ResultCode.dealError(RegisterActivity.this, commonMsg.getResultCode())) {
                String data = commonMsg.getData();
                HashMap read = JsonUtil.read(data, HashMap.class);
                Integer userType = (Integer) read.get("userType");
                Long uId = Long.parseLong(read.get("uid").toString());
                String userToken = (String) read.get("userToken");
                updateUserData(userType, uId, userToken);

            }
            else {
            	Toast.makeText(this, "注册失败，请检查参数", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestProgress(HttpRequest request, int current, int total) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRequestStart(HttpRequest request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
