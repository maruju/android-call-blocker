package org.maruju.android.callblocker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemLongClickListener {
	private AppListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.title_accept_app_list);

		//リストの初期化
		mAdapter = new AppListAdapter(getApplicationContext());

		ListView appListView = (ListView) findViewById(R.id.app_list_view);
		appListView.setAdapter(mAdapter);
		appListView.setOnItemLongClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateList();
	}

	private void updateList() {
		DbAdapter dbAdapter = new DbAdapter(getApplicationContext());
		mAdapter.clear();
		mAdapter.addAll(dbAdapter.getAllAcceptApps());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		String packageName = mAdapter.getItem(position);

		DbAdapter dbAdapter = new DbAdapter(getApplicationContext());
		dbAdapter.removeAcceptApp(packageName);

		updateList();
		return true;
	}

}
