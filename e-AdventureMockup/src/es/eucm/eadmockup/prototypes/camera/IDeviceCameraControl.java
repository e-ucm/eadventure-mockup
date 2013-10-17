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
public interface IDeviceCameraControl {

        // Synchronous interface
        void prepareCamera();

        void startPreview();

        void stopPreview();

        void takePicture();

        // Asynchronous interface - need when called from a non platform thread (GDX OpenGl thread)
        public void startPreviewAsync();

        public void stopPreviewAsync();

        public void takePictureAsync();

        public boolean isReady();

        public void prepareCameraAsync();

}