package com.jxhobo;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jxhobo.http.dataservice.RequestHandler;
import com.jxhobo.http.dataservice.http.HttpRequest;
import com.jxhobo.http.dataservice.http.HttpResponse;
import com.jxhobo.http.util.CommonMsg;
import com.jxhobo.http.util.JsonUtil;
import com.jxhobo.http.util.ResultCode;

public class QueryInfoHandler implements RequestHandler<HttpRequest, HttpResponse> {
	Context context;
	public QueryInfoHandler(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
    public void onRequestFailed(HttpRequest request, HttpResponse response) {
        Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRequestFinish(HttpRequest request, HttpResponse response) {
        CommonMsg commonMsg = CommonMsg.deSerializeJson((byte[]) response.result());
        Log.i("query info", new String((byte[]) response.result()));
        if (commonMsg == null) {
            Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ResultCode.dealError(context, commonMsg.getResultCode())) {
        	String data = commonMsg.getData();
            HashMap read = JsonUtil.read(data, HashMap.class);
            Integer topScore = (Integer) read.get("topScore");
            Integer totalUNum = (Integer) read.get("totalUNum");
            Integer currentIndex = (Integer) read.get("currentIndex");
            Log.i("query info", "top=" + topScore + "total=" + totalUNum + "curridx=" + currentIndex);
            if (topScore != null) {
            	((GameMainActivity)context).leastRecord = topScore.intValue();
            	((GameMainActivity)context).leastPoint.setText("最少分 " + topScore.toString());
            	((GameMainActivity)context).rank.setText("排名 " + (currentIndex.intValue() + 1) + " / " + totalUNum.toString());
            } else {
            	// new record, no rank
            	((GameMainActivity)context).rank.setText("排名 " + totalUNum.toString() + " / " + totalUNum.toString());
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
