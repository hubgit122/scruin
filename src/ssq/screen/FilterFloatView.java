package ssq.screen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class FilterFloatView extends ImageView
{
	protected WindowManager.LayoutParams	wmParams;
	
	private int								r, g, b;
	private boolean							showing	= false;
	
	public FilterFloatView(Context context)
	{
		super(context);
		
		wmParams = new WindowManager.LayoutParams();
		
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE | LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		wmParams.x = 0;
		wmParams.y = 0;
		
		wmParams.width = -1;
		wmParams.height = -1;
		
		wmParams.alpha = 0;
		
		resetColor();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.drawARGB(255, r, g, b);
	}
	
	@Override
	public void setAlpha(int alpha)
	{
		WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		wmParams.alpha = (float) alpha / 255.0f;
		wm.updateViewLayout(this, wmParams);
		
		Scruin.saveAlpha(alpha);
	}
	
	protected void show()
	{
		WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		if (!showing)
		{
			try
			{
				wm.addView(this, this.wmParams);
				showing = true;
			}
			catch (Exception e)
			{
				showing = false;
			}
		}
		else
		{
			wm.updateViewLayout(this, this.wmParams);
		}
	}
	
	public void remove()
	{
		WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		if (showing)
		{
			try
			{
				wm.removeView(this);
				showing = false;
			}
			catch (Exception e)
			{
			}
		}
	}
	
	public void resetColor()
	{
		r = 0;
		g = 0;
		b = 0;
		invalidate();
	}
	
	public void refresh(boolean filterEn, int r2, int g2, int b2, int a2)
	{
		if (!filterEn)
		{
			remove();
		}
		else
		{
			wmParams.alpha = (float) a2 / 255.0f;
			r = r2;
			g = g2;
			b = b2;
			invalidate();
			
			show();
		}
	}
}
