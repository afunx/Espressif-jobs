package com.afunx.view;

import com.afunx.softapconnectcompattester.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class ProgressBar4Executing {

	private TextView mTextViewCompleteness;

	private TextView mTextViewResultLast;

	private TextView mTextViewResultCur;
	
	private AlertDialog mAlertDialog;
	
	private Handler mHandler;

	public void show(Context context, final Runnable cancelRunnable) {
		
		mHandler = new Handler();
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

	private void updateCompletenessInternal(String completeMsg) {
		if (mTextViewCompleteness != null) {
			mTextViewCompleteness.setText(completeMsg);
		}
	}

	public void updateCompleteness(final String completeMsg) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					updateCompletenessInternal(completeMsg);
				}
			});
		}
	}
	
	private void updateResultLastInternal(String resultLastMsg) {
		if (mTextViewResultLast != null) {
			mTextViewResultLast.setText(resultLastMsg);
		}
	}

	public void updateResultLast(final String resultLastMsg) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					updateResultLastInternal(resultLastMsg);
				}
			});
		}
	}
	
	private void updateResultCurInternal(String resultCurMsg) {
		if (mTextViewResultCur != null) {
			mTextViewResultCur.setText(resultCurMsg);
		}
	}
	
	public void updateResultCur(final String resultCurMsg) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					updateResultCurInternal(resultCurMsg);
				}
			});
		}
	}
	
	private void dismissInternal() {
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
	}
	
	public void dismiss() {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					dismissInternal();
				}
			});
		}
	}
}
