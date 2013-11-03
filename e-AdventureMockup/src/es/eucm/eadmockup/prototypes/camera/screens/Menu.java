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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.eadmockup.prototypes.camera.IAnswerListener;
import es.eucm.eadmockup.prototypes.camera.Slideshow;


public class Menu extends BaseScreen implements IAnswerListener {

	public static TextureRegion background, title, gallery;

	private static final int QUESTION_EXIT = 1;

	public static final int ANSWER_A = 1;
	public static final int ANSWER_B = 2;

	private boolean close;

	private TextButton btn2;

	private TextButton btn3;

	private TextButton btn4;

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);
		background = ta.findRegion("background");
		title = ta.findRegion("name");
		gallery = ta.findRegion("gallery");   
		touch = new Vector3();
		setUpRoot();
		
		TextButton btn1 = new TextButton("Foto", skin);
		btn1.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				game.setScreen(game.cameraScreen);
			}
		});
		
		btn2 = new TextButton("Galería", skin);
		btn2.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				game.setScreen(game.view);
			}
		});
		
		btn3 = new TextButton("Grabar", skin);
		btn3.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				game.setScreen(game.video);
			}
		});
		
		btn4 = new TextButton("Vídeo", skin);
		btn4.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				game.setScreen(game.playingscreen);
			}
		});
		
		root.add(btn1).expand().bottom().left();
		root.add(btn3).expand().bottom();
		root.add(btn2).expand().bottom().right();
		root.add(btn4).expand().bottom().right();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		close = false;
		root.setVisible(true);
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
	}

	@Override
	public void onHidden() {
		root.setVisible(false);
	}
	
	public void draw()
	{
		sb.begin();
		sb.disableBlending();
		sb.draw(background, 0, 0, screenw, screenh);
		sb.enableBlending();
		sb.draw(title, halfscreenw/2f, screenh - 200, halfscreenw, 100);
		sb.draw(gallery, 20, 120, screenw - 40, screenh - 340);
		sb.end();
		stage.draw();
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
		return true;
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
