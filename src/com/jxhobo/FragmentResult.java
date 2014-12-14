package com.jxhobo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentResult extends Fragment {
	Context context;
	int index;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_result, container, false);
		ImageView result = (ImageView) rootView.findViewById(R.id.predict_result);
		result.setImageResource(getResId("a" + index, "drawable"));
		return rootView;
	}

	public FragmentResult(Context context, int index) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.index = index;
	}

	/**
     * 根据资源的名字获取它的ID
     *
     * @param name    要获取的资源的名字
     * @param defType 资源的类型，如drawable, string 。。。
     * @return 资源的id
     */
    public int getResId(String name, String defType) {
        String packageName = context.getApplicationInfo().packageName;
        return getResources().getIdentifier(name, defType, packageName);
    }
}
