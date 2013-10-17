/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera;

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
		camera = Camera.open();
	}

	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
		// This method is called when the surface changes, e.g. when it's size is set.
		// We use the opportunity to initialize the camera preview display dimensions.

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
		camera.setDisplayOrientation(90);

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
	}

	public Camera getCamera() {
		return camera;
	}
}
