package game.android.project2048;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextPaint;


/**
 * <br>createBy guoshiwen
 * <br>createTime: 2020/8/15 16:53
 * <br>desc: TODO
 */
public class Item {

	public static final int mItemCorner = Utils.dip2px(3);

//	Item left;
//	Item top;
//	Item right;
//	Item bottom;

	private int value = 0;

	/**
	 * 0 - 3
	 */
	private int x;

	/**
	 * 0 - 3
	 */
	private int y;

	private Point target;
	private Point pendingTarget = new Point();

	public Item(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void draw(Canvas canvas, RectF bounds, Paint paint, TextPaint textPaint) {
		if(value == 0) return;

		int backgroundColor = getBackgroundColor();
		paint.setColor(backgroundColor);
		canvas.drawRoundRect(bounds, mItemCorner, mItemCorner, paint);
		int textColor = getTextColor();
		textPaint.setColor(textColor);
		String valueS = String.valueOf(value);
		float textSize = bounds.width() / Math.max(3, valueS.length() - 1);
		textPaint.setTextSize(textSize);

		Paint.FontMetrics fm = textPaint.getFontMetrics();

		float width = textPaint.measureText(valueS);
		canvas.drawText(valueS, bounds.centerX() - width / 2, bounds.centerY() - textSize / 2 + (-fm.ascent),  textPaint);
	}

	private int getBackgroundColor() {
		switch (value){
//			case 0:
//				return 0xFFD6CDC4;
			case 2:
				return 0xFFEEE4DA;
			case 4:
				return 0xFFEDE0C8;
			case 8:
				return 0xFFF2B179;
			case 16:
				return 0xFFF59563;
			case 32:
				return 0xFFF67C5F;
			case 64:
				return 0xFFF2B179;
			case 128:
				return 0xFFEDCF72;
			case 256:
				return 0xFFEDCC61;
			case 512:
				return 0xFFF2B179;
			case 1024:
				return 0xFFF2B179;
			case 2048:
				return 0xFFEDC22E;
		}
		return 0xFF3C3A32;
	}

	private int getTextColor() {
		switch (value){
			case 2:
			case 4:
				return 0xFF776E65;
		}
		return 0xFFF9F6F2;
	}

	public void setTarget(int x, int y) {
		if(x < 0 || y < 0
				|| x > 3 || y > 3){
			throw new RuntimeException("setTarget Error!!! x = " + x + ", y = " + y);
		}

		if(this.target == null){
			target = pendingTarget;
		}

		this.target.set(x, y);
	}

	public boolean targetIsSelf(){
		if(target == null) return false;
		return target.x == x && target.y == y;
	}

	public void clearTarget(){
		target = null;
	}

	public Point getTarget() {
		return target;
	}
}
