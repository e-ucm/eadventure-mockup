package es.eucm.eadmockup.prototypes.camera.common;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.eadmockup.prototypes.camera.screens.BaseScreen;


public class Thumbnail extends Actor {


	/** Textura usada por el alien. */
	private TextureRegion region;

	public Rectangle bb;

	private boolean selected;

	private String msg;

	private float msgH;

	private boolean clicked;
	
	private int pos;

	private int num;
	
	private int tablePos;
	
	private boolean picture;

	public Thumbnail(TextureRegion tr, float w, float h, boolean p, int tn) {
		region = tr;
		setSize(w, h);
		bb = new Rectangle(getX(), getY(), w, h);
		tr.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		picture=p;
		tablePos=tn;
	}

	/*  @Override
    public void act(float delta) {
            translate(-300 * delta, 0);
            bb.x = getX();
            bb.y = getY();
            bb.width = getWidth();
            bb.height = getHeight();
    }*/

	public Thumbnail(TextureRegion tr, int w, int h, int n, boolean p, int tn) {
		region = tr;
		setSize(w, h);
		bb = new Rectangle(getX(), getY(), w, h);
		this.num = n;
		this.picture=p;
		tablePos=tn;
	}

	public int getNum() {
		return num;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, parentAlpha);
		batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
				getWidth(), getHeight(), getScaleX(), getScaleY(),
				getRotation());
		if(selected){
			BaseScreen.font.draw(batch, msg, getX(), getY() + msgH);
		}
	}


	public boolean selectMe(String s){
		if(!clicked){
			selected = true;
			this.msg = s;
			this.msgH = BaseScreen.font.getBounds(msg).height;
			this.clicked = true;
			this.pos=Integer.valueOf(s);
			return true;
		}else {
			this.selected=false;
			this.msg = "";
			this.clicked=false;
		}
		return false;
	}
	
	public int getPos(){
		return this.pos;
	}

	public void clearSelect() {
		selected = false;
		clicked = false;
	}

	public TextureRegion getRegion() {
		return this.region;
	}
	
	public void decressS(){
		msg=String.valueOf(--pos);
	}
	
	public String toString()
	{
		return ("diapositiva "+num+" en pos "+pos);
	}
	
	public boolean isPicture(){
		return picture;
	}  
	
	public int getTablePos(){
		return tablePos;
	}
}
