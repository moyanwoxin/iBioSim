package analysis.dynamicsim.hierarchical.util.setup;

import org.sbml.jsbml.SpeciesReference;

import analysis.dynamicsim.hierarchical.states.ModelState;
import analysis.dynamicsim.hierarchical.util.math.ReactionNode;
import analysis.dynamicsim.hierarchical.util.math.SpeciesNode;
import analysis.dynamicsim.hierarchical.util.math.SpeciesReferenceNode;

public class SpeciesReferenceSetup
{

	public static void setupSingleModifier(ModelState modelstate, ReactionNode reactionNode, String modifierID)
	{
		// VariableNode node = modelstate.getNode(modifierID);
		// if (node != null)
		// {
		// reactionNode.addModifier(node);
		// }
	}

	public static void setupSingleProduct(ModelState modelstate, ReactionNode reaction, String productID, SpeciesReference product)
	{

		if (product.isSetId() && modelstate.isDeletedBySId(product.getId()))
		{
			return;
		}
		if (product.isSetMetaId() && modelstate.isDeletedByMetaId(product.getMetaId()))
		{
			return;
		}

		double stoichiometryValue = Double.isNaN(product.getStoichiometry()) ? 1 : product.getStoichiometry();

		SpeciesReferenceNode speciesReferenceNode = new SpeciesReferenceNode(stoichiometryValue);

		SpeciesNode species = (SpeciesNode) modelstate.getNode(productID);
		speciesReferenceNode.setSpecies(species);
		reaction.addProduct(speciesReferenceNode);

		if (!product.getConstant())
		{
			if (product.getId().length() > 0)
			{
				speciesReferenceNode.setName(product.getId());
				modelstate.addVariable(speciesReferenceNode);
				speciesReferenceNode.setIsVariableConstant(false);
			}
		}
		species.addODERate(reaction, speciesReferenceNode);

	}

	public static void setupSingleReactant(ModelState modelstate, ReactionNode reaction, String reactantID, SpeciesReference reactant)
	{

		double stoichiometryValue = Double.isNaN(reactant.getStoichiometry()) ? 1 : reactant.getStoichiometry();
		SpeciesReferenceNode speciesReferenceNode = new SpeciesReferenceNode(stoichiometryValue);
		SpeciesNode species = (SpeciesNode) modelstate.getNode(reactantID);
		speciesReferenceNode.setSpecies(species);
		reaction.addReactant(speciesReferenceNode);

		if (!reactant.getConstant())
		{
			if (reactant.getId().length() > 0)
			{
				speciesReferenceNode.setName(reactant.getId());
				modelstate.addVariable(speciesReferenceNode);
				speciesReferenceNode.setIsVariableConstant(false);
			}
		}
		species.subtractODERate(reaction, speciesReferenceNode);

	}

}