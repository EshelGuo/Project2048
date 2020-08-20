package game.android.project2048;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity implements GameControl.Listener {

	public static final String TAG = "MainActivity___";

	private GameControl mGameControl;
	private GameView mGameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGameView = findViewById(R.id.game_view);
		mGameControl = new GameControl(this);
		mGameControl.setListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGameControl.onTouchEvent(event);
	}

	@Override
	public void toLeft() {
		mGameView.left();
	}

	@Override
	public void toRight() {
		mGameView.right();
	}

	@Override
	public void toUp() {
		mGameView.up();
	}

	@Override
	public void toDown() {
		mGameView.down();
	}
}