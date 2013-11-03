/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.picture;


import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurface extends SurfaceView {

	private CameraSurfaceCallback callback;
	
	@SuppressWarnings("deprecation")
	public CameraSurface( Context context ) {
		super( context );

		this.callback = new CameraSurfaceCallback();
		// We're implementing the Callback interface and want to get notified
		// about certain surface events.
		SurfaceHolder sh = getHolder();
		sh.addCallback( this.callback );
		// We're changing the surface to a PUSH surface, meaning we're receiving
		// all buffer data from another component - the camera, in this case.
		sh.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );

	}

	public Camera getCamera() {
		return this.callback.getCamera();
	}
}