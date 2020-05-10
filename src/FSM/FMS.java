package FSM;

public interface FMS {
	public final int STATE_FAILURE = -1;
	
	//Given a current state, and a byte, returns the next state
	public int yy_next(int state, byte c);
	
	//Returns whether the current state is an accepted/final state or not
	public boolean isAcceptState(int state);
	
}
