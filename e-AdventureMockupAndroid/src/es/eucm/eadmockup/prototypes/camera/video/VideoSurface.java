/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.video;


import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoSurface extends SurfaceView {

	private VideoSurfaceCallback callback;
	
	@SuppressWarnings("deprecation")
	public VideoSurface( Context context ) {
		super( context );
		this.callback = new VideoSurfaceCallback();
		
		// We're implementing the Callback interface and want to get notified		
		// about certain surface events.
		SurfaceHolder sh = getHolder();
		sh.addCallback( this.callback );
		// We're changing the surface to a PUSH surface, meaning we're receiving
		// all buffer data from another component - the camera, in this case.
		sh.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );

	}

	public void startRecording() {
		callback.startRecording();
		
	}

	public void stopRecording() {
		callback.stopRecording();
		
	}

	public boolean isRecording() {
		// TODO Auto-generated method stub
		return callback.isRecording();
	}
	
	public void startPlaying() {
		callback.startPlaying();
		
	}

	public void stopPlaying() {
		callback.stopPlaying();
		
	}
	
	public boolean isPlaying()
	{
		return callback.isPlaying();
	}

	public void stop() {
		// TODO Auto-generated method stub
		callback.stopRecOrPlaying();
	}

}