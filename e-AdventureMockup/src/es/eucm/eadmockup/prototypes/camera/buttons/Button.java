/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import es.eucm.eadmockup.prototypes.camera.screens.BaseScreen;

/**This class implements the buttons*/
public class Button{
	
	private TextureRegion active, inactive, showingTexture;
	private Rectangle r;
	
	/**
	 * Constructor
	 * @param inact Texture of the button when pressed.
	 * @param act Default texture of the button.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @param w The width of the button.
	 * @param h The height of the button.
	 */
	public Button(TextureRegion inact, TextureRegion act, float x, float y, float w, float h){
		active = act;
        inactive = inact;
        r = new Rectangle(x,y,w,h);
        this.showingTexture = inactive;
	}
			
	public TextureRegion getTexture()	{
		return showingTexture;
	}
	
	/**
	 * Draw the button on the screen
	 * 
	 */
	public void draw(){
		BaseScreen.sb.draw(this.showingTexture, r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Change the texture if the button is touched.
	 * @param x
	 * @param y
	 */
	public void touch(float x, float y)	{
		if (r.contains(x, y)) {
			this.showingTexture = active;
		}
		else{
			this.showingTexture = inactive;
		}
	}
	
	/**
	 * Change the texture if the button is untouched.
	 * @param x
	 * @param y
	 * @return If the button is untouched
	 */
	public boolean untouch(float x, float y) {
		this.showingTexture = inactive;
		if (r.contains(x, y)) {
			return true;
		}
		return false;
	}

	public float getX() {
		return r.x;
	}


	public void setX(float x) {
		r.x = x;
	}


	public float getY() {
		return r.y;
	}

	public void setY(float y) {
		r.y = y;
	}


}
