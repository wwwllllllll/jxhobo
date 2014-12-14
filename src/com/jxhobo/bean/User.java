package com.jxhobo.bean;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jxhobo.LoginActivity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class User {

	private static User user = null;
	
	private String phoneNumber;
	private String regCode;
	private int pay;
	private String membership;
	private String regDate;
	private String builtRegCode;
	private int memberCount;
	private int memberPay;
	
	private User() {
		phoneNumber = "";
		regCode = "";
		pay = 0;
		membership = "";
		regDate = "";
		builtRegCode = "";
		memberCount = 0;
		memberPay = 0;
	}
	
	public static synchronized User getInstance() {
		if(user == null) {
			user = new User();
		}
		return user;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRegCode() {
		return regCode;
	}

	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public String getMembership() {
		return membership;
	}

	public void setMembership(String membership) {
		this.membership = membership;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	
	public String getBuiltRegCode() {
		return builtRegCode;
	}

	public void setBuiltRegCode(String builtRegCode) {
		this.builtRegCode = builtRegCode;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public int getMemberPay() {
		return memberPay;
	}

	public void setMemberPay(int memberPay) {
		this.memberPay = memberPay;
	}
	
	public void updateToCloudInBackground(final Context context) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("GameUser");
		query.whereEqualTo("phoneNumber", phoneNumber);
		query.findInBackground(new FindCallback<AVObject>() {
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            final AVObject gameUser;
		            if (!avObjects.isEmpty()) {
			            gameUser = avObjects.get(0);
			            gameUser.put("regCode", regCode);
			    		gameUser.put("pay", pay);
			    		gameUser.put("membership", membership);
			    		gameUser.put("regDate", regDate);
			    		gameUser.put("builtRegCode", builtRegCode);
			    		gameUser.put("memberCount", memberCount);
			    		gameUser.put("memberPay", memberPay);
			            gameUser.saveInBackground(new SaveCallback() {
	
			    			@Override
			    			public void done(AVException e) {
			    				// TODO Auto-generated method stub
			    				if (e == null) {
			    					Log.w("Parse", "object id =" + gameUser.getObjectId());
			    				} else {
			    					Toast.makeText(context, "个人数据存取失败，请检查网络！", Toast.LENGTH_SHORT).show();
			    				}
			    			}
			    		});
		            } else {
		            	gameUser = new AVObject("GameUser");
			    		gameUser.put("phoneNumber", phoneNumber);
			    		gameUser.put("regCode", regCode);
			    		gameUser.put("pay", pay);
			    		gameUser.put("membership", membership);
			    		gameUser.put("regDate", regDate);
			    		gameUser.put("builtRegCode", builtRegCode);
			    		gameUser.put("memberCount", memberCount);
			    		gameUser.put("memberPay", memberPay);
			    		gameUser.saveInBackground(new SaveCallback() {

			    			@Override
			    			public void done(AVException e) {
			    				// TODO Auto-generated method stub
			    				if (e == null) {
			    					Log.w("Parse", "object id =" + gameUser.getObjectId());
			    				} else {
			    					Toast.makeText(context, "个人数据存取失败，请检查网络！", Toast.LENGTH_SHORT).show();
			    				}
			    			}
			    		});
		            }
		        } else {
		            Toast.makeText(context, "个人数据存取失败，请检查网络！", Toast.LENGTH_SHORT).show();
		    	}
		    }
		});
	}
	
	public void saveToCloud(final Context context) {
		AVObject gameUser = new AVObject("GameUser");
		gameUser.put("phoneNumber", phoneNumber);
		gameUser.put("regCode", regCode);
		gameUser.put("pay", pay);
		gameUser.put("membership", membership);
		gameUser.put("regDate", regDate);
		gameUser.put("builtRegCode", builtRegCode);
		gameUser.put("memberCount", memberCount);
		gameUser.put("memberPay", memberPay);
		gameUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(AVException e) {
				// TODO Auto-generated method stub
				if (e != null) {
					Toast.makeText(context, "个人数据存取失败，请检查网络！！！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	public void cloudToLocal(AVObject gameUser) {
		setPhoneNumber(gameUser.getString("phoneNumber"));
		setRegCode(gameUser.getString("regCode"));
		setPay(gameUser.getInt("pay"));
		setMembership(gameUser.getString("membership"));
		setRegDate(gameUser.getString("regDate"));
		setBuiltRegCode(gameUser.getString("builtRegCode"));
		setMemberCount(gameUser.getInt("memberCount"));
		setMemberPay(gameUser.getInt("memberPay"));
	}
}
