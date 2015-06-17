package ssq.screen;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Message;

public class DeamonService extends Service
{
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    
    protected class StoppableRunnable implements Runnable
    {
        protected ConditionVariable mConditionVariable = new ConditionVariable(false);
        
        @Override
        public final void run()
        {
            while (true)
            {
                work();
                if (mConditionVariable.block(1000))
                {
                    return;
                }
            }
        }
        
        protected void work()
        {
        }
        
        private void start()
        {
            new Thread(this).start();
        }
        
        public void stop()
        {
            mConditionVariable.open();
        }
    }
    
    private boolean           filterHidenBecauseOfInstall   = false;
    private boolean           tooletHidenBecauseOfPhoneCall = false;
    
    private StoppableRunnable deamonThread                  = new StoppableRunnable()
                                                            {
                                                                @Override
                                                                protected void work()
                                                                {
                                                                    ActivityManager activityManager = (ActivityManager) Scruin.getApp().getSystemService(ACTIVITY_SERVICE);
                                                                    List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
                                                                    RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
                                                                    
                                                                    ComponentName localComponentName = runningTaskInfo.topActivity;
                                                                    String packageName = localComponentName.getPackageName();
                                                                    
                                                                    if (packageName.equals("com.android.packageinstaller"))
                                                                    {
                                                                        if (!filterHidenBecauseOfInstall)
                                                                        {
                                                                            Message msg = new Message();
                                                                            msg.what = Scruin.TOAST;
                                                                            msg.obj = "应用安装过程中会暂停显示护目镜";
                                                                            Scruin.handler.sendMessage(msg);
                                                                            Scruin.handler.sendEmptyMessage(Scruin.REMOVE_FILTER);
                                                                            filterHidenBecauseOfInstall = true;
                                                                        }
                                                                    }
                                                                    else if (filterHidenBecauseOfInstall)
                                                                    {
                                                                        Scruin.handler.sendEmptyMessage(Scruin.SHOW_FILTER);
                                                                        filterHidenBecauseOfInstall = false;
                                                                    }
                                                                    
                                                                    if (packageName.equals("com.android.incallui"))
                                                                    {
                                                                        if (!tooletHidenBecauseOfPhoneCall)
                                                                        {
                                                                            Message msg = new Message();
                                                                            msg.what = Scruin.TOAST;
                                                                            msg.obj = "通话过程中会暂停通知栏拖动功能, 防止耳朵碰到";
                                                                            Scruin.handler.sendMessage(msg);
                                                                            Scruin.handler.sendEmptyMessage(Scruin.REMOVE_TOOLET);
                                                                            tooletHidenBecauseOfPhoneCall = true;
                                                                        }
                                                                    }
                                                                    else if (tooletHidenBecauseOfPhoneCall)
                                                                    {
                                                                        Scruin.handler.sendEmptyMessage(Scruin.SHOW_TOOLET);
                                                                        tooletHidenBecauseOfPhoneCall = false;
                                                                    }
                                                                }
                                                            };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        deamonThread.start();
        return START_STICKY;
    }
    
    @Override
    public void onDestroy()
    {
        deamonThread.stop();
        Scruin.getApp().startService(new Intent(Scruin.SSQ_SCREEN_DEAMON_SERVICE));
        super.onDestroy();
    }
}
