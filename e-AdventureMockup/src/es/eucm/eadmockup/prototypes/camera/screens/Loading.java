/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;

public class Loading extends BaseScreen {

	private final int SEL=6;

	public static NinePatch loadingBar, loadingProgress, loadingSel;
	private TextureAtlas atlas;
	private float xBar, yBar, wBar, hBar, progress;

	private final String font_src = "data/font/impact38bold.fnt";
	private final String skin_src = "data/skin/holo-dark-xhdpi.json";

	@Override
	public void create() {
		Gdx.input.setInputProcessor(null);

		am.load(font_src, BitmapFont.class);
		am.load(skin_src, Skin.class);

		this.wBar = halfscreenw * 1.5f;
		this.hBar = halfscreenw / 7f;
		this.xBar = halfscreenw - this.wBar/2f;
		this.yBar = halfscreenh/2f - hBar/2f;

		this.atlas = new TextureAtlas("data/ninepatch/ninepatch.atlas");

		loadingSel = new NinePatch(atlas.findRegion("1"), 39, 39, 39, 39);
		loadingBar = new NinePatch(atlas.findRegion("2"), 4, 4, 4, 4);
		loadingProgress = new NinePatch(atlas.findRegion("3"), 4, 4, 4, 4);

		loadingSel.setBottomHeight(SEL);
		loadingSel.setTopHeight(SEL);
		loadingSel.setLeftWidth(SEL);
		loadingSel.setRightWidth(SEL);

		am.load(game.menu.atlas_src, TextureAtlas.class);
		am.load(game.cameraScreen.atlas_src, TextureAtlas.class);	
		am.load(game.video.atlas_src, TextureAtlas.class);		
		am.load(game.playingScreen.atlas_src, TextureAtlas.class);

		this.progress = 0f;
	}


	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (am.update()){			

			font = am.get(font_src, BitmapFont.class);					
			font.setScale(2f);
			font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);	
			FileHandler.load();
			Assets.setUp();

			skin = am.get(skin_src, Skin.class);

			game.menu.create();
			game.cameraScreen.create();
			game.view.create();
			game.gallery.create();
			game.transitionScene.create();
			game.scenes.create();
			game.selectView.create();
			game.video.create();
			game.playingScreen.create();

			game.setScreen(game.menu);			
		} else {
			this.progress = am.getProgress();
		}
	}

	@Override
	public void draw() {
		sb.begin();
		loadingBar.draw(sb, xBar, yBar, wBar, hBar);
		loadingProgress.draw(sb, xBar, yBar, wBar*progress, hBar);
		sb.end();
	}

	public void dispose(){ 
		this.atlas.dispose(); 
	}

}
