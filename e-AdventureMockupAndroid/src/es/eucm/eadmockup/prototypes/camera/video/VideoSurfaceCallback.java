/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.video;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoSurfaceCallback implements SurfaceHolder.Callback  {

	public static final String LOGTAG = "System.out";

	private MediaRecorder recorder;
	private MediaPlayer player;
	private Camera camera;	

	boolean recording = false;
	private boolean playing = false;
	boolean usecamera = true;
	boolean previewRunning = false;
	private Size videoSize;

	private SurfaceHolder holder;

	private CamcorderProfile camcorderProfile;

	private String path= Environment.getExternalStorageDirectory() + "videocapture74063146.3gp";//<<<<<<<>>>>>>//

	private boolean wasPreviewRunning;

	

	public VideoSurfaceCallback(){
		Log.d(LOGTAG, "creating videosurfaceCallBack");
		//fileName = Environment.getExternalStorageDirectory() + "/test.mp4";
	}

	private void prepareRecorder() {
		Log.d(LOGTAG, "preparing recorder");
		if(recorder == null){
			recorder = new MediaRecorder();
		}
		recorder.setPreviewDisplay(holder.getSurface());
		
		if(player == null){
			player = new MediaPlayer();
			player.setDisplay(holder);
		}

		if (usecamera) {
			camera.unlock();
			recorder.setCamera(camera);
		}

		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		
		/*recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setVideoSize(videoSize.width, videoSize.height);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);*/
		
		//recorder.setOutputFile(fileName);

		recorder.setProfile(camcorderProfile);

		// This is all very sloppy
		if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
			try {
				File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
				path = newFile.getAbsolutePath();
				recorder.setOutputFile(newFile.getAbsolutePath());
			} catch (IOException e) {
				Log.v(LOGTAG,"Couldn't create file");
				e.printStackTrace();
			}
		} else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
			try {
				File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
				path = newFile.getAbsolutePath();
				recorder.setOutputFile(newFile.getAbsolutePath());
			} catch (IOException e) {
				Log.v(LOGTAG,"Couldn't create file");
				e.printStackTrace();
			}
		} else {
			try {
				File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
				path = newFile.getAbsolutePath();
				recorder.setOutputFile(newFile.getAbsolutePath());
			} catch (IOException e) {
				Log.v(LOGTAG,"Couldn't create file");
				e.printStackTrace();
			}

		}
		//recorder.setMaxDuration(50000); // 50 seconds
		//recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		this.holder = holder;
		Log.v(LOGTAG, "surfaceCreated");

		if (usecamera) {
			if(camera == null){
				camera = Camera.open();
			}

			try {
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				previewRunning = true;
			}
			catch (IOException e) {
				Log.e(LOGTAG,e.getMessage());
				e.printStackTrace();
			}	
		}		

	}


	@SuppressLint("NewApi")
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(LOGTAG, "surfaceChanged");

		if (!recording && usecamera) {
			if (previewRunning){
				camera.stopPreview();
				previewRunning = false;
			}

			try {
				Camera.Parameters p = camera.getParameters();

				List<Size> supportedPrevSizes = p.getSupportedPreviewSizes();
				Size prevSize = supportedPrevSizes.get(0);
				for(Size s : supportedPrevSizes){
					int pix = s.width*s.height;
					if(pix < 1000000 && pix > 500000){
						prevSize = s;
					}
				}
				
				/*List<Size> supportedVideoSizes = p.getSupportedVideoSizes();
				videoSize = supportedVideoSizes.get(0);
				for(Size s : supportedVideoSizes){
					int pix = s.width*s.height;
					if(pix < 1000000 && pix > 500000){
						videoSize = s;
					}
				}*/
				camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
				if(videoSize == null){
					videoSize = camera.new Size(0, 0);
				}
				videoSize.width = camcorderProfile.videoFrameWidth;
				videoSize.height = camcorderProfile.videoFrameHeight;
								
				p.setPreviewSize(prevSize.width, prevSize.height);
				//p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

				camera.setParameters(p);

				camera.setPreviewDisplay(holder);
				camera.startPreview();
				previewRunning = true;
			}
			catch (IOException e) {
				Log.e(LOGTAG,e.getMessage());
				e.printStackTrace();
			}	

			prepareRecorder();	
		}
	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(LOGTAG, "surfaceDestroyed");
		if (recording) {
			recorder.stop();
			recording = false;
		}
		recorder.release();
		recorder = null;
		if (playing){
			player.stop();
			playing =false;
		}
		player.release();
		player = null;
		
		if (usecamera) {
			previewRunning = false;
			//camera.lock();
			camera.release();
			camera = null;
		}
		
	}

	public void startRecording() {
		recording = true;
		recorder.start();
		Log.v(LOGTAG, "Recording Started");
	}

	public void stopRecording(){
		recorder.stop();
		if (usecamera) {
			try {
				camera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
		// recorder.release();
		recording = false;
		Log.v(LOGTAG, "Recording Stopped");
		// Let's prepareRecorder so we can record again
		prepareRecorder();
	}

	public boolean isRecording() {
		// TODO Auto-generated method stub
		return recording;
	}

	public void stopRecOrPlaying() {
		//recorder.stop();
	}
	
	public void startPlaying(){
		if(previewRunning){
			camera.stopPreview();
			wasPreviewRunning = true;
		}
		playing = true;
		if(player == null){
			player = new MediaPlayer();
			player.setDisplay(holder);
		}
		if (player.getCurrentPosition() == 0){
			try{		
				player.setDisplay(holder);				
				player.setDataSource(path);
				player.prepare();
			} catch(IllegalArgumentException e) {
			} catch (IOException e) {
			}
		}

		Log.v(LOGTAG, "Playing Started");
		player.start();
	}

	public void stopPlaying(){
		player.stop();
		if (usecamera) {
			try {
				camera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
		// recorder.release();
		playing = false;
		Log.v(LOGTAG, "Playing Stopped");
		// Let's prepareRecorder so we can record again
		prepareRecorder();
		if(wasPreviewRunning){
			try {
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				wasPreviewRunning = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return playing && player != null && player.isPlaying();
	}
}
