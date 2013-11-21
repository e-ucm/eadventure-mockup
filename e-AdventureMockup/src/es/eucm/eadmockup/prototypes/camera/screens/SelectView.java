/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.facade.IDeviceVideoControl;
import es.eucm.eadmockup.prototypes.camera.facade.IOnCompletionListener;

public class SelectView extends BaseScreen implements IOnCompletionListener{

	private static final float TIMEOUT = 3f;
	private Array<Sprite> list;
	private boolean loading;
	private int cont;
	private boolean contPicture;
	private float time;
	private AssetManager amExt;
	private IDeviceVideoControl videoControl;
	private boolean playing;
	private float timeX, timeY;

	public SelectView(IDeviceVideoControl cameraControl){
		this.videoControl = cameraControl;
	}
	
	@Override
	public void create() {
		amExt = Assets.getExternalAssetsManager();
		this.list = new Array<Sprite>();
		timeX = screenw - font.getBounds("0.1").width;
		timeY = font.getBounds("0").height;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		int max = FileHandler.getResourceID();
		for(int i = list.size+1; i <= max; ++i){
			amExt.load("Slideshow/HalfSized/HalfSized"+i+".jpg", Texture.class);
		}
		this.loading = true;
		this.contPicture = Gallery.nextIsPicture();
		this.cont = Gallery.getNext();
		playing = false;
	
		this.time = 0f;
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
		videoControl.setOnCompletionListener(this);
	}
	
	@Override
	public void onHidden() {
		if(playing){
			videoControl.stopPreviewAsynk();
		}
	}

	@Override
	public void render(float delta) {
		if(loading){
			if(amExt.update()){
				int max = FileHandler.getResourceID();
				for(int i = list.size+1; i <= max; ++i){
					Texture tr = amExt.get("Slideshow/HalfSized/HalfSized"+i+".jpg", Texture.class);
					Sprite s = new Sprite(tr);
					s.setBounds(0, 0, screenw, screenh);
					list.add(s);
				}
				loading = false;
			} 			
		} else if(!playing) {
			time += delta;
			if(time > TIMEOUT){
				setUpNextPicture();
			}
		}
		stage.act(delta);
	}
	
	private void setUpNextPicture(){
		this.contPicture = Gallery.nextIsPicture();
		cont=Gallery.getNext();
		time = 0f;
		
	}

	public void draw()
	{
		if(loading) {	
			Color c = Slideshow.CLEAR_COLOR;
			clearColor(c.r, c.g, c.b, c.a);
			sb.begin();
			font.draw(sb, String.valueOf(amExt.getProgress()*100), 0, screenh);
			sb.end();
		} else {

			if(contPicture && list.size > 0){
				Color c = Slideshow.CLEAR_COLOR;
				clearColor(c.r, c.g, c.b, c.a);
				sb.begin();
				list.get(cont).draw(sb);
				font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, screenh);
				font.draw(sb, String.valueOf(TIMEOUT-time), timeX, timeY);
				sb.end();
			} else if(!playing){

				clearColor(0f, 0f, 0f, 0f);
				playing=true;
				System.out.println("playing true><><><><><><><><><><><><><><><"+(cont));
				//sb.draw(Menu.title,  10, 90, 290, 290);
				videoControl.startPlaying(cont);
			} else if(playing){
				clearColor(0f, 0f, 0f, 0f);
			}
		}		
	}

	private void clearColor(float r, float g, float b, float a){
		GLCommon gl = Gdx.gl;
		gl.glClearColor(r, g, b, a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	@Override
	public void pause() {
		if(playing){
			videoControl.stopPreviewAsynk();
		}
	}
	
	@Override
	public void resume() {
		if(playing){
			onCompletion();
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			game.setScreen(game.gallery);
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			if(!playing){
				setUpNextPicture();
			} else {
				videoControl.stopPreviewAsynk();
				onCompletion();
			}
		}
		return true;
	}

	@Override
	public void onCompletion() {
		setUpNextPicture();
		playing=false;		
		
	}
}
