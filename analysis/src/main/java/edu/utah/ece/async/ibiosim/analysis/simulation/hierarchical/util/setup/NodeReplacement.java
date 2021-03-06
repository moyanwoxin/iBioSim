package edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.util.setup;

import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.model.HierarchicalModel;

/**
 * Pairs a replacing element to the element being replaced, as well as their corresponding models.
 *
 * @author Leandro Watanabe
 * @author <a href="http://www.async.ece.utah.edu/ibiosim#Credits"> iBioSim Contributors </a>
 * @version %I%
 */
class NodeReplacement {
	HierarchicalModel replacingModel;
	HierarchicalModel replacedModel;
	String replacingVariable;
	String replacedVariable;

	NodeReplacement (HierarchicalModel top, HierarchicalModel sub, String topVariable, String subVariable) {
		this.replacingModel = top;
		this.replacedModel = sub;
		this.replacingVariable = topVariable;
		this.replacedVariable = subVariable;
	}
}
