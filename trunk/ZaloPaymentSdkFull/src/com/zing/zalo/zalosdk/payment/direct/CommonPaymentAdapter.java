package com.zing.zalo.zalosdk.payment.direct;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zing.zalo.zalosdk.payment.R;
import com.zing.zalo.zalosdk.payment.direct.PaymentAlertDialog.OnOkListener;
import com.zing.zalo.zalosdk.payment.direct.PaymentProcessingDialog.OnCloseListener;
import com.zing.zalo.zalosdk.payment.direct.PaymentProcessingDialog.Status;

public abstract class CommonPaymentAdapter implements OnClickListener {
	PaymentChannelActivity owner = null;
	Intent cause = null;
	boolean isBillInfoShow = false;
	ZaloPaymentInfo info;
	PaymentProcessingDialog processingDlg;
	PaymentAlertDialog alertDlg;
	boolean shouldStop = false;

	protected int getRID(String name) {
		Log.i(getClass().getName(), owner.getApplicationInfo().packageName);
		return owner.getResources().getIdentifier(name, "id", owner.getApplicationInfo().packageName);
	}
	
	protected int getRDR(String name) {
		
		Log.i(getClass().getName(), owner.getApplicationInfo().packageName);
		return owner.getResources().getIdentifier(name, "drawable", owner.getApplicationInfo().packageName);
	}
	
	protected void setTextViewContent(int id, String text) {
		try {
			TextView view = (TextView) owner.findViewById(id);
			view.setText(text);
		} catch (Exception e) {
		}
	}

	private void stopPaymentIfNeeded(Object attached) {
		if (shouldStop) {
			CommonPaymentAdapter.this.owner.finish();
			onCompletePayment((ZaloPaymentResult) attached);
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	}

	public CommonPaymentAdapter(PaymentChannelActivity owner) {
		this.owner = owner;
		this.cause = owner.getIntent();
		processingDlg = new PaymentProcessingDialog(
				CommonPaymentAdapter.this.owner, new OnCloseListener() {
					@Override
					public void onClose() {
						stopPaymentIfNeeded(processingDlg.getTag());
					}
				});
		alertDlg = new PaymentAlertDialog(owner, new OnOkListener() {
			@Override
			public void onOK() {
				stopPaymentIfNeeded(alertDlg.getTag());
			}
		});
		owner.setContentView(getLayoutId());
		owner.findViewById(R.id.zalosdk_togle_bill_ctl)
				.setOnClickListener(this);
		owner.findViewById(R.id.zalosdk_exit_ctl).setOnClickListener(this);
		owner.findViewById(R.id.zalosdk_back_ctl).setOnClickListener(this);
		owner.findViewById(R.id.zalosdk_ok_ctl).setOnClickListener(this);
		owner.findViewById(R.id.zalosdk_call_ctl).setOnClickListener(this);
		try {
			info = ZaloPaymentInfo.parse(cause.getStringExtra("payInfo"));			
			setTextViewContent(R.id.zalosdk_bill_info_ctl, info.description);
			setTextViewContent(R.id.zalosdk_bill_id_ctl, info.appTranxID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		initPage();
	}

	protected int getLayoutId() {
		return cause.getIntExtra("layout_id",
				R.layout.zalosdk_activity_zing_card);
	}

	protected abstract void initPage();

	protected void togleBillInfo(View v) {
		isBillInfoShow = !isBillInfoShow;
		View ctn = owner.findViewById(R.id.zalosdk_bill_info_ctn);
		if (isBillInfoShow) {
			ctn.setVisibility(View.VISIBLE);
			if (v.getClass().getName() == ImageView.class.getName()) {
				((ImageView) v).setImageResource(R.drawable.zalosdk_btn_hide);
			} else {
				v.setBackgroundResource(R.drawable.zalosdk_btn_hide);
			}
		} else {
			ctn.setVisibility(View.GONE);
			if (v.getClass().getName() == ImageView.class.getName()) {
				((ImageView) v).setImageResource(R.drawable.zalosdk_btn_show);
			} else {
				v.setBackgroundResource(R.drawable.zalosdk_btn_show);
			}
		}
	}

	protected void onBackCtl() {
		Intent intent = new Intent(owner, PaymentGatewayActivity.class);
		intent.putExtra("payInfo", owner.getIntent().getStringExtra("payInfo"));
		owner.finish();
		owner.startActivity(intent);
	}

	private void onCompletePayment(ZaloPaymentResult result) {
		ZaloPaymentListener paymentListner = ZaloPaymentService.Instance
				.getPaymentListner();
		if (paymentListner != null) {
			if (result != null) {
				paymentListner.onComplete(result.code, result.status,
						result.amount);
			} else {
				paymentListner.onCancel();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.zalosdk_exit_ctl) {
			owner.finish();
			try {
				ZaloPaymentService.Instance.getPaymentListner().onCancel();
			} catch (Exception e) {			
			}
		} else if (id == R.id.zalosdk_back_ctl) {
			onBackCtl();
		} else if (id == R.id.zalosdk_togle_bill_ctl) {
			togleBillInfo(owner.findViewById(R.id.zalosdk_show_ctl));
		} else if (id == R.id.zalosdk_ok_ctl) {
			onPaymentStart();
		} else if (id == R.id.zalosdk_call_ctl) {
			TextView phone = (TextView) owner
					.findViewById(R.id.zalosdk_hotline_ctl);
			owner.startActivity(CallHelper.getCallIntent(phone.getText()
					.toString()));
		}
		// could not use switch case since adt-v14
		// switch (v.getId()) {
		// case R.id.exit_ctl:
		// owner.finish();
		// break;
		// case R.id.back_ctl:
		// onBackCtl();
		// break;
		// case R.id.togle_bill_ctl:
		// togleBillInfo(owner.findViewById(R.id.show_ctl));
		// break;
		// case R.id.ok_ctl:
		// onPaymentStart();
		// break;
		// case R.id.call_ctl:
		// TextView phone = (TextView) owner.findViewById(R.id.hotline_ctl);
		// owner.startActivity(CallHelper.getCallIntent(phone.getText()
		// .toString()));
		// break;
		// }
	}
	
	protected final void onPaymentStart() {
		if (isOnline()) {
			processingDlg.showView(Status.PROCESSING);
			executePaymentTask(getPaymentTask());
		} else {
			alertDlg.showAlert("Không thể kết nối đến máy chủ,  vui lòng kiểm tra lại đường truyền");
		}
	}

	protected final void onPaymentComplete(JSONObject result) {
		int code = 0;
		int errStep = 0;
		int status = 0;
		String message = "";
		processingDlg.setTag(null);
		alertDlg.setTag(null);
		if (result != null) {
			shouldStop = result.optBoolean("shouldStop", false);
			code = result.optInt("resultCode", -1);
			errStep = result.optInt("errorStep", 0);
			message = result.optString("resultMessage", "");
			if (errStep > 0) {
				if ((errStep == 2) && !message.isEmpty()) {
					processingDlg.hide();
					alertDlg.showAlert(message);
				} else if (errStep > 2) {
					shouldStop = true;
					alertDlg.showAlert(message);
				} else {
					ZaloPaymentResult paymentResult = new ZaloPaymentResult();
					paymentResult.code = ZaloPaymentResultCode.findById(code);
					processingDlg.setTag(paymentResult);
					processingDlg.showView(Status.FAILED);
				}
				return;
			}
			shouldStop = true;
			if (message.isEmpty()) {
				message = result.optString("message", "");
			}
			status = result.optInt("status", 0);
			ZaloPaymentResult paymentResult = new ZaloPaymentResult();
			paymentResult.status = ZaloPaymentStatus.findById(status);
			paymentResult.code = ZaloPaymentResultCode
					.findByStatus(paymentResult.status);
			paymentResult.amount = result.optInt("amount", 0);
			if (code >= 0) {
				if (status > 0) {
					processingDlg.setTag(paymentResult);
					processingDlg.showView(Status.SUCCESS);
				} else if (status == 0) {
					processingDlg.setTag(paymentResult);
					processingDlg.showView(Status.FAILED);
				} else {
					if (message.length() > 0) {
						alertDlg.setTag(paymentResult);
						alertDlg.showAlert(message);
					} else {
						processingDlg.setTag(paymentResult);
						processingDlg.showView(Status.FAILED);
					}
				}
				Log.i(getClass().getName(), result.toString());
			} else {
				if (message.length() > 0) {
					alertDlg.setTag(paymentResult);
					alertDlg.showAlert(message);
				} else {
					shouldStop = true;
					processingDlg.setTag(paymentResult);
					processingDlg.showView(Status.FAILED);
				}
			}
		} else {
			processingDlg.hide();
			shouldStop = false;
			alertDlg.showAlert("Không thể kết nối đến máy chủ,  vui lòng kiểm tra lại đường truyền");
		}
	}

	public final void executePaymentTask(PaymentTask task) {
		if (task != null) {
			new PaymentHttpRequest().execute(task);
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) owner
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected String getValue(int id) {
		String value = "";
		try {
			value = ((EditText) owner.findViewById(id)).getText().toString();
		} catch (Exception e) {
		}
		return value;
	}

	protected Object getTag(int id) {
		try {
			return ((EditText) owner.findViewById(id)).getTag();
		} catch (Exception e) {
		}
		return null;
	}

	protected abstract PaymentTask getPaymentTask();

	static interface PaymentTask {
		public JSONObject execute();

		public void onCompleted(JSONObject result);
	}

	static class PaymentHttpRequest extends
			AsyncTask<PaymentTask, Void, JSONObject> {
		PaymentTask task;

		@Override
		protected JSONObject doInBackground(PaymentTask... params) {
			if (params.length > 0) {
				task = params[0];
				return task.execute();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (task != null) {
				task.onCompleted(result);
			}
		}
	}
}
