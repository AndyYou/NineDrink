package com.wazzup.ninedrink;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Description extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description);
		findView();
		setLisenter();
		
	}
	
	//�ŧi�ܼ�
	private Button btn_back;
	//�]�w�ƥ�
	private void findView(){
		 btn_back = (Button)findViewById(R.id.btn_back);
	}
	//��ť
	private void setLisenter(){
		btn_back.setOnClickListener(back);
	}
	//�ƥ�
	private Button.OnClickListener back = new Button.OnClickListener(){
		public void onClick(View v){
			finish();
		}
	};
}
