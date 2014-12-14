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

public class UpdateTopScoreHandler implements RequestHandler<HttpRequest, HttpResponse> {
	Context context;
	public UpdateTopScoreHandler(Context context) {
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
        Log.i("update top score", new String((byte[]) response.result()));
        if (commonMsg == null) {
            Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ResultCode.dealError(context, commonMsg.getResultCode())) {
        	Toast.makeText(context, "更新游戏成绩成功", Toast.LENGTH_SHORT).show();
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
