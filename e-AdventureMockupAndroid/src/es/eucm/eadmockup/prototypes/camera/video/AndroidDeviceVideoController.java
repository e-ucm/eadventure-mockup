/**
 * Copyright 2012 Johnny Lish (johnnyoneeyed@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 *
 *  ************************************************************************
 * Modified by Antonio Calvo Morata & Dan Cristian Rotaru	
 * This file is a prototype for eAdventure Mockup
 * 
 */

package es.eucm.eadmockup.prototypes.camera.video;

import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import es.eucm.eadmockup.prototypes.camera.CameraActivity;
import es.eucm.eadmockup.prototypes.camera.facade.IDeviceVideoControl;
import es.eucm.eadmockup.prototypes.camera.facade.IOnCompletionListener;

public class AndroidDeviceVideoController implements IDeviceVideoControl{

	private final CameraActivity activity;
	private final VideoSurface videoSurface;
	private final MiVideoPlayer mPlayer;
	private final LayoutParams mLayoutParams;
	private final Runnable mPrepareVideoAsynkRunnable;
	private final Runnable mStopPreviewAsynkRunnable;
	
	private int videoID;
	private boolean playing;
	private final RelativeLayout previewLayout;

	public AndroidDeviceVideoController(CameraActivity activity) {
		this.previewLayout = new RelativeLayout(activity);
		this.activity = activity;	
		this.mPlayer = new MiVideoPlayer();
		this.videoSurface = new VideoSurface(activity);
		this.mLayoutParams = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ) ;
		this.mPrepareVideoAsynkRunnable = new Runnable(){
			@Override
			public void run() {
				prepareVideo();					
			}			
		};
		this.mStopPreviewAsynkRunnable = new Runnable(){
			@Override
			public void run() {
				stopRemoveViewFromParent();			
			}		
		};
		final RelativeLayout.LayoutParams videoParams = 
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
						RelativeLayout.LayoutParams.MATCH_PARENT);
		videoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		previewLayout.setGravity(Gravity.CENTER);
		previewLayout.addView(videoSurface, videoParams);
	}

	/*RECORDER*/
	@Override
	public void prepareVideoAsynk(){
		activity.post(mPrepareVideoAsynkRunnable);
	}

	@Override
	public void stopPreviewAsynk(){
		activity.post(mStopPreviewAsynkRunnable);
	}

	@Override
	public void startRecording() {
		videoSurface.startRecording();		
	}

	@Override
	public void stopRecording() {
		videoSurface.stopRecording();		
	}

	@Override
	public boolean isRecording() {
		return videoSurface.isRecording();
	}

	private synchronized void prepareVideo(){
		activity.addContentView( previewLayout, mLayoutParams );
	}

	private synchronized void stopRemoveViewFromParent() {
		// stop previewing. 
		ViewParent parentView = previewLayout.getParent();
		if (parentView instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) parentView;
			viewGroup.removeView(previewLayout);
		}
		if(isRecording()){
			stopRecording();
		}
		if(isPlaying()){
			mPlayer.stopAndRemooveView();
		}
	}

	/*PLAYER*/
	@Override
	public void startPlaying(int vidID) {
		videoID = vidID;
		playing = true;
		activity.post(mPlayer);
	}

	private class MiVideoPlayer implements Runnable{
		private final VideoView vv;
		private final RelativeLayout layout;
		private final String rootPath;
		private IOnCompletionListener mListener;
		
		public MiVideoPlayer(){
			this.vv = new VideoView(activity);
			this.layout = new RelativeLayout(activity);
			final RelativeLayout.LayoutParams videoParams = 
					new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
							RelativeLayout.LayoutParams.MATCH_PARENT);
			videoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			this.layout.addView(vv, videoParams);
			this.vv.setOnCompletionListener(new OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer arg0) {
					stopAndRemooveView();
					if(mListener != null){
						mListener.onCompletion();
					}
				}					
			});
			this.rootPath = "file://" + Environment.getExternalStorageDirectory() + "/Slideshow/Videos/Video";
		}

		@Override
		public void run() {
			this.vv.setVideoURI(Uri.parse(this.rootPath + videoID + ".mp4"));
			activity.addContentView(layout, mLayoutParams);
			this.vv.start();
		}
		
		private boolean isPlaying() {
			return this.vv.isPlaying();
		}
		
		private void stopAndRemooveView(){
			this.vv.stopPlayback();
			ViewParent parentView = layout.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(layout);
			}
			playing = false;	
		}
		
		private void setOnCompletionListener(IOnCompletionListener listener){
			this.mListener = listener;
		}
	}

	@Override
	public boolean isPlaying() {
		return this.playing || this.mPlayer.isPlaying();
	}

	@Override
	public void setOnCompletionListener(IOnCompletionListener listener) {
		this.mPlayer.setOnCompletionListener(listener);		
	}

	@Override
	public List<String> getQualities() {
		return videoSurface.getQualities();
	}
}