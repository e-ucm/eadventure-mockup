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

import es.eucm.eadmockup.prototypes.camera.Slideshow;

/**
 * 
 * Helper class that handles the transition between screens.
 *
 */
public class TransitionScreen extends BaseScreen{

	public BaseScreen actualScreen, nextScreen;

	private float elapsedTime;
	private boolean backTrack;
	
	private final float HALF_TRANSITION_TIME = .25f;

	public TransitionScreen() {		
		actualScreen = game.showingScreen;
	}
	
	public BaseScreen initializer(BaseScreen nextScreen)
	{
		this.actualScreen.hide();		
		this.nextScreen = nextScreen;
		this.elapsedTime = HALF_TRANSITION_TIME;
		this.backTrack = false;
		return this;
	}	

	@Override
	public void render(float delta) 
	{	
		GLCommon gl = Gdx.gl;
		Color c = Slideshow.CLEAR_COLOR;
		gl.glClearColor(c.r, c.g, c.b, c.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(this.backTrack) 
		{	
			if(this.elapsedTime >= HALF_TRANSITION_TIME)
			{
				game.showingScreen = this.nextScreen;
			} else {
				this.elapsedTime = this.elapsedTime + delta;				
			}
		} 
		else
		{
			if(elapsedTime <= 0)
			{
				this.elapsedTime = 0;
				this.backTrack = true;
				this.actualScreen.onHidden();
				this.actualScreen = this.nextScreen;
				this.actualScreen.show();
			} else {
				this.elapsedTime = this.elapsedTime - delta;
			}
		}
	}	
	
	@Override
	public void draw() {
		this.actualScreen.draw(); 
	}
}
