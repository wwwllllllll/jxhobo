package com.jxhobo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-11-27
 * Time: 下午10:21
 * To change this template use File | Settings | File Templates.
 */
public class JiugonggePredictResultActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jiugongge_predict_result);
        int index1 = getIntent().getIntExtra("index1", 1);
        int index2 = getIntent().getIntExtra("index2", 2);
        // Set up the ViewPager with the sections adapter.
		ViewPager viewPager = (ViewPager) findViewById(R.id.result_viewpager);
		viewPager.setAdapter(new ResultPagerAdapter(getSupportFragmentManager(), this, index1, index2));
    }
}
