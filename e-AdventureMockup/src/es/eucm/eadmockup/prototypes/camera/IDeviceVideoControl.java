/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera;

/**
 *	Helper class for camera control in different platforms 
 */
public interface IDeviceVideoControl {

	 //VideoCamera
    public void startRecording();
    
    public void stopRecording();
    
    public void startPlaying();
    
    public void stopPlaying();
    
    public boolean isRecording();
	
	public boolean isPlaying();
	
	public void prepareVideoAsynk();
	
	public void stopPreviewAsynk();

}