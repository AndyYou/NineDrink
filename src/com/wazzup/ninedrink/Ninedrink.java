package com.wazzup.ninedrink;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class Ninedrink extends Activity implements SensorEventListener  {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        set_number = getRandom();
        findView();
        otherObject();
        setLisenter();  
        shake();   
    }
    //�Ҧ��ܼƫŧi
    private ImageView btn_1;
    private ImageView btn_reset;
    private ImageView btn_close;
    private int[] set_number; 
    //�ΨӧP�_�O�_�w½�}
    private boolean is_btn_pressed = false;
    //�Ѥd�Ҧ��ܼ�
    private int cheat_mod = 0; // 0:NoStatus,1:AlwaysLose,2:AlwaysWin
    //�����n��
    private static final int MIN_FORCE = 10;
    private static final int MIN_DIRECTION_CHANGE = 3;
    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;
    private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;
    private long mFirstDirectionChangeTime = 0;
    private long mLastDirectionChangeTime;
    private int mDirectionChangeCount = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;
    private OnShakeListener mShakeListener;
    private SensorManager mSensorManager;
    private Ninedrink mSensorListener;
    //Vibrator����
    private Vibrator mVibrator;
    //�n�̤���
    public interface OnShakeListener {
        void onShake();
    }
    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }
    private void findView(){
    	btn_1 = (ImageView)findViewById(R.id.imgbtn_1);;
    	btn_reset = (ImageView)findViewById(R.id.btn_reset); 	
    	btn_close = (ImageView)findViewById(R.id.imgbtn_close);
    }
    private void setLisenter(){
    	btn_reset.setOnClickListener(reset);
    	btn_close.setOnClickListener(close);
    	btn_1.setOnClickListener(open);
    }
    private void otherObject(){
    	mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }
    //�n�̨ƥ�����]�w
    @Override
    public void onSensorChanged(SensorEvent se) {
    	float x = se.values[SensorManager.DATA_X];
    	float y = se.values[SensorManager.DATA_Y];
    	float z = se.values[SensorManager.DATA_Z];
    	float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);
    	if (totalMovement > MIN_FORCE) {
    		long now = System.currentTimeMillis();
        if (mFirstDirectionChangeTime == 0) {
        	mFirstDirectionChangeTime = now;
        	mLastDirectionChangeTime = now;
        }
        long lastChangeWasAgo = now - mLastDirectionChangeTime;
        if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {
        	mLastDirectionChangeTime = now;
        	mDirectionChangeCount++; 
        	lastX = x;
        	lastY = y;
        	lastZ = z;
        	if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {
        		long totalDuration = now - mFirstDirectionChangeTime;
        		if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
        			mShakeListener.onShake();
        			resetShakeParameters();
        		}
        	}
        }else{
          resetShakeParameters();
        }
      }
    }

    private void resetShakeParameters() {
      mFirstDirectionChangeTime = 0;
      mDirectionChangeCount = 0;
      mLastDirectionChangeTime = 0;
      lastX = 0;
      lastY = 0;
      lastZ = 0;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	// TODO Auto-generated method stub
    	
    }
    //�n�̰ʧ@
    private void shake(){
    	mSensorListener = new Ninedrink();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,
        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI);
        mSensorListener.setOnShakeListener(new Ninedrink.OnShakeListener() {
        public void onShake() {
        	reset_card();
          }
        });
    }
    //�~�P�ƥ�
    private View.OnClickListener reset = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			reset_card();
		}
    };
    //���c�~�P�ƥ�
    private void reset_card(){
    	btn_1.setImageResource(R.drawable.selector);
		is_btn_pressed = false;	
		cheat_mod = 0; //�٭�w�]��
		/*
		btn_1_pressed = false;
		btn_2_pressed = false;
		btn_3_pressed = false;
		btn_4_pressed = false;
		*/
		set_number = getRandom();
		mVibrator.vibrate(500);
    }
    //½�P�ƥ�
    private View.OnClickListener open = new View.OnClickListener(){
    	@Override
		public void onClick(View v) {
    		switch_cards(0);
    		
    		Animation animation = AnimationUtils.loadAnimation(Ninedrink.this, R.anim.back_scale);
    		animation.setAnimationListener(new Animation.AnimationListener() {
    			@Override
    			public void onAnimationStart(Animation animation) { }
    			
    			@Override
    			public void onAnimationRepeat(Animation animation) { }
    			
    			@Override
    			public void onAnimationEnd(Animation animation) {
    				  btn_1.setImageResource(set_number[0]);
    				  btn_1.startAnimation(AnimationUtils.loadAnimation(Ninedrink.this, R.anim.front_scale));
    			}
    		});
			if(!is_btn_pressed) btn_1.startAnimation(animation);
			call_v(0);
			is_btn_pressed= true;
		}
    };
	//�üƱƧ�
    private int[] getRandom(){
    		//��l��
    		int x[] = {R.drawable.icon_7,R.drawable.icon_8,R.drawable.icon_9,R.drawable.icon_1,R.drawable.icon_jocker};
    		int y, tmp;
            Random r = new Random(System.currentTimeMillis()); 
            //�~�P
            for(int i=0;i<5;i++){
            	y = r.nextInt(4);
            	tmp = x[i];
            	x[i] = x[y];
            	x[y] = tmp;
            }

            return x;
    }
    //���
    protected static final int MENU_ABOUT = Menu.FIRST;
    protected static final int MENU_Quit = Menu.FIRST+1;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	menu.add(0,MENU_ABOUT,0,"����");
    	menu.add(0,MENU_Quit,0,"����");
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	super.onOptionsItemSelected(item);
    	switch(item.getItemId()){
    	case MENU_ABOUT:
    		openOptionsDialog();
    		break;
    	case MENU_Quit:
    		finish();
    		break;
    	}
    	return true;
    }
    private void openOptionsDialog(){
		new AlertDialog.Builder(Ninedrink.this).setTitle(R.string.about_title)
			.setMessage(R.string.about_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int i) {	
				}
			})
			.setNegativeButton(R.string.lb_homepage, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int i) {
					Uri uri = Uri.parse(getString(R.string.url_homepage));
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);						
				}
			})
			.show();
	}
    //�����{��
    private Button.OnClickListener close = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {       
            int pid = android.os.Process.myPid(); 
            android.os.Process.killProcess(pid); 	
		}  	
    };
    //�Ѥd�Ҧ�:��ť���q��
   @Override
   public boolean onKeyDown(int keyCode,KeyEvent event){   
	   if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){	   
		   cheat_mod = 1; //Always lose
	   }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
		   cheat_mod = 2; //Always win and Plus Drink XD
	   }else{
		   cheat_mod = 0; //Cancel
	   }
	   return true;
   }
   public void switch_cards(int n){
	   if(cheat_mod == 1){
		   if(set_number[n] != R.drawable.icon_9 && is_btn_pressed == false  ){
						 set_number[n] = R.drawable.icon_9;
		   }			   	   
	   }else if(cheat_mod == 2){
		   if(set_number[n] != R.drawable.icon_7 && is_btn_pressed == false  ){
			   set_number[n] = R.drawable.icon_7;	 
		   }		
	   }else{}
   }
   //��F�_��
   private void call_v(int n){
	   if (set_number[n] == R.drawable.icon_9){
		   mVibrator.vibrate(500);
	   }
   }
   /*//���հT��
   private void openTestDialog(){
		Toast popup =  Toast.makeText(Ninedrink.this, R.string.about_title, Toast.LENGTH_SHORT);
	    popup.show();
	}
	*/


}