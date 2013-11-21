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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

import es.eucm.eadmockup.prototypes.camera.buttons.Button;
import es.eucm.eadmockup.prototypes.camera.facade.IDevicePictureControl;


public class CameraScreen extends BaseScreen {

	private static final float HALF_SECOND = .5f;

	private static Button takePic;

	private IDevicePictureControl cameraControl;

	private boolean started;

	private float triggerTime;

	public CameraScreen(IDevicePictureControl cameraControl){
		this.cameraControl = cameraControl;
	}

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);

		takePic = new Button(ta.findRegion("camButton"),ta.findRegion("camButton"), halfscreenw-60, 20, 120, 60);   
		touch = new Vector3();
	}

	/**
	 * Active the camera.
	 */
	@Override
	public void show() {
		Gdx.input.setInputProcessor(null);
		cameraControl.prepareCameraAsync();
		started = true;
		triggerTime = -1f;
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// | GL20.GL_DEPTH_BUFFER_BIT);
		/*gl.glEnable(GL20.GL_DEPTH_TEST);
		gl.glEnable(GL20.GL_TEXTURE);                     
		gl.glEnable(GL20.GL_TEXTURE_2D);     
		gl.glDepthFunc(GL20.GL_LEQUAL);
		gl.glClearDepthf(1.0F);*/

		if(started && cameraControl.isReady()){
			started = false;
			cameraControl.startPreviewAsync();
			triggerTime = 0f;
		}

		if(triggerTime != -1){
			triggerTime += delta;
			if(triggerTime > HALF_SECOND){
				Gdx.input.setInputProcessor(this);
				triggerTime = -1f;
			}
		}
	}

	public void draw()
	{
		sb.begin();
		takePic.draw();
		sb.end();
	}

	@Override
	public void hide() {
		cameraControl.stopPreviewAsync();
	}

	@Override
	public void pause() {
		cameraControl.stopPreviewAsync();
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

			takePic.touch(touch.x, touch.y);
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

			if(takePic.untouch(touch.x, touch.y)){
				cameraControl.takePictureAsync();
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			takePic.touch(touch.x, touch.y);
		}
		return false;
	}

	/**
	 * if the photo is taken, save the total number of the pictures.
	 * @param succeed
	 */
	public void onPictureTaken(boolean succeed) {
		//game.setScreen(game.menu);
		cameraControl.stopPreviewAsync();
		cameraControl.prepareCameraAsync();
		started = true;
		triggerTime = -.5f;
	}
}
