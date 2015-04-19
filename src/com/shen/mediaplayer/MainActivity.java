package com.shen.mediaplayer;

import java.io.File;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText nameText;
	private String path;
	private MediaPlayer mediaPlayer;
	private boolean pause = false;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameText = (EditText) this.findViewById(R.id.filename);
		mediaPlayer = new MediaPlayer();
		TelephonyManager telephonyManager=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
	}

	private final class MyPhoneListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://来电
				if(mediaPlayer.isPlaying()){
					position=mediaPlayer.getCurrentPosition();
					mediaPlayer.stop();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE://
				if(position>0&&path!=null){
					play(position);
					position=0;
				}
			default:
				break;
			}
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(mediaPlayer.isPlaying()){
			position=mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
		super.onPause();
	}


/*	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(position>0&&path!=null){
			play(position);
			position=0;
		}
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		mediaPlayer.release();
		mediaPlayer = null;
		super.onDestroy();
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void mediaplay(View v) {
		switch (v.getId()) {
		case R.id.playbutton:
			String filename = nameText.getText().toString();
			File audio = new File(Environment.getExternalStorageDirectory(), filename);
			if (audio.exists()) {
				path = audio.getAbsolutePath();
				play(0);
			} else {
				Toast.makeText(getApplicationContext(), R.string.nofile, 1)
						.show();
			}
			break;
		case R.id.pausebutton:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				pause = true;
				((Button) v).setText(R.string.continues);
			} else {
				mediaPlayer.start();// 继续播放
				pause = false;
				((Button) v).setText(R.string.pausebutton);
			}
			break;
		case R.id.resetbutton:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.seekTo(0);// 从开始处播放
			} else {
				if (path != null) {
					play(0);
				}
			}
			break;
		case R.id.stopbutton:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			break;

		default:
			break;
		}

	}

	private void play(int position) {
		try {
			mediaPlayer.reset();// 把各项参数恢复到最初始状态
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();// 进行缓冲
			mediaPlayer.setOnPreparedListener(new PreparedListener(position));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final class PreparedListener implements OnPreparedListener {

		private int position;
		public PreparedListener(int position) {
			this.position=position;
		}
		public void onPrepared(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			mediaPlayer.start();// 进行播放
			if(position>0){
				mediaPlayer.seekTo(position);
			}
		}

	}

}
