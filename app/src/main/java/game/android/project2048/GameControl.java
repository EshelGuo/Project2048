package game.android.project2048;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * <br>createBy guoshiwen
 * <br>createTime: 2020/8/15 15:51
 * <br>desc: TODO
 */
public class GameControl extends GestureDetector.SimpleOnGestureListener {

	public static final String TAG = "GameControl";

	private final Context mContext;
	private GestureDetector mGestureDetector;
	private Listener mListener;

	private boolean unused;

	private final int DISTANCE;

	private float distanceX;
	private float distanceY;

	public GameControl(Context context) {
		mContext = context;
		mGestureDetector = new GestureDetector(context, this);
		DISTANCE = Utils.dip2px(70);
	}

	public void setListener(Listener listener) {
		mListener = listener;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		unused = true;
		distanceX = 0;
		distanceY = 0;
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if(!unused) {
			return true;
		}

		this.distanceX += distanceX;
		this.distanceY += distanceY;
		if(Math.abs(this.distanceX) >= DISTANCE){
			Log.i(TAG, this.distanceX > 0 ? "左" : "右");
			if(mListener != null){
				if(this.distanceX > 0) mListener.toLeft(); else mListener.toRight();
			}
			unused = false;
			return true;
		}

		if(Math.abs(this.distanceY) >= DISTANCE){
			Log.i(TAG, this.distanceY > 0 ? "上" : "下");
			if(mListener != null) {
				if (this.distanceY > 0) mListener.toUp();else mListener.toDown();
			}
			unused = false;
			return true;
		}
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}



	public interface Listener {
		void toLeft();
		void toRight();
		void toUp();
		void toDown();
	}
}
