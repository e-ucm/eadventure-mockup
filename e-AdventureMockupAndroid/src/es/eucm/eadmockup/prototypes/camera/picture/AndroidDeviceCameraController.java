/**
 * Copyright 2012 Johnny Lish (johnnyoneeyed@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 *
 *  ************************************************************************
 * Modified by Antonio Calvo Morata & Dan Cristian Rotaru	
 * This file is a prototype for eAdventure Mockup
 * 
 */

package es.eucm.eadmockup.prototypes.camera.picture;

import java.io.File;
import java.io.FileOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.eadmockup.prototypes.camera.CameraActivity;
import es.eucm.eadmockup.prototypes.camera.IDeviceCameraControl;
import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.screens.CameraScreen;

import android.hardware.Camera;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class AndroidDeviceCameraController implements IDeviceCameraControl, Camera.PictureCallback, Camera.AutoFocusCallback{

	private final CameraActivity activity;
	private CameraSurface cameraSurface;
	private Slideshow slideshow;

	public AndroidDeviceCameraController(CameraActivity activity) {
		this.activity = activity;
	}

	public synchronized void prepareCamera() {
		System.out.println("prepareCamera");
		//activity.setFixedSize(960,640);
		if (cameraSurface == null) {
			System.out.println("	camara nula");
			cameraSurface = new CameraSurface(activity);
		}
		activity.addContentView( cameraSurface, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
	}

	public synchronized void startPreview() {
		System.out.println("startPreviw");
		// ...and start previewing. From now on, the camera keeps pushing preview
		// images to the surface.
		if (cameraSurface != null && cameraSurface.getCamera() != null) {
			cameraSurface.getCamera().startPreview();
			System.out.println("	Empezando preview");
		}
	}

	public synchronized void stopPreview() {
		// stop previewing. 
		System.out.println("stopPreviw");
		if (cameraSurface != null) {
			ViewParent parentView = cameraSurface.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(cameraSurface);
				System.out.println("	eliminando reviw");
			}
			if (cameraSurface.getCamera() != null) {
				cameraSurface.getCamera().stopPreview();
				System.out.println("	parada previw");
			}
		}
		/*activity.restoreFixedSize();
		System.out.println("	restorefixedsize");*/
	}

	public void setCameraParametersForPicture(Camera camera) {
		// Before we take the picture - we make sure all camera parameters are as we like them
		// Use max resolution and auto focus
		System.out.println("setCameraParametersForPicture(NO_OP)");
		/*Camera.Parameters p = camera.getParameters();
		List<Camera.Size> supportedSizes = p.getSupportedPictureSizes();
		int maxSupportedWidth = Integer.MAX_VALUE;
		int maxSupportedHeight = Integer.MAX_VALUE;
		for (Camera.Size size : supportedSizes) {
			if (size.width < maxSupportedWidth) {
				maxSupportedWidth = size.width;
				maxSupportedHeight = size.height;
			}
		}		
		System.out.println("/////////////////////////new pictureSize :"+maxSupportedWidth+" "+maxSupportedHeight);
		p.setPictureSize(maxSupportedWidth, maxSupportedHeight);
		p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		System.out.println("	modo autofocus");

		camera.setParameters( p ); 
		System.out.println("	seteo parametros");*/
	}

	@Override
	public synchronized void takePicture() {
		System.out.println("takePicture");
		// the user request to take a picture - start the process by requesting focus
		setCameraParametersForPicture(cameraSurface.getCamera());
		System.out.println("	parámetros de imágen seteados");
		cameraSurface.getCamera().autoFocus(this);
		System.out.println("	coge autofocus");
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera camera) {
		System.out.println("onAutoFocus");
		// Focus process finished, we now have focus (or not)
		if (success) {
			if (camera != null) {
				//camera.stopPreview();
				System.out.println("	parando preview");
				// We now have focus take the actual picture
				camera.takePicture(null, null, null, this);
				System.out.println("	tomando foto");
			}
		}
	}

	@Override
	public synchronized void onPictureTaken(byte[] data, Camera camera) {
		// We got the picture data - keep it
		System.out.println("onPictureTaken");
		
		
		FileHandle fh = Gdx.files.external("prueba");
		
		if(!fh.exists()){
			fh.mkdirs();
		}
		
		File pictureFileDir = fh.file();

	    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

	     System.out.println( "Can't create directory to save image.");
	      Toast.makeText(activity, "Can't create directory to save image.",
	          Toast.LENGTH_LONG).show();
	      return;

	    }
	    String photoFile = "Picture_" + (++CameraScreen.ID) + ".jpg";

	    Slideshow.filename = pictureFileDir.getPath() + File.separator + photoFile;
	    
	    File pictureFile = new File( Slideshow.filename);

	    try {
	      FileOutputStream fos = new FileOutputStream(pictureFile);
	      fos.write(data);
	      fos.close();
	      Toast.makeText(activity, "New Image saved:" + photoFile,
	          Toast.LENGTH_LONG).show();
	      slideshow.cameraScreen.onPictureTaken(true);
	    } catch (Exception error) {
	      System.out.println( "File" +  Slideshow.filename + "not saved: "
	          + error.getMessage());
	      Toast.makeText(activity, "Image could not be saved.",
	          Toast.LENGTH_LONG).show();
	      slideshow.cameraScreen.onPictureTaken(false);
	    }
	    Slideshow.filename = "/prueba" +  File.separator + photoFile;
	    
	    
	}
	
	@Override
	public void prepareCameraAsync() {
		System.out.println("prepareCameraAsync");
		
		activity.post(prepareCameraAsyncRunnable);
	}
	
	private final Runnable prepareCameraAsyncRunnable = new Runnable() {
		public void run() {
			prepareCamera();
		}
	};

	@Override
	public synchronized void startPreviewAsync() {
		System.out.println("startPreviewAsync");
		Runnable r = new Runnable() {
			public void run() {
				startPreview();
			}
		};//TODO
		activity.post(r);
	}

	@Override
	public synchronized void stopPreviewAsync() {
		System.out.println("stopPreviewAsync");
		Runnable r = new Runnable() {
			public void run() {
				stopPreview();
			}
		};
		activity.post(r);
	}

	@Override
	public synchronized void takePictureAsync() {
		System.out.println("takePictureAsync");
		Runnable r = new Runnable() {
			public void run() {
				takePicture();
			}
		};
		activity.post(r);
	}

	/*private Bitmap decodeFile(File f){

		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=70;

			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}*/

	@Override
	public boolean isReady() {
		System.out.println("isReady");
		if (cameraSurface!=null && cameraSurface.getCamera() != null) {
			return true;
		}
		return false;
	}

	public void setSlideshow(Slideshow slideshow) {
		this.slideshow = slideshow;		
	}
}