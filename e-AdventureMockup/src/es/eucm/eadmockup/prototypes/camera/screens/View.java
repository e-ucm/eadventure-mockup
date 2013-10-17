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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class View extends BaseScreen {

	private static final float TIMEOUT = 3f;
	private Array<Sprite> list;
	private boolean loading;
	private int cont;
	private float time;
	private AssetManager amExternal;

	@Override
	public void create() {
		this.list = new Array<Sprite>();
		amExternal = new AssetManager(new ExternalFileHandleResolver());
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		for(int i = list.size+1; i <= CameraScreen.ID; ++i){
			amExternal.load("prueba/Picture_"+i+".jpg", Texture.class);
		}
		this.loading = true;
		this.cont = 0;
		this.time = 0f;
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		gl.glClearColor(1f, 1f, 1f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(loading){
			if(amExternal.update()){
				for(int i = list.size+1; i <= CameraScreen.ID; ++i){
					Texture tr = amExternal.get("prueba/Picture_"+i+".jpg", Texture.class);
					Sprite s = new Sprite(tr);
					s.rotate90(true);
					s.setBounds(0, 0, screenw, screenh);
					list.add(s);
				}
				loading = false;
			} 			
		} else {
			time += delta;
			if(time > TIMEOUT){
				incrementCont();
				time = 0f;
			}
		}
	}

	private void incrementCont() {
		int size = list.size;
		if(size > 0){
			cont = (1+cont)%size;
		}
	}

	public void draw()
	{
		if(loading) {			
			font.draw(sb, String.valueOf(amExternal.getProgress()*100), 0, screenh);
		} else {
			if(list.size > 0){
				list.get(cont).draw(sb);
			}
		}
	}

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
			incrementCont();
			time = 0f;
		}
		return false;
	}
}
