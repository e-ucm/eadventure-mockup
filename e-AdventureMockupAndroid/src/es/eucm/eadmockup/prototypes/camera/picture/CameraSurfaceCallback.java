/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.picture;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

public class CameraSurfaceCallback implements SurfaceHolder.Callback  {

	private final int MAX_PIXELS = 1000000;

	private Camera camera;


	public void surfaceCreated( SurfaceHolder holder ) {
		// Once the surface is created, simply open a handle to the camera hardware.
		if(camera == null){
			camera = Camera.open();
			prepareCamera(holder);
		}

		/*if (mediaRecorder == null) {
			System.out.println("<<<<media recorder inicializado");
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setPreviewDisplay(holder.getSurface());
		}

		if (mediaPlayer == null) {
			System.out.println("<<<<media player inicializado");
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(holder);				
		}		*/
	}

	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
		//prepareCamera(holder);
		/*if (isPreviewRunning)
        {
			camera.stopPreview();
        }
        Parameters parameters = camera.getParameters();
        Display display = ((WindowManager)activity.getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(height, width);                           
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(width, height);                           
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(height, width);               
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);
        }

        camera.setParameters(parameters);
        previewCamera(holder); */
	}

	public void previewCamera(SurfaceHolder holder)
	{        
		try 
		{          
			System.out.println("CameraSurfaceCallback.previewCamera");
			camera.setPreviewDisplay(holder);          
			camera.startPreview();
		}
		catch(Exception e)
		{
			System.out.println("no se pudo iniciar el preview en surfacechanged"); 
		}
	}

	private void prepareCamera(SurfaceHolder holder){
		// This method is called when the surface changes, e.g. when it's size is set.
		// We use the opportunity to initialize the camera preview display dimensions.
		System.out.println("CameraSurfaceCallback.prepareCamera");
		camera.setDisplayOrientation(0);

		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

		// You need to choose the most appropriate previewSize for your app
		Camera.Size previewSize = null;//.... select one of previewSizes here

		for (Size size : previewSizes) {
			System.out.println("Available resolution preview: "+size.width+" "+size.height);
			if (wantToUseThisResolution(size)) {
				previewSize = size;
				break;
			}
		}			
		parameters.setPreviewSize(previewSize.width, previewSize.height);


		List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

		// You need to choose the most appropriate previewSize for your app
		Camera.Size pictureSize = null;// .... select one of pictureSizes here

		for (Size size : pictureSizes) {
			System.out.println("Available resolution picture: "+size.width+" "+size.height);
			if (wantToUseThisResolution(size)) {
				pictureSize = size;
				break;
			}
		}
		parameters.setPictureSize(pictureSize.width, pictureSize.height);

		camera.setParameters( parameters );

		// We also assign the preview display to this surface...
		try {
			camera.setPreviewDisplay( holder );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private boolean wantToUseThisResolution(Size size) {
		return size.width*size.height < MAX_PIXELS;
	}

	public void surfaceDestroyed( SurfaceHolder holder ) {
		// Once the surface gets destroyed, we stop the preview mode and release
		// the whole camera since we no longer need it.
		camera.stopPreview();
		camera.release();
		camera = null;
		/*mediaRecorder.release();
		mediaPlayer.release();
		mediaRecorder = null;
		mediaPlayer = null;*/
	}

	public Camera getCamera() {
		return camera;
	}
/*
	public void prepareRecorder(){
		mediaRecorder.setCamera(camera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	}*/

	/*public void startRecording() {
		//prepareRecorder();
		System.out.println("startRecording");
		mediaRecorder.setOutputFile(fileName);
		try {
			       				
			mediaRecorder.prepare();
		} catch (IllegalStateException e) { e.printStackTrace(System.out);
		} catch (IOException e) { e.printStackTrace(System.out);
		}				        			
		        				        			
		mediaRecorder.start();
		recording = true;
	}

	public void stopRecording() {
		System.out.println("stopRecording");
		 
		if (mediaRecorder != null && recording) {
			recording = false;					
			mediaRecorder.stop();	
			mediaRecorder.reset();        			
			         			
		} 
	}

	public void startPlaying() {
		System.out.println("startPlaying");
		             		
		try {
			mediaPlayer.setDataSource(fileName);
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}				

		mediaPlayer.start();

	}

	public void stopPlaying() {
		System.out.println("stopPlaying");
		
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.stop();
			mediaPlayer.reset();
		}
	}


	public boolean isRecording(){
		return this.recording;
	}
	
	public boolean isPlaying(){
		return mediaPlayer.isPlaying();
	}*/
	
	
	
}
