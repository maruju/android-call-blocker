package org.maruju.android.callblocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ConfirmActivity extends FragmentActivity implements DialogInterface.OnClickListener {
	public static final String EXTRA_PACKAGE_NAME = "package_name";
	public static final String EXTRA_PHONE_NUMBER = "phone_number";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		ConfirmDialogFragment.showDialog(getSupportFragmentManager(), getIntent().getExtras());
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEUTRAL:
			acceptCall();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;
		}
	}

	private void acceptCall() {
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", getIntent().getStringExtra(EXTRA_PHONE_NUMBER), null));
		startActivityForResult(callIntent, 0);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		//戻ってきたら終了
		finish();
	}

	public static class ConfirmDialogFragment extends DialogFragment {
		public static void showDialog(FragmentManager fragmentManager, Bundle args) {
			Fragment fragment = new ConfirmDialogFragment();
			fragment.setArguments(args);

			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(fragment, null);
			ft.addToBackStack(null);
			ft.commit();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			PackageManager packageManager = getActivity().getPackageManager();
			String packageName = getArguments().getString(EXTRA_PACKAGE_NAME);
			String phoneNumber = getArguments().getString(EXTRA_PHONE_NUMBER);

			//情報の取得
			Drawable applicationIcon;
			CharSequence applicationName;
			try {
				applicationIcon = packageManager.getApplicationIcon(packageName);
				applicationName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0));
			} catch (NameNotFoundException e) {
				return null;
			}

			DialogInterface.OnClickListener onClickListener = getOnClickListener();
			return new AlertDialog.Builder(getActivity())
			.setIcon(applicationIcon)
			.setTitle(applicationName)
			.setMessage(getString(R.string.confirm_call, phoneNumber))
			.setNeutralButton(R.string.accept_call, onClickListener)
			.setNegativeButton(R.string.reject_call, onClickListener)
			.create();
		}

		private DialogInterface.OnClickListener getOnClickListener() {
			if (getTargetFragment() instanceof DialogInterface.OnClickListener) {
				return (OnClickListener) getTargetFragment();
			} else if (getActivity() instanceof DialogInterface.OnClickListener) {
				return (OnClickListener) getActivity();
			} else {
				return null;
			}
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);

			//とりあえずActivityを終了
			Activity activity = getActivity();
			if (activity != null) {
				activity.finish();
			}
		}
	}

}
