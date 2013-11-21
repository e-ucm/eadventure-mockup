/**************************************************************************\
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.common.Scene;
import es.eucm.eadmockup.prototypes.camera.common.Thumbnail;

public class Scenes extends BaseScreen{

	private Array<Scene> list;
	private static Array<Integer> imagenes;
	private Array<Cell<Actor>> principalCells;
	private boolean loading;
	private AssetManager amExt;
	private ScrollPane pane;
	private int addedImages;

	@Override
	public void create() {

		this.list = new Array<Scene>();
		game.transitionScene.setScenes(list);

		Scenes.imagenes = new Array<Integer>();
		this.principalCells = new Array<Cell<Actor>>();
		addedImages = 1;
		amExt = Assets.getExternalAssetsManager();

		root = new Table();
		inputMultiplexer = new InputMultiplexer(stage, this);


		pane = new ScrollPane(root, skin);
		pane.setFillParent(true);
		pane.setScrollingDisabled(true, false);
		pane.setVisible(false);
		// pane.setCancelTouchFocus(false);	

		stage.addActor(pane);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		imagenes.clear();
		int max = FileHandler.getResourceID();
		for(int i = list.size+1; i <= max; ++i){
			amExt.load("Slideshow/Thumbnails/Thumbnail" + i + ".jpg", Texture.class);
		}

		for(int i = principalCells.size+1; i <= max; ++i){
			@SuppressWarnings("unchecked")
			Cell<Actor> c = root.add().pad(20f);
			principalCells.add(c);
			if((i)%3==0){
				root.row();
			}
		}

		this.loading = true;
		pane.setVisible(true);
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
	}

	@Override
	public void onHidden() {
		pane.setVisible(false);
		for(Actor a : root.getChildren()){
			((Thumbnail)a).clearSelect();			
		}
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(loading){
			if(amExt.update()){
				int max = FileHandler.getResourceID();
				for(int i = addedImages; i <= max; ++i){
					TextureRegion th = new TextureRegion(amExt.get("Slideshow/Thumbnails/Thumbnail" + i + ".jpg", Texture.class));
					final Thumbnail t = new Thumbnail(th, 200, 200, i, true, i);
					final Scene st = new Scene(i);
					list.add(st);
					t.addListener(new ClickListener(){
						@Override
						public void clicked(InputEvent event, float x, float y) {
								imagenes.add(t.getNum());
								game.transitionScene.setCurrentID(st.getId());
								game.setScreen(game.transitionScene);
						}
					});

					principalCells.get(i-1).setWidget(t);
					addedImages = max+1;
				}

				loading = false;
			} 			
		}
		stage.act(delta);
	}

	public void draw()
	{
		sb.begin();
		if(loading) {	
			font.draw(sb, String.valueOf(amExt.getProgress()*100), 0, screenh);
		} else {
			font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, screenh);
		}		
		sb.end();
		stage.draw();
		//Table.drawDebug(stage);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			stage.addAction(sequence(moveTo(stage.getWidth(), 0, .25f), run(new Runnable(){

				@Override
				public void run() {
					game.setScreen(game.menu);

				}

			})));
		}
		return true;
	}

}

