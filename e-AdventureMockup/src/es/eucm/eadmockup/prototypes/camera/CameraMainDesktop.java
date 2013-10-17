/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/


package es.eucm.eadmockup.prototypes.camera;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

import es.eucm.eadmockup.prototypes.camera.screens.Menu;

public class CameraMainDesktop implements IDeviceCameraControl, IActionResolver {

	private static CameraMainDesktop main;
	private static Slideshow ss;
	public static void main(String[] args) {
		
		if(main == null){
			main = new CameraMainDesktop();
		}
		
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
				TexturePacker2.process(settings, dataPC+name, data+name, name);
			}
			
		}		
		ss = new Slideshow(main, main);
		new LwjglApplication(ss, "Prototype", 320, 480, true);
	}

	@Override
	public void prepareCamera() {
		System.out.println("prepareCamera()");		
	}

	@Override
	public void startPreview() {
		System.out.println("startPreview()");			
	}

	@Override
	public void stopPreview() {
		System.out.println("stopView()");	
		
	}

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
		ss.cameraState.onPictureTaken(true);
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

	@Override
	public void showDecisionBox(final int questionNumber, final String alertBoxTitle,
			final String alertBoxQuestion, String answerA, String answerB,
			final IAnswerListener ql) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				int result = JOptionPane.showConfirmDialog(null, alertBoxQuestion, alertBoxTitle, JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION){
					ql.onReceiveAnswer(questionNumber, Menu.ANSWER_A);
				} else if (result == JOptionPane.NO_OPTION){
					ql.onReceiveAnswer(questionNumber, Menu.ANSWER_B);
				} 	
			}
		});
		
	}

}
