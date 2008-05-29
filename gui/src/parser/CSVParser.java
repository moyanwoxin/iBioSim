package parser;

import java.io.*;
import java.util.*;

import javax.swing.*;
import biomodelsim.*;

public class CSVParser extends Parser {
	public CSVParser(String filename, BioSim biosim) {
		super(new ArrayList<String>(), new ArrayList<ArrayList<Double>>(), biosim);
		try {
			boolean warning = false;
			FileInputStream fileInput = new FileInputStream(new File(filename));
			ProgressMonitorInputStream prog = new ProgressMonitorInputStream(component,
					"Reading Reb2sac Output Data From " + new File(filename).getName(), fileInput);
			InputStream input = new BufferedInputStream(prog);
			boolean reading = true;
			char cha;
			while (reading) {
				String word = "";
				boolean readWord = true;
				boolean moveToData = false;
				while (readWord && !moveToData) {
					int read = input.read();
					if (read == -1) {
						reading = false;
						readWord = false;
					}
					cha = (char) read;
					if (cha == '\n') {
						moveToData = true;
					}
					else if (cha == ',') {
						readWord = false;
					}
					else if (cha == ' ' && word.equals("")) {
					}
					else {
						word += cha;
					}
				}
				if (!word.equals("")) {
					species.add(word);
				}
				if (moveToData) {
					for (int i = 0; i < species.size(); i++) {
						data.add(new ArrayList<Double>());
					}
				}
				int counter = 0;
				while (moveToData) {
					word = "";
					readWord = true;
					int read;
					while (readWord) {
						read = input.read();
						cha = (char) read;
						while (!Character.isWhitespace(cha) && cha != ',' && cha != ':' && cha != ';'
								&& cha != '!' && cha != '?' && cha != '\"' && cha != '\'' && cha != '('
								&& cha != ')' && cha != '{' && cha != '}' && cha != '[' && cha != ']' && cha != '<'
								&& cha != '>' && cha != '_' && cha != '*' && cha != '=' && read != -1) {
							word += cha;
							read = input.read();
							cha = (char) read;
						}
						if (read == -1) {
							reading = false;
							moveToData = false;
						}
						readWord = false;
					}
					int insert;
					if (!word.equals("")) {
						if (word.equals("nan")) {
							if (!warning) {
								JOptionPane.showMessageDialog(component, "Found NAN in data."
										+ "\nReplacing with 0s.", "NAN In Data", JOptionPane.WARNING_MESSAGE);
								warning = true;
							}
							word = "0";
						}
						if (counter < species.size()) {
							insert = counter;
						}
						else {
							insert = counter % species.size();
						}
						(data.get(insert)).add(Double.parseDouble(word));
						counter++;
					}
				}
			}
			input.close();
			prog.close();
			fileInput.close();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(component, "Error Reading Data!"
					+ "\nThere was an error reading the simulation output data.", "Error Reading Data",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
