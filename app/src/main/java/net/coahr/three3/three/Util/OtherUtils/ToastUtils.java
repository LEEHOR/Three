package net.coahr.three3.three.Util.OtherUtils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.coahr.three3.three.R;

public class ToastUtils {

	private static Toast mToast;
	private static Toast toast2;
	public static void showLong(Activity activity,String text) {
		if (mToast == null) {
			mToast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public static void showShort(Activity activity,String text) {
		if (mToast == null) {
			mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public static void Toast_showImage(Activity activity,final String tvStr, final int imageResource,int  duration){
		if (toast2 == null) {

			toast2 = new Toast(activity);

		}
		View view = LayoutInflater.from(activity).inflate(R.layout.toast_layout, null);

		TextView tv = (TextView) view.findViewById(R.id.toast_tv);

		tv.setText(TextUtils.isEmpty(tvStr) ? "" : tvStr);

		ImageView iv = (ImageView) view.findViewById(R.id.toast_iv);

		/*if (imageResource > 0) {

			iv.setVisibility(View.VISIBLE);

			iv.setImageResource(imageResource);

		} else {

			iv.setVisibility(View.GONE);

		}*/

		toast2.setView(view);

		toast2.setGravity(Gravity.CENTER, 0, 0);

		toast2.setDuration(duration);

		toast2.show();
	}

}
