/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.video;

import es.eucm.eadmockup.prototypes.camera.CameraActivity;
import es.eucm.eadmockup.prototypes.camera.IDeviceVideoControl;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;

public class AndroidDeviceVideoController implements IDeviceVideoControl{

	private final CameraActivity activity;
	private VideoSurface videoSurface;

	public AndroidDeviceVideoController(CameraActivity activity) {
		this.activity = activity;	
	}
	
	private synchronized void prepareVideo(){
		System.out.println("preparando video");

		if (videoSurface == null) {
			System.out.println("	video nula");
			videoSurface = new VideoSurface(activity);
		}
		activity.addContentView( videoSurface, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
	}
	
	private synchronized void stopRecOrPlay() {
		// stop previewing. 
		System.out.println("stopRec");
		if (videoSurface != null) {
			ViewParent parentView = videoSurface.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(videoSurface);
				System.out.println("	eliminando rec");
			}
			if (videoSurface != null) {
				videoSurface.stop();
				System.out.println("	parada rec");
			}
		}
	}
	
	@Override
	public void prepareVideoAsynk(){
		activity.post(new Runnable(){

			@Override
			public void run() {
				prepareVideo();						
			}			
		});
	}
	
	@Override
	public void stopPreviewAsynk(){
		activity.post(stopPreviewAsynkRunnable);
	}
	
	private final Runnable stopPreviewAsynkRunnable = new Runnable(){
		@Override
		public void run() {
			stopRecOrPlay();			
		}		
	};

	@Override
	public void startRecording() {
		videoSurface.startRecording();		
	}

	@Override
	public void stopRecording() {
		videoSurface.stopRecording();		
	}

	@Override
	public void startPlaying() {
		//videoSurface.startPlaying();
		activity.post(new Runnable(){

			@Override
			public void run() {
				
		        Intent intent = new Intent();
		        
		        intent.setAction(Intent.ACTION_VIEW);
		         
		        intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/myvideo.mp4"),
		         
		        "video/*");
		         
		        activity.startActivity(intent);
				
			}
			
		});
	}

	@Override
	public void stopPlaying() {
		videoSurface.stopPlaying();	
		
	}

	@Override
	public boolean isRecording() {
		// TODO Auto-generated method stub
		return videoSurface.isRecording();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;// videoSurface.isPlaying();
	}
}