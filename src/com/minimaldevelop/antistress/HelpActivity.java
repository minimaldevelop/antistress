package com.minimaldevelop.antistress;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		TextView helpText = (TextView) findViewById(R.id.helpText);
		helpText.setText(Html.fromHtml(getString(R.string.helpText)));
	}
}
