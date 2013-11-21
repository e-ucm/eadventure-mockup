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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.facade.IAnswerListener;


public class Menu extends BaseScreen implements IAnswerListener {

	public static TextureRegion title;

	private boolean close;

	private TextButton btnVistaPrevia;

	private TextButton btnGaleria;

	private TextButton btnVerVideo;

	private TextButton btnGrabarVideo;

	private TextButton btnEscenas;

	private TextButton btnFoto;

	@Override
	public void create() {
		TextureAtlas ta = am.get(atlas_src, TextureAtlas.class);
		title = ta.findRegion("name");
		setUpRoot();

		MiTransitionListener transitionListener = new MiTransitionListener();
		btnFoto = new TextButton("Foto", skin);
		btnFoto.addListener(transitionListener);

		btnVistaPrevia = new TextButton("Vista Previa", skin);
		btnVistaPrevia.addListener(transitionListener);

		btnGaleria = new TextButton("Galería", skin);
		btnGaleria.addListener(transitionListener);

		btnVerVideo = new TextButton("Ver vídeo", skin);
		btnVerVideo.addListener(transitionListener);

		btnGrabarVideo = new TextButton("Grabar vídeo", skin);
		btnGrabarVideo.addListener(transitionListener);		

		btnEscenas = new TextButton("Escenas", skin);
		btnEscenas.addListener(transitionListener);

		root.add(btnGrabarVideo).left();
		root.add(btnVerVideo).colspan(2).right();
		root.row();
		root.add(btnFoto).expand().colspan(3);
		root.row();
		root.add(btnVistaPrevia).left();
		root.add(btnGaleria);
		root.add(btnEscenas).right();
		//root.debug();
	}

	private class MiTransitionListener extends ClickListener{

		@Override
		public void clicked(InputEvent event, float x, float y) {
			final BaseScreen next = getNextScreen(event.getListenerActor());
			if(next == null){
				return;
			}
			stage.addAction(sequence(moveTo(-stage.getWidth(), 0, .25f, Interpolation.circleIn), run(new Runnable(){
				@Override
				public void run() {
					game.setScreen(next);
				}
			})));
		}

		private BaseScreen getNextScreen(Actor target) {
			BaseScreen next = null;
			if(target == btnFoto){
				next = game.cameraScreen;
			} else if(target == btnEscenas){
				next = game.scenes;				
			} else if(target == btnGaleria){
				next = game.gallery;				
			} else if(target == btnGrabarVideo){
				next = game.video;				
			} else if(target == btnVerVideo){
				next = game.playingScreen;				
			} else if(target == btnVistaPrevia){
				next = game.view;				
			}
			return next;
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		close = false;
		root.setVisible(true);
		stage.addAction(sequence(moveTo(-stage.getWidth(), 0),moveTo(0, 0, .25f, Interpolation.exp10Out)));

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
		sb.draw(title, halfscreenw/2f, screenh - 200, halfscreenw, 100);
		sb.end();
		stage.draw();
		//Table.drawDebug(stage);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			if(!close){
				close = true;
				resolver.showDecisionBox(IAnswerListener.QUESTION_EXIT, "¿Salir?", "¿Estás seguro?", 
						"Sí", 
						"No", this);
			}
		}
		return true;
	}

	@Override
	public void onReceiveAnswer(int question, int answer) {
		if(question == IAnswerListener.QUESTION_EXIT){
			if(close){
				if(answer == IAnswerListener.ANSWER_A){
					Gdx.app.exit();
				} else {
					close = false;
				}
			}
		} 		
	}
}
