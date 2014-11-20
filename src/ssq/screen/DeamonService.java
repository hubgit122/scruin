package ssq.screen;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
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
		protected ConditionVariable	mConditionVariable	= new ConditionVariable(false);
		
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
	
	private boolean				tooletHidenBecauseOfInstall	= false;
	private StoppableRunnable	deamonThread				= new StoppableRunnable()
															{
																//													SharedPreferences	preferences	= getApplication().getSharedPreferences(Scruin.SCREEN, MODE_PRIVATE);
																//													Editor				editor		= preferences.edit();
																
																@Override
																protected void work()
																{
																	ActivityManager activityManager = (ActivityManager) Scruin.getApp().getSystemService(ACTIVITY_SERVICE);
																	List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
																	RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
																	runningTaskInfo = (ActivityManager.RunningTaskInfo) (runningTaskInfo);
																	ComponentName localComponentName = runningTaskInfo.topActivity;
																	//														boolean tooletHidenBecauseOfInstall = preferences.getBoolean(Scruin.HIDDEN, false);
																	String packageName = localComponentName.getPackageName();
																	
																	if (packageName.equals("com.android.packageinstaller"))
																	{
																		if (!tooletHidenBecauseOfInstall)
																		{
																			Message msg = new Message();
																			msg.what = Scruin.TOAST;
																			msg.obj = "应用安装过程中会暂停显示护目镜";
																			Scruin.handler.sendMessage(msg);
																			Scruin.handler.sendEmptyMessage(Scruin.REMOVE_FILTER);
																			tooletHidenBecauseOfInstall = true;
																			//																editor.putBoolean(Scruin.HIDDEN, true);
																			//																editor.commit();
																		}
																	}
																	else if (tooletHidenBecauseOfInstall)
																	{
																		Scruin.handler.sendEmptyMessage(Scruin.SHOW_FILTER);
																		tooletHidenBecauseOfInstall = false;
																		//															editor.putBoolean(Scruin.HIDDEN, false);
																		//															editor.commit();
																	}
																}
															};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		deamonThread.start();
		tooletHidenBecauseOfInstall = false;
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
