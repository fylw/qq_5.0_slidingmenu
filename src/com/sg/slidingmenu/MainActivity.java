package com.sg.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.sg.slidingmenu.R;
import com.sg.slidingmenu.view.SlidingMenu;

public class MainActivity extends Activity {

	private SlidingMenu mSlidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.id_menu);
	}

	public void toggleLeftMenu(View view) {
		mSlidingMenu.toggleLight();
	}

	public void toggleRightMenu(View view) {
		mSlidingMenu.toggleRight();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something...
			if (!mSlidingMenu.isShowMain()) {
				mSlidingMenu.showMain();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
