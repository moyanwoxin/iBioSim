package sbol;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.sbolstandard.libSBOLj.*;

import java.io.*;
import java.net.URI;
import java.util.*;

import main.Gui;

public class DnaComponentPanel extends JPanel implements MouseListener {

	private HashMap<String, DnaComponent> compMap;
	private HashMap<String, SequenceFeature> featMap;
	private JTextArea viewArea;
	private JList compList = new JList();
	
	public DnaComponentPanel(HashMap<String, DnaComponent> compMap, HashMap<String, SequenceFeature> featMap, JTextArea viewArea) {
		super(new BorderLayout());
		this.compMap = compMap;
		this.featMap = featMap;
		this.viewArea = viewArea;
		
		compList.addMouseListener(this);
		
		JLabel componentLabel = new JLabel("DNA Components:");
		
		JScrollPane componentScroll = new JScrollPane();
		componentScroll.setMinimumSize(new Dimension(260, 200));
		componentScroll.setPreferredSize(new Dimension(276, 132));
		componentScroll.setViewportView(compList);
		
		this.add(componentLabel, "North");
		this.add(componentScroll, "Center");
	}
	
	public void setComponents(Set<String> compIds) {
		Object[] idObjects = compIds.toArray();
		compList.setListData(idObjects);
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == compList) {
			viewArea.setText("");
			Object[] selected = compList.getSelectedValues();
			for (Object o : selected) {
				if (compMap.containsKey(o.toString())) {
					DnaComponent dnac = compMap.get(o.toString());
					viewArea.append("Name:  " + dnac.getName() + "\n");
					viewArea.append("Description:  " + dnac.getDescription() + "\n");
					viewArea.append("Annotations:  ");

					//Creation of to-be-sorted annotation array
					SequenceAnnotation[] sortedSA = new SequenceAnnotation[dnac.getAnnotations().size()];
					int n = 0;
					for (SequenceAnnotation sa : dnac.getAnnotations()) {
						sortedSA[n] = sa;
						n++;
					}
					//Insert sort of annotations by starting position
					for (int j = 1; j < sortedSA.length; j++) {
						SequenceAnnotation keyAnnotation = sortedSA[j];
						int key = keyAnnotation.getStart();
						int i = j - 1;
						while (i >= 0 && sortedSA[i].getStart() > key) {
							sortedSA[i + 1] = sortedSA[i];
							i = i - 1;
						}
						sortedSA[i + 1] = keyAnnotation;
					}
					//Processing sorted annotations and associated sequence features for display
					String annotations = "";
					for (int k = 0; k < sortedSA.length; k++) {
						for (SequenceFeature sf : sortedSA[k].getFeatures()) {
							annotations = annotations + sf.getDisplayId() + " + "; // once libSBOL up to speed iterate over DNA components
						}
						annotations = annotations.substring(0, annotations.length() - 2);
						String sign = sortedSA[k].getStrand();
						if (sign.equals("+"))
							sign = "";
						annotations = annotations + sign + sortedSA[k].getStart() + " to " + sign + sortedSA[k].getStop() + ", "; 
					}
					viewArea.append(annotations.substring(0, annotations.length() - 2) + "\n");
					viewArea.append("DNA Sequence:  " + dnac.getDnaSequence().getDnaSequence() + "\n\n");
				} else if (featMap.containsKey(o.toString())) {
					SequenceFeature sf = featMap.get(o.toString());
					viewArea.append("Name:  " + sf.getName() + "\n");
					viewArea.append("Description:  " + sf.getDescription() + "\n");
					viewArea.append("Types:  ");
					String types = "";
					for (URI uri : sf.getTypes()) {
						if (!uri.getFragment().equals("SequenceFeature"))
							types = types + uri.getFragment() + ", ";
					}
					viewArea.append(types.substring(0, types.length() - 2) + "\n");
					viewArea.append("DNA Sequence:  " + sf.getDnaSequence().getDnaSequence() + "\n\n");
				}
			}
		} 
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
