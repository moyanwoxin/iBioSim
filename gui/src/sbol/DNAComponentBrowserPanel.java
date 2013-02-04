package sbol;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.sbolstandard.core.*;
import org.sbolstandard.core.impl.AggregatingResolver.UseFirstFound;

import java.net.URI;
import java.util.*;

public class DNAComponentBrowserPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private LinkedList<URI> compURIs;
	private LinkedList<String> compIDs;
//	private HashMap<String, DnaComponent> compMap;
//	private HashMap<String, SequenceAnnotation> annoMap;
//	private HashMap<String, DnaSequence> seqMap;
	private UseFirstFound<DnaComponent, URI> aggregateCompResolver;
	private UseFirstFound<SequenceAnnotation, URI> aggregateAnnoResolver;
	private UseFirstFound<DnaSequence, URI> aggregateSeqResolver;
	
	private JTextArea viewArea;
	private JList compList = new JList();
	
	public DNAComponentBrowserPanel(UseFirstFound<DnaComponent, URI> aggregateCompResolver, UseFirstFound<SequenceAnnotation, URI> aggregateAnnoResolver, 
			UseFirstFound<DnaSequence, URI> aggregateSeqResolver, JTextArea viewArea) {
		super(new BorderLayout());
		this.aggregateCompResolver = aggregateCompResolver;
		this.aggregateAnnoResolver = aggregateAnnoResolver;
		this.aggregateSeqResolver = aggregateSeqResolver;
		this.viewArea = viewArea;
		
		compList.addMouseListener(this);
		
		JLabel componentLabel = new JLabel("DNA Components:");
		
		JScrollPane componentScroll = new JScrollPane();
		componentScroll.setMinimumSize(new Dimension(260, 200));
		componentScroll.setPreferredSize(new Dimension(276, 132));
//		componentScroll.setMinimumSize(new Dimension(276, 50));
//		componentScroll.setPreferredSize(new Dimension(276, 50));
		componentScroll.setViewportView(compList);
		
		this.add(componentLabel, "North");
		this.add(componentScroll, "Center");
	}
	
	public void setComponents(LinkedList<String> compIDs, LinkedList<URI> compURIs) {
		this.compURIs = compURIs;
		this.compIDs = compIDs;
		Object[] idObjects = compIDs.toArray();
		compList.setListData(idObjects);
	}
	
	public void filterComponents(String filterType) {
		viewArea.setText("");
		LinkedList<URI> filteredURIs = new LinkedList<URI>();
		LinkedList<String> filteredIDs = new LinkedList<String>();
		for (int i = 0; i < compURIs.size(); i++) {
			DnaComponent dnac = aggregateCompResolver.resolve(compURIs.get(i));
			if (filterType.equals("all") || 
					SBOLUtility.convertURIToSOType(dnac.getTypes().iterator().next()).equals(filterType)) {
				filteredURIs.add(compURIs.get(i));
				filteredIDs.add(compIDs.get(i));
			}
		}
		setComponents(filteredIDs, filteredURIs);
	}
	
	public LinkedList<URI> getSelectedURIs() {
		LinkedList<URI> selectedURIs = new LinkedList<URI>();
		int[] selectedIndices = compList.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++)
			selectedURIs.add(compURIs.get(selectedIndices[i]));
		return selectedURIs;
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == compList) {
			viewArea.setText("");
			LinkedList<URI> selectedURIs = getSelectedURIs();
			for (URI compURI : selectedURIs) {
				DnaComponent dnac = aggregateCompResolver.resolve(compURI);
				if (dnac != null) {
//					DnaComponent dnac = compMap.get(compURI);
					
					viewArea.append("Display ID:  " + dnac.getDisplayId() + "\n");
					
					if (dnac.getName() != null && !dnac.getName().equals(""))
						viewArea.append("Name:  " + dnac.getName() + "\n");
					else
						viewArea.append("Name:  NA\n");
					
					if (dnac.getDescription() != null && !dnac.getDescription().equals(""))
						viewArea.append("Description:  " + dnac.getDescription() + "\n");
					else 
						viewArea.append("Description:  NA\n");
					
					LinkedList<SequenceAnnotation> unsortedSA = new LinkedList<SequenceAnnotation>();
					if (dnac.getAnnotations() != null) {
						for (SequenceAnnotation sa : dnac.getAnnotations()) {
							SequenceAnnotation resolvedSA = aggregateAnnoResolver.resolve(sa.getURI());
							if (resolvedSA != null)
//								unsortedSA.add(annoMap.get(sa.getURI().toString()));
								unsortedSA.add(resolvedSA);
							else
								unsortedSA.add(sa);
						}
					}
					if (unsortedSA.size() > 0) {
						SequenceAnnotation[] sortedSA = sortAnnotations(unsortedSA);
						String annotations = processAnnotations(sortedSA);
						viewArea.append("Annotations:  ");
						viewArea.append(annotations + "\n");
					} else 
						viewArea.append("Annotations:  NA\n");
					
					viewArea.append("Types:  ");
					String types = "";
					for (URI uri : dnac.getTypes()) 
						types = types + SBOLUtility.convertURIToSOType(uri) + ", ";
					if (types.length() > 0)
						viewArea.append(types.substring(0, types.length() - 2) + "\n");
					else
						viewArea.append("NA\n");
					DnaSequence seq = dnac.getDnaSequence();
					if (seq != null) {
						DnaSequence resolvedSeq = aggregateSeqResolver.resolve(seq.getURI());
						if (resolvedSeq != null)
							viewArea.append("DNA Sequence:  " + resolvedSeq.getNucleotides() + "\n\n");
						else 
							viewArea.append("DNA Sequence:  " + seq.getNucleotides() + "\n\n");
					} else
						viewArea.append("DNA Sequence:  NA\n\n");
				}
			}
		} 
	}
	
	private SequenceAnnotation[] sortAnnotations(LinkedList<SequenceAnnotation> unsortedSA) {
		SequenceAnnotation[] sortedSA = new SequenceAnnotation[unsortedSA.size()];
		int n = 0;
		for (SequenceAnnotation sa : unsortedSA) {
			sortedSA[n] = sa;
			n++;
		}
		//Insert sort of annotations by starting position
		for (int j = 1; j < sortedSA.length; j++) {
			SequenceAnnotation keyAnnotation = sortedSA[j];
			int key = keyAnnotation.getBioStart();
			int i = j - 1;
			while (i >= 0 && sortedSA[i].getBioStart() > key) {
				sortedSA[i + 1] = sortedSA[i];
				i = i - 1;
			}
			sortedSA[i + 1] = keyAnnotation;
		}
		return sortedSA;
	}
	
	private String processAnnotations(SequenceAnnotation[] arraySA) {
		String annotations = "";
		for (int k = 0; k < arraySA.length; k++) {
			DnaComponent subComponent = arraySA[k].getSubComponent();
			if (subComponent != null) {
				DnaComponent resolvedSubComponent = aggregateCompResolver.resolve(subComponent.getURI());
				if (resolvedSubComponent != null)
					annotations = annotations + resolvedSubComponent.getDisplayId();
				else
					annotations = annotations + subComponent.getDisplayId();
			} else
				annotations = annotations + "NA"; 
			String symbol = arraySA[k].getStrand().getSymbol();
			annotations = annotations + " " + symbol + arraySA[k].getBioStart() + " to " + symbol + arraySA[k].getBioEnd() + ", "; 
			
		}
		annotations = annotations.substring(0, annotations.length() - 2);
		return annotations;
	}
	
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
