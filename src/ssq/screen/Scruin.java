package ssq.screen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

public class Scruin extends Application
{
  private static final String   EXPAND_NOTIFICATIONS_PANEL     = "expandNotificationsPanel";
  private static final String   EXPAND2                        = "expand";
  private static final String   ANDROID_APP_STATUS_BAR_MANAGER = "android.app.StatusBarManager";
  private static final String   STATUSBAR                      = "statusbar";
  private static final String   STATUS_BAR_HEIGHT              = "status_bar_height";
  private static final String   COM_ANDROID_INTERNAL_R$DIMEN   = "com.android.internal.R$dimen";
  private static Scruin         ApplicationContext;
  protected int                 screenw;
  protected int                 screenh;
  protected boolean             isLandscape;
  
  public static final String    SCREEN                         = "screen";
  public static final String    FILTER_EN                      = "FILTER_EN";
  public static final String    RGB_EN                         = "RGB_EN";
  public static final String    TOOLET_EN                      = "TOOLET_EN";
  public static final String    RELATIVELY_EN                  = "RELATIVELY_EN";
  public static final String    RED                            = "RED";
  public static final String    GREEN                          = "GREEN";
  public static final String    BLUE                           = "BLUE";
  public static final String    ALPHA                          = "ALPHA";
  
  protected static final String SSQ_SCREEN_DEAMON_SERVICE      = "ssq.screen.deamonService";
  protected static final String HIDDEN                         = "hidden";
  protected static final int    TOAST                          = 3;
  protected static final int    VIBRATE_LITTLE                 = 7;
  protected static final int    SHOW_FILTER                    = 1;
  protected static final int    REMOVE_FILTER                  = 2;
  protected TooletFloatView     tooletView;
  
  @SuppressLint("NewApi")
  @Override
  public void onCreate()
  {
    super.onCreate();
    ApplicationContext = this;
    tooletView = new TooletFloatView(this);
    refreshToolet();
    getApp().startService(new Intent(SSQ_SCREEN_DEAMON_SERVICE));
  }
  
  private static void refreshMetrics()
  {
    DisplayMetrics dm = new DisplayMetrics();
    ((WindowManager) getApp().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
    getApp().screenw = dm.widthPixels;
    getApp().screenh = dm.heightPixels;
  }
  
  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    refreshToolet();
    super.onConfigurationChanged(newConfig);
  }
  
  @Override
  public void onTerminate()
  {
    tooletView.remove();
    super.onTerminate();
  }
  
  protected static Scruin getApp()
  {
    return ApplicationContext;
  }
  
  static protected Handler handler = new Handler()
                                   {
                                     @Override
                                     public void handleMessage(Message msg)
                                     {
                                       switch (msg.what)
                                       {
                                         case TOAST:
                                           Toast.makeText(getApp(), (String) msg.obj, Toast.LENGTH_LONG).show();
                                           break;
                                         case VIBRATE_LITTLE:
                                           vibrateLittle();
                                           break;
                                         case SHOW_FILTER:
                                           refreshToolet();
                                           break;
                                         case REMOVE_FILTER:
                                           getApp().tooletView.filterFloatView.remove();
                                           getApp().tooletView.remove();
                                           break;
                                         default:
                                           break;
                                       }
                                       super.handleMessage(msg);
                                     }
                                   };
  
  public static void OpenNotify()
  {
    int currentApiVersion = android.os.Build.VERSION.SDK_INT;
    try
    {
      Object service = getApp().getSystemService(STATUSBAR);
      Class<?> statusbarManager = Class.forName(ANDROID_APP_STATUS_BAR_MANAGER);
      Method expand = null;
      if (service != null)
      {
        if (currentApiVersion <= 16)
        {
          expand = statusbarManager.getMethod(EXPAND2);
        }
        else
        {
          expand = statusbarManager.getMethod(EXPAND_NOTIFICATIONS_PANEL);
        }
        expand.setAccessible(true);
        expand.invoke(service);
      }
      
    }
    catch (Exception e)
    {
    }
  }
  
  protected static void showToast(String string)
  {
    Message msg = new Message();
    msg.what = TOAST;
    msg.obj = string;
    handler.sendMessage(msg);
  }
  
  protected static void vibrateLittle()
  {
    Vibrator vibrator = (Vibrator) getApp().getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(new long[] { 0, 80 }, -1);
  }
  
  protected static void saveAlpha(int a)
  {
    Editor editor = getApp().getSharedPreferences(SCREEN, MODE_PRIVATE).edit();
    editor.putInt(ALPHA, a);
    editor.commit();
  }
  
  protected static void refreshToolet()
  {
    refreshMetrics();
    SharedPreferences sharedPreferences = getApp().getSharedPreferences(SCREEN, MODE_PRIVATE);
    getApp().tooletView.refresh(sharedPreferences.getBoolean(FILTER_EN, false), sharedPreferences.getBoolean(RGB_EN, false), sharedPreferences.getBoolean(TOOLET_EN, false), sharedPreferences.getBoolean(RELATIVELY_EN, false), sharedPreferences.getInt(RED, 0), sharedPreferences.getInt(GREEN, 0), sharedPreferences.getInt(BLUE, 0), sharedPreferences.getInt(ALPHA, 0));
  }
  
  public static int getAlpha()
  {
    SharedPreferences sharedPreferences = getApp().getSharedPreferences(SCREEN, MODE_PRIVATE);
    return sharedPreferences.getInt(ALPHA, 0);
  }
  
  public static int getStatusBarHeight()
  {
    Class<?> c = null;
    Object obj = null;
    Field field = null;
    int x = 0, statusBarHeight = 0;
    try
    {
      c = Class.forName(COM_ANDROID_INTERNAL_R$DIMEN);
      obj = c.newInstance();
      field = c.getField(STATUS_BAR_HEIGHT);
      x = Integer.parseInt(field.get(obj).toString());
      statusBarHeight = getApp().getResources().getDimensionPixelSize(x);
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
    return statusBarHeight;
  }
}