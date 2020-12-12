package at.uibk.dps.ee.core.enactable;

import java.util.Set;

import com.google.gson.JsonObject;

import at.uibk.dps.ee.core.ControlStateListener;
import at.uibk.dps.ee.core.exception.StopException;

/**
 * Interface for application components which can be enacted.
 * 
 * @author Fedor Smirnov
 */
public abstract class Enactable implements ControlStateListener{

	/**
	 * The enactment state of the enactable
	 * 
	 * @author Fedor Smirnov
	 *
	 */
	public enum State{
		/**
		 * Waiting for the provision of input data. Not enactable.
		 */
		WAITING,
		/**
		 * Input data present. Ready to start the enactment.
		 */
		READY,
		/**
		 * Running the underlying functionality.
		 */
		RUNNING,
		/**
		 * Paused by the enactment control.
		 */
		PAUSED,
		/**
		 * Stopped due to an internal Stop Exception
		 */
		STOPPED,
		/**
		 * Enactment finished, output data retrieved.
		 */
		FINISHED
	}
	
	protected State state = State.WAITING;
	
	protected final Set<EnactableStateListener> stateListeners;
	
	protected Enactable(final Set<EnactableStateListener> stateListeners) {
		this.stateListeners = stateListeners;
	}
	
	
	/**
	 * Triggers the execution from the current state of the enactable. This results
	 * in an execution and a change of states until (a) the execution is finished
	 * and the enactable returns the output data, (b) the execution is paused from
	 * outside, or (c) the execution is stopped due to an internal condition,
	 * throwing a {@link StopException}.
	 * 
	 * @return the output data
	 */
	public final JsonObject play() throws StopException{
		setState(State.RUNNING);
		try {
			JsonObject result = myPlay();
			setState(State.FINISHED);
			return result;
		}catch(StopException stopExc) {
			setState(State.STOPPED);
			throw stopExc;
		}
	}

	
	/**
	 * Method to define the class-specific play behavior.
	 * 
	 * @return the output data
	 * @throws StopException a stop exception.
	 */
	protected abstract JsonObject myPlay() throws StopException;
	
	
	/**
	 * Pauses the execution of the enactable by stopping the execution while
	 * preserving the inner state of the enactable.
	 */
	public final void pause() {
		myPause();
		setState(State.PAUSED);
	}
	
	public Set<EnactableStateListener> getStateListeners(){
		return stateListeners;
	}

	/**
	 * Method for the class-specific reaction to a pause request.
	 * 
	 */
	protected abstract void myPause();
	
	/**
	 * Sets the object into its initial state. Can also be used to reset the state.
	 * 
	 * @param input the data required for the execution
	 */
	public final void init(final JsonObject inputData) {
		myInit(inputData);
		setState(State.READY);
	}
	
	
	/**
	 * Method to provide the class-specific init details.
	 * 
	 * @param inputData the input data.
	 */
	protected abstract void myInit(JsonObject inputData);

	public State getState() {
		return state;
	}

	/**
	 * Sets the State of the enactable and notifies all state listeners.
	 * 
	 * @param state the new state
	 */
	public void setState(State state) {
		final State previous = this.state;
		final State current = state;
		for (EnactableStateListener stateListener : stateListeners) {
			stateListener.enactableStateChanged(this, previous, current);
		}
		this.state = state;
	}
	
	

}