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

	private final int MAX_PREVIEW_PIXELS = 1000000;
	private final int MAX_PHOTO_PIXELS = 2000000;

	private Camera camera;
	private static Size photoSize;

	public void surfaceCreated( SurfaceHolder holder ) {
		// Once the surface is created, simply open a handle to the camera hardware.
		if(camera == null){
			camera = Camera.open();
			if(camera != null){
				prepareCamera(holder);
			}
		}
	}

	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
		
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
			if (wantToUseThisPreviewResolution(size)) {
				previewSize = size;
				break;
			}
		}			
		parameters.setPreviewSize(previewSize.width, previewSize.height);

		List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
		// You need to choose the most appropriate photoSize for your app
		for (Size size : pictureSizes) {
			if (wantToUseThisPhotoResolution(size)) {
				photoSize = size;
			} else if(size.width == 1920 && size.height == 1080){
				photoSize = size;
				break;
			}
		}
		parameters.setPictureSize(photoSize.width, photoSize.height);

		camera.setParameters( parameters );

		// We also assign the preview display to this surface...
		try {
			camera.setPreviewDisplay(holder);
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private boolean wantToUseThisPreviewResolution(Size size) {
		return size.width*size.height < MAX_PREVIEW_PIXELS;
	}

	private boolean wantToUseThisPhotoResolution(Size size) {
		int w = size.width, h = size.height, pixels = w*h;
		boolean secondHD = pixels < MAX_PHOTO_PIXELS;
		return  secondHD;
	}

	public void surfaceDestroyed( SurfaceHolder holder ) {
		// Once the surface gets destroyed, we stop the preview mode and release
		// the whole camera since we no longer need it.
		if(camera != null){
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public static Size getPhotoSize() {
		return photoSize;
	}
}
