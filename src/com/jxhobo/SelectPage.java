package com.jxhobo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.alipay.sdk.pay.PaySelectActivity;
import com.jxhobo.bean.User;
import com.jxhobo.broad.Constants;
import com.jxhobo.http.dataservice.RequestHandler;
import com.jxhobo.http.dataservice.http.HttpRequest;
import com.jxhobo.http.dataservice.http.HttpResponse;
import com.jxhobo.http.util.Account;
import com.jxhobo.http.util.CommonMsg;
import com.jxhobo.http.util.HttpUtil;
import com.jxhobo.http.util.JsonUtil;
import com.jxhobo.http.util.ResultCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SelectPage extends Activity {
    private List<Integer> newCodeList = new ArrayList<Integer>();
    
    private ListView gameListView;
    private GameListViewAdapter gameListViewAdapter;
    private AlertDialog.Builder glassDialog;
    private AlertDialog.Builder requestMembershipDialog;
    private AlertDialog.Builder notAvailableDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_new);
        
        gameListView = (ListView) findViewById(R.id.game_list);
        gameListViewAdapter = new GameListViewAdapter(this);
		gameListView.setAdapter(gameListViewAdapter);
		gameListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					Intent jiugongge = new Intent(SelectPage.this, GameMainActivity.class);
		            startActivity(jiugongge);
				} else if (position == 1) {
					Date date = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date membership = date;
					try {
						membership = dateFormat.parse(User.getInstance().getMembership());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (date.before(membership)) {
						// still membership
						Intent jiugonggePredict = new Intent(SelectPage.this, JiugonggePredictActivity.class);
		                startActivity(jiugonggePredict);
					} else {
						requestMembershipDialog.show();
					}
				} else {
					Date date = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date membership = date;
					try {
						membership = dateFormat.parse(User.getInstance().getMembership());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (date.before(membership)) {
						// still membership
						notAvailableDialog.show();
					} else {
						requestMembershipDialog.show();
					}
				}
			}
		});
		
		glassDialog = new AlertDialog.Builder(this);
        glassDialog.setTitle("一起创业吧！")
        	.setIcon(android.R.drawable.ic_dialog_map)
        	.setMessage("如果您充值1000元，您就得到和我们一起创业的机会！请充值后了解详情！")
        	.setPositiveButton("确定", null);
        
		requestMembershipDialog = new AlertDialog.Builder(this);
		requestMembershipDialog.setTitle("请充值！")
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setMessage("充值成为会员后，就享受无限制畅玩!")
        	.setPositiveButton("确定", null);
		
		notAvailableDialog = new AlertDialog.Builder(this);
		notAvailableDialog.setTitle("敬请期待！")
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setMessage("1月15日上线，敬请期待！")
        	.setPositiveButton("确定", null);
    }

    private void registerBroadCast() {
        // TODO Auto-generated method stub
        IntentFilter checkNewCodeFilter = new IntentFilter(Constants.CHECK_CODE_ACTION);
        checkNewCodeFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(chenckNewCodeReceive, checkNewCodeFilter);
    }

    private BroadcastReceiver chenckNewCodeReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int newCode = intent.getIntExtra(Constants.CHECK_CODE_ACTION_KEY, 0);
            Log.i("newCode", newCode + "");
            newCodeList.add(newCode);
            Log.i("newCodelistsize", newCodeList.size() + "");
        }

    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem invite_code = menu.findItem(R.id.invite_code);
        MenuItem glass = menu.findItem(R.id.glass);        
        if (Account.getInstance().getUserType() == 2) {
			// agent, no glass
        	glass.setVisible(false);
        } else {
        	//no agent, no invite_code
        	invite_code.setVisible(false);
        	Date now = new Date();
        	Calendar ca = Calendar.getInstance();
    		ca.setTime(now);
    		ca.add(Calendar.DATE, 3);
    		if (now.after(ca.getTime())) {
    			glass.setVisible(false);
    		}
        }
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case R.id.glass:
            glassDialog.show();
            break;
        case R.id.pay:
        	//Toast.makeText(this, "pay", Toast.LENGTH_SHORT).show();
        	startActivity(new Intent(this, PaySelectActivity.class));
            break;
        case R.id.invite_code:
        	//Toast.makeText(this, "invite code", Toast.LENGTH_SHORT).show();
        	//HttpUtil.generateInviteCode(this, new GenerateInviteCodeHandler(this));
        	startActivity(new Intent(this, InviteCodeInfoActivity.class));
        	break;
        default:
            break;
        }
    	return super.onOptionsItemSelected(item);
	}
}
