/*******************************************************************************
 * Copyright 2012 bmanuel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package es.eucm.eadmockup.prototypes.camera.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Bloom;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.CrtMonitor;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Curvature;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Vignette;
import es.eucm.eadmockup.prototypes.camera.postprocessing.effects.Zoomer;
import es.eucm.eadmockup.prototypes.camera.postprocessing.filters.CrtScreen.RgbMode;
import es.eucm.eadmockup.prototypes.camera.postprocessing.filters.RadialBlur;

/**
 * Encapsulates postprocessing functionalities
 * 
 * @author bmanuel
 */
public final class PostProcessing implements Disposable, PostProcessorListener {

	public PostProcessor postProcessor;
	public Bloom bloom;
	public Curvature curvature;
	public Zoomer zoomer;
	public CrtMonitor crt;
	public Vignette vignette;
	public boolean zoomRadialBlur;
	public float zoomAmount, zoomFactor;
	private boolean blending;

	public PostProcessing() {
		int vpW = Gdx.graphics.getWidth()/2;
		int vpH = Gdx.graphics.getHeight()/2;
		blending = false;
		
		// create the postprocessor
		// ShaderLoader.Pedantic = false;
		postProcessor = new PostProcessor( false, false, false );

		// optionally create a listener
		postProcessor.setListener( this );
		PostProcessor.EnableQueryStates = false;

		// create the effects you want
		bloom = new Bloom( (vpW/4), (vpH/4) );
		curvature = new Curvature();
		zoomer = new Zoomer( vpW, vpH, RadialBlur.Quality.Low );
		crt = new CrtMonitor( vpW, vpH, false, false, RgbMode.ChromaticAberrations );
		vignette = new Vignette( vpW, vpH, false );

		// add them to the postprocessor
		postProcessor.addEffect( curvature );
		postProcessor.addEffect( zoomer );
		postProcessor.addEffect( vignette );
		postProcessor.addEffect( crt );
		postProcessor.addEffect( bloom );

		initializeEffects();
	}

	private void initializeEffects() {
		// specify a negative value to blur inside-to-outside,
		// so that to avoid artifacts at borders
		zoomer.setBlurStrength( -0.1f );
		zoomer.setOrigin( Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 );
		curvature.setZoom( 1f );
		vignette.setIntensity( 1f );

		crt.setEnabled( false );
		vignette.setEnabled( false );
		curvature.setEnabled( false );
		zoomer.setEnabled( false );
	}

	@Override
	public void dispose() {
		postProcessor.dispose();
	}

	public boolean begin() {
		return postProcessor.capture();
	}

	public void end() {
		postProcessor.render();
	}

	public void rebind() {
		postProcessor.rebind();
	}

	public boolean isEnabled() {
		return postProcessor.isEnabled();
	}

	public boolean isReady() {
		return postProcessor.isReady();
	}

	public void setEnabled( boolean enabled ) {
		postProcessor.setEnabled( enabled );
	}

	public void enableBlending() {
		bloom.enableBlending( GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR );
		blending = true;
	}

	public void disableBlending() {
		bloom.disableBlending();
		blending = false;
	}

	public void update( float elapsedSecs ) {
		// animate some effects
		float smoothing = 1.5f;
		zoomFactor = lerp( zoomFactor * smoothing, zoomAmount / smoothing, 0.1f ) * 0.5f;
		zoomer.setZoom( 1f + 4.0f * zoomFactor );
		if( zoomRadialBlur ) {
			zoomer.setBlurStrength( -0.1f * zoomFactor );
		}

		// this effect needs an external clock source to
		// emulate scanlines properly
		crt.setTime( elapsedSecs );
	}

	@Override
	public void beforeRenderToScreen() {
		if( blending ) {
			Gdx.gl20.glEnable( GL20.GL_BLEND );
			Gdx.gl20.glBlendFunc( GL20.GL_SRC_COLOR, GL20.GL_SRC_ALPHA );
		}
	}

	private static float lerp( float prev, float curr, float alpha ) {
		return prev + alpha * (curr - prev);
	}
}
