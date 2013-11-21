/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.eadmockup.prototypes.camera.buttons.Button;
import es.eucm.eadmockup.prototypes.camera.facade.IDeviceVideoControl;


public class VideoScreen extends BaseScreen {

	public static String QUALITY;

	private static Button startRec;

	private IDeviceVideoControl videoControl;

	private String rec;

	private List<String> qual;
	
	public VideoScreen(IDeviceVideoControl cameraControl){
		this.videoControl = cameraControl;
	}

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);
		TextureRegion ta2 = ta.findRegion("camButton");

		startRec = new Button(ta2, ta2, halfscreenw-60, 20, 120, 60);   
		touch = new Vector3();
		setUpRoot();

		this.qual = videoControl.getQualities();
		QUALITY = qual.get(0);
		final SelectBox qualities = new SelectBox(qual.toArray(), skin);

		qualities.addListener( new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				SelectBox source = (SelectBox)event.getListenerActor();
				int index =  source.getSelectionIndex();
				String old = QUALITY;
				QUALITY = qual.get(index);

				if(QUALITY != old){
					game.setScreen(game.video);
				}
			}
		} );
		root.top().right();
		root.add(qualities);
	}

	/**
	 * Active the camera.
	 */
	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		root.setVisible(true);
		videoControl.prepareVideoAsynk();
		rec = "RECORDING: no";
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		stage.act(delta);
	}

	public void draw()
	{
		sb.begin();
		startRec.draw();
		font.draw(sb, rec, 0, screenh);
		sb.end();
		stage.draw();
	}
	
	@Override
	public void onHidden() {
		root.setVisible(false);
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
			game.setScreen(game.menu);
		}
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			startRec.touch(touch.x, touch.y);
		}
		return false;
	}

	/**
	 * take a photo if the camera button is untouched.
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			if(startRec.untouch(touch.x, touch.y)){
				System.out.println("recording");
				if(videoControl.isRecording()){
					videoControl.stopRecording();
					if(videoControl.isRecording()){
						rec = "RECORDING: sí";
					} else { 
						rec = "RECORDING: no";						
					}
				} else {
					videoControl.startRecording();
					if(videoControl.isRecording()){
						rec = "RECORDING: sí";
					} else { 
						rec = "RECORDING: no";						
					}
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

			startRec.touch(touch.x, touch.y);
		}
		return false;
	}
}
