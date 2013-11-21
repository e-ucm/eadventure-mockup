package es.eucm.eadmockup.prototypes.camera.common;

import com.badlogic.gdx.math.Rectangle;

public class Scene {

	private int id;
	private Scene nextScene;
	private Rectangle hitBox;
	
	public Scene(int id){
		this.id = id;
	}
	
	public void setHitBox(Rectangle rt){
		this.hitBox=rt;
	}
	
	public void setNextScene(Scene nextScene) {
		this.nextScene = nextScene;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public Scene getNextScene() {
		return nextScene;
	}

	public Rectangle getHitBox() {
		return hitBox;
	}

	public int getId() {
		return id;
	}

	public boolean haveExit()
	{
		return (nextScene!= null);
	}
}
