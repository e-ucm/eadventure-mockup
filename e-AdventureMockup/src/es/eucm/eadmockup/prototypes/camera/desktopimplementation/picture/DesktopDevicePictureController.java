package es.eucm.eadmockup.prototypes.camera.desktopimplementation.picture;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.facade.IDevicePictureControl;

public class DesktopDevicePictureController implements IDevicePictureControl{

	private Slideshow slideshow;

	@Override
	public void takePicture() {
		System.out.println("takePicture()");		
	}
	
	@Override
	public void startPreviewAsync() {
		System.out.println("startPreviewAsync()");			
	}

	@Override
	public void stopPreviewAsync() {
		System.out.println("stopPreviewAsync()");			
	}

	@Override
	public void takePictureAsync() {
		System.out.println("takePictureAsync()");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.slideshow.cameraScreen.onPictureTaken(true);
	}

	@Override
	public boolean isReady() {
		System.out.println("isReady()");
		return true;
	}

	@Override
	public void prepareCameraAsync() {
		System.out.println("prepareCameraAsync()");
		
	}
	
	public void setSlideshow(Slideshow slideshow) {
		this.slideshow = slideshow;
	}
}
