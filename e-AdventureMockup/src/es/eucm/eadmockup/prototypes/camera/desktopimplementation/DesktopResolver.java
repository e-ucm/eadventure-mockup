package es.eucm.eadmockup.prototypes.camera.desktopimplementation;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import es.eucm.eadmockup.prototypes.camera.facade.IActionResolver;
import es.eucm.eadmockup.prototypes.camera.facade.IAnswerListener;

public class DesktopResolver implements IActionResolver {

	@Override
	public void showDecisionBox(final int questionNumber, final String alertBoxTitle,
			final String alertBoxQuestion, final String answerA, final String answerB, final IAnswerListener ql) {
		if(questionNumber == IAnswerListener.QUESTION_EXIT){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run () {
					int result = JOptionPane.showConfirmDialog(null, alertBoxQuestion, alertBoxTitle, JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION){
						ql.onReceiveAnswer(questionNumber, IAnswerListener.ANSWER_A);
					} else if (result == JOptionPane.NO_OPTION){
						ql.onReceiveAnswer(questionNumber, IAnswerListener.ANSWER_B);
					} 	
				}
			});		
		}
	}
}
