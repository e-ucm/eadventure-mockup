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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.eadmockup.prototypes.camera.Slideshow;
import es.eucm.eadmockup.prototypes.camera.common.Assets;
import es.eucm.eadmockup.prototypes.camera.common.FileHandler;
import es.eucm.eadmockup.prototypes.camera.postprocessing.PostProcessing;

public class View extends BaseScreen{

	private static final float TIMEOUT = 3f;
	private Array<Sprite> list;
	private boolean loading;
	private int cont;
	private float time;
	private AssetManager amExt;
	private PostProcessing post;

	@Override
	public void create() {
		amExt = Assets.getExternalAssetsManager();
		this.list = new Array<Sprite>();
		post = new PostProcessing();

		setUpRoot();

		post.setEnabled(false);


		//BLOOM---
		final CheckBox cbBloom =new CheckBox(" Bloom", skin);
		post.bloom.setEnabled(false);
		cbBloom.setChecked(post.bloom.isEnabled());
		cbBloom.addListener( new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				CheckBox source = (CheckBox)event.getListenerActor();
				post.bloom.setEnabled( source.isChecked() );
			}
		} );

		//CURVATURE---
		final CheckBox cbCurvature =new CheckBox("Curvature", skin);
		cbCurvature.setChecked(post.curvature.isEnabled());
		cbCurvature.addListener(new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				CheckBox source = (CheckBox)event.getListenerActor();
				post.curvature.setEnabled( source.isChecked() );

			}
		} );

		//CTR---
		final CheckBox cbCrt =new CheckBox("Old CRT emulation", skin);
		cbCrt.setChecked(post.crt.isEnabled());
		cbCrt.addListener(new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				CheckBox source = (CheckBox)event.getListenerActor();
				post.crt.setEnabled( source.isChecked() );
			}
		} );;

		//--VIGNETTE

		final Slider slVignetteI =  new Slider( 0, 1f, 0.01f,  false, skin);
		slVignetteI.setValue(post.vignette.getIntensity());
		slVignetteI.addListener(new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				Slider source = (Slider)event.getListenerActor();
				post.vignette.setIntensity( source.getValue() );
			}
		} );

		final SelectBox sbGradientMap = new SelectBox(new String[] { "Cross processing ", "Sunset ", "Mars",
				"Vivid ", "Greenland ", "Cloudy ", "Muddy " }, skin);

		sbGradientMap.addListener( new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				if( post.vignette.isGradientMappingEnabled() ) {
					SelectBox source = (SelectBox)event.getListenerActor();
					switch( source.getSelectionIndex() ) {
					case 0:
						post.vignette.setLutIndexVal( 0, 16 );
						break;
					case 1:
						post.vignette.setLutIndexVal( 0, 5 );
						break;
					case 2:
						post.vignette.setLutIndexVal( 0, 7 );
						break;
					case 3:
						post.vignette.setLutIndexVal( 0, 6 );
						break;
					case 4:
						post.vignette.setLutIndexVal( 0, 8 );
						break;
					case 5:
						post.vignette.setLutIndexVal( 0, 3 );
						break;
					case 6:
						post.vignette.setLutIndexVal( 0, 0 );
						break;
					}
				}
			}
		} );

		final Table tVignette = new Table();
		tVignette.setVisible(false);

		tVignette.top();
		tVignette.add(slVignetteI);
		tVignette.row();
		tVignette.add(sbGradientMap);	

		final Table tVig2 = new Table();
		tVig2.setVisible(false);

		final CheckBox cbGradientMapping =new CheckBox("Perform gradient mapping", skin);			
		tVig2.add(cbGradientMapping);

		final Texture grt = new Texture(Gdx.files.internal("data/gradient-mapping.png"));
		post.vignette.setEnabled(false);
		cbGradientMapping.setChecked(post.vignette.isGradientMappingEnabled());
		cbGradientMapping.addListener(new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				CheckBox source = (CheckBox)event.getListenerActor();
				if( source.isChecked() ) {
					post.vignette.setLutTexture(grt);
					sbGradientMap.fire( new ChangeListener.ChangeEvent() );
					tVignette.setVisible(true);
					//tVig2.setVisible(true);
				} else {
					post.vignette.setLutTexture( null );
					post.vignette.setLutIndexVal( 0, -1 );
					tVignette.setVisible(false);
					//tVig2.setVisible(false);
				}
			}
		} );



		final CheckBox cbVignette =new CheckBox("Vignette", skin);
		cbVignette.setChecked(post.vignette.isEnabled());
		cbVignette.addListener(new ClickListener(){
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				CheckBox source = (CheckBox)event.getListenerActor();
				boolean b =  source.isChecked();
				post.vignette.setEnabled(b);
				tVig2.setVisible(b);
				if(post.vignette.isGradientMappingEnabled()){
					tVignette.setVisible(b);
				}
			}
		} );

		//ZOOMER---

		final CheckBox cbZoomer =new CheckBox("Zoomer", skin);
		cbZoomer.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				CheckBox source = (CheckBox)event.getListenerActor();
				post.zoomer.setEnabled( source.isChecked() );
				if( post.isEnabled() ) {
					if( post.zoomer.isEnabled() ) {
						post.zoomAmount = 0;
						post.zoomFactor = 0;
					}
				}
			}
		});

		//Chackbox table
		Table t = new Table();
		t.setBackground(new NinePatchDrawable(Loading.loadingBar));

		t.add(cbBloom).left();
		t.row();
		t.add(cbCurvature).left();
		t.row();
		t.add(cbCrt).left();
		t.row();
		t.add(cbVignette).left();
		t.row();
		t.add(cbZoomer).left();
		t.row();
		final ScrollPane spt = new ScrollPane(t, skin);
		spt.setScrollingDisabled(true, false);


		spt.setVisible(false);

		//ENABLE POST
		final CheckBox cbPost =new CheckBox("Post-processing", skin);
		cbPost.setChecked( post.isEnabled());
		cbPost.addListener( new ClickListener() {
			@Override
			public void clicked( InputEvent event, float x, float y ) {
				CheckBox source = (CheckBox)event.getListenerActor();
				boolean b = source.isChecked() ;
				post.setEnabled( b );
				spt.setVisible(b);
				if(!b || (b && post.vignette.isEnabled())){
					tVignette.setVisible(b);
					tVig2.setVisible(b);
				}
			}
		} );


		//Root table;...

		root.pad(5f);
		root.add(tVig2).expand().left().bottom();
		root.add(cbPost);
		root.row();
		root.add(tVignette).expand().top();
		root.add(spt).expand().right();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		int max = FileHandler.getResourceID();
		for(int i = list.size+1; i <= max; ++i){
			amExt.load("Slideshow/HalfSized/HalfSized"+i+".jpg", Texture.class);
		}
		this.loading = true;
		root.setVisible( true );
		this.cont = 0;
		this.time = 0f;
		stage.addAction(sequence(moveTo(stage.getWidth(), 0),moveTo(0, 0, .25f)));
	}

	@Override
	public void hide() {

	}

	@Override
	public void onHidden() {
		root.setVisible( false );

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
				for(int i = list.size+1; i <= max; ++i){
					Texture tr = amExt.get("Slideshow/HalfSized/HalfSized"+i+".jpg", Texture.class);
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
		//Table.drawDebug(stage);
		post.update(delta);
	}

	private void incrementCont() {
		int size = list.size;
		if(size > 0){
			cont = (1+cont)%size;
		}
	}

	public void draw()
	{

		boolean willPostProcess = post.isReady();
		if(willPostProcess && (post.postProcessor.getEnabledEffectsCount() == 1)) {
			post.enableBlending();
		} else {
			post.disableBlending();
		}


		if(loading) {	
			sb.begin();
			font.draw(sb, String.valueOf(amExt.getProgress()*100), 0, screenh);
			sb.end();
		} else {
			post.begin();
			sb.begin();
			if(list.size > 0){
				list.get(cont).draw(sb);
			} else {
				sb.draw(Menu.title,  10, 90, 290, 290);
			}
			font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 10, screenh);
			sb.end();
			post.end();
		}	
		stage.draw();
	}

	@Override
	public void resume() {
		post.rebind();
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
		}
		return true;
	}

	public void dispose(){
		post.dispose();
	}

	@Override
	public boolean scrolled(int amount) {
		post.zoomAmount += amount * -1;
		post.zoomAmount = MathUtils.clamp( post.zoomAmount, 0, 15 );
		return true;
	}
}
