package org.maruju.android.callblocker;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CallBlockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//電話番号の取得
		String phoneNumber = getPhoneNumber(intent);
		//デフォルトは許可
		setResultData(phoneNumber);

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		//動作中のタスクが取得できなければとりあえず許可
		List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(2);
		if (runningTasks.isEmpty()) {
			return;
		}
		ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(0);
		//先頭が電話アプリなら次を確認
		if (taskInfo.baseActivity.getPackageName().equals("com.android.phone")) {
			if (runningTasks.size() < 2) {
				return;
			}
			taskInfo = runningTasks.get(1);
		}

		//自アプリからの発信も許可
		String myPackageName = context.getPackageName();
		String taskPackageName = taskInfo.baseActivity.getPackageName();
		Log.d("mypackage", myPackageName);
		Log.d("taskpackage", taskPackageName);
		if (myPackageName.equals(taskPackageName)) {
			Log.d("mypackage", "accept call");
			return;
		}

		//あとは全部拒否
		setResultData(null);
		Log.d("callblocker", "reject call");

		//確認画面を呼び出し
		Intent confirmActivityIntent = new Intent(context, ConfirmActivity.class);
		confirmActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		confirmActivityIntent.putExtra(ConfirmActivity.EXTRA_PACKAGE_NAME, taskPackageName);
		confirmActivityIntent.putExtra(ConfirmActivity.EXTRA_PHONE_NUMBER, phoneNumber);
		context.startActivity(confirmActivityIntent);
	}

	private String getPhoneNumber(Intent intent) {
		String phoneNumber;

		//編集済みデータを取得
		phoneNumber = getResultData();

		//取得できなかったら元の番号を使用
		if (phoneNumber == null) {
			phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		}

		return phoneNumber;
	}

}
