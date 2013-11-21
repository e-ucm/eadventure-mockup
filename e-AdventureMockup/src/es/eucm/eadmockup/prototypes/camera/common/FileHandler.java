package es.eucm.eadmockup.prototypes.camera.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHandler {

	private static final String rootDir = "Slideshow";
	private static final String docName = "data.dat";
	private static final String origianlsDirName = "Originals";
	private static final String halfsDirName = "HalfSized";
	private static final String thumbnailsDirName = "Thumbnails";
	private static final String videoThumbnailsDirName = "VideoThumbnails";
	private static final String videosDirName = "Videos";
	private static final String FILE_SEPARATOR = "/";
	private static final String STRING_SEPARATOR = "_";
	
	private static int resourceID;
	private static int videoID;
	private static FileHandle fhData, fhOriginals, fhHalfs, fhThumbs, fhVideos, fhVideoThumbs;
	private static boolean somethingChanged;

	public static void load(){
		somethingChanged = false;
		try {	
			
			String rootPath = FILE_SEPARATOR + rootDir + FILE_SEPARATOR;
			
			fhOriginals =  Gdx.files.external(rootPath + origianlsDirName);
			if(!fhOriginals.exists()){
				fhOriginals.mkdirs();
			} 
			
			fhHalfs =  Gdx.files.external(rootPath + halfsDirName);
			if(!fhHalfs.exists()){
				fhHalfs.mkdirs();
			}
			
			fhThumbs =  Gdx.files.external(rootPath + thumbnailsDirName);
			if(!fhThumbs.exists()){
				fhThumbs.mkdirs();
			}
			
			String videosPath = rootPath + videosDirName;
			fhVideos =  Gdx.files.external(videosPath);
			if(!fhVideos.exists()){
				fhVideos.mkdirs();
			}
			
			fhVideoThumbs =  Gdx.files.external(videosPath + FILE_SEPARATOR + videoThumbnailsDirName);
			if(!fhVideoThumbs.exists()){
				fhVideoThumbs.mkdirs();
			}
			
			FileHandle fhRoot =  Gdx.files.external(rootPath);
			fhData =  Gdx.files.external(rootPath + docName);
			if(!fhRoot.exists()){
				fhRoot.mkdirs();
				resourceID = 0;
				videoID = 0;
			} else if(fhData.exists()){
				
				try {
					String[] s = fhData.readString().split(STRING_SEPARATOR);
					resourceID = Integer.valueOf(s[0]);
					videoID = Integer.valueOf(s[1]);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					resourceID = 0;	
					videoID = 0;	
				}
			}
		} catch (Exception e) { 
			e.printStackTrace(System.out);			
		} 
	}

	public static void save(){
		if(somethingChanged){
			try {
				
				fhData.writeString(resourceID + STRING_SEPARATOR + videoID, false);			
				
			} catch (Exception e) {
				e.printStackTrace(System.out);	
			}
			somethingChanged = false;
		}
	}

	public static int getResourceID(){
		return resourceID;
	}
	
	public static void incrementResourceID(){
		++resourceID;
		somethingChanged = true;
	}

	public static FileHandle getOriginalsFileHandle() {
		return fhOriginals;		
	}
	
	public static FileHandle getHalfSizedFileHandle() {
		return fhHalfs;		
	}
	
	public static FileHandle getThumbnailsFileHandle() {
		return fhThumbs;		
	}
	
	public static FileHandle getVideoThumbnailsFileHandle() {
		return fhVideoThumbs;		
	}

	public static int getVideoID() {
		return videoID;
	}
	
	public static void incrementVideoID(){
		++videoID;
		somethingChanged = true;
	}
}
