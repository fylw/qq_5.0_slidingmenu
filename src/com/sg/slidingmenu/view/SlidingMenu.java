package com.sg.slidingmenu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.sg.slidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * 
 *  #(c)  <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 根据www.imooc.com 网站提供的教程demo修改完成。增加右侧菜单滑出效果；并修改部分动画以达到像qq客户端左右侧滑效果
 * 
 *  <br/>创建说明: 2015-1-20 上午9:51:12 shenggguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class SlidingMenu extends HorizontalScrollView{
	private LinearLayout mWapper;
	private ViewGroup mLeftMenu;
	private ViewGroup mContent;
	private ViewGroup mRightMenu;
	private int mScreenWidth;//屏幕宽度
	private int mScreenHeight;//屏幕高度
	//左侧侧菜单布局右侧方向的padding值
	private int mLeftMenuPaddingRightWidth;//px
	private int mLeftMenuPaddingRight = 50;//dp 50是默认值
	//右侧菜单布局左侧方向的padding值
	private int mRightMenuPaddingLeftWidth;//px
	private int mRightMenuPaddingLeft = 250;//dp 250是默认值
	private boolean once;

	private boolean isLeftOpen = false;
	private boolean isRightOpen = false;
	private float x;
	
	public SlidingMenu(Context context) {
		this(context, null);
	}
	
	/**
	 * 未使用自定义属性时，调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 当使用了自定义属性时，会调用此构造方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 获取我们定义的属性
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.SlidingMenu_rightMenuPaddingLeft:
				// dp to px
				mRightMenuPaddingLeft = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));
				break;
			case R.styleable.SlidingMenu_leftMenuPaddingRight:
				// dp to px
				mLeftMenuPaddingRight = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));
				break;
			}
		}
		a.recycle();

		// 获取屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
	}


	/**
	 * 设置子View的宽和高 设置自己的宽和高
	 */
	@SuppressLint("DrawAllocation") @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			mWapper = (LinearLayout) getChildAt(0);
			if(mWapper.getChildCount() == 1){
				//子布局的数量为一个。 则作为内容页
				mContent = (ViewGroup) mWapper.getChildAt(0);
			} else if(mWapper.getChildCount() == 2){
				//子布局的数量为两个。 第一个作为左侧菜单； 第二个作为内容页
				mContent = (ViewGroup) mWapper.getChildAt(1);
				mLeftMenu = (ViewGroup) mWapper.getChildAt(0);
				mLeftMenuPaddingRightWidth = mLeftMenu.getLayoutParams().width = mScreenWidth
						- mLeftMenuPaddingRight;
			} else if(mWapper.getChildCount() > 2){
				//子布局的数量超过三个。 第一个作为左侧菜单； 第二个作为内容页；第三个作为右侧菜单
				mContent = (ViewGroup) mWapper.getChildAt(1);
				mLeftMenu = (ViewGroup) mWapper.getChildAt(0);
				mRightMenu = (ViewGroup) mWapper.getChildAt(2);
				mLeftMenuPaddingRightWidth = mLeftMenu.getLayoutParams().width = mScreenWidth
						- mLeftMenuPaddingRight;
				mRightMenuPaddingLeftWidth = mRightMenu.getLayoutParams().width = mScreenWidth
						- mRightMenuPaddingLeft;
				ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mRightMenu.getLayoutParams();  // , 1是可选写的
				int height = lp.height;
				Log.d("SlidingMenu", "height:" + height + ",mScreenHeight:" + mScreenHeight);
				//内容区域偏移量作为左侧菜单的上下内边距
				int mRightMenuPadingTopAndBotton = (int)(mScreenHeight / 2 * 0.2f);
				((MarginLayoutParams) lp).setMargins(0, mRightMenuPadingTopAndBotton,  0, mRightMenuPadingTopAndBotton); 
				mRightMenu.setLayoutParams(lp);
			} else {
				//如果没有子布局则创建一个内容页
				mContent = new LinearLayout(getContext()); 
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				mContent.setLayoutParams(params);
			}
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 通过设置偏移量，将menu隐藏
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) { // 内容区域向右移动
			this.scrollTo(mLeftMenuPaddingRightWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			x = ev.getX();
			break;
		case MotionEvent.ACTION_UP:
			float x2 = ev.getX();
			if (x < x2) {// 向右滑
				// 隐藏在左边的宽度
				int scrollX = getScrollX();// 获取滑动距离
				if (scrollX >= mLeftMenuPaddingRightWidth / 2) {
					this.smoothScrollTo(mLeftMenuPaddingRightWidth, 0);
					isLeftOpen = false;
				} else {
					this.smoothScrollTo(0, 0);
					isLeftOpen = true;
				}
			} else {
				// 隐藏在右边边的宽度
				int scrollX = getScrollX();// 获取滑动距离
				if (scrollX - mLeftMenuPaddingRightWidth >= mRightMenuPaddingLeftWidth / 2) {
					this.smoothScrollTo(mLeftMenuPaddingRightWidth + mScreenWidth, 0);
					isRightOpen = false;
				} else {
					this.smoothScrollTo(mLeftMenuPaddingRightWidth, 0);
					isRightOpen = true;
				}
			}

			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 打开左侧菜单
	 */
	public void openLeftMenu() {
		if (isLeftOpen)
			return;
		this.smoothScrollTo(0, 0);
		isLeftOpen = true;
	}
	
	/**
	 * 关闭左侧菜单
	 */
	public void closeLeftMenu() {
		if (!isLeftOpen)
			return;
		this.smoothScrollTo(mLeftMenuPaddingRightWidth, 0);
		isLeftOpen = false;
	}
	

	/**
	 * 打开右侧菜单
	 */
	public void openRightMenu() {
		if (isRightOpen)
			return;
		this.smoothScrollTo(mLeftMenuPaddingRightWidth + mScreenWidth, 0);
		isRightOpen = true;
	}

	/**
	 * 功能描述:关闭右侧菜单
	 */
	public void closeRightMenu() {
		if (!isRightOpen)
			return;
		this.smoothScrollTo(mLeftMenuPaddingRightWidth, 0);
		isRightOpen = false;
	}

	/**
	 * 切换左侧菜单
	 */
	public void toggleLight() {
		if (isLeftOpen) {
			closeLeftMenu();
		} else {
			openLeftMenu();
		}
	}
	
	/**
	 * 切换右侧菜单
	 */
	public void toggleRight() {
		if (isRightOpen) {
			closeRightMenu();
		} else {
			openRightMenu();
		}
	}
	
	/**
	 * 显示内容
	 */
	public void showMain() {
		if(isLeftOpen){
			closeLeftMenu();
		} else if(isRightOpen){
			closeRightMenu();
		}
	}
	
	/**
	 * 获取是否是否显示主界面
	 */
	public Boolean isShowMain() {
		if(isLeftOpen || isRightOpen){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 滚动发生时
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		Log.i("SlidingMenu", "l:" + l + ",mMenuRightWidth:" + mLeftMenuPaddingRightWidth + ",mMenuLeftWidth:" + mRightMenuPaddingLeftWidth);
		if(mLeftMenu != null &&l <= mLeftMenuPaddingRightWidth){
			float scale = l * 1.0f / mLeftMenuPaddingRightWidth; // 1 ~ 0
			
			// 幕客网客户端侧滑效果
			// 调用属性动画，设置TranslationX
			//ViewHelper.setTranslationX(mMenu, mMenuWidth * scale);
	
			// 仿QQ客户端侧滑效果
			/**
			 * 区别1：内容区域1.0~0.8 缩放的效果 scale : 1.0~0.0 (0.8 + 0.2 * scale)
			 * 
			 * 区别2：菜单的偏移量需要修改
			 * 
			 * 区别3：菜单的显示时有缩放以及透明度变化 缩放：0.7 ~1.0 (1.0 - scale * 0.3) 透明度 0.6 ~ 1.0 
			 * (0.6 + 0.4 * (1- scale)) ;
			 * 
			 */
			float rightScale = 0.8f + 0.2f * scale;
			float leftScale = 1.0f - scale * 0.3f;
			float leftAlpha = 0.6f + 0.4f * (1 - scale);
	
			// 调用属性动画，设置TranslationX
			ViewHelper.setTranslationX(mLeftMenu, mLeftMenuPaddingRightWidth * scale * 0.8f);//左侧区域隐藏0.2，显示0.8
			
			ViewHelper.setScaleX(mLeftMenu, leftScale);
			ViewHelper.setScaleY(mLeftMenu, leftScale);
			ViewHelper.setAlpha(mLeftMenu, leftAlpha);
			// 设置content的缩放的中心点
			ViewHelper.setPivotX(mContent, 0);
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			// 设置content的缩放比例
			ViewHelper.setScaleX(mContent, rightScale);
			ViewHelper.setScaleY(mContent, rightScale);
		} else if(mRightMenu != null && (mRightMenuPaddingLeftWidth + mLeftMenuPaddingRightWidth -l) < mRightMenuPaddingLeftWidth){
			float scale = (mRightMenuPaddingLeftWidth + mLeftMenuPaddingRightWidth -l) * 1.0f / mRightMenuPaddingLeftWidth; // 1 ~ 0
			// 仿QQ客户端侧滑效果
			/**
			 * 区别1：内容区域1.0~0.8 缩放的效果 scale : 1.0~0.0 (0.8 + 0.2 * scale)
			 * 
			 * 区别2：菜单的偏移量需要修改
			 * 
			 * 区别3：菜单的显示时有缩放以及透明度变化 缩放：0.6 ~1.0 (1.0 - scale * 0.4) 透明度 0.6 ~ 1.0 
			 * (0.6 + 0.4 * (1- scale)) ;
			 */
			float contentScale = 0.8f + 0.2f * scale;
			float rightScale = 1.0f - scale * 0.4f;
			float leftAlpha = 0.6f + 0.4f * (1 - scale);

			// 调用属性动画，设置TranslationX
			ViewHelper.setTranslationX(mRightMenu, mRightMenuPaddingLeftWidth * scale * 0.8f);//左侧区域隐藏0.2，显示0.8
			ViewHelper.setScaleX(mRightMenu, rightScale);
			ViewHelper.setScaleY(mRightMenu, rightScale);
			ViewHelper.setAlpha(mRightMenu, leftAlpha);
			
			// 设置content的缩放的中心点
			ViewHelper.setPivotX(mContent, mContent.getWidth());
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			// 设置content的缩放比例
			ViewHelper.setScaleX(mContent, contentScale);
			ViewHelper.setScaleY(mContent, contentScale);
		}
	}

}
