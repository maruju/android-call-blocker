package org.maruju.android.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallBlockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//とりあえず全部拒否
		setResultData(null);
	}

}
