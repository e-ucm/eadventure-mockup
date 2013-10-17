/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Loading extends BaseScreen {

	private NinePatch loadingBar, loadingProgress;
	private TextureAtlas atlas;
	private float xBar, yBar, wBar, hBar, progress;

	private final String font_src = "data/font/impact38bold.fnt";

	@Override
	public void create() {

		am.load(font_src, BitmapFont.class);

		this.wBar = halfscreenw * 1.5f;
		this.hBar = halfscreenw / 7f;
		this.xBar = halfscreenw - this.wBar/2f;
		this.yBar = halfscreenh/2f - hBar/2f;

		this.atlas = new TextureAtlas("data/ninepatch/ninepatch.atlas");

		this.loadingBar = new NinePatch(atlas.findRegion("2"), 4, 4, 4, 4);
		this.loadingProgress = new NinePatch(atlas.findRegion("3"), 4, 4, 4, 4);

		am.load(game.menu.atlas_src, TextureAtlas.class);
		am.load(game.cameraState.atlas_src, TextureAtlas.class);		
		
		this.progress = 0f;
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		gl.glClearColor(1f, 1f, 1f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (am.update()){			
			if (font == null){
				font = am.get(font_src, BitmapFont.class);					
				font.scale(1.5f);
				font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);		
			}
			game.menu.create();
			game.cameraState.create();
			game.view.create();

			game.setScreen(game.menu);			
		} else {
			this.progress = am.getProgress();
		}
	}

	@Override
	public void draw() {
		loadingBar.draw(sb, xBar, yBar, wBar, hBar);
		loadingProgress.draw(sb, xBar, yBar, wBar*progress, hBar);
	}

	public void dispose(){ 
		this.atlas.dispose(); 
	}

}
