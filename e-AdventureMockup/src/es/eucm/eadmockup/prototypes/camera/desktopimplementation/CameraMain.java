package es.eucm.eadmockup.prototypes.camera.desktopimplementation;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.desktopimplementation.picture.DesktopDevicePictureController;
import es.eucm.eadmockup.prototypes.camera.desktopimplementation.video.DesktopDeviceVideoController;

public class CameraMain {
	
	public static void main(String[] args) {			
		
		boolean do_pack = false; 

		if(do_pack){
			
			Settings settings = new Settings();
			settings.fast = false;
			settings.pot = false;
			settings.maxHeight = 1024;
			settings.maxWidth = 1024;
			settings.paddingX = 2;
			settings.paddingY = 2;
			settings.filterMin = TextureFilter.Linear;
			settings.filterMag = TextureFilter.Linear;			

			String dataPC = "dataPC/screens/";
			String data = "data/screens/";
			File f = new File("dataPC/screens");
			File[] screens = f.listFiles();
			
			for(int i = 0; i < screens.length; ++i){	
				String name = screens[i].getName();
				TexturePacker2.process(settings, dataPC + name, data+name, name);
			}
			
		}	
		
		DesktopDevicePictureController pictureControl = new DesktopDevicePictureController();
		DesktopDeviceVideoController videoControl = new DesktopDeviceVideoController();
		Slideshow slideshow = new Slideshow(pictureControl, videoControl, new DesktopResolver());
		pictureControl.setSlideshow(slideshow);
		
		new LwjglApplication(slideshow, "Prototype", 854, 480, true);
	}
}
