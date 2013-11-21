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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.Scene;
import es.eucm.eadmockup.prototypes.camera.common.Thumbnail;

public class TransitionScene extends BaseScreen{

	private final float rRESIZEWH = 50;
	private final float rADDWH = 50;
	
	private final int timePrecision = 100;

	private enum Quadrant{
		ONE, TWO, THREE, FOUR
	}

	private Array<Scene> list;
	private Scene current;
	private Sprite currentSprite;
	private int currentID;
	private float x, y;
	private boolean drawingNP, toDrag, dragging, dragged;
	private Rectangle rHitbox, rResize, rAdd;
	private Quadrant np2q;
	private ScrollPane pane;
	private int loadedThumbnails;
	private boolean paneHasAction;
	private long time;

	@Override
	public void create() {

		touch= new Vector3();
		rHitbox = new Rectangle();
		rResize = new Rectangle();
		rResize.setWidth(rRESIZEWH);
		rResize.setHeight(rRESIZEWH);
		rAdd = new Rectangle();
		rAdd.setWidth(rADDWH);
		rAdd.setHeight(rADDWH);	

		root = new Table();
		root.left().top();
		root.setBackground(new NinePatchDrawable(Loading.loadingBar));
		inputMultiplexer = new InputMultiplexer(stage, this);

		pane = new ScrollPane(root, skin);
		pane.setSize(220, screenh);
		pane.setScrollingDisabled(true, false);
		pane.setScrollbarsOnTop(true);
		pane.setVisible(false);
		this.dragged = false;
		this.time=0;

		stage.addActor(pane);
		loadedThumbnails =1;

		currentSprite = new Sprite();
		currentSprite.setBounds(0, 0, screenw, screenh);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);

		this.current = list.get(currentID-1);
		if(current.haveExit()){
			rHitbox = current.getHitBox();

			rResize.x = rHitbox.x + rHitbox.width - rRESIZEWH;
			rResize.y = rHitbox.y;

			rAdd.x = rHitbox.x;
			rAdd.y = rHitbox.y + rHitbox.height - rADDWH;
		
		} else {
			this.drawingNP = false;
			this.toDrag = false;
			this.dragging=false;
		}
		AssetManager amExt = Assets.getExternalAssetsManager();

		String currStr = "Slideshow/HalfSized/HalfSized" + (current.getId()) + ".jpg";

		if(!amExt.isLoaded(currStr)){
			amExt.load(currStr, Texture.class);
			amExt.finishLoading();
		}
		currentSprite.setRegion(amExt.get(currStr, Texture.class));

		int max = list.size;

		for(int i = loadedThumbnails; i <= max; ++i){	
			final Thumbnail t;
			root.add(
					t = new Thumbnail(
							new TextureRegion(
									amExt.get("Slideshow/Thumbnails/Thumbnail" + i + ".jpg", Texture.class)), 200, 200, i, false, i)
					).pad(5f);
			t.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.transitionScene.onImageSelected(list.get(t.getNum()-1));
				}
			});
			root.row();
		}
		loadedThumbnails = max+1;


		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
	}

	@Override
	public void onHidden() {
		pane.setVisible(false);
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
	}

	public void draw()
	{
		sb.begin();
		if(list.size > 0){
			currentSprite.draw(sb);
			if(drawingNP){
				Loading.loadingSel.draw(sb, rHitbox.x, rHitbox.y, rHitbox.width, rHitbox.height);
				if(toDrag) {
					Loading.loadingSel.draw(sb, rResize.x, rResize.y, rResize.width, rResize.height);
					if(!dragging){
						Loading.loadingSel.draw(sb, rAdd.x, rAdd.y, rAdd.width, rAdd.height);
					}
				}
			}
		} else {
			sb.draw(Menu.title,  10, 90, 290, 290);
		}
		font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, screenh);
		sb.end();	
		stage.draw();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			stage.addAction(sequence(moveTo(-stage.getWidth(), 0, .25f), run(new Runnable(){
				@Override
				public void run() {
					game.setScreen(game.scenes);
				}
			})));
		}
		return true;
	}

	private void limitHitBox(){
		if(rHitbox.x < 0){
			rHitbox.x = 0;
		} else if(rHitbox.x + rHitbox.width > screenw){
			rHitbox.x = screenw - rHitbox.width;
		}

		if(rHitbox.y < 0){
			rHitbox.y = 0;
		} else if(rHitbox.y + rHitbox.height > screenh){
			rHitbox.y = screenh - rHitbox.height;
		}
	}

	/**
	 * Event fired when we've choosen an image from the scroll pane.
	 */
	public void onImageSelected(Scene target) {
		pane.setVisible(false);
		current.setHitBox(new Rectangle(rHitbox));
		current.setNextScene(target);	
		System.out.println("setting nextScreen with ID: " + target.getId());
	}

	private void onDragging(){
		x=touch.x - rHitbox.width/2;
		y=touch.y - rHitbox.height/2;

		rHitbox.setX(x);
		rHitbox.setY(y);
		limitHitBox();

		float x2, y2;
		if(np2q == Quadrant.ONE){
			x2 = rHitbox.x + rHitbox.width - rResize.width;
			y2 = rHitbox.y + rHitbox.height - rResize.height;
		} else if(np2q == Quadrant.TWO){
			x2 = rHitbox.x;
			y2 = rHitbox.y + rHitbox.height - rResize.height;
		} else if(np2q == Quadrant.THREE){
			x2 = rHitbox.x;
			y2 = rHitbox.y;
		} else { //FOUR
			x2 = rHitbox.x + rHitbox.width - rResize.width;
			y2 = rHitbox.y;
		}
		rResize.setX(x2);
		rResize.setY(y2);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			time = System.currentTimeMillis();
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);
			if(rAdd.contains(touch.x, touch.y)){
				pane.setColor(1f, 1f, 1f, 0f);
				pane.setVisible(true);
				pane.addAction(Actions.fadeIn(1f));
				return true;
			}
			if(!paneHasAction && pane.isVisible()){
				paneHasAction = true;
				pane.addAction(Actions.sequence(Actions.fadeOut(1f), run(new Runnable(){
					@Override
					public void run() {
						pane.setVisible(false);	
						paneHasAction = false;					
					}					
				})));
			}

			if(toDrag){
				if(rResize.contains(touch.x, touch.y)){
					toDrag = false;
					if(rHitbox.x<rResize.x){
						x = rHitbox.x;
					} else {
						x = rHitbox.x + rHitbox.width;
					}
					if(rHitbox.y<rResize.y){
						y = rHitbox.y;
					} else {
						y = rHitbox.y + rHitbox.height;
					} 

				} else if(rHitbox.contains(touch.x,  touch.y)){
					if(drawingNP){
						dragging = true;
						/*onDragging();*/
					}						
				}
			}else{
				if(!drawingNP){
					drawingNP=true;
					x=touch.x;
					y=touch.y;
					rHitbox.set(touch.x, touch.y, 0, 0);
				}				
			}
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		if(pointer == 0 && System.currentTimeMillis()-time>timePrecision){
			this.dragged=true;
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);
			if(toDrag){
				if(dragging){
					if(drawingNP){
						onDragging();
					}	
				}

			} else {
				if(drawingNP){

					float auxX, auxY;
					float w = Math.abs(touch.x-x);
					float h = Math.abs(touch.y-y);
					if(x<touch.x){
						auxX = x;
					} else {
						auxX = touch.x;
					}
					if(y<touch.y){
						auxY=y;
					} else {
						auxY= touch.y;
					}
					rHitbox.set(auxX, auxY, w, h);
				}
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if(pointer == 0 /*&& System.currentTimeMillis()-time<=timePrecision*/){
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);
			if(!dragged && 
					current.haveExit() && 
					current.getHitBox().contains(touch.x, touch.y) && 
					!rAdd.contains(touch.x,  touch.y) &&
					!rResize.contains(touch.x, touch.y)){
				setCurrentID(current.getNextScene().getId());
				dragging = false;
				game.setScreen(game.transitionScene);
				return true;
			}
			dragged = false;
			if(toDrag){
				if(dragging){
					if(drawingNP){
						onDragging();
					}	
					dragging = false;
				}
			}else{

				if(drawingNP){

					float auxX, x2, auxY, y2;
					float w = Math.abs(touch.x-x);
					float h = Math.abs(touch.y-y);

					if(x<touch.x){
						if(y<touch.y){
							np2q = Quadrant.ONE;

							auxX = x;
							x2 = auxX + w - rRESIZEWH;
							auxY=y;
							y2 = auxY + h - rRESIZEWH;
						} else {
							np2q = Quadrant.FOUR;

							auxX = x;
							x2 = auxX + w - rRESIZEWH;
							auxY= touch.y;
							y2 = auxY;
						}
					} else {
						if(y<touch.y){
							np2q = Quadrant.TWO;

							auxX = touch.x;
							x2 = auxX;
							auxY=y;
							y2 = auxY + h - rRESIZEWH;
						} else {
							np2q = Quadrant.THREE;

							auxX = touch.x;
							x2 = auxX;
							auxY= touch.y;
							y2 = auxY;
						}
					}

					rHitbox.set(auxX, auxY, w, h);
					rResize.setX(x2);
					rResize.setY(y2);
					toDrag = true;
				}
			}

			/*Setting AddButton Rectangle x,y*/
			float xAdd, yAdd;
			if(np2q == Quadrant.THREE){
				xAdd = rHitbox.x + rHitbox.width - rResize.width;
				yAdd = rHitbox.y + rHitbox.height - rResize.height;
			} else if(np2q == Quadrant.FOUR){
				xAdd = rHitbox.x;
				yAdd = rHitbox.y + rHitbox.height - rResize.height;
			} else if(np2q == Quadrant.ONE){
				xAdd = rHitbox.x;
				yAdd = rHitbox.y;
			} else { //TWO
				xAdd = rHitbox.x + rHitbox.width - rResize.width;
				yAdd = rHitbox.y;
			}
			rAdd.setX(xAdd);
			rAdd.setY(yAdd);
		}
		time=0;
		return true;
	}

	public void setScenes(Array<Scene> ls){
		this.list = ls;
	}

	public void setCurrentID(int num) {
		System.out.println("setting currentID: " + num);
		currentID = num;

	}

}
