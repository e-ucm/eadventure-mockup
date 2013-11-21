/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.video;


import java.util.List;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoSurface extends SurfaceView {

	private VideoSurfaceCallback callback;
	
	@SuppressWarnings("deprecation")
	public VideoSurface( Context context ) {
		super( context );
		this.callback = new VideoSurfaceCallback(this);
		
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
		return callback.isRecording();
	}

	public List<String> getQualities() {
		return callback.setUpSupportedProfiles();
	}
}