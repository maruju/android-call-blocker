package org.maruju.android.callblocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends ArrayAdapter<String> {
	private final PackageManager mPackageManager;
	private final Map<String, ApplicationInfo> mAppInfoCache;
	private final LayoutInflater mInflater;

	public AppListAdapter(Context context) {
		super(context, 0, new ArrayList<String>());

		mPackageManager = context.getPackageManager();
		mAppInfoCache = new HashMap<String, ApplicationInfo>();
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String packageName = getItem(position);

		ApplicationInfo appInfo = getAppInfo(packageName);

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_app, parent, false);

			holder = new ViewHolder();
			holder.appIconView = (ImageView) convertView.findViewById(R.id.app_icon_view);
			holder.appNameView = (TextView) convertView.findViewById(R.id.app_name_view);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.appIconView.setImageDrawable(mPackageManager.getApplicationIcon(appInfo));
		holder.appNameView.setText(mPackageManager.getApplicationLabel(appInfo));

		return convertView;
	}

	private ApplicationInfo getAppInfo(String packageName) {
		ApplicationInfo appInfo = null;

		//キャッシュから取得
		appInfo = mAppInfoCache.get(packageName);

		//見つからなければシステムから取得
		if (appInfo == null) {
			try {
				appInfo = mPackageManager.getApplicationInfo(packageName, 0);
				mAppInfoCache.put(packageName, appInfo);
			} catch (NameNotFoundException e) {
			}
		}

		return appInfo;
	}

	private static class ViewHolder {
		TextView appNameView;
		ImageView appIconView;
	}
}
