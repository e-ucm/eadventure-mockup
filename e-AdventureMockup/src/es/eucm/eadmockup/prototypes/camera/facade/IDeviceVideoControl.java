/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.facade;

import java.util.List;

/**
 *	Helper class for camera control in different platforms 
 */
public interface IDeviceVideoControl {
	/*RECORDER*/
	public void prepareVideoAsynk();
	
	public void stopPreviewAsynk();
	
    public void startRecording();
    
    public void stopRecording();

    public boolean isRecording();
	
    /*PLAYER*/
    public void startPlaying(int videoID);
    
	public boolean isPlaying();
	
	public void setOnCompletionListener(IOnCompletionListener listener);
	
	public List<String> getQualities();

}