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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import es.eucm.eadmockup.prototypes.camera.IAnswerListener;
import es.eucm.eadmockup.prototypes.camera.buttons.Button;

public class Menu extends BaseScreen implements IAnswerListener {

	private static TextureRegion background, title, gallery;
	private static Button add, view;

	private static final int QUESTION_EXIT = 1;

	public static final int ANSWER_A = 1;
	public static final int ANSWER_B = 2;

	private boolean close;

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);
		background = ta.findRegion("background");
		title = ta.findRegion("name");
		gallery = ta.findRegion("gallery");
		add = new Button(ta.findRegion("addButtonOff"),ta.findRegion("addButtonOn"), 20, 20, 60, 60);
		view = new Button(ta.findRegion("viewButtonOff"),ta.findRegion("viewButtonOn"), screenw-80, 20, 60, 60);   
		touch = new Vector3();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		close = false;
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		gl.glClearColor(1f, 1f, 1f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void draw()
	{
		sb.disableBlending();
		sb.draw(background, 0, 0, screenw, screenh);
		sb.enableBlending();
		sb.draw(title, 75, 400, 160, 50);
		sb.draw(gallery, 10, 90, 290, 290);

		add.draw();
		view.draw();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			if(!close){
				close = true;
				resolver.showDecisionBox(QUESTION_EXIT, "¿Salir?", "¿Estás seguro?", 
						"Sí", 
						"No", this);
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			add.touch(touch.x, touch.y);
			view.touch(touch.x, touch.y);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			if(add.untouch(touch.x, touch.y)){
				game.setScreen(game.cameraState);
			} else if(view.untouch(touch.x, touch.y)){
				game.setScreen(game.view);
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer == 0){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			add.touch(touch.x, touch.y);
			view.touch(touch.x, touch.y);
		}
		return false;
	}

	@Override
	public void onReceiveAnswer(int question, int answer) {
		if(question == QUESTION_EXIT){
			if(close){
				if(answer == ANSWER_A){
					Gdx.app.exit();
				} else {
					close = false;
				}
			}
		} 		
	}
}
