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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import es.eucm.eadmockup.prototypes.camera.postprocessing.PostProcessor;
import es.eucm.eadmockup.prototypes.camera.postprocessing.PostProcessorEffect;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Bloom;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.CrtMonitor;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Curvature;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Vignette;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Zoomer;
import es.eucm.eadmockup.prototypes.camera.postprocessing.filters.RadialBlur;
import es.eucm.eadmockup.prototypes.camera.postprocessing.filters.CrtScreen.RgbMode;

import es.eucm.eadmockup.prototypes.camera.Slideshow;

public class View extends BaseScreen implements Disposable{

	private static final float TIMEOUT = 3f;
	private Array<Sprite> list;
	private boolean loading;
	private int cont;
	private float time;
	private AssetManager amExternal;
	private PostProcessor postProcessor;
	private boolean postProcessing;
	private Array<PostProcessorEffect> effects;
	private int effect;
	private String effectName;
	private float effectY;
	private Vignette vi;
	private SelectBox selectb;
	private Window window;

	@Override
	public void create() {
		this.list = new Array<Sprite>();
		amExternal = new AssetManager(new ExternalFileHandleResolver());
		postProcessor = new PostProcessor(false, false, false /*Gdx.app.getType() == Application.ApplicationType.Desktop*/);

		effects = new Array<PostProcessorEffect>();

		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		Bloom bloom = new Bloom(w/4, h/4);
		CrtMonitor crt = new CrtMonitor(w, h, false, false, RgbMode.ChromaticAberrations);
		Curvature cu = new Curvature();
		vi = new Vignette(w, h, false);
		Zoomer z = new Zoomer(w, h, RadialBlur.Quality.Low);

		z.setBlurStrength( -0.1f );
		z.setOrigin( Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 );
		cu.setZoom( 1f );
		vi.setIntensity( 1f );
		vi.setLutTexture( new Texture( Gdx.files.internal("data/gradient-mapping.png") ) );

		effects.add(bloom);
		effects.add(crt);
		effects.add(cu);
		effects.add(vi);
		effects.add(z);

		for(PostProcessorEffect ppe : effects){
			ppe.setEnabled(false);
			postProcessor.addEffect(ppe);
		}

		postProcessor.setClearColor(Slideshow.CLEAR_COLOR);

		// UI API        
		

		selectb = new SelectBox(new String[] { "Cross processing ", "Sunset ", "Mars",
				"Vivid ", "Greenland ", "Cloudy ", "Muddy ", "HOLA" }, skin);
		
		selectb.addListener(new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				if( vi.isGradientMappingEnabled() ) {
					SelectBox source = (SelectBox)event.getListenerActor();
					switch( source.getSelectionIndex() ) {
					case 0:
						vi.setLutIndexVal( 0, 16 );
						break;
					case 1:
						vi.setLutIndexVal( 0, 5 );
						break;
					case 2:
						vi.setLutIndexVal( 0, 7 );
						break;
					case 3:
						vi.setLutIndexVal( 0, 6 );
						break;
					case 4:
						vi.setLutIndexVal( 0, 8 );
						break;
					case 5:
						vi.setLutIndexVal( 0, 3 );
						break;
					case 6:
						vi.setLutIndexVal( 0, 0 );
						break;
					}
				}
			}
		} );
		selectb.setColor(Color.CYAN);
		selectb.setVisible(false);

		setUpRoot();
		
		root.add(selectb).expand().top().right().pad(10f);

		this.effectY = font.getLineHeight() + font.getDescent();
		
		////
		TextButton btn0 = new TextButton("0", skin);
		btn0.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				vi.setLutIntensity(1);
			}
		});
		
		TextButton btn1 = new TextButton("1", skin);
		btn1.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				vi.setLutIntensity(5);
			}
		});
		
		TextButton btn2 = new TextButton("2", skin);
		btn2.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				vi.setLutIntensity(10);
			}
		});
		
		TextButton btn3 = new TextButton("3", skin);
		btn3.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				vi.setLutIntensity(15);
			}
		});
		
		TextButton btn4 = new TextButton("4", skin);
		btn4.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				vi.setLutIntensity(20);
			}
		});
		
		///
		
		window = new Window("EFECTOS", skin);
		window.row();
		window.add("Efectos").center();
		window.row();

		//window.defaults().spaceBottom(1).spaceLeft(1).size(40, 20);
		//window.row().fill().expandX();
		
		window.add("Intensidades").center();
		window.row();
		window.add(btn0);
		window.add(btn1);
		window.add(btn2);
		window.add(btn3);
		window.add(btn4);
		
		/*
		window.row();
		window.add(new TextButton("6", skin));
		window.add(new TextButton("7", skin)).minWidth(100).fillX().colspan(3);
		window.row();
		window.add(new TextButton("8", skin));
		window.add(new TextButton("9", skin)).minWidth(100).expandX().fillX().colspan(3);
		window.row();
		window.add(new TextButton("10", skin)).fill().expand().colspan(4).maxHeight(200);
		window.row();
		window.add(new TextButton("11", skin)).colspan(2);
		window.add(new TextButton("12", skin)).minWidth(100).expandX().fillX().colspan(2);
		window.row();
		*/
		
		window.pack();
		
		window.addListener(new InputListener(){
			
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Example", "touch started at (" + x + ", " + y + ")");
                return true;
			}
			
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Example", "touch done at (" + x + ", " + y + ")");
			}
		});
		
		
		root.addActor(window);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		for(int i = list.size+1; i <= CameraScreen.ID; ++i){
			amExternal.load("prueba/Picture_"+i+".jpg", Texture.class);
		}
		this.loading = true;
		this.cont = 0;
		this.time = 0f;
		this.effect = -1;
		this.effectName = "Ninguno";

		root.setVisible(true);
	}
	
	@Override
	public void hide() {
		//animaciones de salida
	}
	
	@Override
	public void onHidden() {
		root.setVisible(false);
	}

	@Override
	public void render(float delta) {
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(loading){
			if(amExternal.update()){
				for(int i = list.size+1; i <= CameraScreen.ID; ++i){
					Texture tr = amExternal.get("prueba/Picture_"+i+".jpg", Texture.class);
					Sprite s = new Sprite(tr);
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
		stage.act(delta);
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
			sb.begin();
			font.draw(sb, String.valueOf(amExternal.getProgress()*100), 0, screenh);
			sb.end();
		} else {
			if(postProcessing){
				postProcessor.capture();
			}
			sb.begin();
			if(list.size > 0){
				list.get(cont).draw(sb);
			} else {
				sb.draw(Menu.background,  0, 0, screenw, screenh);
				sb.draw(Menu.gallery,  10, 90, 290, 290);
			}
			font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, screenh);
			font.draw(sb, effectName, 10, effectY);
			sb.end();

			if(postProcessing){
				postProcessor.render();
			}
		}		
		stage.draw();
	}

	@Override
	public void resume() {
		postProcessor.rebind();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			game.setScreen(game.menu);
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			incrementCont();
			time = 0f;
			postProcessing = true;
			PostProcessorEffect ppe = null;
			if(effect != -1){
				ppe = effects.get(effect);
				ppe.setEnabled(false);
			}
			int nextEff = MathUtils.random(0, effects.size-1);
			if(nextEff == effect){
				effect = (nextEff+1)%effects.size;
			} else {
				effect = nextEff;
			}

			ppe = effects.get(effect);
			ppe.setEnabled(true);
			effectName = effect + " " + ppe.getClass().getSimpleName();

			if(effect == 3){
				selectb.setVisible(true);
			} else {
				selectb.setVisible(false);
			}

		}
		return true;
	}

	public void dispose() {
		postProcessor.dispose();
		amExternal.dispose();
	}
}
