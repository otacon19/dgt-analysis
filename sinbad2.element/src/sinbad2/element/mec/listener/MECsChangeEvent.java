package sinbad2.element.mec.listener;


public class MECsChangeEvent {
	
	private EMECsChange _change;
	private Object _oldValue;
	private Object _newValue;
	private boolean _inUndoRedo;
	
	private MECsChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
		_inUndoRedo = false;
	}
	
	public MECsChangeEvent(EMECsChange change, Object oldValue, Object newValue, boolean inUndoRedo) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
		_inUndoRedo = inUndoRedo;
	}
	
	public EMECsChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}
	
	public boolean getInUndoRedo() {
		return _inUndoRedo;
	}
}

