/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import es.eucm.eadmockup.prototypes.camera.buttons.Button;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.common.Thumbnail;
import es.eucm.eadmockup.prototypes.camera.facade.IDeviceVideoControl;
import es.eucm.eadmockup.prototypes.camera.facade.IOnCompletionListener;


public class PlayingScreen extends BaseScreen implements IOnCompletionListener{

	private Button startPlaying;
	private IDeviceVideoControl videoControl;
	private int existingButtons;
	private int selectedVideoID;
	private ScrollPane pane;
	private boolean loading;
	private AssetManager amExt;
	private float fontX;
	private boolean drawPlay;

	public PlayingScreen(IDeviceVideoControl cameraControl){
		this.videoControl = cameraControl;
	}

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);
		TextureRegion ta2 = ta.findRegion("camButton");
		startPlaying = new Button(ta2, ta2, halfscreenw-60, 20, 120, 60);  
		touch = new Vector3();
		root = new Table();
		root.left().top();
		root.setBackground(new NinePatchDrawable(Loading.loadingBar));
		inputMultiplexer = new InputMultiplexer(stage, this);

		pane = new ScrollPane(root, skin);
		pane.setSize(170, screenh);

		pane.setScrollingDisabled(true, false);
		pane.setupFadeScrollBars(0f, 0f);
		pane.setVisible(false);

		stage.addActor(pane);
		existingButtons = 1;

		amExt = Assets.getExternalAssetsManager();
	}

	/**
	 * Active the camera.
	 */
	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		int max = FileHandler.getVideoID();
		for(int i = existingButtons; i <= max; ++i){
			amExt.load("Slideshow/Videos/VideoThumbnails/VideoThumbnail" + i + ".jpg", Texture.class);
		}

		selectedVideoID = 1;
		fontX = screenw - font.getBounds(String.valueOf(selectedVideoID)).width;
		pane.setVisible(true);
		loading = true;
		drawPlay = true;
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
		videoControl.setOnCompletionListener(this);
	}

	@Override
	public void onHidden() {
		pane.setVisible(false);
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		if(loading){
			if(amExt.update()){
				loading = false;
				int max = FileHandler.getVideoID();
				for(int i = existingButtons; i <= max; ++i){
					final int id = i;
					TextureRegion tr = new TextureRegion(amExt.get("Slideshow/Videos/VideoThumbnails/VideoThumbnail" + i + ".jpg", Texture.class));
					Thumbnail t = new Thumbnail(tr, 150, 150, false, i);
					t.addListener(new ClickListener(){
						@Override
						public void clicked(InputEvent event, float x, float y) {
							selectedVideoID = id;
							fontX = screenw - font.getBounds(String.valueOf(selectedVideoID)).width;
							if(!videoControl.isPlaying()){
								videoControl.startPlaying(selectedVideoID);
								if(pane.isVisible()){
									pane.addAction(Actions.sequence(Actions.fadeOut(1f), run(new Runnable(){
										@Override
										public void run() {
											pane.setVisible(false);					
										}					
									})));
								}
							}
						}
					});
					root.add(t).pad(5f);
					root.row();
				}
				existingButtons = max + 1;
			}			
		}
		stage.act(delta);
	}

	public void draw()
	{
		sb.begin();
		if(drawPlay){
			startPlaying.draw();
		}
		font.draw(sb, String.valueOf(selectedVideoID), fontX, screenh);
		sb.end();
		stage.draw();
	}

	@Override
	public void hide() {
		videoControl.stopPreviewAsynk();
	}

	@Override
	public void pause() {
		videoControl.stopPreviewAsynk();
	}

	@Override
	public void resume() {
		game.setScreen(game.menu);
	}

	/**
	 * return to menu if the back key is pressed.
	 */
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			stage.addAction(sequence(moveTo(-stage.getWidth(), 0, .25f), run(new Runnable(){
				@Override
				public void run() {
					game.setScreen(game.menu);
				}
			})));
		}
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			startPlaying.touch(touch.x, touch.y);
		}
		return false;
	}

	/**
	 * Take a photo if the camera button is untouched.
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			if(startPlaying.untouch(touch.x, touch.y)){
				if(!videoControl.isPlaying()){
					videoControl.startPlaying(selectedVideoID);
					if(pane.isVisible()){
						pane.addAction(Actions.sequence(Actions.fadeOut(1f), run(new Runnable(){
							@Override
							public void run() {
								pane.setVisible(false);					
							}					
						})));
					}
					drawPlay = false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			startPlaying.touch(touch.x, touch.y);
		}
		return false;
	}

	@Override
	public void onCompletion() {
		pane.setVisible(true);	
		pane.addAction(Actions.fadeIn(1f));
		drawPlay = true;		
	}
}
