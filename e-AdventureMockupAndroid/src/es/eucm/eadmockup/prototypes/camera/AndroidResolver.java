/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.eadmockup.prototypes.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import es.eucm.eadmockup.prototypes.camera.facade.IActionResolver;
import es.eucm.eadmockup.prototypes.camera.facade.IAnswerListener;

public class AndroidResolver implements IActionResolver {

	private CameraActivity activity;

	public AndroidResolver(CameraActivity activity){
		this.activity = activity;
	}
	
	@Override
	public void showDecisionBox(final int questionNumber, final String alertBoxTitle, final String alertBoxQuestion, 
			final String answerA, final String answerB, final IAnswerListener ql) {	
		
		activity.post(new Runnable() {
			public void run() {
				new AlertDialog.Builder(activity)
				.setTitle(alertBoxTitle)
				.setMessage(alertBoxQuestion)
				.setPositiveButton(answerA, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ql.onReceiveAnswer(questionNumber, IAnswerListener.ANSWER_A);
						dialog.cancel();
					}
				})
				.setNegativeButton(answerB, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ql.onReceiveAnswer(questionNumber, IAnswerListener.ANSWER_B);
						dialog.cancel();
					}
				})
				.setCancelable(false)
				.create()
				.show();
			}
		});
		
	}
}
