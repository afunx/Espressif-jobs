package com.afunx.view;

import com.afunx.softapconnectcompattester.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

public class ProgressBar4Executing {

	private TextView mTextViewCompleteness;

	private TextView mTextViewResultLast;

	private TextView mTextViewResultCur;
	
	private AlertDialog mAlertDialog;

	public void show(Context context, final Runnable cancelRunnable) {

		View view = View.inflate(context, R.layout.exec_progress, null);
		mTextViewCompleteness = (TextView) view
				.findViewById(R.id.tv_exec_progress_completeness);
		mTextViewResultLast = (TextView) view
				.findViewById(R.id.tv_exec_progress_result_last);
		mTextViewResultCur = (TextView) view
				.findViewById(R.id.tv_exec_progress_result_cur);

		AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setView(view)
				.setTitle(R.string.softap_progress_bar_title)
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								cancelRunnable.run();
							}

						}).show();
		alertDialog.setCancelable(false);
		mAlertDialog = alertDialog;
	}

	public void updateCompleteness(String completeMsg) {
		if (mTextViewCompleteness != null) {
			mTextViewCompleteness.setText(completeMsg);
		}
	}

	public void updateResultLast(String resultLastMsg) {
		if (mTextViewResultLast != null) {
			mTextViewResultLast.setText(resultLastMsg);
		}
	}

	public void updateResultCur(String resultCurMsg) {
		if (mTextViewResultCur != null) {
			mTextViewResultCur.setText(resultCurMsg);
		}
	}
	
	public void dismiss() {
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
	}
}
