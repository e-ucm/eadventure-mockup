/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.facade;

/**
 * Helper class for resolver's features.
 */
public interface IAnswerListener {

	public static final int QUESTION_EXIT = 1;
	
	public static final int ANSWER_A = 1;
	public static final int ANSWER_B = 2;
	
	public void onReceiveAnswer(int question, int answer);

}
