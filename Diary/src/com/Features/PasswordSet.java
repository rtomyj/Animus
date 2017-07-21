package com.Features;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.BaseClasses.Activity_Base;
import com.UtilityClasses.UI;
import com.rtomyj.Diary.R;

public class PasswordSet extends Activity_Base {
	EditText password;
	TextView hint;
	String temp;
	int attempts = 0;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UI.setTheme(this, userUIPreferences.theme);

		this.setContentView(R.layout.passcode);

		//getSupportActionBar().hide();
		password = (EditText) findViewById(R.id.password);
		hint = (TextView) findViewById(R.id.hint);

		if (sp.getString("Theme", "Default").equals("Material")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));
		}
		else if (sp.getString("Theme", "Default").equals("Material 3")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialBrown));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialBrown));
		}
		else if (sp.getString("Theme", "Default").equals("Material 4")){
			password.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));
			hint.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));
		}
		else if (sp.getString("Theme", "Default").contains("Onyx")){
			password.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));
			hint.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));

			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
			LinearLayout numberPad = (LinearLayout) findViewById(R.id.numberPad);
			parent.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));

			numberPad.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
		}



		hint.setText("Enter new password");

	}

	public void number(View v) {

		if (v.getTag().toString().equals("backspace")) {
			String temp = password.getText().toString();
			if (temp.length() > 0) {
				temp = temp.substring(0, temp.length() - 1);
				password.setText(null);
				password.append(temp);
			}
		} else {
			password.append(v.getTag().toString());
		}

		if (password.getText().toString().length() == 4) {

			if (attempts == 1){
				if (password.getText().toString().equals(temp)){
					Intent returnIntent = new Intent();
				this.setResult(RESULT_OK, returnIntent);
				sp.edit().putString("PasswordValue", temp.trim()).commit();
				
				finish();
				}
				else{
					hint.setText(getResources().getString(R.string.enter_new_password));
					password.setText(null);
					temp = null;
				}
			}
			else{
			temp = password.getText().toString();
			password.setText(null);
			hint.setText(getResources().getString(R.string.re_enter_password));
				attempts++;
			
			}

		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
