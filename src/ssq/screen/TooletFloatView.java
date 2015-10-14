package ssq.screen;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

class TooletFloatView extends ImageView
{
    private static final String          创建滑动工具失败 = "创建滑动工具失败";
    //	private static final String 创建滑动工具成功_请长按通知栏测试 = "创建滑动工具成功, 请长按通知栏测试";
    private boolean                      showing  = false;
    protected FilterFloatView            filterFloatView;
    // 此wmParams为获取的全局变量，用以保存悬浮窗口的属性
    protected WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    private boolean                      longPressedDraging;
    
    public TooletFloatView(Context context)
    {
        super(context);
        
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = -1;
        wmParams.alpha = 1f;
        longPressedDraging = false;
        
        filterFloatView = new FilterFloatView(getContext());
        
        iconStartX = 0;
        iconStartY = 0;
        tapTime = 0;
        tapTime = 0;
        doubleTapped = false;
        movedOut = true;
    }
    
    private float   iconStartX   = 0;
    private float   iconStartY   = 0;
    private long    tapTime      = 0;
    private boolean movedOut     = true;
    private boolean doubleTapped = false;
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        canvas.drawARGB(0, 0, 0, 0);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if ((tapTime + 1500 > Calendar.getInstance().getTimeInMillis()) && ((Math.abs(x - iconStartX) <= 50) && (Math.abs(y - iconStartY) <= 50)))
                {
                    doubleTapped = true;
                    onDoubleTap(iconStartX, iconStartY, x, y);
                    iconStartX = x;
                    iconStartY = y;
                }
                else
                {
                    // Log.d("Gesture", "tapTime: " + tapTime + ", now: " +
                    // Calendar.getInstance().getTimeInMillis() + ", x: " + x +
                    // ", y: " + y + ", iconStartX: "+ iconStartX + ", iconStartY: "
                    // + iconStartY);
                    iconStartX = x;
                    iconStartY = y;
                    tapTime = Calendar.getInstance().getTimeInMillis();
                    longPressedDraging = false;
                    doubleTapped = false;
                    movedOut = false;
                    
                    onSingleTap(x, y);
                }
            }
                break;
            case MotionEvent.ACTION_MOVE:
            {
                // 判断是否在原地长按
                if (!longPressedDraging && ((Math.abs(x - iconStartX) > 50) || (Math.abs(y - iconStartY) > 50)))
                {
                    // Log.d("Gesture", "movedOut: " + x + " : " + iconStartX +
                    // " ; " + y + " : " + iconStartY);
                    movedOut = true;
                    if (y > wmParams.height || x > wmParams.width)
                    {
                        onMovedOut(x, y);
                    }
                }
                else if (!movedOut && !longPressedDraging && (Calendar.getInstance().getTimeInMillis() - tapTime > 1000))
                {
                    longPressedDraging = true;
                    onLongPressedConfirm(x, y);
                }
                else
                {
                    // Log.d("Gesture", "movedOut: " + movedOut +
                    // ", longPressedDraging: "+ longPressedDraging +
                    // ", now - tap: " + (Calendar.getInstance().getTimeInMillis() -
                    // tapTime));
                    onDrag(x, y);
                }
                
                if (longPressedDraging)
                {
                    onLongPressedDrag(x, y);
                    if (doubleTapped)
                    {
                        onDoubleTappedLongPressDrag(x, y);
                    }
                    else
                    {
                        onSingleTappedLongPressDrag(x, y);
                    }
                }
            }
                break;
            
            case MotionEvent.ACTION_UP:
            {
                onUp(x, y);
            }
                break;
        }
        return false;
    }
    
    private void onMovedOut(float x, float y)
    {
        // Log.d("Gesture", "onMovedOut");
        Scruin.OpenNotify();
        remove();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                show();
            }
        };
        handler.postDelayed(runnable, 5000);
    }
    
    private void onDrag(float x, float y)
    {
        // Log.d("Gesture", "onDrag");
    }
    
    private void onSingleTappedLongPressDrag(float x, float y)
    {
        int w = Scruin.getApp().screenw;
        // Log.d("Gesture", "onSingleTappedLongPressDrag");
        int alpaha = Math.min(Math.max((int) (255 * (w / 2 * 3 - 2 * x) / w), 0), 235);
        
        filterFloatView.setAlpha(alpaha);
        
        if (alpaha == 0)
        {
            filterFloatView.remove();
        }
        else
        {
            filterFloatView.show();
        }
    }
    
    private void onDoubleTappedLongPressDrag(float x, float y)
    {
        setScreenMode(0);
        int w = Scruin.getApp().screenw;
        // Log.d("Gesture", "onDoubleTappedLongPressDrag");
        setScreenBrightness(Math.min(Math.max((int) (255 * (2 * x - w / 2) / w), 20), 255));
    }
    
    private void onUp(float x, float y)
    {
        // Log.d("Gesture", "onUp");
        if (longPressedDraging && doubleTapped)
        {
            if (Math.abs(x - longPressStartX) < 20 && Math.abs(y - longPressStartY) < 20)
            {
                setScreenMode(1);
            }
        }
    }
    
    private void onSingleTap(float x, float y)
    {
        // Log.d("Gesture", "onSingleTap");
    }
    
    private void onDoubleTap(float x, float y, float x2, float y2)
    {
        // Log.d("Gesture", "onDoubleTap");
    }
    
    private void onLongPressedDrag(float x, float y)
    {
        // Log.d("Gesture", "onLongPressedDrag");
    }
    
    float longPressStartX, longPressStartY;
    
    private void onLongPressedConfirm(float x, float y)
    {
        // Log.d("Gesture", "onLongPressedConfirm");
        Scruin.handler.sendEmptyMessage(Scruin.VIBRATE_LITTLE);
        longPressStartX = x;
        longPressStartY = y;
    }
    
    private void updateSize()
    {
        wmParams.height = Math.max(Scruin.getStatusBarHeight(), 30);
        // Log.d("Scruin.getStatusBarHeight()", "" +
    }
    
    //
    // /**
    // * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
    // * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
    // */
    // private int getScreenMode()
    // {
    // int screenMode = 0;
    // try
    // {
    // screenMode = Settings.System.getInt(Scruin.getApp().getContentResolver(),
    // Settings.System.SCREEN_BRIGHTNESS_MODE);
    // } catch (Exception localException)
    // {
    //
    // }
    // return screenMode;
    // }
    //
    // /**
    // * 获得当前屏幕亮度值 0--255
    // */
    // private int getScreenBrightness()
    // {
    // int screenBrightness = 255;
    // try
    // {
    // screenBrightness =
    // Settings.System.getInt(Scruin.getApp().getContentResolver(),
    // Settings.System.SCREEN_BRIGHTNESS);
    // } catch (Exception localException)
    // {
    // }
    // return screenBrightness;
    // }
    
    /**
     * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private void setScreenMode(int paramInt)
    {
        try
        {
            Settings.System.putInt(Scruin.getApp().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        }
        catch (Exception localException)
        {
        }
    }
    
    /**
     * 设置全局屏幕亮度值 0-255
     */
    private boolean setScreenBrightness(int paramInt)
    {
        // Log.d("Gesture", "setScreenBrightness: "+paramInt);
        try
        {
            Settings.System.putInt(Scruin.getApp().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        }
        catch (Exception e)
        {
        }
        return true;
    }
    
    public void refresh(boolean filterEn, boolean rgbEn, boolean tooletEn, boolean relativelyEn, int r, int g, int b, int a)
    {
        if (!filterEn)
        {
            filterFloatView.remove();
        }
        else
        {
            filterFloatView.refresh(filterEn, r, g, b, a);
        }
        
        if (!rgbEn)
        {
            filterFloatView.resetColor();
        }
        
        if (!tooletEn || !filterEn)
        {
            try
            {
                remove();
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            show();
        }
    }
    
    public void show()
    {
        WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        updateSize();
        if (!showing)
        {
            try
            {
                wm.addView(this, this.wmParams);
                showing = true;
                //				Scruin.showToast(创建滑动工具成功_请长按通知栏测试);
            }
            catch (Exception e)
            {
                try
                {
                    wm.removeView(this);
                }
                catch (Exception e2)
                {
                    Scruin.showToast(创建滑动工具失败);
                }
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
        try
        {
            wm.removeView(this);
        }
        catch (Exception e)
        {
        }
        finally
        {
            showing = false;
        }
    }
}