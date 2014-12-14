package com.jxhobo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jxhobo.bean.Person;
import com.jxhobo.bean.User;
import com.jxhobo.http.dataservice.RequestHandler;
import com.jxhobo.http.dataservice.http.HttpRequest;
import com.jxhobo.http.dataservice.http.HttpResponse;
import com.jxhobo.http.util.*;

import java.util.HashMap;
import java.util.List;

public class LoginActivity extends Activity implements RequestHandler<HttpRequest, HttpResponse> {
    private EditText tv_mobile;
    private EditText tv_key;
    private SharedPreferences ps;
    private SQLiteDatabase db;
    private String mobile;
    private String key;
    private AlertDialog.Builder forgetPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉顶部的actionbar
        setContentView(R.layout.login_new);
        tv_mobile = (EditText) findViewById(R.id.tv_mobile);
        tv_key = (EditText) findViewById(R.id.tv_key);
        ps = getSharedPreferences("config", MODE_PRIVATE);
        
        forgetPasswordDialog = new AlertDialog.Builder(this);
        forgetPasswordDialog.setTitle("忘记密码？")
        	.setIcon(android.R.drawable.ic_dialog_email)
        	.setMessage("如果您无法回忆起您的密码，请将您的手机号发邮件到lhmsj76@qq.com，我们核实后会回复您并重置您的帐号。")
        	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
    }


    public void zhuce(View v) {
        Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(it);
        finish();
    }

    public void sure(View v) {
        if (tv_mobile.getText().toString().length()==0 || tv_key.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(), "手机号或者密码为空", 1).show();
        } else {
            mobile = tv_mobile.getText().toString();
            key = tv_key.getText().toString();
            HttpUtil.login(this, mobile, key, this);


			/*
	    	dbOpenHelper.getWritableDatabase();
	    	Person person=new Person();
	    	person.set_id(mobile);
	    	person.setKey(key);
	    	save(person);*/


        }
    }

    private String findcode(String mobile) {
        // SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select code from person where _id=?", new String[]{mobile});
        if (cursor.moveToFirst()) {

            //获取key
            String code = cursor.getString(cursor.getColumnIndex("code"));

            return code;
        }
        return null;

    }


    public void save(Person person) {
	    	/*SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
	    	db.execSQL("insert into person (_id,key,code) values (?,?,?)", new Object[]{person.get_id(),person.getKey(),person.getZhucecode()});*/
    }

    public String find(String mobile) {
        // SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select key from person where _id=?", new String[]{mobile});
        if (cursor.moveToFirst()) {

            //获取key
            String key = cursor.getString(cursor.getColumnIndex("key"));

            return key;
        }
        return null;

    }

    public void forgotKey(View v) {
        /*if (tv_mobile.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "手机号码不能为空", 1).show();
        } else if (findMobile(tv_mobile.getText().toString())) {
            String findkey = find(tv_mobile.getText().toString());
            Toast.makeText(getApplicationContext(), findkey, 1).show();
        } else {
            Toast.makeText(getApplicationContext(), "手机号不存在", 1).show();
        }*/
    	forgetPasswordDialog.show();
    }

    public boolean findMobile(String mobile) {
        // SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from person where _id=?", new String[]{mobile});
        if (cursor.moveToFirst()) {
            //Toast.makeText(getApplicationContext(), "有的", 1).show();
            //获取key
            //String getmobile= cursor.getString(cursor.getColumnIndex("_id"));

            return true;
        }
        return false;

    }

    @Override
    public void onRequestFailed(HttpRequest request, HttpResponse response) {
        Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRequestFinish(HttpRequest request, HttpResponse response) {
        CommonMsg commonMsg = CommonMsg.deSerializeJson((byte[]) response.result());
        Log.i("login in", new String((byte[]) response.result()));
        if (commonMsg == null) {
            Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ResultCode.dealError(this, commonMsg.getResultCode())) {
            String data = commonMsg.getData();
            HashMap read = JsonUtil.read(data, HashMap.class);
            Integer userType = (Integer) read.get("userType");
            Long uId = Long.parseLong(read.get("uid").toString());
            String userToken = (String) read.get("userToken");
            updateUserData(userType, uId, userToken);
        }
    }

    private void updateUserData(int userType, Long uId, String token) {
        Account.getInstance().setPhone(mobile);
        Account.getInstance().setToken(token);
        Account.getInstance().setUserId(uId);
        Account.getInstance().setUserType(userType);
        
        AVQuery<AVObject> query = new AVQuery<AVObject>("GameUser");
		query.whereEqualTo("phoneNumber", mobile);
		query.findInBackground(new FindCallback<AVObject>() {
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            AVObject gameUser = avObjects.get(0);
		            User.getInstance().cloudToLocal(gameUser);
		            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
		            Intent it = new Intent(LoginActivity.this, SelectPage.class);
		            startActivity(it);
		            finish();
		        } else {
		            Log.d("失败", "查询错误: " + e.getMessage());
		            Toast.makeText(getApplicationContext(), "个人数据获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
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
