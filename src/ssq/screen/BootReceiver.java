package ssq.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 捕获开机通知, 启动监控服务
 * 
 * @author ssqstone
 */
public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context paramContext, Intent paramIntent)
	{
		Scruin.getApp();
	}
}
