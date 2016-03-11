package com.example.jakkot93.audiorecord;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;

public class MainActivity extends Activity {

    Boolean recording, isSpeakerPhoneOn;
    int oldAudioMode, oldRingerMode;

    AudioRecord recorder;
    AudioManager audioManager = null;

    int SAMPLERATE = 8000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        oldAudioMode = audioManager.getMode();
        oldRingerMode = audioManager.getRingerMode();
        isSpeakerPhoneOn = audioManager.isSpeakerphoneOn();

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLERATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 1024);
        recorder.startRecording();

        Thread recordThread = new Thread(new Runnable() {

            @Override
            public void run() {
                recording = true;
                recordAndWriteAudioData();
            }

        });
        recordThread.start();
    }

    private void recordAndWriteAudioData() {
        byte audioData[] = new byte[1024];
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 1024, AudioTrack.MODE_STREAM);
        at.play();

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        while (recording) {
            recorder.read(audioData, 0, 1024);
            at.write(audioData, 0, 1024);
        }
        at.stop();
        at.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        audioManager.setSpeakerphoneOn(isSpeakerPhoneOn);
        audioManager.setMode(oldAudioMode);
        audioManager.setRingerMode(oldRingerMode);
    }
}