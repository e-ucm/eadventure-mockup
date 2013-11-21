package es.eucm.eadmockup.prototypes.camera.desktopimplementation.video;

import java.util.ArrayList;
import java.util.List;
import es.eucm.eadmockup.prototypes.camera.facade.IDeviceVideoControl;
import es.eucm.eadmockup.prototypes.camera.facade.IOnCompletionListener;

public class DesktopDeviceVideoController implements IDeviceVideoControl{

	@Override
	public void startRecording() {		
		System.out.println("startRecording()");
	}

	@Override
	public void stopRecording() {	
		System.out.println("stopRecording()");	
	}

	@Override
	public void startPlaying(int id) {
		System.out.println("startPlaying()");		
	}

	@Override
	public boolean isRecording() {
		System.out.println("isRecording()");
		return false;
	}

	@Override
	public boolean isPlaying() {
		System.out.println("isPlaying()");
		return false;
	}

	@Override
	public void prepareVideoAsynk() {
		System.out.println("prepareVideoAsynk()");		
	}

	@Override
	public void stopPreviewAsynk() {
		System.out.println("startRecording()");		
	}

	@Override
	public void setOnCompletionListener(IOnCompletionListener listener) {
		System.out.println("setOnCompletionListener()");	

	}

	@Override
	public List<String> getQualities() {
		List<String> qual = new ArrayList<String>();
		qual.add("480p");
		qual.add("720p");
		qual.add("1080p");
		return qual;
	}
}
