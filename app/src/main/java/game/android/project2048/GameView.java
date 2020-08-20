package game.android.project2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <br>createBy guoshiwen
 * <br>createTime: 2020/8/15 16:21
 * <br>desc: TODO
 */
public class GameView extends View {

	public static final String TAG = "GameView";

	private Paint mPaint;
	private TextPaint mTextPaint;
	private RectF mRectF = new RectF();
	private Rect mRect = new Rect();
	private List<Item> mItems = new ArrayList<>(16);

	private int space = Utils.dip2px(10);
	private int mSize;
	private int mItemSize;

	private Random mRandom = new Random();

	private int mBackgroundCorner = Utils.dip2px(5);
	private List<Item> mEmptyItems;
	private Item[][] mMatrix = new Item[4][4];
	private ValueAnimator mAnimator;
	private int mProgress;

	public GameView(Context context) {
		this(context, null);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		initItems();
	}

	private void initItems() {
		mEmptyItems = new ArrayList<>(16);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				Item item = new Item(x, y);
				mMatrix[y][x] = item;
				mItems.add(item);

//				if(x != 0){
//					//Associated left and right
//					item.left = mMatrix[y][x - 1];
//					item.left.right = item;
//				}
//
//				if(y != 0){
//					//Associated top and bottom
//					item.top = mMatrix[y - 1][x];
//					item.top.bottom = item;
//				}
			}
		}
		generateValue();
	}

	private boolean generateValue(){
		int value = 0;
		int v = mRandom.nextInt(100);
		if(v <= 80){
			value = 2;
		}else {
			value = 4;
		}

		mEmptyItems.clear();
		for (int i = 0; i < mItems.size(); i++) {
			Item item = mItems.get(i);
			if(item.getValue() == 0){
				mEmptyItems.add(item);
			}
		}

		if(mEmptyItems.size() == 0){
			return false;
		}

		int index = mRandom.nextInt(mEmptyItems.size());
		Item item = mEmptyItems.get(index);
		item.setValue(value);

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBackground(canvas);
		drawItems(canvas);
	}

	private void drawItems(Canvas canvas) {
		Log.i(TAG, "onAnimationUpdate() called with: mProgress = [" + mProgress + "]");
		for (Item item : mItems) {
			int x = item.getX();
			int y = item.getY();

			int offsetXpx = 0;
			int offsetYpx = 0;
			Point target = item.getTarget();
			if(target != null){
				if(target.x != item.getX()){
					int offsetX = target.x - item.getX();
					offsetXpx = (offsetX) * (mItemSize + space);
					offsetXpx = offsetXpx * mProgress / 100;
				}

				if(target.y != item.getY()){
					int offsetY = target.y - item.getY();
					offsetYpx = (offsetY) * (mItemSize + space);
					offsetYpx = offsetYpx * mProgress / 100;
				}

			}

			int left = x * mItemSize + space * (x + 1) + offsetXpx;
			int top = y * mItemSize + space * (y + 1) + offsetYpx;
			int right = left + mItemSize;
			int bottom = top + mItemSize;

			mRectF.set(left, top, right, bottom);
			item.draw(canvas, mRectF, mPaint, mTextPaint);
		}
	}

	private void drawBackground(Canvas canvas) {
		mPaint.setColor(0xFFBBADA0);
		mRectF.set(0, 0, mSize, mSize);
		canvas.drawRoundRect(mRectF, mBackgroundCorner, mBackgroundCorner, mPaint);

		mPaint.setColor(0xFFD6CDC4);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int left = x * mItemSize + space * (x + 1);
				int top = y * mItemSize + space * (y + 1);
				int right = left + mItemSize;
				int bottom = top + mItemSize;
				mRectF.set(left, top, right, bottom);
				canvas.drawRoundRect(mRectF, Item.mItemCorner, Item.mItemCorner, mPaint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		int size = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
		mSize = size - Utils.dip2px(30);
		mItemSize = (mSize - 5 * space) / 4;
		super.onMeasure(MeasureSpec.makeMeasureSpec(mSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mSize, MeasureSpec.EXACTLY));
	}

	public void left(){
		if (isAnimationRunning()) return;
		boolean canChange = false;
		boolean isMerge = false;

		for (int y = 0; y < 4; y++) {
			int lastUsableX = 0;
			int lastValue = -1;

			for (int x = 0; x < 4; x++){
				Item item = mMatrix[y][x];

				if(item.getValue() == 0){
					item.clearTarget();
					continue;
				}

				if(lastValue == item.getValue()){
					lastValue = -1;
					item.setTarget(lastUsableX - 1, item.getY());
					isMerge = true;
					canChange = true;
					continue;
				}

				if(lastValue != item.getValue()){
					lastValue = item.getValue();
					item.setTarget(lastUsableX, item.getY());
					lastUsableX += 1;
					if(!item.targetIsSelf()){
						canChange = true;
					}
				}
			}
		}

		if(canChange) {
			runAnimation(Gravity.LEFT, isMerge);
		}else {
			Toast.makeText(getContext(),"can not change", Toast.LENGTH_SHORT).show();
		}
	}

	public void right(){
		if (isAnimationRunning()) return;
		boolean canChange = false;
		boolean isMerge = false;

		for (int y = 0; y < 4; y++) {
			int lastUsableX = 3;
			int lastValue = -1;

			for (int x = 3; x >= 0; x--){
				Item item = mMatrix[y][x];

				if(item.getValue() == 0){
					item.clearTarget();
					continue;
				}

				if(lastValue == item.getValue()){
					lastValue = -1;
					item.setTarget(lastUsableX + 1, item.getY());
					canChange = true;
					isMerge = true;
					continue;
				}

				if(lastValue != item.getValue()){
					lastValue = item.getValue();
					item.setTarget(lastUsableX, item.getY());
					lastUsableX -= 1;
					if(!item.targetIsSelf()){
						canChange = true;
					}
				}
			}
		}

		if(canChange) {
			runAnimation(Gravity.RIGHT, isMerge);
		}else {
			Toast.makeText(getContext(),"can not change", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isAnimationRunning() {
		boolean animationRunning = mAnimator != null && mAnimator.isRunning();
		if(animationRunning) {
			Toast.makeText(getContext(), "animationRunning", Toast.LENGTH_SHORT).show();
		}
		return animationRunning;
	}

	private void runAnimation(final int gravity, boolean isMerge) {
		mAnimator = ValueAnimator.ofInt(0, 100);
		mAnimator.setDuration(120);
		if(isMerge){
			mAnimator.setInterpolator(new OvershootInterpolator());
		}else {
			mAnimator.setInterpolator(new LinearInterpolator());
		}
		mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mProgress = (int) animation.getAnimatedValue();
				invalidate();
			}
		});
		mAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mProgress = 0;
				updateValue(gravity);
				generateValue();
				invalidate();
			}
		});
		mAnimator.start();
	}

	private void updateValue(int gravity) {
		switch (gravity){
			case Gravity.BOTTOM:
			case Gravity.RIGHT:{
				for (int y = 3; y >= 0; y--) {
					for (int x = 3; x >= 0; x--) {
						valueToTarget(mMatrix[y][x]);
					}
				}
			}
			break;
			case Gravity.TOP:
			case Gravity.LEFT:{
				for (int y = 0; y < 4; y++) {
					for (int x = 0; x < 4; x++) {
						valueToTarget(mMatrix[y][x]);
					}
				}
			}
			break;
		}
	}

	private void valueToTarget(Item item){
		Point index = item.getTarget();
		if(index != null &&
				(index.x != item.getX() || index.y != item.getY())){
			Item target = mMatrix[index.y][index.x];
//			if (target.getValue() != 0 && target.getValue() != item.getValue()) {
//				valueToTarget(target);
//				if(target.getValue() != item.getValue()){
//					throw new RuntimeException();
//				}
//			}
			target.setValue(target.getValue() + item.getValue());
			item.setValue(0);
		}
	}

	public void up(){
		if (isAnimationRunning()) return;
		boolean canChange = false;
		boolean isMerge = false;

		for (int x = 0; x < 4; x++){
			int lastUsableY = 0;
			int lastValue = -1;
			for (int y = 0; y < 4; y++) {
				Item item = mMatrix[y][x];

				if(item.getValue() == 0){
					item.clearTarget();
					continue;
				}

				if(lastValue == item.getValue()){
					lastValue = -1;
					item.setTarget(item.getX(), lastUsableY - 1);
					canChange = true;
					isMerge = true;
					continue;
				}

				if(lastValue != item.getValue()){
					lastValue = item.getValue();
					item.setTarget(item.getX(), lastUsableY);
					lastUsableY += 1;
					if(!item.targetIsSelf()){
						canChange = true;
					}
				}
			}
		}

		if(canChange) {
			runAnimation(Gravity.TOP, isMerge);
		}else {
			Toast.makeText(getContext(),"can not change", Toast.LENGTH_SHORT).show();
		}
	}

	public void down(){
		if (isAnimationRunning()) return;
		boolean canChange = false;
		boolean isMerge = false;

		for (int x = 0; x < 4; x++){
			int lastUsableY = 3;
			int lastValue = -1;
			for (int y = 3; y >= 0; y--) {
				Item item = mMatrix[y][x];

				if(item.getValue() == 0){
					item.clearTarget();
					continue;
				}

				if(lastValue == item.getValue()){
					lastValue = -1;
					item.setTarget(item.getX(), lastUsableY + 1);
					canChange = true;
					isMerge = true;
					continue;
				}

				if(lastValue != item.getValue()){
					lastValue = item.getValue();
					item.setTarget(item.getX(), lastUsableY);
					lastUsableY -= 1;
					if(!item.targetIsSelf()){
						canChange = true;
					}
				}
			}
		}

		if(canChange) {
			runAnimation(Gravity.BOTTOM, isMerge);
		}else {
			Toast.makeText(getContext(),"can not change", Toast.LENGTH_SHORT).show();
		}
	}
}
