package com.jxhobo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jxhobo.http.util.HttpUtil;

public class GameMainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private List<TextView> numTvs;
    private List<View> numLvs;
    private int[] sourceData = new int[198];
    private int[][] createData = new int[9][9];
    private int createdNum = 0;
    private int[] index;
    private final int changeNextPageMsg = 1;
    private final long delayMillis = 2000;
    private int changePageIndex = 0;
    private TextView curNumTv;
    private TextView startTv;
    private TextView leaveNumTv;
    private TextView clock;
    private int lastSelected = -1;
    private boolean isStart = false;
    private int timing = 117 * 2;
    private int hitCount = 0;
    private AlertDialog.Builder invalidGameDialog;
    public TextView leastPoint;
    public TextView rank;
    public int leastRecord = 198;
    
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case changeNextPageMsg:
                    if (!isStart) return;
                    changePageIndex++;
                    if (changePageIndex == 117) {
                        //TODO:结束
                        leaveNumTv.setText("剩余" + 0 + "张");
                        getResult();
                    } else {
                        curNumTv.setText(sourceData[changePageIndex] + "");
                        leaveNumTv.setText("剩余" + (117 - changePageIndex) + "张");
                        sendChangeNextPage();
                    }
                    timing -= delayMillis / 1000;
                    if (timing < 0) {
                    	timing = 0;
                    }
                    clock.setText(timing + "秒");
                    break;
            }
        }
    };

    private void getResult() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            int i1 = index[i];
            if (i1 != 8) {
                count += createData[i][index[i]];
            }
        }
        int i = count / 6;
        if (i < 1 || i > 198) {
            i = i % 6 + 1;
        }
        if (hitCount < 12) {
        	handler.removeMessages(changeNextPageMsg);
        	reset();
        	invalidGameDialog.show();
        	return;
        }
        if (i < leastRecord) {
        	HttpUtil.updateTopScore(this, i, new UpdateTopScoreHandler(this));
        	leastPoint.setText("最少分 " + i);
        }
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("index", i);
        startActivity(intent);
        handler.removeMessages(changeNextPageMsg);
        reset();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        curNumTv = (TextView) findViewById(R.id.cur_num_tv);
        startTv = (TextView) findViewById(R.id.start_tv);
        startTv.setOnClickListener(onClickListener);
        leaveNumTv = (TextView) findViewById(R.id.leave_num);
        clock = (TextView) findViewById(R.id.clock_timing);
        leastPoint = (TextView) findViewById(R.id.least_point);
        rank = (TextView) findViewById(R.id.rank);
        
        //HttpUtil.queryInfo(this, new QueryInfoHandler(this));
        
        numTvs = new ArrayList<TextView>(9);
        numLvs = new ArrayList<View>(9);
        for (int i = 0; i < sourceData.length; i++) {
            sourceData[i] = i + 1;
        }
        numTvs.add( (TextView)findViewById(R.id.num_tv1));
        numTvs.add( (TextView)findViewById(R.id.num_tv2));
        numTvs.add( (TextView)findViewById(R.id.num_tv3));
        numTvs.add( (TextView)findViewById(R.id.num_tv4));
        numTvs.add( (TextView)findViewById(R.id.num_tv5));
        numTvs.add( (TextView)findViewById(R.id.num_tv6));
        numTvs.add( (TextView)findViewById(R.id.num_tv7));
        numTvs.add( (TextView)findViewById(R.id.num_tv8));
        numTvs.add( (TextView)findViewById(R.id.num_tv9));
        for (TextView numTv : numTvs) {
            numTv.setOnClickListener(onClickListener);
        }

//        for (int i = 0; i < 9; i++) {
//            View view = getLayoutInflater().inflate(R.layout.num_item, numsGl,false);
//            TextView textView = (TextView) view.findViewById(R.id.num_tv);
//            textView.setBackgroundResource(getResId("num_bg" + (i + 2), "drawable"));
//            textView.setHintTextColor(android.R.color.transparent);
//            textView.setOnClickListener(onClickListener);
//            numTvs.add(textView);
//            numLvs.add(view);
//            //指定该组件所在的行
//            GridLayout.Spec rowSpec = GridLayout.spec(i / 3 + 1);
//            //指定该组件所在的列
//            GridLayout.Spec columnSpec = GridLayout.spec(i % 3);
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
//            //指定该组件占满容器
//            params.setGravity(Gravity.CENTER);
//            numsGl.addView(view, params);
//        }
        reset();
        
        invalidGameDialog = new AlertDialog.Builder(this);
        invalidGameDialog.setTitle("本局无效！")
        	.setIcon(android.R.drawable.ic_dialog_alert)
        	.setMessage("您点击卡牌消除的次数太少，因此本局无效，请重新再来！")
        	.setPositiveButton("确定", null);
    }

    public void sendChangeNextPage() {
        handler.sendEmptyMessageDelayed(changeNextPageMsg, delayMillis);
    }

    public void reset() {
        index = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};
        createdNum = 0;
        changePageIndex = 0;
        lastSelected = -1;
        startTv.setText("开始");
        initData();
        curNumTv.setText(sourceData[changePageIndex] + "");
        leaveNumTv.setText("剩余" + (117 - changePageIndex) + "张");
        timing = 117 * 2;
        clock.setText(timing + "");
        hitCount = 0;
    }

    private void initData() {
        Random rand = new Random();
        for (int i = 0; i < sourceData.length; i++) {
            int data = rand.nextInt(198 - createdNum);
            int i2 = sourceData[data];
            if (createdNum < 81) {
                insertData(i2);
            }

            createdNum++;
            sourceData[data] = sourceData[198 - createdNum];
            sourceData[198 - createdNum] = i2;
        }

        for (int i = 0; i < 9; i++) {
            nextPage(i);
        }

    }

    public void nextPage(int page) {
        index[page] = index[page] + 1;
        if (index[page] == 8) {
            numTvs.get(page).setText("OK");
        } else {
            numTvs.get(page).setText(createData[page][index[page]] + "");
        }
    }

    private void insertData(int num) {
        createData[createdNum / 9][createdNum % 9] = num;
    }

    /**
     * 根据资源的名字获取它的ID
     *
     * @param name    要获取的资源的名字
     * @param defType 资源的类型，如drawable, string 。。。
     * @return 资源的id
     */
    public int getResId(String name, String defType) {
        String packageName = getApplicationInfo().packageName;
        return getResources().getIdentifier(name, defType, packageName);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.start_tv) {
                if (isStart) {
                	isStart = false;
                	handler.removeMessages(changeNextPageMsg);
                	reset();
                } else {
                    isStart = true;
                    sendChangeNextPage();
                    startTv.setText("重来");
                }
                return;
            }
            int i = numTvs.indexOf(view);
            if (i >= 0) {
                if (!isStart) {
                    Toast.makeText(GameMainActivity.this, "请点击开始", Toast.LENGTH_SHORT).show();
                    //HttpUtil.updateTopScore(GameMainActivity.this, 100, new UpdateTopScoreHandler(GameMainActivity.this));
                    //HttpUtil.queryInfo(GameMainActivity.this, new QueryInfoHandler(GameMainActivity.this));
                    return;
                }
                int i1 = sourceData[changePageIndex];
                int i2 = createData[i][index[i]];
                if (i1 % 10 == i2 % 10) {
                    if (i1 == lastSelected) {
                        Toast.makeText(GameMainActivity.this, "已消除", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lastSelected = i1;
                    nextPage(i);
                }
                hitCount++;
                //Toast.makeText(GameMainActivity.this, "hitcount=" + hitCount, Toast.LENGTH_SHORT).show();
            }
        }
    };

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HttpUtil.queryInfo(this, new QueryInfoHandler(this));
	}
}
