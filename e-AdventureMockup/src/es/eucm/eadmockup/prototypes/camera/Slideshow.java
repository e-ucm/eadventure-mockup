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

import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.facade.IActionResolver;
import es.eucm.eadmockup.prototypes.camera.facade.IDevicePictureControl;
import es.eucm.eadmockup.prototypes.camera.facade.IDeviceVideoControl;
import es.eucm.eadmockup.prototypes.camera.screens.BaseScreen;
import es.eucm.eadmockup.prototypes.camera.screens.CameraScreen;
import es.eucm.eadmockup.prototypes.camera.screens.Gallery;
import es.eucm.eadmockup.prototypes.camera.screens.Loading;
import es.eucm.eadmockup.prototypes.camera.screens.Menu;
import es.eucm.eadmockup.prototypes.camera.screens.PlayingScreen;
import es.eucm.eadmockup.prototypes.camera.screens.Scenes;
import es.eucm.eadmockup.prototypes.camera.screens.SelectView;
import es.eucm.eadmockup.prototypes.camera.screens.TransitionScreen;
import es.eucm.eadmockup.prototypes.camera.screens.TransitionScene;
import es.eucm.eadmockup.prototypes.camera.screens.VideoScreen;
import es.eucm.eadmockup.prototypes.camera.screens.View;
import static es.eucm.eadmockup.prototypes.camera.screens.BaseScreen.*;

public class Slideshow implements ApplicationListener{

	private TransitionScreen transitionScreen;

	public BaseScreen showingScreen;
	
	public static final Color CLEAR_COLOR = Color.BLACK; 

	/***
	 * Game States
	 */
	private Loading loading;	
	public Menu menu;
	public CameraScreen cameraScreen;
	public View view;
	public Gallery gallery;
	public SelectView selectView;
	public VideoScreen video;
	public PlayingScreen playingScreen;
	public Scenes scenes;
	public TransitionScene transitionScene;

	private float delta;

	private IDevicePictureControl pictureControl;
	private IDeviceVideoControl videoControl;
	private IActionResolver actionResolver;

	public Slideshow(IDevicePictureControl cameraControl, IDeviceVideoControl videoControl, IActionResolver resolver){
		this.pictureControl = cameraControl;
		this.videoControl = videoControl;
		this.actionResolver = resolver;
	}
	
	@Override
	public void create() {
		
		Gdx.input.setCatchBackKey(true);

		// BaseScreen's statics initialization
		game = this;
		sb = new SpriteBatch(35);	
		sb.setProjectionMatrix(camera.combined);

		am = new AssetManager();
		resolver = this.actionResolver;
		stage = new Stage(screenw, screenh, true);	        	
		
		//Application's screens
		this.menu = new Menu();
		this.cameraScreen = new CameraScreen(pictureControl);
		this.video = new VideoScreen(videoControl);
		this.playingScreen = new PlayingScreen(videoControl);
		this.view = new View();
		this.gallery = new Gallery();
		this.scenes = new Scenes();
		this.transitionScene = new TransitionScene();
		this.selectView = new SelectView(videoControl);
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
		FileHandler.save();
		font.dispose();
		loading.dispose();
		view.dispose();
		am.dispose();
		sb.dispose();
		Assets.dispose();
		BaseScreen.disposeStatics();
		System.exit(0);
	}

	@Override
	public void pause() {
		this.showingScreen.pause();
		FileHandler.save();
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
