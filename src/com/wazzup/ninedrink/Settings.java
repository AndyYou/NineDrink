package com.wazzup.ninedrink;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class Settings extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		findView();
		setListener();
		mOpenHelper = new NDDBOpenHelper(this);
		getAll();
	}

	//�ŧi
	private Button btn_cancel;
	private Button btn_done;
	private Button btn_selectall ;
	private int selected_number = 0 ;
	private boolean is_selected_all = false;
	private CheckBox[] cbox_poker = new CheckBox[14] ;
	private boolean[] poker_list ={false,false,false,false,false,false,false,false,false,false,false,false,false,false};
	private int[] cboxId = {
		R.id.checkBox0,R.id.checkBox1,R.id.checkBox2,
		R.id.checkBox3,R.id.checkBox4,R.id.checkBox5,
		R.id.checkBox6,R.id.checkBox7,R.id.checkBox8,
		R.id.checkBox9,R.id.checkBox10,R.id.checkBox11,
		R.id.checkBox12,R.id.checkBox13
	};

	//�]�w��Ʈw
	public NDDBOpenHelper mOpenHelper;

	//�]�w�U����
	private void findView(){
		for(int i = 0; i < 14; i++){
			cbox_poker[i] = (CheckBox)findViewById(cboxId[i]);
		}
		btn_cancel = (Button)findViewById(R.id.btn_setcancel);
		btn_done = (Button)findViewById(R.id.btn_setcomplete);
		btn_selectall = (Button)findViewById(R.id.btn_selectall);
	}

	//��ť
	private void setListener(){
		btn_cancel.setOnClickListener(setcancel);
		for(int i = 0; i < 14; i++){
			cbox_poker[i].setOnCheckedChangeListener(selected);
		}
		btn_done.setOnClickListener(setdone);
		btn_selectall.setOnClickListener(selectAll);
	}

	private Button.OnClickListener setdone = new Button.OnClickListener(){
		public void onClick(View v){
			int selectCount = 0;
			for(int i = 0; i < 14; i++){
				if(cbox_poker[i].isChecked())
					selectCount++;
			}
			if(selectCount < 2){
				setTitle(R.string.limit_msg);
				limitDialog();
			} else {
				for(int i = 0; i < 14; i++){
					updateItem(i, poker_list[i]);
				}
				//finish(); //�p�G�u��finish()��^db���|�q�ԡA�]�w���|�ήɡC
				Intent i = new Intent();
				i.setClass(Settings.this, Ninedrink.class);
				startActivity(i);
				finish();
			}
		}
	};

	private Button.OnClickListener setcancel = new Button.OnClickListener(){
		public void onClick(View v){
			finish();
		}
	};

	private Button.OnClickListener selectAll = new Button.OnClickListener(){
		public void onClick(View v){
			selectAllEvent();
		}
	};
	//���c selectAll �ƥ�
	public void selectAllEvent(){
		for(int i = 0; i < 14; i++){
			cbox_poker[i].setChecked(is_selected_all);
		}
	}

	private CheckBox.OnCheckedChangeListener selected= new CheckBox.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton btnView, boolean isChecked){
			// TODO Auto-generated method stub

			
			for(int i = 0; i < 14; i++){
				if(cboxId[i] == btnView.getId()){
					poker_list[i] = isChecked;
					break;
				}	
			}	
			getSelected();
		}
	};

	// ���o�Ҧ��O��
	public void getAll(){
		int i = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor result = db.query(Constants.TABLE_NAME, null, null, null, null, null, null);
			
		result.moveToFirst();
		while (!result.isAfterLast()){
			i = result.getInt(0);
			poker_list[i] = Boolean.valueOf(result.getString(1).equals("0") ? "false" : "true");
			cbox_poker[i].setChecked(poker_list[i]);
			result.moveToNext();
		}
		getSelected();
		
	}

	//���c�`�� CheckBox�O�_�Ŀ� �]�w�ƥ�
	public void getSelected(){
		for(int j=0;j<cbox_poker.length;j++){
			if(!cbox_poker[j].isChecked()) {
				btn_selectall.setText(R.string.select_all);
				is_selected_all = true;
				break;
			}else{
				btn_selectall.setText(R.string.select_cancelall);
				is_selected_all = false;
			}
		}
	}
	//��s�@�����
	public void updateItem(int poker_number ,boolean is_into){
		try {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			ContentValues args = new ContentValues();
			args.put("is_into", is_into);
			db.update("set_poker", args, "poker_number = "+ String.valueOf(poker_number) , null);
		} catch (SQLException e){
			setTitle("��s��ƥ���");
		}
	}
	private void limitDialog() {
		Toast popup =  Toast.makeText(Settings.this, R.string.limit_msg, Toast.LENGTH_SHORT);
		popup.show();
	}
}
