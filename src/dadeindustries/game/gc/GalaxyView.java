package dadeindustries.game.gc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class GalaxyView extends View implements OnTouchListener {

	Context ctxt;
	
	Paint paint = new Paint();
	int displayWidth  = 0;
	int displayHeight = 0;
	int squareSize = 0;
	Bitmap b; // temp variable

	
	public GalaxyView(Context context) {
		super(context);
		ctxt = context;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
		squareSize = displayWidth / 6;
		this.setBackgroundColor(Color.BLACK);
	    paint.setColor(Color.WHITE);
	    paint.setStrokeWidth(3);
	    this.setOnTouchListener(this);
	  //  new Bitmap(R.drawable.bp);

	}

	@Override
	public void onDraw(Canvas canvas) {

		// vertical lines
		for(int i=1; i<=displayWidth; i++){
			canvas.drawLine(i*squareSize, 0, i*squareSize, displayHeight, paint);
		}

		// horizontal lines
		for(int k=1; k<displayHeight; k++){
			canvas.drawLine(0, k*squareSize, displayWidth, k*squareSize, paint);
		}
		
		// highlight current square
		if(currentX>=0 && currentY>=0)
		{
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);

			int a = currentX*squareSize;
			int b = currentY*squareSize;
		    paint.setStrokeWidth(6);
			canvas.drawRect(a, b, a+squareSize, b+squareSize, paint);
		    paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			

		}
		
		// 	draw current icons		
		//	canvas.drawBitmap(bitmap, NULL, dst, paint);		
	}
	
	private int currentX = -1;
	private int currentY = -1;
	
	@Override
	public boolean onTouch(View view, MotionEvent motion) {

		// Use the type of motion event to potentially figure out 
		// whether user wishes to scroll map
		
		if(motion.getActionMasked()==MotionEvent.ACTION_MOVE)
		{
			Toast.makeText(ctxt, "Movement!", Toast.LENGTH_SHORT).show();
		}
			
		int x = (int) (motion.getX() / squareSize);
		int y = (int) (motion.getY() / squareSize);
		currentX = x;
		currentY = y;
		this.invalidate();
		return true;
	}
}