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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.common.Thumbnail;

public class Gallery extends BaseScreen{

	private Array<Thumbnail> list;
	private static Array<Integer> sources;
	private static Array<Integer> tablePos;
	private static Array<Boolean> type;
	private Array<Cell<Actor>> bottomCells, principalCells;
	private boolean loading;
	private AssetManager amExt;
	private ScrollPane pane;
	private Integer NUM;
	private Table bottom;
	private ScrollPane pane2;
	private int added;
	private boolean something;
	private static int next;
	
	private int currentV, currentP;

	@Override
	public void create() {
		this.list = new Array<Thumbnail>();
		sources = new Array<Integer>();
		tablePos = new Array<Integer>();
		type = new Array<Boolean>();
		this.bottomCells = new Array<Cell<Actor>>();
		this.principalCells = new Array<Cell<Actor>>();
		this.currentP=0;
		this.currentV=0;
		added = 1;

		next=0;
		amExt = Assets.getExternalAssetsManager();

		root = new Table();
		//root.debug();
		inputMultiplexer = new InputMultiplexer(stage, this);


		pane = new ScrollPane(root, skin);
		//pane.pack();
		pane.setWidth(screenw);
		pane.setHeight(screenh*.75f);
		pane.setY(screenh*.25f);
		pane.setScrollingDisabled(true, false);
		pane.setVisible(false);
		// pane.setCancelTouchFocus(false);	

		bottom = new Table();
		//bottom.debug();
		bottom.left();

		pane2 = new ScrollPane(bottom, skin);
		pane2.setVisible(false);
		//pane2.pack();
		pane2.setWidth(screenw);
		pane2.setHeight(screenh*.25f);

		pane2.setScrollingDisabled(false, true);
		pane2.setupFadeScrollBars(0, 0);

		stage.addActor(pane);
		stage.addActor(pane2);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		NUM = 0;
		sources.clear();
		tablePos.clear();
		type.clear();
		next=0;
		int maxPicture = FileHandler.getResourceID();
		for(int i = currentP+1; i <= maxPicture; ++i){
			amExt.load("Slideshow/Thumbnails/Thumbnail" + i + ".jpg", Texture.class);
		}
		
		int maxVideo = FileHandler.getVideoID();
		for(int i = currentV+1; i <= maxVideo; ++i){
			amExt.load("Slideshow/Videos/VideoThumbnails/VideoThumbnail" + i + ".jpg", Texture.class);
		}

		for(int i = principalCells.size+1; i <= maxPicture+maxVideo; ++i){
			@SuppressWarnings("unchecked")
			Cell<Actor> c = root.add().pad(20f);
			principalCells.add(c);
			if((i+(currentP+currentV))%3==0){
				root.row();
			}
		}

		for(int i = bottomCells.size; i <= maxPicture+maxVideo; ++i){
			@SuppressWarnings("unchecked")
			Cell<Actor> c = bottom.add().pad(8f);
			bottomCells.add(c);			
		}

		this.loading = true;
		pane.setVisible(true);
		pane2.setVisible(true);
		something = false;
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
	}

	@Override
	public void hide() {
		//animaciones de salida
	}

	@Override
	public void onHidden() {
		pane.setVisible(false);
		pane2.setVisible(false);
		//imagenes.clear();
		for(Actor a : root.getChildren()){
			((Thumbnail)a).clearSelect();			
		}
		bottom.clear();
		bottomCells.clear();
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(loading){
			if(amExt.update()){
				int maxP = FileHandler.getResourceID();
				int maxV = FileHandler.getVideoID();
				for(int i = added; i <= maxP+maxV; ++i){
					final Thumbnail t;
					if (i<=maxP+currentV){
						currentP++;
						TextureRegion tr = new TextureRegion(amExt.get("Slideshow/Thumbnails/Thumbnail" + currentP + ".jpg", Texture.class));
						t = new Thumbnail(tr, 200, 200, currentP, true, (currentP+currentV));	
					} else {
						currentV++;
						TextureRegion tr = new TextureRegion(amExt.get("Slideshow/Videos/VideoThumbnails/VideoThumbnail" + currentV + ".jpg", Texture.class));
						t = new Thumbnail(tr, 200, 200, currentV, false, (currentP+currentV));
					}
						
					list.add(t);
					t.addListener(new ClickListener(){

						@Override
						public void clicked(InputEvent event, float x, float y) {
							int n = NUM+1;

							if(t.selectMe(String.valueOf(n))){
								NUM = n;

								sources.add(t.getNum());
								System.out.println("added = "+(currentP+currentV));
								tablePos.add(t.getTablePos());
								type.add(t.isPicture());
								final Thumbnail t2 = new Thumbnail(t.getRegion(), 80, 80, t.isPicture(), t.getTablePos());

								bottomCells.get(NUM).setWidget(t2);
								if(!something){
									something = true;
									final TextButton btn = new TextButton("Ver", skin);
									btn.addListener(new ClickListener(){
										@Override
										public void clicked( InputEvent event, float x, float y ) {
											if(sources.size!=0){
												game.setScreen(game.selectView);
											}
										}
									});
									bottomCells.get(0).setWidget(btn);
								}
							} else{

								NUM--;

								for(int i=t.getPos(); i<sources.size; i++)
								{
									System.out.println("i "+i+" posTabla "+tablePos.get(i-1));
									list.get(tablePos.get(i)-1).decressS();										
								}


								for(int i=t.getPos()-1; i<sources.size-1; i++)
								{
									System.out.println("indice ======= "+i);
									sources.set(i, sources.get(i+1));
									tablePos.set(i, tablePos.get(i+1));
									type.set(i, type.get(i+1));
								}
								sources.removeIndex(sources.size-1);
								tablePos.removeIndex(tablePos.size-1);
								type.removeIndex(type.size-1);
								//System.out.println("Posiciones "+imagenes.toString());
								//System.out.println("Thumbnails "+list.toString());
								if(t.getPos() <= sources.size){
									for(int i=t.getPos(); i<=NUM; i++)
									{
										final Cell<Actor> right = bottomCells.get(i+1);
										final Cell<Actor> left = bottomCells.get(i);
										final Actor aRight = bottomCells.get(i+1).getWidget();
										aRight.addAction(sequence(moveTo(left.getWidgetX(), left.getWidgetY(), .25f, Interpolation.pow2), run(new Runnable(){

											@Override
											public void run() {
												right.setWidget(null);
												left.setWidget( aRight);
											}

										})));
									}
								} else {
									bottomCells.get(t.getPos()).setWidget(null);

								}

							}
						}
					});

					principalCells.get(i-1).setWidget(t);
					added = maxP+maxV+1;
				}

				loading = false;
			} 			
		}
		stage.act(delta);
	}

	public class MiListener extends ClickListener{
		private Table bot;
		private Thumbnail t;
		public MiListener(Thumbnail t, Table bot){
			this.bot = bot;
			this.t = t;
		}
		@Override
		public void clicked(InputEvent event, float x, float y) {
			int n = NUM+1;
			if(t.selectMe(String.valueOf(n))){
				NUM = n;

				sources.add(t.getNum());
				type.add(t.isPicture());
				Thumbnail t2 = new Thumbnail(t.getRegion(), 80, 80, t.isPicture(), t.getTablePos());
				this.bot.add(t2).pad(5f);

			}
		}
	}

	public void draw()
	{
		if(loading) {	
			sb.begin();
			font.draw(sb, String.valueOf(amExt.getProgress()*100), 0, screenh);
			sb.end();
		} else {
			sb.begin();
			font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, screenh);
			sb.end();
		}		
		stage.draw();
		//Table.drawDebug(stage);
	}



	@Override
	public void resume() {
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

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){



		}
		return true;
	}

	public static int getNext()
	{
		System.out.print("next actual = "+ next);
		int aux = next;
		next=(1+next)%sources.size;
		return sources.get(aux)-1; //the first image is the position zero in the imageArray
	}
	
	/**
	 * Call before getNext()
	 * 
	 */
	public static boolean nextIsPicture(){
		return type.get(next);
	}

}

