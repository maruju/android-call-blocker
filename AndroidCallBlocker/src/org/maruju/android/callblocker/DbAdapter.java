package org.maruju.android.callblocker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "database.db";

	private DbHelper dbHelper;

	public DbAdapter(Context context) {
		dbHelper = new DbHelper(context);
	}

	public boolean checkApp(String packageName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;

		try {
			c = db.rawQuery("SELECT * FROM accept_apps WHERE package_name = ?;", new String[]{packageName});

			//見つかれば許可
			return c.getCount() != 0;
		} finally {
			if (c != null) {
				c.close();
			}
			db.close();
		}
	}

	public List<String> getAllAcceptApps() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;

		try {
			c = db.rawQuery("SELECT package_name FROM accept_apps;", null);

			List<String> result = new ArrayList<String>();

			if (c.moveToFirst()) {
				do {
					result.add(c.getString(0));
				} while (c.moveToNext());
			}

			return result;

		} finally {
			if (c != null) {
				c.close();
			}
			db.close();
		}
	}

	public void addAcceptApp(String packageName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		try {
			db.execSQL("INSERT OR IGNORE INTO accept_apps(package_name) values(?);", new String[]{packageName});
		} finally {
			db.close();
		}
	}

	public void removeAcceptApp(String packageName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		try {
			db.execSQL("DELETE FROM accept_apps WHERE package_name = ?;", new String[]{packageName});
		} finally {
			db.close();
		}
	}

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE accept_apps ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "package_name TEXT UNIQUE NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
