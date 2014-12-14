package com.jxhobo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-11-27
 * Time: 下午10:21
 * To change this template use File | Settings | File Templates.
 */
public class ResultActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result);
        int index = getIntent().getIntExtra("index", 1);
        ImageView imageView = (ImageView) findViewById(R.id.result_iv);
        imageView.setImageResource(getResId("a" + index, "drawable"));
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
}
