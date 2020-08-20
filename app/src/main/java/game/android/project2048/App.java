package game.android.project2048;

import android.app.Application;

/**
 * <br>createBy guoshiwen
 * <br>createTime: 2020/8/15 16:32
 */
public class App extends Application {

	private static App INSTANCE;

	public static App instance(){
	    return INSTANCE;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		INSTANCE = this;
	}
}
