package com.neji.kaboo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;
    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;

    private ImageView imageLoad;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);
        imageLoad = (ImageView)findViewById(R.id.profile_image);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallStart = System.currentTimeMillis();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        if (call != null) {
            if (!mAddedListener) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText("Calling.. " + globalVariable.getDriverName());//call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
                mAddedListener = true;
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
        updateUI();
    }

    private void updateUI() {
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();

        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallerName.setText("Calling.. " + globalVariable.getDriverName());//call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
            if (call.getState() == CallState.ESTABLISHED) {
                addVideoViews();
            }
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
           if (call.getDetails().getEndCause().toString().equals("HUNG_UP")){
               buildConfirmAlert();
           } else{
               finish();
           }
        }
     //  finish();
    }

    public void endaCall(String cause){
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
            if (cause.equals("HUNG_UP")){
                buildConfirmAlert();
            }
        }

      // finish();
    }

    public void turnSpeaker(View view){
       AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
       audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (audioManager.isSpeakerphoneOn()){
            audioManager.setSpeakerphoneOn(false);
            Toast.makeText(this,"Speaker turned off",Toast.LENGTH_SHORT).show();
        } else{
            audioManager.setSpeakerphoneOn(true);
            Toast.makeText(this,"Speaker turned on",Toast.LENGTH_SHORT).show();
        }

    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
            mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }

        if ((System.currentTimeMillis() - mCallStart) > 300000){
                endCall();
        }
    }

    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());
            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vc.toggleCaptureDevicePosition();
                }
            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }
    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }


    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());

            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();

                Toast.makeText(CallScreenActivity.this, call.getDetails().getEndCause().toString(), Toast.LENGTH_LONG).show();
// This will store the details of the call
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            ParseObject phonecall = new ParseObject("CallDuration");
            phonecall.put("DriverName", globalVariable.getDriverName().toString());
            phonecall.put("UserName", ParseUser.getCurrentUser().getUsername().toString());
            phonecall.put("DriverPhone", globalVariable.getPhone());
            phonecall.put("UserPhone", ParseUser.getCurrentUser().get("phone").toString());
            phonecall.put("Duration",mCallDuration.getText().toString());
            phonecall.put("EndCause",call.getDetails().getEndCause().toString());
            phonecall.saveInBackground();

           // endaCall(call.getDetails().getEndCause().toString());
endCall();

        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            mCallStart = System.currentTimeMillis();
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
//Save the call details here
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            ParseObject phonecall = new ParseObject("DriverPhoneCalls");
            phonecall.put("DriverName", globalVariable.getDriverName().toString());
            phonecall.put("UserName", ParseUser.getCurrentUser().getUsername().toString());
            phonecall.put("DriverPhone", globalVariable.getPhone());
            phonecall.put("UserPhone", ParseUser.getCurrentUser().get("phone").toString());
            phonecall.put("Category", "Free");
            phonecall.saveInBackground();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
    }

    private void buildConfirmAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Ride");

        // Setting Dialog Message
        alertDialog.setMessage("Do you wish to give this driver this job?");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_menu_save);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // createdoc();
                        // Write your code here to invoke YES event
                        //Upload the file first
                       giveJob();
finish();
                    }
                });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event

                        dialog.cancel();
                        finish();
                    }
                });

        // Showing Alert Message
        alertDialog.show();

    }

    private void giveJob(){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        ParseObject livejob = new ParseObject("Jobs");
        livejob.put("DriverName", globalVariable.getDriverName().toString());
        livejob.put("UserName", ParseUser.getCurrentUser().getUsername().toString());
        livejob.put("DriverPhone", globalVariable.getPhone());
        livejob.put("UserPhone", ParseUser.getCurrentUser().get("phone").toString());
       livejob.put("Status", "New");
        livejob.saveInBackground();
    }
}
