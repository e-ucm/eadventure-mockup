/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.SurfaceHolder;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.screens.VideoScreen;

public class VideoSurfaceCallback implements SurfaceHolder.Callback  {

	public static final String LOGTAG = "System.out";
	private final String P480 = "480p";
	private final String P720 = "720p";
	private final String P1080 = "1080p";

	private MediaRecorder recorder;
	private Camera camera;	

	boolean recording = false;
	private CamcorderProfile mRecorderProfile;

	private SurfaceHolder holder;

	private final String rootPath;
	private String auxVideoPath;

	private List<String> qual;
	private final VideoSurface videoSurface;


	public VideoSurfaceCallback(VideoSurface videoSurface){
		this.videoSurface = videoSurface;
		rootPath = Environment.getExternalStorageDirectory() + "/Slideshow/Videos/Video";
		setUpSupportedProfiles();
	}

	public List<String> setUpSupportedProfiles(){
		qual = new ArrayList<String>();
		if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)){
			qual.add(P480);
		}
		if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)){
			qual.add(P720);
		}
		if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)){
			qual.add(P1080);
		}
		return qual;
	}

	private CamcorderProfile getProfile(){
		CamcorderProfile prof = null;
		if(VideoScreen.QUALITY.equals(P480)){
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		} else if(VideoScreen.QUALITY.equals(P720)){
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		} else if(VideoScreen.QUALITY.equals(P1080)){
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
		}  

		if(prof == null){
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);			
		}
		return prof;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		mRecorderProfile = getProfile();

		if(this.holder == null){
			this.holder = holder;
		}

		if(recorder == null){
			this.recorder = new MediaRecorder();
		}

		if(camera == null){
			camera = getCameraInstance();
		}

		// The Surface has been created, now tell the camera where to draw the preview.
		try {

			boolean supported = false;			
			Camera.Parameters parameters = camera.getParameters();
			int h = mRecorderProfile.videoFrameHeight;
			int w = mRecorderProfile.videoFrameWidth;
			List<Size> suppPrevs = parameters.getSupportedPreviewSizes();
			for(Size s : suppPrevs){
				if(s.width == w && s.height == h){
					supported = true;
					break;
				}
			}			
			if(supported){
				parameters.setPreviewSize(w, h);
				camera.setParameters(parameters);
				System.out.println("setting: " + w + "x" + h);
			} else {
				
				float ratio = (float)w/(float)h;
				for(Size s : suppPrevs){
					float ratioS = (float)s.width/(float)s.height;
					if(ratioS == ratio){
						System.out.println("Another ratio found: " + s.width + ", " +  s.height);
						parameters.setPreviewSize(s.width, s.height);
						camera.setParameters(parameters);
						break;
					}
				}				
			}

			setUpPreviewAspectRatio(w, h);
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
		}
		Log.v(LOGTAG, "surfaceCreated");
	}

	private void setUpPreviewAspectRatio(int w, int h){

		//Get the SurfaceView layout parameters
		android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

		float viewportWidth = w;
		float viewportHeight = h;	
		float viewPortAspect = viewportWidth / viewportHeight;	
		float physicalWidth = Gdx.graphics.getWidth();
		float physicalHeight = Gdx.graphics.getHeight();
		float physicalAspect = physicalWidth / physicalHeight;	

		System.out.println("----inicial---");
		System.out.println("physicalWidth: " + physicalWidth + ", physicalHeight: " + physicalHeight);
		System.out.println("viewportWidth: " + viewportWidth + ", viewportHeight: " + viewportHeight);

		if ( physicalAspect < viewPortAspect) {
			viewportHeight = viewportHeight * (physicalWidth/viewportWidth);
			viewportWidth = physicalWidth;
		} else {
			viewportWidth = viewportWidth * (physicalHeight/viewportHeight);
			viewportHeight = physicalHeight;
		}
		System.out.println("----final---");
		System.out.println("viewportWidth: " + viewportWidth + ", viewportHeight: " + viewportHeight);

		lp.width = (int) viewportWidth;
		lp.height = (int) viewportHeight;

		videoSurface.setLayoutParams(lp);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(LOGTAG, "surfaceChanged, width: " + width + ", height: " + height);
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (holder.getSurface() == null){
			// preview surface does not exist
			Log.v(LOGTAG, "ERR: preview surface does not exist");			
			return;
		}
	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(LOGTAG, "surfaceDestroyed");		

		if(recorder != null){
			if (recording) {
				recorder.stop();
				recording = false;
			}
			recorder.release();
			recorder = null;
		}

		if (camera != null){
			camera.release();        // release the camera for other applications
			camera = null;
		}
	}

	public void startRecording() {
		recording = true;

		if(!prepareMediaRecorder()){
			System.out.println("Fail in prepareMediaRecorder()!\n - Try again -");
			return;
		}

		try{
			recorder.start();
			recording = true;
			Log.v(LOGTAG, "Recording Started");
		} catch(Exception e){
			System.out.println("Exception trying to start recording, try again!");
			e.printStackTrace(System.out);
			recording = false;
		}
	}

	private boolean prepareMediaRecorder(){

		camera.unlock();
		recorder.setCamera(camera);

		recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		recorder.setProfile(mRecorderProfile);

		int id = FileHandler.getVideoID() + 1;
		auxVideoPath = rootPath + id + ".mp4";	
		recorder.setOutputFile(auxVideoPath);
		/*recorder.setMaxDuration(60000); // Set max duration 60 sec.
		recorder.setMaxFileSize(5000000); // Set max file size 5M*/

		recorder.setPreviewDisplay(holder.getSurface());

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			surfaceDestroyed(null);
			return false;
		} catch (IOException e) {
			surfaceDestroyed(null);
			return false;
		}
		return true;
	}

	private Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	public void stopRecording(){
		// stop recording and release camera
		recorder.stop();  // stop the recording

		int vidID = FileHandler.getVideoID() + 1;
		String num = String.valueOf(vidID) + ".jpg";

		String thumbPath = FileHandler.getVideoThumbnailsFileHandle().file().getAbsolutePath() +  File.separator;

		String MINI_KIND = thumbPath + "VideoThumbnail" + num;

		try {			
			OutputStream fos = null;

			//MINI_KIND Thumbnail 96x96
			Bitmap bmMiniKind = ThumbnailUtils.createVideoThumbnail(auxVideoPath, Thumbnails.MINI_KIND);

			if(bmMiniKind == null){
				System.out.println("Video corrupt or format not supported! (bmMiniKind)");
				return;
			}

			fos = new FileOutputStream(new File(MINI_KIND));
			bmMiniKind.compress(Bitmap.CompressFormat.JPEG, 95, fos);
			fos.flush();
			fos.close();
			if(bmMiniKind != null){
				bmMiniKind.recycle();
				bmMiniKind = null;
			}
			fos = null;

			FileHandler.incrementVideoID();		
			Log.v(LOGTAG, "Recording Stopped");
		} catch (Exception e) {
			e.printStackTrace();
			// Something went wrong creating the Video Thumbnail
			Log.v(LOGTAG, "Something went wrong creating the Video Thumbnail");
		}
		recording = false;	
	}

	public boolean isRecording() {
		return recording;
	}
}
