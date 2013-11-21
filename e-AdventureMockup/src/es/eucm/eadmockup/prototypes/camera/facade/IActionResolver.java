/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera.facade;

/**
 * Helper class that executes platform specific code.
 */
public interface IActionResolver {
	
	public void showDecisionBox(int questionNumber,
			String alertBoxTitle, String alertBoxQuestion,
			String answerA, String answerB, IAnswerListener ql);

}
