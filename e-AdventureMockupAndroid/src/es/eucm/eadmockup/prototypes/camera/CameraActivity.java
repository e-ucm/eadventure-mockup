/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import es.eucm.eadmockup.prototypes.camera.picture.AndroidDeviceCameraController;
import es.eucm.eadmockup.prototypes.camera.video.AndroidDeviceVideoController;

public class CameraActivity extends AndroidApplication{

	/*private int origWidth;
	private int origHeight;*/


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		// we need to change the default pixel format - since it does not include an alpha channel 
		// we need the alpha channel so the camera preview will be seen behind the GL scene
		cfg.r = 8;
		cfg.g = 8;
		cfg.b = 8;
		cfg.a = 8;

		AndroidDeviceCameraController cameraControl = new AndroidDeviceCameraController(this);
		AndroidDeviceVideoController videoControl = new AndroidDeviceVideoController(this);
		Slideshow slideshow = new Slideshow(cameraControl, videoControl, new AndroidResolver(this));
		cameraControl.setSlideshow(slideshow);
		initialize(slideshow, cfg);
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
		// we don't want the screen to turn off during the long image saving process 
		graphics.getView().setKeepScreenOn(true);
		// keep the original screen size  
	/*	origWidth = graphics.getWidth();
		origHeight = graphics.getHeight();*/
	}

	public void post(Runnable r) {
		handler.post(r);
	}

	/*public void restoreFixedSize() {
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			glView.getHolder().setFixedSize(origWidth, origHeight);
		}
	}*/
}
