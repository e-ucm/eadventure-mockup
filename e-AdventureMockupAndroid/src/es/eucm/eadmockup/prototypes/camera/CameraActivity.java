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

import es.eucm.eadmockup.prototypes.camera.picture.AndroidDevicePictureController;
import es.eucm.eadmockup.prototypes.camera.video.AndroidDeviceVideoController;

public class CameraActivity extends AndroidApplication{
	
	@Override
	public void onBackPressed() {}
	
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

		AndroidDevicePictureController pictureControl = new AndroidDevicePictureController(this);
		AndroidDeviceVideoController videoControl = new AndroidDeviceVideoController(this);
		Slideshow slideshow = new Slideshow(pictureControl, videoControl, new AndroidResolver(this));
		pictureControl.setSlideshow(slideshow);
		initialize(slideshow, cfg);
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
	}

	public void post(Runnable r) {
		handler.post(r);
	}
}
