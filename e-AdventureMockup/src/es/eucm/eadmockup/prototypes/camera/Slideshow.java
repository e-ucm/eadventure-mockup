/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/


package es.eucm.eadmockup.prototypes.camera;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.eadmockup.prototypes.camera.screens.BaseScreen;
import es.eucm.eadmockup.prototypes.camera.screens.CameraScreen;
import es.eucm.eadmockup.prototypes.camera.screens.Loading;
import es.eucm.eadmockup.prototypes.camera.screens.Menu;
import es.eucm.eadmockup.prototypes.camera.screens.PlayingScreen;
import es.eucm.eadmockup.prototypes.camera.screens.TransitionScreen;
import es.eucm.eadmockup.prototypes.camera.screens.VideoScreen;
import es.eucm.eadmockup.prototypes.camera.screens.View;
import static es.eucm.eadmockup.prototypes.camera.screens.BaseScreen.*;

public class Slideshow implements ApplicationListener{

	public static String filename;

	private TransitionScreen transitionScreen;

	public BaseScreen showingScreen;
	
	public static final Color CLEAR_COLOR = Color.WHITE; 

	/***
	 * Game States
	 */
	private Loading loading;	
	public Menu menu;
	public CameraScreen cameraScreen;
	public View view;
	public VideoScreen video;
	public PlayingScreen playingscreen;

	private float delta;

	private IDeviceCameraControl cameraControl;
	private IDeviceVideoControl videoControl;
	private IActionResolver resolver;

	public Slideshow(IDeviceCameraControl cameraControl, IDeviceVideoControl videoControl, IActionResolver resolver){
		this.cameraControl = cameraControl;
		this.videoControl = videoControl;
		this.resolver = resolver;
	}
	
	@Override
	public void create() {
		
		Gdx.input.setCatchBackKey(true);

		// Base screen
		game = this;
		sb = new SpriteBatch(35);	
		sb.setProjectionMatrix(camera.combined);

		am = new AssetManager();
		BaseScreen.resolver = this.resolver;
		settings = Gdx.app.getPreferences("eadmockup_camera");	
		stage = new Stage(screenw, screenh, true);	        	
		
		//Screens
		this.menu = new Menu();
		this.cameraScreen = new CameraScreen(cameraControl);
		this.video = new VideoScreen(videoControl);
		this.playingscreen = new PlayingScreen(videoControl);
		this.view = new View();
		this.loading = new Loading();
		this.loading.create();
		this.showingScreen = loading;			

		this.transitionScreen = new TransitionScreen();	

	}

	@Override
	public void render() {
		delta = Gdx.graphics.getDeltaTime();

		showingScreen.render(delta);	

		showingScreen.draw();	
	}

	@Override
	public void dispose() {
		settings.flush();
		font.dispose();
		loading.dispose();
		am.dispose();
		sb.dispose();
		view.dispose();
		stage.dispose();
		System.exit(0);
	}

	@Override
	public void pause() {
		this.showingScreen.pause();
	}

	@Override
	public void resume() {
		this.showingScreen.resume();
	}

	@Override
	public void resize(int w, int h) { }	

	public void setScreen(BaseScreen nextScreen){ 
		this.showingScreen = this.transitionScreen.initializer(nextScreen); 
	}

}
