package com.mysms;

import java.text.DecimalFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	static float l = 10, h = 40, guess = 20;
	private static final SmsManager smsManager = SmsManager.getDefault();

	private boolean flagLUBFound = false;
	
	float initialGap = new Float(-1.0);
	private float[] gaps = {initialGap, initialGap, initialGap};
	
	// let's say current LUB is 5

	// 5.1 which other user already has
	private String MESSAGE_NUNL = ".*neither lowest nor unique.*".toUpperCase();
	// 5.1 mine only
	private String MESSAGE_UNL = ".*unique but not lowest.*".toUpperCase();
	// 4.9 which other user already has
	private String MESSAGE_LBNU = ".*lower than Lowest unique bid but is not unique.*".toUpperCase();
	// 5
	private String MESSAGE_LUB = ".*LUB.*".toUpperCase();
	// 
	private String MESSAGE_ALREADY = ".*somebody has already.*".toUpperCase();

	@Override
	public void onReceive(Context _context, Intent _intent) {
		if (_intent.getAction().equals(SMS_RECEIVED)) {
			Bundle bundle = _intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++)
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				for (SmsMessage message : messages) {
					String msg = message.getMessageBody();
					String result = parseMessageAndSetNewValues(_context, msg);
					if (!flagLUBFound) {
						guess = getNewGuess(_context);
						// also check result for some more accuracy
						MySms.sentField.setText(MySms.sentField.getText() + ", " + guess);
						smsManager.sendTextMessage(MySms.to, null, "OB "
								+ guess, null, null);
					} else {
						guess = guess + (float) 0.01;
						DecimalFormat df = new DecimalFormat("#.##");
						guess = Float.parseFloat(df.format(guess));
						//flagLUBFound = false;
						MySms.sentField.setText(MySms.sentField.getText() + ", " + guess);
						smsManager.sendTextMessage(MySms.to, null, "OB " + guess, null, null);
					}
				}
			}
		}
	}

	public float getNewGuess(Context c) {
		// if h and l are same, h should be given higher value
		DecimalFormat df = new DecimalFormat("#.##");
		if (h == l) {
			Toast.makeText(c, "h=l=" + h, Toast.LENGTH_LONG);
			h = Float.parseFloat(df.format(h + 2));
		}
		h = Float.parseFloat(df.format(h));
		float mid = (h + l) / 2; // TODO: should round the value to two decimal
									// places
		float oldGuess = guess; 
		guess = Float.parseFloat(df.format(mid));
		if(oldGuess == guess){
			guess = guess + (float) 0.01;
		}
		guess = Float.parseFloat(df.format(mid));
		return guess;
	}

	public String parseMessageAndSetNewValues(Context c, String msg) {
		// TODO: also parse the value that was sent from the server.
		// might be useful to check if the second message reaches the server at
		// first
		if (msg.toUpperCase().matches(MESSAGE_NUNL)) {
			h = guess;
			// Toast.makeText(c, MESSAGE_NUNL, Toast.LENGTH_LONG).show();
			return MESSAGE_NUNL;
		}
		if (msg.toUpperCase().matches(MESSAGE_UNL)) {
			l = guess;
			// Toast.makeText(c, MESSAGE_UNL, Toast.LENGTH_LONG).show();
			return MESSAGE_UNL;
		}
		if (msg.toUpperCase().matches(MESSAGE_LBNU)) {
			l = guess;
			// Toast.makeText(c, MESSAGE_LBNU, Toast.LENGTH_LONG).show();
			return MESSAGE_LBNU;
		}
		if (msg.toUpperCase().matches(MESSAGE_LUB) || msg.toUpperCase().matches(MESSAGE_ALREADY)) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(c);
			dialog.setTitle("" + new Date().toString()).setMessage(
					"Found LUB at " + guess)
					.setNegativeButton("Close",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,int arg1) {}
							}).show();
			flagLUBFound = true;
			return MESSAGE_LUB;
		}
		return "";
	}
}
