package gcm2sbml.visitor;

import java.io.PrintStream;
import java.util.Collection;

import org.sbml.libsbml.KineticLaw;
import org.sbml.libsbml.Parameter;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SpeciesReference;

import gcm2sbml.network.BaseSpecies;
import gcm2sbml.network.BiochemicalSpecies;
import gcm2sbml.network.ConstantSpecies;
import gcm2sbml.network.DimerSpecies;
import gcm2sbml.network.Promoter;
import gcm2sbml.network.SpasticSpecies;
import gcm2sbml.network.SpeciesInterface;

public class PrintActivatedProductionVisitor extends AbstractPrintVisitor {

	public PrintActivatedProductionVisitor(SBMLDocument document, Promoter p,
			Collection<SpeciesInterface> species, double act, double stoc) {
		super(document);
		this.act = act;
		this.promoter = p;
		this.species = species;
		this.stoc = stoc;
	}

	/**
	 * Prints out all the species to the file
	 * 
	 */
	public void run() {		
		for (SpeciesInterface specie : species) {
			specie.accept(this);
		}
	}

	public void visitSpecies(SpeciesInterface specie) {
		// TODO Auto-generated method stub

	}

	public void visitDimer(DimerSpecies specie) {
		Reaction r = new Reaction("R_act_production_"+promoter.getName() + "_" + specie.getName());
		r.addReactant(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		for (SpeciesInterface species : promoter.getOutputs()) {
			r.addProduct(new SpeciesReference(species.getName(), stoc));
		}
		r.addProduct(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		r.setReversible(false);
		r.setFast(false);
		KineticLaw kl = new KineticLaw();
		kl.addParameter(new Parameter("koc", act));
		kl.setFormula("act*" + "RNAP_" + promoter.getName()+ "_" + specie.getName());
		r.setKineticLaw(kl);
		document.getModel().addReaction(r);
		
	}

	public void visitBiochemical(BiochemicalSpecies specie) {
		Reaction r = new Reaction("R_act_production_"+promoter.getName() + "_" + specie.getName());
		r.addReactant(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		for (SpeciesInterface species : promoter.getOutputs()) {
			r.addProduct(new SpeciesReference(species.getName(), stoc));
		}
		r.addProduct(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		r.setReversible(false);
		r.setFast(false);
		KineticLaw kl = new KineticLaw();
		kl.addParameter(new Parameter("koc", act));
		kl.setFormula("act*" + "RNAP_" + promoter.getName()+ "_" + specie.getName());
		r.setKineticLaw(kl);
		document.getModel().addReaction(r);		
	}

	public void visitBaseSpecies(BaseSpecies specie) {
		Reaction r = new Reaction("R_act_production_"+promoter.getName() + "_" + specie.getName());
		r.addReactant(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		for (SpeciesInterface species : promoter.getOutputs()) {
			r.addProduct(new SpeciesReference(species.getName(), stoc));
		}
		r.addProduct(new SpeciesReference("RNAP_"+ specie.getName() + "_" + promoter.getName(), 1));
		r.setReversible(false);
		r.setFast(false);
		KineticLaw kl = new KineticLaw();
		kl.addParameter(new Parameter("koc", act));
		kl.setFormula("act*" + "RNAP_" + promoter.getName()+ "_" + specie.getName());
		r.setKineticLaw(kl);
		document.getModel().addReaction(r);
	}

	public void visitConstantSpecies(ConstantSpecies specie) {
		Reaction r = new Reaction("R_act_production_"+promoter.getName() + "_" + specie.getName());
		r.addReactant(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		for (SpeciesInterface species : promoter.getOutputs()) {
			r.addProduct(new SpeciesReference(species.getName(), stoc));
		}
		r.addProduct(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		r.setReversible(false);
		r.setFast(false);
		KineticLaw kl = new KineticLaw();
		kl.addParameter(new Parameter("koc", act));
		kl.setFormula("act*" + "RNAP_" + promoter.getName()+ "_" + specie.getName());
		r.setKineticLaw(kl);
		document.getModel().addReaction(r);
	}

	public void visitSpasticSpecies(SpasticSpecies specie) {
		Reaction r = new Reaction("R_act_production_"+promoter.getName() + "_" + specie.getName());
		r.addReactant(new SpeciesReference("RNAP_"+ "_" + promoter.getName()+ "_" + specie.getName(), 1));
		for (SpeciesInterface species : promoter.getOutputs()) {
			r.addProduct(new SpeciesReference(species.getName(), stoc));
		}
		r.addProduct(new SpeciesReference("RNAP_" + promoter.getName()+ "_" + specie.getName(), 1));
		r.setReversible(false);
		r.setFast(false);
		KineticLaw kl = new KineticLaw();
		kl.addParameter(new Parameter("koc", act));
		kl.setFormula("act*" + "RNAP_" + promoter.getName()+ "_" + specie.getName());
		r.setKineticLaw(kl);
		document.getModel().addReaction(r);
	}
	
	private Promoter promoter = null;
	
	private double act = .25;
	
	private double stoc = 1;
	
	private Collection<SpeciesInterface> species = null;
	

}
