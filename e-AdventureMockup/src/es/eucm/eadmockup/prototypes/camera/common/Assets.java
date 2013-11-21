package es.eucm.eadmockup.prototypes.camera.common;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;

public class Assets {


	private static AssetManager amExternal;
	
	
	public static void setUp(){
		amExternal = new AssetManager(new ExternalFileHandleResolver());
	}
	
	public static AssetManager getExternalAssetsManager(){
		return amExternal;
	}
	
	public static void dispose(){
		amExternal.dispose();
	}
}
