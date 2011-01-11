package com.mysms;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MySms extends Activity implements OnClickListener{
	final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	
	static final String to = "2722";	
	static String[] sentArray;
	
	private EditText lowestField;
	private EditText highestField;
	private EditText initialField;
	public static TextView sentField;
	private Button submitButton;
	private Button exitButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		lowestField = (EditText) findViewById(R.id.lowest);
		highestField = (EditText) findViewById(R.id.highest);
		initialField = (EditText) findViewById(R.id.initial);
		
		sentField = (TextView) findViewById(R.id.sent);
		
		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(this);
		
		exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(this);
		IntentFilter filter = new IntentFilter(SMS_RECEIVED);
		BroadcastReceiver smsReceiver = new SmsReceiver();
		registerReceiver(smsReceiver, filter);
	}
	
	@Override
	public void onClick(View view) {
		if(view == exitButton){
			finish();
		}
		if(view == submitButton){
			try{
				SmsReceiver.l = Float.parseFloat(lowestField.getText().toString());
				SmsReceiver.h = Float.parseFloat(highestField.getText().toString());
				SmsReceiver.guess = Float.parseFloat(initialField.getText().toString());
			}
			catch(Exception e){
				Toast.makeText(this, "Error parsing inputs", Toast.LENGTH_SHORT).show();
			}
			//initiate the first sms with the first guess
			Toast.makeText(this, "Sending first message", Toast.LENGTH_SHORT).show();
		}
	}
}