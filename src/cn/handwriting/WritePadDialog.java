package cn.handwriting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hexing.imeter.R;


public class WritePadDialog extends Dialog {

	Context context;
	LayoutParams p ;
	DialogListener dialogListener;

	public WritePadDialog(Context context,DialogListener dialogListener) {
		super(context);
		this.context = context;
		this.dialogListener = dialogListener;
	}

	static final int BACKGROUND_COLOR = 0x88ffffff;

	static final int BRUSH_COLOR = Color.BLACK;

	PaintView mView;
	
	int tabletHeight;
	

	/** The index of the current color to use. */
	int mColorIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.write_pad);
		
		LinearLayout mTablet = (LinearLayout)findViewById(R.id.tablet);
		
		Button btnClear = (Button) findViewById(R.id.tablet_clear);
		Button btnOk = (Button) findViewById(R.id.tablet_ok);
		Button btnCancel = (Button)findViewById(R.id.tablet_cancel);
		
		p = getWindow().getAttributes();  //获取对话框当前的参数值   
		DisplayMetrics dm = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
		//p.height = (int) (dm.heightPixels * 0.9);   //高度设置为屏幕的0.4 
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		mTablet.measure(w, h);  
		
		tabletHeight =mTablet.getMeasuredHeight();
		
		 
		p.width = (int) (dm.widthPixels * 0.95);    //宽度设置为屏幕的0.6
		p.height = (int) (p.width * 0.618);
		
		
		getWindow().setAttributes(p);     //设置生效
				
		mView = new PaintView(context);
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
		frameLayout.addView(mView);
		mView.requestFocus();
		
		btnClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 mView.clear();
			}
		});

		
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					dialogListener.refreshActivity(mView.getCachebBitmap());
					WritePadDialog.this.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
	}
	

	/**
	 * This view implements the drawing canvas.
	 * 
	 * It handles all of the input events and drawing functions.
	 */
	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Bitmap cachebBitmap;
		private Path path;

		public Bitmap getCachebBitmap() {
			return cachebBitmap;
		}

		public PaintView(Context context) {
			super(context);					
			init();			
		}

		private void init(){
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);					
			path = new Path();
			cachebBitmap = Bitmap.createBitmap(p.width, p.height-tabletHeight, Config.ARGB_8888);			
			cacheCanvas = new Canvas(cachebBitmap);
			cacheCanvas.drawColor(Color.WHITE);
		}
		public void clear() {
			if (cacheCanvas != null) {
				
				//paint.setColor(BACKGROUND_COLOR);
				cacheCanvas.drawPaint(paint);
				paint.setColor(Color.BLACK);
				cacheCanvas.drawColor(Color.WHITE);
				invalidate();			
			}
		}

		
		
		@Override
		protected void onDraw(Canvas canvas) {
			// canvas.drawColor(BRUSH_COLOR);
			canvas.drawBitmap(cachebBitmap, 0, 0, null);
			canvas.drawPath(path, paint);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			
			int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
			int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}

			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;

			Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (cachebBitmap != null) {
				newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
			}
			cachebBitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private float cur_x, cur_y;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				cur_x = x;
				cur_y = y;
				path.moveTo(cur_x, cur_y);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				path.quadTo(cur_x, cur_y, x, y);
				cur_x = x;
				cur_y = y;
				break;
			}

			case MotionEvent.ACTION_UP: {
				cacheCanvas.drawPath(path, paint);
				path.reset();
				break;
			}
			}

			invalidate();

			return true;
		}
	}

}
