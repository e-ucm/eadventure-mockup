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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import es.eucm.eadmockup.prototypes.camera.screens.BaseScreen;
import es.eucm.eadmockup.prototypes.camera.screens.CameraScreen;
import es.eucm.eadmockup.prototypes.camera.screens.Loading;
import es.eucm.eadmockup.prototypes.camera.screens.Menu;
import es.eucm.eadmockup.prototypes.camera.screens.TransitionScreen;
import es.eucm.eadmockup.prototypes.camera.screens.View;
import static es.eucm.eadmockup.prototypes.camera.screens.BaseScreen.*;

public class Slideshow implements ApplicationListener{

	public static String filename;

	private TransitionScreen transitionScreen;

	public BaseScreen showingScreen;

	/***
	 * Game States
	 */
	private Loading loading;	
	public Menu menu;
	public CameraScreen cameraState;
	public View view;


	private float delta;

	private IDeviceCameraControl cameraControl;
	private IActionResolver resolver;

	public Slideshow(IDeviceCameraControl cameraControl, IActionResolver resolver){
		this.cameraControl = cameraControl;
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

		this.menu = new Menu();
		this.cameraState = new CameraScreen(cameraControl);
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

		sb.begin();
		showingScreen.draw();	
		sb.end();
	}

	@Override
	public void dispose() {
		settings.flush();
		font.dispose();
		loading.dispose();
		am.dispose();
		sb.dispose();
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
