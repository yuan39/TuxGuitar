/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.track.UndoableAddTrack;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddTrackAction extends Action{
	
	public static final String NAME = "action.track.add";
	
	public AddTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		//comienza el undoable
		UndoableAddTrack undoable = UndoableAddTrack.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		Caret caret = getEditor().getTablature().getCaret();
		
		TGTrack track = getSongManager().addTrack();
		updateTablature();
		caret.update(track.getNumber(),caret.getPosition(),1);
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo(track));
		
		return 0;
	}
}
