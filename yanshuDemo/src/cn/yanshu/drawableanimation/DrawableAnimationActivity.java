package cn.yanshu.drawableanimation;

import cn.yanshu.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class DrawableAnimationActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawableanimation_activity);
		
		ImageView  imgview=(ImageView) findViewById(R.id.drawableanimation_img);
		
		imgview.setBackgroundResource(R.drawable.draw_animation);
	}

}
