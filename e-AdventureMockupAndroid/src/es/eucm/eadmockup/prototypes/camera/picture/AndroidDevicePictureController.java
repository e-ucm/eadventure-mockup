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
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.Toast;
import es.eucm.eadmockup.prototypes.camera.CameraActivity;
import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.facade.IDevicePictureControl;

public class AndroidDevicePictureController implements IDevicePictureControl, Camera.PictureCallback, Camera.AutoFocusCallback{

	private final CameraActivity activity;
	private CameraSurface cameraSurface;
	private Slideshow slideshow;
	private final LayoutParams mLayoutParams;
	private final Runnable prepareCameraAsyncRunnable;
	private final Runnable startPreviewAsyncRunnable;
	private final Runnable stopPreviewAsyncRunnable;

	public AndroidDevicePictureController(CameraActivity activity) {
		this.activity = activity;
		this.mLayoutParams = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
		this.prepareCameraAsyncRunnable = new Runnable() {
			public void run() {
				prepareCamera();
			}
		};
		this.startPreviewAsyncRunnable = new Runnable() {
			public void run() {
				startPreview();
			}
		};
		this.stopPreviewAsyncRunnable = new Runnable() {
			public void run() {
				stopPreview();
			}
		};
	}

	public synchronized void prepareCamera() {
		System.out.println("prepareCamera");
		//activity.setFixedSize(960,640);
		if (cameraSurface == null) {
			System.out.println("	camara nula");
			cameraSurface = new CameraSurface(activity);
		}
		activity.addContentView( cameraSurface, mLayoutParams );
	}

	public synchronized void startPreview() {
		System.out.println("startPreviw");
		// ...and start previewing. From now on, the camera keeps pushing preview
		// images to the surface.
		if (cameraSurface != null && cameraSurface.getCamera() != null) {
			cameraSurface.getCamera().startPreview();
		}
	}

	public synchronized void stopPreview() {
		// stop previewing. 
		if (cameraSurface != null) {
			ViewParent parentView = cameraSurface.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(cameraSurface);
			}
			Camera cam = cameraSurface.getCamera();
			if (cam != null) {
				cam.stopPreview();
			}
		}
	}

	@Override
	public synchronized void takePicture() {
		System.out.println("takePicture");
		// the user request to take a picture - start the process by requesting focus
		cameraSurface.getCamera().autoFocus(this);
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera camera) {
		System.out.println("onAutoFocus");
		// Focus process finished, we now have focus (or not)
		if (success) {
			if (camera != null) {
				// We now have focus take the actual picture
				camera.takePicture(null, null, null, this);
			}
		}
	}

	@Override
	public synchronized void onPictureTaken(byte[] data, Camera camera) {
		// We got the picture data - keep it

		String oriPath = FileHandler.getOriginalsFileHandle().file().getAbsolutePath() +  File.separator;
		String halfPath = FileHandler.getHalfSizedFileHandle().file().getAbsolutePath() +  File.separator;
		String thumbPath = FileHandler.getThumbnailsFileHandle().file().getAbsolutePath() +  File.separator;
			
		int resID = 1 + FileHandler.getResourceID();
		String num = String.valueOf(resID) + ".jpg";
		
		String originalFileName = oriPath + "Original" + num;
		String halfFileName = halfPath + "HalfSized" + num;
		String thumbnailFileName = thumbPath + "Thumbnail" + num;

		try {
			OutputStream fos = new FileOutputStream(new File(originalFileName));
			fos.write(data);
			fos.close();

			Bitmap imageBitmap =  BitmapFactory.decodeByteArray(data, 0, data.length);

			Size photoSize = CameraSurfaceCallback.getPhotoSize();
			int w = photoSize.width; 
			int h = photoSize.height;
			
			//Thumbnail 
			Bitmap aux = Bitmap.createScaledBitmap(imageBitmap, w/10, h/10, false);
			
			fos = new FileOutputStream(new File(thumbnailFileName));
			aux.compress(Bitmap.CompressFormat.JPEG, 75, fos);
			fos.flush();
			fos.close();
			if(aux != imageBitmap && aux != null){
				aux.recycle();
				aux = null;
			}

			//HalfScaled image
			aux = Bitmap.createScaledBitmap(imageBitmap, w/2, h/2, true);

			fos = new FileOutputStream(new File(halfFileName));
			aux.compress(Bitmap.CompressFormat.JPEG, 95, fos);
			fos.flush();
			fos.close();
			if(aux != imageBitmap && aux != null){
				aux.recycle();
				aux = null;
			}
			
			if(imageBitmap != null){
				imageBitmap.recycle();
				imageBitmap = null;
			}
			
			fos = null;
			
			Toast.makeText(activity, "New Image saved, id: " + resID,
					Toast.LENGTH_SHORT).show();

			FileHandler.incrementResourceID();
			
			slideshow.cameraScreen.onPictureTaken(true);
		} catch (Exception error) {
			System.out.println( "File not saved: " + error.getMessage());
			Toast.makeText(activity, "Image could not be saved.", Toast.LENGTH_LONG).show();
			slideshow.cameraScreen.onPictureTaken(false);
		}
	}

	@Override
	public void prepareCameraAsync() {
		activity.post(prepareCameraAsyncRunnable);
	}

	@Override
	public synchronized void startPreviewAsync() {
		activity.post(startPreviewAsyncRunnable);
	}

	@Override
	public synchronized void stopPreviewAsync() {
		activity.post(stopPreviewAsyncRunnable);
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

	@Override
	public boolean isReady() {
		System.out.println("isReady");
		if (cameraSurface != null && cameraSurface.getCamera() != null) {
			return true;
		}
		return false;
	}

	public void setSlideshow(Slideshow slideshow) {
		this.slideshow = slideshow;		
	}
}