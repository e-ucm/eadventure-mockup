/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

import es.eucm.eadmockup.prototypes.camera.IDeviceVideoControl;
import es.eucm.eadmockup.prototypes.camera.buttons.Button;


public class VideoScreen extends BaseScreen {

	private static Button startRec, startPlaying;

	private IDeviceVideoControl videoControl;

	private String rec, pl;

	private float playX;

	public VideoScreen(IDeviceVideoControl cameraControl){
		this.videoControl = cameraControl;
	}

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);

		startRec = new Button(ta.findRegion("camButton"),ta.findRegion("camButton"), halfscreenw/2-60, 20, 120, 60);  
		startPlaying = new Button(ta.findRegion("camButton"),ta.findRegion("camButton"), .75f*screenw-60, 20, 120, 60);  
		touch = new Vector3();

	}

	/**
	 * Active the camera.
	 */
	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		videoControl.prepareVideoAsynk();
		rec = "REC no";
		pl = "PL no";
		playX = screenw - font.getBounds(pl).width;
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL20.GL_DEPTH_TEST);
		gl.glEnable(GL20.GL_TEXTURE);                     
		gl.glEnable(GL20.GL_TEXTURE_2D);     
		gl.glDepthFunc(GL20.GL_LEQUAL);
		gl.glClearDepthf(1.0F);
		
		
	}

	public void draw()
	{
		sb.begin();
		startRec.draw();
		startPlaying.draw();
		font.draw(sb, rec, 0, screenh);
		font.draw(sb, pl, playX, screenh);
		sb.end();
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
			startPlaying.touch(touch.x, touch.y);
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
						rec = "REC sí";
					} else { 
						rec = "REC no";						
					}
				} else {
					videoControl.startRecording();
					if(videoControl.isRecording()){
						rec = "REC sí";
					} else { 
						rec = "REC no";						
					}
				}
			} else if(startPlaying.untouch(touch.x, touch.y)){
				System.out.println("playing");
				if(videoControl.isPlaying()){
					videoControl.stopPlaying();
					if(videoControl.isPlaying()){
						pl = "PL sí";
					} else {
						pl = "PL no";						
					}
				} else {
					videoControl.startPlaying();
					if(videoControl.isPlaying()){
						pl = "PL sí";
					} else {
						pl = "PL no";						
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
			startPlaying.touch(touch.x, touch.y);
		}
		return false;
	}
}
