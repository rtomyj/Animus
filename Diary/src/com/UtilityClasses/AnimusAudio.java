package com.UtilityClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.View;
import android.widget.ImageSwitcher;
import com.rtomyj.Diary.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by CaptainSaveAHoe on 6/7/17.
 */

public class AnimusAudio {
    private MediaPlayer mediaPlayer;
    private Context context;
    private String filename;
    private ImageSwitcher switchPlayPause;
    private boolean isRecordingSaved;
    private ImageSwitcher audioSwitcher;


    public AnimusAudio(Context context, String filename, ImageSwitcher audioSwitcher){
        this.context = context;
        this.filename = filename;
        this.audioSwitcher = audioSwitcher;
        try {
            newAudio();
        }catch (IOException e){

        }
    }


    public AnimusAudio(Context context, String filename, ImageSwitcher audioSwitcher, boolean previousRecording){
        this.context = context;
        this.filename = filename;
        this.audioSwitcher = audioSwitcher;

        if (previousRecording){
            audioSwitcher.showNext();
            isRecordingSaved = previousRecording;
        }

    }


    public void playAudio() {
        mediaPlayer.start();
        switchPlayPause.showNext();
    }

    public void pauseAudio() {
        mediaPlayer.pause();
        switchPlayPause.showPrevious();
    }

    public void replay() throws IOException {

        if (!mediaPlayer.isPlaying())
            switchPlayPause.showNext();

        mediaPlayer.stop();

        File audioFile = new File(context.getFilesDir(), filename + ".mpeg4");
        mediaPlayer.release();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setDataSource(audioFile.getPath());
        mediaPlayer.prepare();


    }

    private AlertDialog.Builder makeAudioDialog() {
        final View mView;
        AlertDialog.Builder audioPlayer;
        audioPlayer = new AlertDialog.Builder(context);
        mView = View.inflate(context, R.layout.audio_dialog, null);
        audioPlayer.setView(mView);
        switchPlayPause = (ImageSwitcher) mView.findViewById(R.id.playPause);


        audioPlayer.setPositiveButton(R.string.re_record,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            newAudio();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        audioPlayer.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mediaPlayer.release();
                mediaPlayer = null;
            }
        });


        audioPlayer.setNegativeButton(context.getResources().getString(R.string.delete_confirmation),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File audioFile = new File(context.getFilesDir(), filename + ".mpeg4");
                        audioFile.delete();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        audioSwitcher.showPrevious();

                        isRecordingSaved = false;
                    }
                });
        audioPlayer.setCancelable(false);

        audioPlayer.create();
        return audioPlayer;
    }




    public void messWithAudio() {
        AlertDialog.Builder audioPlayer = makeAudioDialog();

        File audioFile = new File(context.getFilesDir(), filename + ".mpeg4");
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFile.getPath());
                audioPlayer.show();

                mediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }




    private void newAudio() throws IOException {

        audioSwitcher.showNext();

        final MediaRecorder audioRecorder;

        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        File audioFile = new File(context.getFilesDir(), filename + ".mpeg4");
        audioFile.createNewFile();

        isRecordingSaved = true;

        audioRecorder.setOutputFile(audioFile.getPath());
        try {
            audioRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProgressDialog audioRecordingProgress = new ProgressDialog(context);

        audioRecordingProgress.setTitle(context.getResources().getString(R.string.recording));
        audioRecordingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        audioRecordingProgress.setButton(context.getResources().getString(R.string.stop_recording),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        audioRecordingProgress.dismiss();
                        audioRecorder.stop();
                        audioRecorder.release();
                    }
                });
        audioRecordingProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface p1) {
                audioRecorder.stop();
                audioRecorder.release();
            }
        });
        audioRecordingProgress.setCancelable(false);
        audioRecorder.start();
        audioRecordingProgress.show();
    }

    public boolean isRecordingSaved(){
        return isRecordingSaved;
    }




}
