package at.uibk.dps.ee.core;

import at.uibk.dps.ee.core.enactable.Enactable;

/**
 * The {@link WorkflowProvider} is used to obtain the {@link Enactable} of
 * the overall workflow.
 * 
 * @author Fedor Smirnov
 */
public interface WorkflowProvider {

	/**
	 * Returns an enactable application.
	 * 
	 * @return the enactable application
	 */
	Enactable getEnactableApplication();
}