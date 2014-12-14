package com.alipay.sdk.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jxhobo.R;
import com.jxhobo.bean.User;
import com.jxhobo.http.util.Account;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class PaySelectActivity extends Activity {

	TextView pay12;
	TextView pay120;
	TextView pay1000;
	AlertDialog.Builder mailForInviteCodeDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_layout_new);
		pay12 = (TextView) findViewById(R.id.pay_12);
		pay120 = (TextView) findViewById(R.id.pay_120);
		pay1000 = (TextView) findViewById(R.id.pay_1000);
		if (Account.getInstance().getUserType() == 2) {
			// agent, no pay 1000 option
			pay1000.setVisibility(View.GONE);
		}
		mailForInviteCodeDialog = new AlertDialog.Builder(this);
		mailForInviteCodeDialog.setTitle("创业邀请码")
        	.setIcon(android.R.drawable.ic_dialog_email)
        	.setMessage("请您将手机号，支付宝帐号发送到lhmsj76@qq.com，我们会给你发送创业邀请码和一个电子协议。你使用创业邀请码再次登录后，就可以生成自己的邀请码，未来就可以得到所有使用你发的注册码注册用户充值额的10%的提成！")
        	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
		
		pay12.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String orderInfo = getOrderInfo("扑梭充值", "扑梭充值12元", "12.00");
				pay(orderInfo, 12);
			}
		});
		pay120.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String orderInfo = getOrderInfo("扑梭充值", "扑梭充值120元", "120.00");
				pay(orderInfo, 120);
			}
		});
		pay1000.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String orderInfo = getOrderInfo("扑梭充值", "扑梭充值1000元", "1000.00");
				pay(orderInfo, 1000);
			}
		});
	}

	public static final String PARTNER = "2088211803136807";
	public static final String SELLER = "1075575379@qq.com";
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAK7Q3/dIl8KSHXdgwqqsefjz3WUXOkStja09Zskbkd3Lu7ymJ0alv87hWFiMDbPf071Z8AouCzhUk5pfmwfpby0ZDjLmdGSH42Lm0RtAE2j9FKhwpOfueLzweTuMhEQfi62r0vzZ/kh1fLIIP3WaWgEDTjY3EbyG3aAFk86btSHfAgMBAAECgYEAkwjX95M1Ic/nvMrDsO3UwVHLuwUoWy9mZHmqhCgOLfD3/Jn7mu46P/VFa2SQ+eOeojqNLOLBSSXJ6CPVr3flqzLNGKGTTeV4GgNljcQ16SuqZJP1aFEJLev6+y/k19f3pBjprGI63k7dS+7FMcLUUCbTlnk899J0zP7Eey1S/zECQQDZ2AMVy2eleQLn69MZKYE/OypHJfk82RMsBj9ccyeXE51uQPWZegvkAE+YFIkQkyjPtpciShVxpBBiMOzjH2kLAkEAzW+EmObQgB2PxMnzIFRkLnJX0U082rpCzJKIXZSN1u5mAgEhldgUzjdBgpmfJ9tuvBja+pIEGboUQuGdNcY2/QI/Orb+HeG1DeSP4/Kr8xw8uc44lvSlOMFJaZRFf6WKBi5DQHRBvhwnGEz9JwBh8RW46kNMePF+1iJiSBTnxE+zAkAPXBsp43gLXCp3qDHJZQXogxwVxhSatfpwmTQg6w5/12tBExyTu4gysubvANVpgXqSDbmxqmEsG0GQjpXWg29xAkBRlvpawXLxy+ZHmqnsw1BfJwr39YdBtR2BYRQHGI2oiKacXxgtbS9x5T0kJKLbaBY7UVFpWkh/ftQxc1PDrv/Y";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					int pay = User.getInstance().getPay();
					pay += msg.arg1;
					User.getInstance().setPay(pay);
					if (msg.arg1 == 12 || msg.arg1 == 120) {
		            	//update membership
		            	Date date = new Date();
		            	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date membership = date;
						try {
							membership = dateFormat.parse(User.getInstance().getMembership());
						} catch (ParseException exc) {
							// TODO Auto-generated catch block
							exc.printStackTrace();
						}
						if (!date.after(membership)) {
							membership = date;
						}
						String strDate = updateMembership(membership, msg.arg1, dateFormat); 
						User.getInstance().setMembership(strDate);
					}
					User.getInstance().updateToCloudInBackground(PaySelectActivity.this);
					if (msg.arg1 == 1000) {
		            	mailForInviteCodeDialog.show();
		            }
					if (Account.getInstance().getUserType() == 1) {
						//non agent, update according agent
						final int mount = msg.arg1;
						AVQuery<AVObject> query = new AVQuery<AVObject>("GameUser");
						query.whereEqualTo("builtRegCode", User.getInstance().getRegCode());
						query.findInBackground(new FindCallback<AVObject>() {
						    public void done(List<AVObject> avObjects, AVException e) {
						        if (e == null) {
						            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
						            final AVObject gameUser = avObjects.get(0);
						            int memberPay = gameUser.getInt("memberPay");
						            memberPay += mount;
						            gameUser.put("memberPay", memberPay);
						            gameUser.saveInBackground(new SaveCallback() {

						    			@Override
						    			public void done(AVException e) {
						    				// TODO Auto-generated method stub
						    				if (e == null) {
						    					Log.w("avos", "object id =" + gameUser.getObjectId());
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
					Toast.makeText(PaySelectActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PaySelectActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(PaySelectActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PaySelectActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	private String updateMembership(Date membership, int pay, SimpleDateFormat dateFormat) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(membership);
		if (pay == 12) {
			ca.add(Calendar.MONTH, 1);
		} else { // 1 year and 3 month
			ca.add(Calendar.YEAR, 1);
			ca.add(Calendar.MONTH, 3);
		}
		return dateFormat.format(ca.getTime());
	}
	
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String orderInfo, final int mount) {
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PaySelectActivity.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);
				Log.w("pay result", result);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				msg.arg1 = mount;
				mHandler.sendMessage(msg);
				
				/*String result = "resultStatus={9000}";
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				msg.arg1 = mount;
				mHandler.sendMessage(msg);*/
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
