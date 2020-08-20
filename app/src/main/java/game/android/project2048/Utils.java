package game.android.project2048;

/**
 * <br>createBy guoshiwen
 * <br>createTime: 2020/8/15 16:32
 * <br>desc: TODO
 */
public class Utils {

	public static int dip2px(float dipValue) {
		final float scale = App.instance().getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
