package ssq.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * ���񿪻�֪ͨ, ������ط���
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
