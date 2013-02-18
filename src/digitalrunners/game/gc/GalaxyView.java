package digitalrunners.game.gc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GalaxyView extends View implements OnClickListener, OnTouchListener {

	Context ctxt;
	
	Paint paint = new Paint();
	int displayWidth  = 0;
	int displayHeight = 0;
	int squareSize = 0;
	
	public GalaxyView(Context context) {
		super(context);
		ctxt = context;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
		squareSize = displayWidth / 4;
		this.setBackgroundColor(Color.BLACK);
	    paint.setColor(Color.WHITE);
	    paint.setStrokeWidth(3);
	}

	@Override
	public void onDraw(Canvas canvas) {

		// vertical lines
		for(int i=1; i<=4; i++){
			canvas.drawLine(i*squareSize, 0, i*squareSize, displayHeight, paint);
		}

		// horizontal lines
		for(int k=1; k<displayHeight; k++){
			canvas.drawLine(0, k*squareSize, displayWidth, k*squareSize, paint);
		}
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent motion) {

		// Use the type of motion event to potentially figure out 
		// whether user wishes to scroll map
		if(view.equals(this)){
			paint.setColor(Color.RED);
		}
		return false;
	}

	@Override
	public void onClick(View view) {

		Toast.makeText(ctxt, "Woah!", Toast.LENGTH_SHORT).show();
	}
}