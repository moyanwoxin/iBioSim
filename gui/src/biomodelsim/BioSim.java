package biomodelsim;

import gcm2sbml.gui.GCM2SBMLEditor;
import gcm2sbml.network.GeneticNetwork;
import gcm2sbml.parser.GCMFile;
import gcm2sbml.parser.GCMParser;
import graph.Graph;

import java.awt.AWTError;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.Application;

import learn.Learn;

import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SBMLWriter;

import reb2sac.Reb2Sac;
import reb2sac.Run;
import sbmleditor.SBML_Editor;
import buttons.Buttons;
import datamanager.DataManager;

/**
 * This class creates a GUI for the Tstubd program. It implements the
 * ActionListener class. This allows the GUI to perform actions when menu items
 * are selected.
 * 
 * @author Curtis Madsen
 */
public class BioSim implements MouseListener, ActionListener {

	private JFrame frame; // Frame where components of the GUI are displayed

	private JMenu file; // The file menu

	private JMenuItem newProj; // The new menu item

	private JMenuItem newModel; // The new menu item

	private JMenuItem newCircuit; // The new menu item

	private JMenuItem exit; // The exit menu item

	private JMenuItem importSbml; // The import sbml menu item

	private JMenuItem importDot; // The import dot menu item

	private JMenuItem manual; // The manual menu item

	private JMenuItem about; // The about menu item

	private JMenuItem openProj; // The open menu item

	private JMenuItem pref; // The preferences menu item

	private JMenuItem graph; // The graph menu item

	private JMenuItem probGraph;

	private String root; // The root directory

	private FileTree tree; // FileTree

	private JTabbedPane tab; // JTabbedPane for different tools

	private JPanel mainPanel; // the main panel

	private Log log; // the log

	private JPopupMenu popup; // popup menu

	private String separator;

	private KeyEventDispatcher dispatcher;

	private JMenuItem recentProjects[];

	private String recentProjectPaths[];

	private int numberRecentProj;

	private int ShortCutKey;

	public boolean checkUndeclared, checkUnits;

	private JCheckBox Undeclared, Units;

	private Pattern IDpat = Pattern.compile("([a-zA-Z]|_)([a-zA-Z]|[0-9]|_)*");

	public class MacOSAboutHandler extends Application {

		public MacOSAboutHandler() {
			addApplicationListener(new AboutBoxHandler());
		}

		class AboutBoxHandler extends ApplicationAdapter {
			public void handleAbout(ApplicationEvent event) {
				about();
				event.setHandled(true);
			}
		}
	}

	public class MacOSPreferencesHandler extends Application {

		public MacOSPreferencesHandler() {
			addApplicationListener(new PreferencesHandler());
		}

		class PreferencesHandler extends ApplicationAdapter {
			public void handlePreferences(ApplicationEvent event) {
				preferences();
				event.setHandled(true);
			}
		}
	}

	public class MacOSQuitHandler extends Application {

		public MacOSQuitHandler() {
			addApplicationListener(new QuitHandler());
		}

		class QuitHandler extends ApplicationAdapter {
			public void handleQuit(ApplicationEvent event) {
				exit();
				event.setHandled(true);
			}
		}

	}

	/**
	 * This is the constructor for the Proj class. It initializes all the input
	 * fields, puts them on panels, adds the panels to the frame, and then
	 * displays the frame.
	 * 
	 * @throws Exception
	 */
	public BioSim() {
		if (File.separator.equals("\\")) {
			separator = "\\\\";
		}
		else {
			separator = File.separator;
		}

		// Creates a new frame
		frame = new JFrame("iBioSim");
		frame.setIconImage(new ImageIcon(System.getenv("BIOSIM") + File.separator + "gui"
				+ File.separator + "icons" + File.separator + "iBioSim.png").getImage());

		// Makes it so that clicking the x in the corner closes the program
		WindowListener w = new WindowListener() {
			public void windowClosing(WindowEvent arg0) {
				exit.doClick();
			}

			public void windowOpened(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowDeactivated(WindowEvent arg0) {
			}
		};
		frame.addWindowListener(w);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		popup = new JPopupMenu();

		// Creates a menu for the frame
		JMenuBar menuBar = new JMenuBar();
		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		JMenu importMenu = new JMenu("Import");
		JMenu newMenu = new JMenu("New");
		menuBar.add(file);
		menuBar.add(help);
		manual = new JMenuItem("Manual");
		about = new JMenuItem("About");
		openProj = new JMenuItem("Open Project");
		pref = new JMenuItem("Preferences");
		newProj = new JMenuItem("Project");
		newCircuit = new JMenuItem("Genetic Circuit Model");
		newModel = new JMenuItem("SBML Model");
		graph = new JMenuItem("TSD Graph");
		probGraph = new JMenuItem("Probability Graph");
		importSbml = new JMenuItem("SBML Model");
		importDot = new JMenuItem("Genetic Circuit Model");
		exit = new JMenuItem("Exit");
		openProj.addActionListener(this);
		pref.addActionListener(this);
		manual.addActionListener(this);
		newProj.addActionListener(this);
		newCircuit.addActionListener(this);
		newModel.addActionListener(this);
		exit.addActionListener(this);
		about.addActionListener(this);
		importSbml.addActionListener(this);
		importDot.addActionListener(this);
		graph.addActionListener(this);
		probGraph.addActionListener(this);
		ShortCutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ShortCutKey));
		newProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ShortCutKey));
		openProj.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ShortCutKey));
		newCircuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ShortCutKey));
		newModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ShortCutKey));
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ShortCutKey));
		manual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ShortCutKey));
		graph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ShortCutKey));
		probGraph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ShortCutKey));
		importDot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ShortCutKey));
		importSbml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ShortCutKey));
		exit.setMnemonic(KeyEvent.VK_X);
		newProj.setMnemonic(KeyEvent.VK_P);
		openProj.setMnemonic(KeyEvent.VK_O);
		newCircuit.setMnemonic(KeyEvent.VK_C);
		newCircuit.setDisplayedMnemonicIndex(8);
		newModel.setMnemonic(KeyEvent.VK_S);
		about.setMnemonic(KeyEvent.VK_A);
		manual.setMnemonic(KeyEvent.VK_M);
		graph.setMnemonic(KeyEvent.VK_G);
		probGraph.setMnemonic(KeyEvent.VK_B);
		importDot.setMnemonic(KeyEvent.VK_T);
		importDot.setDisplayedMnemonicIndex(14);
		importSbml.setMnemonic(KeyEvent.VK_L);
		importDot.setEnabled(false);
		importSbml.setEnabled(false);
		newCircuit.setEnabled(false);
		newModel.setEnabled(false);
		graph.setEnabled(false);
		probGraph.setEnabled(false);
		file.add(newMenu);
		newMenu.add(newProj);
		newMenu.add(newCircuit);
		newMenu.add(newModel);
		newMenu.add(graph);
		newMenu.add(probGraph);
		file.add(openProj);
		// openMenu.add(openProj);
		file.addSeparator();
		file.add(importMenu);
		importMenu.add(importDot);
		importMenu.add(importSbml);
		file.addSeparator();
		help.add(manual);
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
			new MacOSAboutHandler();
			new MacOSPreferencesHandler();
			new MacOSQuitHandler();
			Application application = new Application();
			application.addPreferencesMenuItem();
			application.setEnabledPreferencesMenu(true);
		}
		else {
			file.add(pref);
			file.add(exit);
			file.addSeparator();
			help.add(about);
		}
		root = null;

		// Create recent project menu items
		numberRecentProj = 0;
		recentProjects = new JMenuItem[5];
		recentProjectPaths = new String[5];
		for (int i = 0; i < 5; i++) {
			recentProjects[i] = new JMenuItem();
			recentProjects[i].addActionListener(this);
			recentProjectPaths[i] = "";
		}
		recentProjects[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ShortCutKey));
		recentProjects[0].setMnemonic(KeyEvent.VK_1);
		recentProjects[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ShortCutKey));
		recentProjects[1].setMnemonic(KeyEvent.VK_2);
		recentProjects[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ShortCutKey));
		recentProjects[2].setMnemonic(KeyEvent.VK_3);
		recentProjects[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ShortCutKey));
		recentProjects[3].setMnemonic(KeyEvent.VK_4);
		recentProjects[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ShortCutKey));
		recentProjects[4].setMnemonic(KeyEvent.VK_5);
		Preferences biosimrc = Preferences.userRoot();
		for (int i = 0; i < 5; i++) {
			recentProjects[i].setText(biosimrc.get("biosim.recent.project." + i, ""));
			recentProjectPaths[i] = biosimrc.get("biosim.recent.project.path." + i, "");
			if (!recentProjectPaths[i].equals("")) {
				file.add(recentProjects[i]);
				numberRecentProj = i + 1;
			}
		}
		if (biosimrc.get("biosim.check.undeclared", "").equals("false")) {
			checkUndeclared = false;
		}
		else {
			checkUndeclared = true;
		}
		if (biosimrc.get("biosim.check.units", "").equals("false")) {
			checkUnits = false;
		}
		else {
			checkUnits = true;
		}

		// Open .biosimrc here

		// Packs the frame and displays it
		mainPanel = new JPanel(new BorderLayout());
		tree = new FileTree(null, this);
		log = new Log();
		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(950, 550));
		tab.setUI(new TabbedPaneCloseButtonUI());
		mainPanel.add(tree, "West");
		mainPanel.add(tab, "Center");
		mainPanel.add(log, "South");
		frame.setContentPane(mainPanel);
		frame.setJMenuBar(menuBar);
		frame.pack();
		Dimension screenSize;
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			screenSize = tk.getScreenSize();
		}
		catch (AWTError awe) {
			screenSize = new Dimension(640, 480);
		}
		Dimension frameSize = frame.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		int x = screenSize.width / 2 - frameSize.width / 2;
		int y = screenSize.height / 2 - frameSize.height / 2;
		frame.setLocation(x, y);
		frame.setVisible(true);
		dispatcher = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_TYPED) {
					if (e.getKeyChar() == '') {
						if (tab.getTabCount() > 0) {
							KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(
									dispatcher);
							if (save(tab.getSelectedIndex()) != 0) {
								tab.remove(tab.getSelectedIndex());
							}
							KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
									dispatcher);
						}
					}
				}
				return false;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
	}

	public void preferences() {
		final JFrame f = new JFrame("Preferences");
		Undeclared = new JCheckBox("Check for undeclared units in SBML");
		if (checkUndeclared) {
			Undeclared.setSelected(true);
		}
		else {
			Undeclared.setSelected(false);
		}
		Units = new JCheckBox("Check units in SBML");
		if (checkUnits) {
			Units.setSelected(true);
		}
		else {
			Units.setSelected(false);
		}
		JButton savePref = new JButton("Save");
		savePref.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Preferences biosimrc = Preferences.userRoot();
				if (Undeclared.isSelected()) {
					checkUndeclared = true;
					biosimrc.put("biosim.check.undeclared", "true");
				}
				else {
					checkUndeclared = false;
					biosimrc.put("biosim.check.undeclared", "false");
				}
				if (Units.isSelected()) {
					checkUnits = true;
					biosimrc.put("biosim.check.units", "true");
				}
				else {
					checkUnits = false;
					biosimrc.put("biosim.check.units", "false");
				}
				f.dispose();
			}
		});
		JButton cancelPref = new JButton("Cancel");
		cancelPref.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.dispose();
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(cancelPref);
		buttons.add(savePref);
		JPanel aboutPanel = new JPanel(new BorderLayout());
		aboutPanel.add(Undeclared, "North");
		aboutPanel.add(Units, "Center");
		aboutPanel.add(buttons, "South");
		f.setContentPane(aboutPanel);
		f.pack();
		Dimension screenSize;
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			screenSize = tk.getScreenSize();
		}
		catch (AWTError awe) {
			screenSize = new Dimension(640, 480);
		}
		Dimension frameSize = f.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		int x = screenSize.width / 2 - frameSize.width / 2;
		int y = screenSize.height / 2 - frameSize.height / 2;
		f.setLocation(x, y);
		f.setVisible(true);
	}

	public void about() {
		final JFrame f = new JFrame("About");
		// frame.setIconImage(new ImageIcon(System.getenv("BIOSIM") + File.separator
		// + "gui"
		// + File.separator + "icons" + File.separator + "iBioSim.png").getImage());
		JLabel bioSim = new JLabel("iBioSim", JLabel.CENTER);
		Font font = bioSim.getFont();
		font = font.deriveFont(Font.BOLD, 36.0f);
		bioSim.setFont(font);
		JLabel version = new JLabel("Version 0.95", JLabel.CENTER);
		JLabel uOfU = new JLabel("University of Utah", JLabel.CENTER);
		JButton credits = new JButton("Credits");
		credits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] options = { "Close" };
				JOptionPane.showOptionDialog(f, "Nathan Barker\nHiroyuki Kuwahara\n"
						+ "Curtis Madsen\nChris Myers\nNam Nguyen", "Credits", JOptionPane.YES_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			}
		});
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.dispose();
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(credits);
		buttons.add(close);
		JPanel aboutPanel = new JPanel(new BorderLayout());
		JPanel uOfUPanel = new JPanel(new BorderLayout());
		uOfUPanel.add(bioSim, "North");
		uOfUPanel.add(version, "Center");
		uOfUPanel.add(uOfU, "South");
		aboutPanel.add(new javax.swing.JLabel(new javax.swing.ImageIcon(System.getenv("BIOSIM")
				+ File.separator + "gui" + File.separator + "icons" + File.separator + "iBioSim.png")),
				"North");
		// aboutPanel.add(bioSim, "North");
		aboutPanel.add(uOfUPanel, "Center");
		aboutPanel.add(buttons, "South");
		f.setContentPane(aboutPanel);
		f.pack();
		Dimension screenSize;
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			screenSize = tk.getScreenSize();
		}
		catch (AWTError awe) {
			screenSize = new Dimension(640, 480);
		}
		Dimension frameSize = f.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		int x = screenSize.width / 2 - frameSize.width / 2;
		int y = screenSize.height / 2 - frameSize.height / 2;
		f.setLocation(x, y);
		f.setVisible(true);
	}

	public void exit() {
		for (int i = 0; i < tab.getTabCount(); i++) {
			if (save(i) == 0) {
				return;
			}
		}
		Preferences biosimrc = Preferences.userRoot();
		for (int i = 0; i < numberRecentProj; i++) {
			biosimrc.put("biosim.recent.project." + i, recentProjects[i].getText());
			biosimrc.put("biosim.recent.project.path." + i, recentProjectPaths[i]);
		}
		System.exit(1);
	}

	/**
	 * This method performs different functions depending on what menu items are
	 * selected.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == about) {
			about();
		}
		else if (e.getSource() == manual) {
			try {
				String directory = "";
				String theFile = "iBioSim.html";
				String command = "";
				if (System.getProperty("os.name").contentEquals("Linux")) {
					directory = System.getenv("BIOSIM") + "/docs/";
					command = "gnome-open ";
				}
				else if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
					directory = System.getenv("BIOSIM") + "/docs/";
					command = "open ";
				}
				else {
					directory = System.getenv("BIOSIM") + "\\docs\\";
					command = "cmd /c start ";
				}
				File work = new File(directory);
				log.addText("Executing:\n" + command + directory + theFile + "\n");
				Runtime exec = Runtime.getRuntime();
				exec.exec(command + theFile, null, work);
			}
			catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "Unable to open manual.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the exit menu item is selected
		else if (e.getSource() == exit) {
			exit();
		}
		// if the open popup menu is selected on a sim directory
		else if (e.getActionCommand().equals("openSim")) {
			openSim();
		}
		else if (e.getActionCommand().equals("openLearn")) {
			openLearn();
		}
		// if the create simulation popup menu is selected on a dot file
		else if (e.getActionCommand().equals("createSim")) {
			try {
				simulate(true);
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "You must select a valid gcm file for simulation.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the simulate popup menu is selected on an sbml file
		else if (e.getActionCommand().equals("simulate")) {
			try {
				simulate(false);
			}
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(frame, "You must select a valid sbml file for simulation.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the delete popup menu is selected on a sim directory
		else if (e.getActionCommand().equals("deleteSim")) {
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (tab.getTitleAt(i).equals(
						tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
					tab.remove(i);
				}
			}
			File dir = new File(tree.getFile());
			if (dir.isDirectory()) {
				deleteDir(dir);
			}
			else {
				System.gc();
				dir.delete();
			}
			refreshTree();
		}
		// if the delete popup menu is selected
		else if (e.getActionCommand().equals("delete")) {
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (tab.getTitleAt(i).equals(
						tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
					tab.remove(i);
				}
			}
			System.gc();
			new File(tree.getFile()).delete();
			refreshTree();
		}
		// if the edit popup menu is selected on a dot file
		else if (e.getActionCommand().equals("createSBML")) {
			try {
				String[] dot = tree.getFile().split(separator);
				String sbmlFile = dot[dot.length - 1].substring(0, dot[dot.length - 1].length() - 3)
						+ "sbml";
				// log.addText("Executing:\ngcm2sbml.pl " + tree.getFile() + " " + root
				// + separator + sbmlFile
				// + "\n");
				// Runtime exec = Runtime.getRuntime();
				// String filename = tree.getFile();
				// String directory = "";
				// String theFile = "";
				// if (filename.lastIndexOf('/') >= 0) {
				// directory = filename.substring(0,
				// filename.lastIndexOf('/') + 1);
				// theFile = filename.substring(filename.lastIndexOf('/') + 1);
				// }
				// if (filename.lastIndexOf('\\') >= 0) {
				// directory = filename.substring(0, filename
				// .lastIndexOf('\\') + 1);
				// theFile = filename
				// .substring(filename.lastIndexOf('\\') + 1);
				// }
				// File work = new File(directory);

				GCMParser parser = new GCMParser(tree.getFile());
				GeneticNetwork network = parser.buildNetwork();
				GeneticNetwork.setRoot(root + File.separator);
				network.mergeSBML(root + separator + sbmlFile);
				refreshTree();
				addTab(sbmlFile, new SBML_Editor(root + separator + sbmlFile, null, log, this, null, null),
						"SBML Editor");
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Unable to create SBML file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the edit popup menu is selected on a dot file
		else if (e.getActionCommand().equals("dotEditor")) {
			try {
				String directory = "";
				String theFile = "";
				String filename = tree.getFile();
				if (filename.lastIndexOf('/') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('/') + 1);
					theFile = filename.substring(filename.lastIndexOf('/') + 1);
				}
				if (filename.lastIndexOf('\\') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('\\') + 1);
					theFile = filename.substring(filename.lastIndexOf('\\') + 1);
				}
				int i = getTab(theFile);
				if (i != -1) {
					tab.setSelectedIndex(i);
				}
				else {
					File work = new File(directory);
					addTab(theFile, new GCM2SBMLEditor(work.getAbsolutePath(), theFile, this, log),
							"GCM Editor");
				}
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Unable to open gcm file editor.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		// if the edit popup menu is selected on an sbml file
		else if (e.getActionCommand().equals("sbmlEditor")) {
			try {
				boolean done = false;
				for (int i = 0; i < tab.getTabCount(); i++) {
					if (tab.getTitleAt(i).equals(
							tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
						tab.setSelectedIndex(i);
						done = true;
					}
				}
				if (!done) {
					addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
							new SBML_Editor(tree.getFile(), null, log, this, null, null), "SBML Editor");
				}
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "You must select a valid sbml file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the graph popup menu is selected on an sbml file
		else if (e.getActionCommand().equals("graph")) {
			try {
				for (int i = 0; i < tab.getTabCount(); i++) {
					if (tab.getTitleAt(i).equals(
							tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
						tab.setSelectedIndex(i);
						if (save(i) != 1) {
							return;
						}
						break;
					}
				}
				Run run = new Run(null);
				JCheckBox dummy = new JCheckBox();
				dummy.setSelected(false);
				run.createProperties(0, 1, 1, 1, tree.getFile().substring(
						0,
						tree.getFile().length()
								- (tree.getFile().split(separator)[tree.getFile().split(separator).length - 1]
										.length())), 314159, 1, new String[0], new String[0], "tsd.printer", "amount",
						tree.getFile().split(separator), "none", frame, tree.getFile(), 0.1, 0.1, 0.1, 15,
						dummy, "", dummy, null);
				String filename = tree.getFile();
				String directory = "";
				String theFile = "";
				if (filename.lastIndexOf('/') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('/') + 1);
					theFile = filename.substring(filename.lastIndexOf('/') + 1);
				}
				if (filename.lastIndexOf('\\') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('\\') + 1);
					theFile = filename.substring(filename.lastIndexOf('\\') + 1);
				}
				File work = new File(directory);
				String out = theFile;
				if (out.length() > 4 && out.substring(out.length() - 5, out.length()).equals(".sbml")) {
					out = out.substring(0, out.length() - 5);
				}
				else if (out.length() > 3 && out.substring(out.length() - 4, out.length()).equals(".xml")) {
					out = out.substring(0, out.length() - 4);
				}
				log.addText("Executing:\nreb2sac --target.encoding=dot --out=" + directory + out + ".dot "
						+ directory + theFile + "\n");
				Runtime exec = Runtime.getRuntime();
				Process graph = exec.exec("reb2sac --target.encoding=dot --out=" + out + ".dot " + theFile,
						null, work);
				String error = "";
				String output = "";
				InputStream reb = graph.getErrorStream();
				int read = reb.read();
				while (read != -1) {
					error += (char) read;
					read = reb.read();
				}
				reb.close();
				reb = graph.getInputStream();
				read = reb.read();
				while (read != -1) {
					output += (char) read;
					read = reb.read();
				}
				reb.close();
				if (!output.equals("")) {
					log.addText("Output:\n" + output + "\n");
				}
				if (!error.equals("")) {
					log.addText("Errors:\n" + error + "\n");
				}
				graph.waitFor();
				if (error.equals("")) {
					if (System.getProperty("os.name").contentEquals("Linux")) {
						log.addText("Executing:\ndotty " + directory + out + ".dot\n");
						exec.exec("dotty " + out + ".dot", null, work);
					}
					else if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
						log.addText("Executing:\nopen " + directory + out + ".dot\n");
						exec.exec("open " + out + ".dot", null, work);
					}
					else {
						log.addText("Executing:\ndotty " + directory + out + ".dot\n");
						exec.exec("dotty " + out + ".dot", null, work);
					}
				}
				String remove;
				if (tree.getFile().substring(tree.getFile().length() - 4).equals("sbml")) {
					remove = tree.getFile().substring(0, tree.getFile().length() - 4) + "properties";
				}
				else {
					remove = tree.getFile().substring(0, tree.getFile().length() - 4) + ".properties";
				}
				System.gc();
				new File(remove).delete();
				refreshTree();
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Error graphing sbml file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the browse popup menu is selected on an sbml file
		else if (e.getActionCommand().equals("browse")) {
			try {
				for (int i = 0; i < tab.getTabCount(); i++) {
					if (tab.getTitleAt(i).equals(
							tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
						tab.setSelectedIndex(i);
						if (save(i) != 1) {
							return;
						}
						break;
					}
				}
				Run run = new Run(null);
				JCheckBox dummy = new JCheckBox();
				dummy.setSelected(false);
				run.createProperties(0, 1, 1, 1, tree.getFile().substring(
						0,
						tree.getFile().length()
								- (tree.getFile().split(separator)[tree.getFile().split(separator).length - 1]
										.length())), 314159, 1, new String[0], new String[0], "tsd.printer", "amount",
						tree.getFile().split(separator), "none", frame, tree.getFile(), 0.1, 0.1, 0.1, 15,
						dummy, "", dummy, null);
				String filename = tree.getFile();
				String directory = "";
				String theFile = "";
				if (filename.lastIndexOf('/') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('/') + 1);
					theFile = filename.substring(filename.lastIndexOf('/') + 1);
				}
				if (filename.lastIndexOf('\\') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('\\') + 1);
					theFile = filename.substring(filename.lastIndexOf('\\') + 1);
				}
				File work = new File(directory);
				String out = theFile;
				if (out.length() > 4 && out.substring(out.length() - 5, out.length()).equals(".sbml")) {
					out = out.substring(0, out.length() - 5);
				}
				else if (out.length() > 3 && out.substring(out.length() - 4, out.length()).equals(".xml")) {
					out = out.substring(0, out.length() - 4);
				}
				log.addText("Executing:\nreb2sac --target.encoding=xhtml --out=" + directory + out
						+ ".xhtml " + tree.getFile() + "\n");
				Runtime exec = Runtime.getRuntime();
				Process browse = exec.exec("reb2sac --target.encoding=xhtml --out=" + out + ".xhtml "
						+ theFile, null, work);
				String error = "";
				String output = "";
				InputStream reb = browse.getErrorStream();
				int read = reb.read();
				while (read != -1) {
					error += (char) read;
					read = reb.read();
				}
				reb.close();
				reb = browse.getInputStream();
				read = reb.read();
				while (read != -1) {
					output += (char) read;
					read = reb.read();
				}
				reb.close();
				if (!output.equals("")) {
					log.addText("Output:\n" + output + "\n");
				}
				if (!error.equals("")) {
					log.addText("Errors:\n" + error + "\n");
				}
				browse.waitFor();

				String command = "";
				if (error.equals("")) {
					if (System.getProperty("os.name").contentEquals("Linux")) {
						command = "gnome-open ";
					}
					else if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
						command = "open ";
					}
					else {
						command = "cmd /c start ";
					}
					log.addText("Executing:\n" + command + directory + out + ".xhtml\n");
					exec.exec(command + out + ".xhtml", null, work);
				}
				String remove;
				if (tree.getFile().substring(tree.getFile().length() - 4).equals("sbml")) {
					remove = tree.getFile().substring(0, tree.getFile().length() - 4) + "properties";
				}
				else {
					remove = tree.getFile().substring(0, tree.getFile().length() - 4) + ".properties";
				}
				System.gc();
				new File(remove).delete();
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Error viewing sbml file in a browser.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the graph dot popup menu is selected
		else if (e.getActionCommand().equals("graphDot")) {
			try {
				String filename = tree.getFile();
				String directory = "";
				String theFile = "";
				if (filename.lastIndexOf('/') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('/') + 1);
					theFile = filename.substring(filename.lastIndexOf('/') + 1);
				}
				if (filename.lastIndexOf('\\') >= 0) {
					directory = filename.substring(0, filename.lastIndexOf('\\') + 1);
					theFile = filename.substring(filename.lastIndexOf('\\') + 1);
				}
				File work = new File(directory);
				if (System.getProperty("os.name").contentEquals("Linux")) {
					log.addText("Executing:\ndotty " + directory + theFile + "\n");
					Runtime exec = Runtime.getRuntime();
					exec.exec("dotty " + theFile, null, work);
				}
				else if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
					log.addText("Executing:\nopen " + directory + theFile + "\n");
					Runtime exec = Runtime.getRuntime();
					exec.exec("cp " + theFile + " " + theFile + ".dot", null, work);
					exec = Runtime.getRuntime();
					exec.exec("open " + theFile + ".dot", null, work);
				}
				else {
					log.addText("Executing:\ndotty " + directory + theFile + "\n");
					Runtime exec = Runtime.getRuntime();
					exec.exec("dotty " + theFile, null, work);
				}
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Unable to view this gcm file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the new menu item is selected
		else if (e.getSource() == newProj) {
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (save(i) == 0) {
					return;
				}
			}
			String filename = Buttons.browse(frame, null, null, JFileChooser.DIRECTORIES_ONLY, "New");
			if (!filename.trim().equals("")) {
				filename = filename.trim();
				File f = new File(filename);
				if (f.exists()) {
					Object[] options = { "Overwrite", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "File already exists."
							+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						File dir = new File(filename);
						if (dir.isDirectory()) {
							deleteDir(dir);
						}
						else {
							System.gc();
							dir.delete();
						}
					}
					else {
						return;
					}
				}
				new File(filename).mkdir();
				try {
					new FileWriter(new File(filename + separator + ".prj")).close();
				}
				catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "Unable create a new project.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				root = filename;
				refresh();
				tab.removeAll();
				addRecentProject(filename);
				importDot.setEnabled(true);
				importSbml.setEnabled(true);
				newCircuit.setEnabled(true);
				newModel.setEnabled(true);
				graph.setEnabled(true);
				probGraph.setEnabled(true);
			}
		}
		// if the open project menu item is selected
		else if (e.getSource() == pref) {
			preferences();
		}
		else if ((e.getSource() == openProj) || (e.getSource() == recentProjects[0])
				|| (e.getSource() == recentProjects[1]) || (e.getSource() == recentProjects[2])
				|| (e.getSource() == recentProjects[3]) || (e.getSource() == recentProjects[4])) {
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (save(i) == 0) {
					return;
				}
			}
			File f;
			if (root == null) {
				f = null;
			}
			else {
				f = new File(root);
			}
			String projDir = "";
			if (e.getSource() == openProj) {
				projDir = Buttons.browse(frame, f, null, JFileChooser.DIRECTORIES_ONLY, "Open");
			}
			else if (e.getSource() == recentProjects[0]) {
				projDir = recentProjectPaths[0];
			}
			else if (e.getSource() == recentProjects[1]) {
				projDir = recentProjectPaths[1];
			}
			else if (e.getSource() == recentProjects[2]) {
				projDir = recentProjectPaths[2];
			}
			else if (e.getSource() == recentProjects[3]) {
				projDir = recentProjectPaths[3];
			}
			else if (e.getSource() == recentProjects[4]) {
				projDir = recentProjectPaths[4];
			}
			if (!projDir.equals("")) {
				if (new File(projDir).isDirectory()) {
					boolean isProject = false;
					for (String temp : new File(projDir).list()) {
						if (temp.equals(".prj")) {
							isProject = true;
						}
					}
					if (isProject) {
						root = projDir;
						refresh();
						tab.removeAll();
						addRecentProject(projDir);
						importDot.setEnabled(true);
						importSbml.setEnabled(true);
						newCircuit.setEnabled(true);
						newModel.setEnabled(true);
						graph.setEnabled(true);
						probGraph.setEnabled(true);
					}
					else {
						JOptionPane.showMessageDialog(frame, "You must select a valid project.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					JOptionPane.showMessageDialog(frame, "You must select a valid project.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		// if the new circuit model menu item is selected
		else if (e.getSource() == newCircuit) {
			if (root != null) {
				try {
					String simName = JOptionPane.showInputDialog(frame, "Enter GCM Model ID:", "Model ID",
							JOptionPane.PLAIN_MESSAGE);
					if (simName != null && !simName.trim().equals("")) {
						simName = simName.trim();
						if (simName.length() > 4) {
							if (!simName.substring(simName.length() - 4).equals(".gcm")) {
								simName += ".gcm";
							}
						}
						else {
							simName += ".gcm";
						}
						String modelID = "";
						if (simName.length() > 3) {
							if (simName.substring(simName.length() - 4).equals(".gcm")) {
								modelID = simName.substring(0, simName.length() - 4);
							}
							else {
								modelID = simName.substring(0, simName.length() - 3);
							}
						}
						if (!(IDpat.matcher(modelID).matches())) {
							JOptionPane.showMessageDialog(frame,
									"A model ID can only contain letters, numbers, and underscores.", "Invalid ID",
									JOptionPane.ERROR_MESSAGE);
						}
						else {
							File f = new File(root + separator + simName);
							if (f.exists()) {
								Object[] options = { "Overwrite", "Cancel" };
								int value = JOptionPane.showOptionDialog(frame, "File already exists."
										+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
										JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
								if (value == JOptionPane.YES_OPTION) {
									File dir = new File(root + separator + simName);
									if (dir.isDirectory()) {
										deleteDir(dir);
									}
									else {
										System.gc();
										dir.delete();
									}
									for (int i = 0; i < tab.getTabCount(); i++) {
										if (tab.getTitleAt(i).equals(simName)) {
											tab.remove(i);
										}
									}
								}
								else {
									return;
								}
							}
							f.createNewFile();
							new GCMFile().save(f.getAbsolutePath());
							int i = getTab(f.getName());
							if (i != -1) {
								tab.remove(i);
							}
							addTab(f.getName(), new GCM2SBMLEditor(root + separator, f.getName(), this, log),
									"GCM Editor");
							refreshTree();
						}
					}
				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, "Unable to create new model.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		// if the new SBML model menu item is selected
		else if (e.getSource() == newModel) {
			if (root != null) {
				try {
					String simName = JOptionPane.showInputDialog(frame, "Enter SBML Model ID:", "Model ID",
							JOptionPane.PLAIN_MESSAGE);
					if (simName != null && !simName.trim().equals("")) {
						simName = simName.trim();
						if (simName.length() > 4) {
							if (!simName.substring(simName.length() - 5).equals(".sbml")
									&& !simName.substring(simName.length() - 4).equals(".xml")) {
								simName += ".sbml";
							}
						}
						else {
							simName += ".sbml";
						}
						String modelID = "";
						if (simName.length() > 4) {
							if (simName.substring(simName.length() - 5).equals(".sbml")) {
								modelID = simName.substring(0, simName.length() - 5);
							}
							else {
								modelID = simName.substring(0, simName.length() - 4);
							}
						}
						if (!(IDpat.matcher(modelID).matches())) {
							JOptionPane.showMessageDialog(frame,
									"A model ID can only contain letters, numbers, and underscores.", "Invalid ID",
									JOptionPane.ERROR_MESSAGE);
						}
						else {
							File f = new File(root + separator + simName);
							if (f.exists()) {
								Object[] options = { "Overwrite", "Cancel" };
								int value = JOptionPane.showOptionDialog(frame, "File already exists."
										+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
										JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
								if (value == JOptionPane.YES_OPTION) {
									File dir = new File(root + separator + simName);
									if (dir.isDirectory()) {
										deleteDir(dir);
									}
									else {
										System.gc();
										dir.delete();
									}
									for (int i = 0; i < tab.getTabCount(); i++) {
										if (tab.getTitleAt(i).equals(simName)) {
											tab.remove(i);
										}
									}
								}
								else {
									return;
								}
							}
							f.createNewFile();
							SBMLDocument document = new SBMLDocument();
							document.createModel();
							// document.setLevel(2);
							document.setLevelAndVersion(2, 3);
							Compartment c = document.getModel().createCompartment();
							c.setId("default");
							c.setSize(1.0);
							document.getModel().setId(modelID);
							FileOutputStream out = new FileOutputStream(f);
							SBMLWriter writer = new SBMLWriter();
							String doc = writer.writeToString(document);
							byte[] output = doc.getBytes();
							out.write(output);
							out.close();
							addTab(
									f.getAbsolutePath().split(separator)[f.getAbsolutePath().split(separator).length - 1],
									new SBML_Editor(f.getAbsolutePath(), null, log, this, null, null), "SBML Editor");
							refreshTree();
						}
					}
				}
				catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "Unable to create new model.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must open or create a project first.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the import sbml menu item is selected
		else if (e.getSource() == importSbml) {
			if (root != null) {
				String filename = Buttons.browse(frame, new File(root), null, JFileChooser.FILES_ONLY,
						"Import SBML");
				if (!filename.equals("")) {
					String[] file = filename.split(separator);
					try {
						SBMLReader reader = new SBMLReader();
						SBMLDocument document = reader.readSBML(filename);
						if (document.getNumErrors() > 0) {
							JOptionPane.showMessageDialog(frame, "Invalid SBML file.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
						else {
							long numErrors = document.checkConsistency();
							if (numErrors > 0) {
								final JFrame f = new JFrame("SBML Errors and Warnings");
								String message = "Imported SBML file contains the errors listed below. ";
								message += "It is recommended that you fix them before using this model or you may get unexpected results.\n\n";
								for (long i = 0; i < numErrors; i++) {
									String error = document.getError(i).getMessage(); // .replace(".
									// ",".\n");
									message += i + ":" + error + "\n";
								}
								JTextArea messageArea = new JTextArea(message);
								messageArea.setLineWrap(true);
								messageArea.setEditable(false);
								JScrollPane scroll = new JScrollPane();
								scroll.setMinimumSize(new Dimension(600, 600));
								scroll.setPreferredSize(new Dimension(600, 600));
								scroll.setViewportView(messageArea);
								JButton close = new JButton("Dismiss");
								close.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										f.dispose();
									}
								});
								JPanel consistencyPanel = new JPanel(new BorderLayout());
								consistencyPanel.add(scroll, "Center");
								consistencyPanel.add(close, "South");
								f.setContentPane(consistencyPanel);
								f.pack();
								Dimension screenSize;
								try {
									Toolkit tk = Toolkit.getDefaultToolkit();
									screenSize = tk.getScreenSize();
								}
								catch (AWTError awe) {
									screenSize = new Dimension(640, 480);
								}
								Dimension frameSize = f.getSize();
								if (frameSize.height > screenSize.height) {
									frameSize.height = screenSize.height;
								}
								if (frameSize.width > screenSize.width) {
									frameSize.width = screenSize.width;
								}
								int x = screenSize.width / 2 - frameSize.width / 2;
								int y = screenSize.height / 2 - frameSize.height / 2;
								f.setLocation(x, y);
								f.setVisible(true);
							}
							FileOutputStream out = new FileOutputStream(new File(root + separator
									+ file[file.length - 1]));
							SBMLWriter writer = new SBMLWriter();
							String doc = writer.writeToString(document);
							byte[] output = doc.getBytes();
							out.write(output);
							out.close();
							refreshTree();
						}
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "Unable to import file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must open or create a project first.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the import dot menu item is selected
		else if (e.getSource() == importDot) {
			if (root != null) {
				String filename = Buttons.browse(frame, new File(root), null, JFileChooser.FILES_ONLY,
						"Import Genetic Circuit");
				if (filename.length() > 3
						&& !filename.substring(filename.length() - 4, filename.length()).equals(".gcm")) {
					JOptionPane.showMessageDialog(frame, "You must select a valid gcm file to import.",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				else if (!filename.equals("")) {
					String[] file = filename.split(separator);
					try {
						// GCMParser parser =
						new GCMParser(filename);
						FileOutputStream out = new FileOutputStream(new File(root + separator
								+ file[file.length - 1]));
						FileInputStream in = new FileInputStream(new File(filename));
						int read = in.read();
						while (read != -1) {
							out.write(read);
							read = in.read();
						}
						in.close();
						out.close();
						refreshTree();
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "Unable to import file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must open or create a project first.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// if the Graph data menu item is clicked
		else if (e.getSource() == graph) {
			if (root != null) {
				String graphName = JOptionPane.showInputDialog(frame, "Enter A Name For The TSD Graph:",
						"TSD Graph Name", JOptionPane.PLAIN_MESSAGE);
				if (graphName != null && !graphName.trim().equals("")) {
					graphName = graphName.trim();
					if (graphName.length() > 3) {
						if (!graphName.substring(graphName.length() - 4).equals(".grf")) {
							graphName += ".grf";
						}
					}
					else {
						graphName += ".grf";
					}
					File f = new File(root + separator + graphName);
					if (f.exists()) {
						Object[] options = { "Overwrite", "Cancel" };
						int value = JOptionPane.showOptionDialog(frame, "File already exists."
								+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
						if (value == JOptionPane.YES_OPTION) {
							File dir = new File(root + separator + graphName);
							if (dir.isDirectory()) {
								deleteDir(dir);
							}
							else {
								System.gc();
								dir.delete();
							}
							for (int i = 0; i < tab.getTabCount(); i++) {
								if (tab.getTitleAt(i).equals(graphName)) {
									tab.remove(i);
								}
							}
						}
						else {
							return;
						}
					}
					Graph g = new Graph(null, "amount",
							graphName.trim().substring(0, graphName.length() - 4), "tsd.printer", root, "time",
							this, null, log, graphName.trim(), true, false);
					addTab(graphName.trim(), g, "TSD Graph");
					g.save();
					refreshTree();
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must open or create a project first.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == probGraph) {
			if (root != null) {
				String graphName = JOptionPane.showInputDialog(frame,
						"Enter A Name For The Probability Graph:", "Probability Graph Name",
						JOptionPane.PLAIN_MESSAGE);
				if (graphName != null && !graphName.trim().equals("")) {
					graphName = graphName.trim();
					if (graphName.length() > 3) {
						if (!graphName.substring(graphName.length() - 4).equals(".prb")) {
							graphName += ".prb";
						}
					}
					else {
						graphName += ".prb";
					}
					File f = new File(root + separator + graphName);
					if (f.exists()) {
						Object[] options = { "Overwrite", "Cancel" };
						int value = JOptionPane.showOptionDialog(frame, "File already exists."
								+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
						if (value == JOptionPane.YES_OPTION) {
							File dir = new File(root + separator + graphName);
							if (dir.isDirectory()) {
								deleteDir(dir);
							}
							else {
								System.gc();
								dir.delete();
							}
							for (int i = 0; i < tab.getTabCount(); i++) {
								if (tab.getTitleAt(i).equals(graphName)) {
									tab.remove(i);
								}
							}
						}
						else {
							return;
						}
					}
					Graph g = new Graph(null, "amount",
							graphName.trim().substring(0, graphName.length() - 4), "tsd.printer", root, "time",
							this, null, log, graphName.trim(), false, false);
					addTab(graphName.trim(), g, "Probability Graph");
					g.save();
					refreshTree();
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must open or create a project first.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getActionCommand().equals("createLearn")) {
			if (root != null) {
				for (int i = 0; i < tab.getTabCount(); i++) {
					if (tab.getTitleAt(i).equals(
							tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
						tab.setSelectedIndex(i);
						if (save(i) != 1) {
							return;
						}
						break;
					}
				}
				String lrnName = JOptionPane.showInputDialog(frame, "Enter Learn ID:", "Learn View ID",
						JOptionPane.PLAIN_MESSAGE);
				if (lrnName != null && !lrnName.trim().equals("")) {
					lrnName = lrnName.trim();
					try {
						File f = new File(root + separator + lrnName);
						if (f.exists()) {
							Object[] options = { "Overwrite", "Cancel" };
							int value = JOptionPane.showOptionDialog(frame, "File already exists."
									+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
							if (value == JOptionPane.YES_OPTION) {
								File dir = new File(root + separator + lrnName);
								if (dir.isDirectory()) {
									deleteDir(dir);
								}
								else {
									System.gc();
									dir.delete();
								}
								for (int i = 0; i < tab.getTabCount(); i++) {
									if (tab.getTitleAt(i).equals(lrnName)) {
										tab.remove(i);
									}
								}
							}
							else {
								return;
							}
						}
						new File(root + separator + lrnName).mkdir();
						// new FileWriter(new File(root + separator + lrnName + separator +
						// ".lrn")).close();
						String sbmlFile = tree.getFile();
						String[] getFilename = sbmlFile.split(separator);
						String sbmlFileNoPath = getFilename[getFilename.length - 1];
						try {
							FileOutputStream out = new FileOutputStream(new File(root + separator
									+ lrnName.trim() + separator + lrnName.trim() + ".lrn"));
							out.write(("genenet.file=" + sbmlFileNoPath + "\n").getBytes());
							out.close();
						}
						catch (Exception e1) {
							JOptionPane.showMessageDialog(frame, "Unable to save parameter file!",
									"Error Saving File", JOptionPane.ERROR_MESSAGE);
						}
						refreshTree();
						JTabbedPane lrnTab = new JTabbedPane();
						lrnTab.addTab("Data Manager", new DataManager(root + separator + lrnName, this));
						lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("Data Manager");
						lrnTab.addTab("Learn", new Learn(root + separator + lrnName, log, this));
						lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("Learn");
						lrnTab.addTab("TSD Graph", new Graph(null, "amount", lrnName + " data", "tsd.printer",
								root + separator + lrnName, "time", this, null, log, null, true, true));
						lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("TSD Graph");
						/*
						 * JLabel noData = new JLabel("No data available"); Font font =
						 * noData.getFont(); font = font.deriveFont(Font.BOLD, 42.0f);
						 * noData.setFont(font);
						 * noData.setHorizontalAlignment(SwingConstants.CENTER);
						 * lrnTab.addTab("Learn", noData);
						 * lrnTab.getComponentAt(lrnTab.getComponents().length -
						 * 1).setName("Learn"); JLabel noData1 = new JLabel("No data
						 * available"); font = noData1.getFont(); font =
						 * font.deriveFont(Font.BOLD, 42.0f); noData1.setFont(font);
						 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
						 * lrnTab.addTab("TSD Graph", noData1);
						 * lrnTab.getComponentAt(lrnTab.getComponents().length -
						 * 1).setName("TSD Graph");
						 */
						addTab(lrnName, lrnTab, null);
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "Unable to create Learn View directory.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must open or create a project first.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getActionCommand().equals("copy")) {
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (tab.getTitleAt(i).equals(
						tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
					tab.setSelectedIndex(i);
					if (save(i) != 1) {
						return;
					}
					break;
				}
			}
			String modelID = null;
			String copy = JOptionPane.showInputDialog(frame, "Enter A New Filename:", "Copy",
					JOptionPane.PLAIN_MESSAGE);
			if (copy != null) {
				copy = copy.trim();
			}
			else {
				return;
			}
			try {
				if (!copy.equals("")) {
					if (tree.getFile().length() >= 5
							&& tree.getFile().substring(tree.getFile().length() - 5).equals(".sbml")
							|| tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".xml")) {
						if (copy.length() > 4) {
							if (!copy.substring(copy.length() - 5).equals(".sbml")
									&& !copy.substring(copy.length() - 4).equals(".xml")) {
								copy += ".sbml";
							}
						}
						else {
							copy += ".sbml";
						}
						if (copy.length() > 4) {
							if (copy.substring(copy.length() - 5).equals(".sbml")) {
								modelID = copy.substring(0, copy.length() - 5);
							}
							else {
								modelID = copy.substring(0, copy.length() - 4);
							}
						}
					}
					else if (tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".gcm")) {
						if (copy.length() > 3) {
							if (!copy.substring(copy.length() - 4).equals(".gcm")) {
								copy += ".gcm";
							}
						}
						else {
							copy += ".gcm";
						}
					}
					else if (tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
						if (copy.length() > 3) {
							if (!copy.substring(copy.length() - 4).equals(".grf")) {
								copy += ".grf";
							}
						}
						else {
							copy += ".grf";
						}
					}
				}
				if (copy
						.equals(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
					JOptionPane.showMessageDialog(frame, "Unable to copy file."
							+ "\nNew filename must be different than old filename.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				boolean write = true;
				if (new File(root + separator + copy).exists()) {
					Object[] options = { "Overwrite", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "File already exists."
							+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						write = true;
						File dir = new File(root + separator + copy);
						if (dir.isDirectory()) {
							deleteDir(dir);
						}
						else {
							System.gc();
							dir.delete();
						}
						for (int i = 0; i < tab.getTabCount(); i++) {
							if (tab.getTitleAt(i).equals(copy)) {
								tab.remove(i);
							}
						}
					}
					else {
						write = false;
					}
				}
				if (write) {
					if (modelID != null) {
						SBMLReader reader = new SBMLReader();
						SBMLDocument document = new SBMLDocument();
						document = reader.readSBML(tree.getFile());
						document.getModel().setId(modelID);
						FileOutputStream out = new FileOutputStream(new File(root + separator + copy));
						SBMLWriter writer = new SBMLWriter();
						String doc = writer.writeToString(document);
						byte[] output = doc.getBytes();
						out.write(output);
						out.close();
					}
					else if (tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".gcm")
							|| tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
						FileOutputStream out = new FileOutputStream(new File(root + separator + copy));
						FileInputStream in = new FileInputStream(new File(tree.getFile()));
						int read = in.read();
						while (read != -1) {
							out.write(read);
							read = in.read();
						}
						in.close();
						out.close();
					}
					else {
						boolean sim = false;
						for (String s : new File(tree.getFile()).list()) {
							if (s.length() > 3 && s.substring(s.length() - 4).equals(".sim")) {
								sim = true;
							}
						}
						if (sim) {
							new File(root + separator + copy).mkdir();
							// new FileWriter(new File(root + separator + copy + separator +
							// ".sim")).close();
							String[] s = new File(tree.getFile()).list();
							for (String ss : s) {
								if (ss.length() > 4 && ss.substring(ss.length() - 5).equals(".sbml")) {
									SBMLReader reader = new SBMLReader();
									SBMLDocument document = reader.readSBML(tree.getFile() + separator + ss);
									FileOutputStream out = new FileOutputStream(new File(root + separator + copy
											+ separator + ss));
									SBMLWriter writer = new SBMLWriter();
									String doc = writer.writeToString(document);
									byte[] output = doc.getBytes();
									out.write(output);
									out.close();
								}
								else if (ss.length() > 10 && ss.substring(ss.length() - 11).equals(".properties")) {
									FileOutputStream out = new FileOutputStream(new File(root + separator + copy
											+ separator + ss));
									FileInputStream in = new FileInputStream(
											new File(tree.getFile() + separator + ss));
									int read = in.read();
									while (read != -1) {
										out.write(read);
										read = in.read();
									}
									in.close();
									out.close();
								}
								else if (ss.length() > 3
										&& (ss.substring(ss.length() - 4).equals(".tsd")
												|| ss.substring(ss.length() - 4).equals(".dat")
												|| ss.substring(ss.length() - 4).equals(".sad")
												|| ss.substring(ss.length() - 4).equals(".pms") || ss.substring(
												ss.length() - 4).equals(".sim")) && !ss.equals(".sim")) {
									FileOutputStream out;
									if (ss.substring(ss.length() - 4).equals(".pms")) {
										out = new FileOutputStream(new File(root + separator + copy + separator
												+ ss.substring(0, ss.length() - 4) + ".sim"));
									}
									else {
										out = new FileOutputStream(new File(root + separator + copy + separator + ss));
									}
									FileInputStream in = new FileInputStream(
											new File(tree.getFile() + separator + ss));
									int read = in.read();
									while (read != -1) {
										out.write(read);
										read = in.read();
									}
									in.close();
									out.close();
								}
							}
						}
						else {
							new File(root + separator + copy).mkdir();
							String[] s = new File(tree.getFile()).list();
							for (String ss : s) {
								if (ss.length() > 3
										&& (ss.substring(ss.length() - 4).equals(".tsd") || ss.substring(
												ss.length() - 4).equals(".lrn"))) {
									FileOutputStream out = new FileOutputStream(new File(root + separator + copy
											+ separator + ss));
									FileInputStream in = new FileInputStream(
											new File(tree.getFile() + separator + ss));
									int read = in.read();
									while (read != -1) {
										out.write(read);
										read = in.read();
									}
									in.close();
									out.close();
								}
							}
						}
					}
					refreshTree();
				}
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Unable to copy file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getActionCommand().equals("rename")) {
			try {
				for (int i = 0; i < tab.getTabCount(); i++) {
					if (tab.getTitleAt(i).equals(
							tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
						tab.setSelectedIndex(i);
						if (save(i) != 1) {
							return;
						}
						break;
					}
				}
				String modelID = null;
				String rename = JOptionPane.showInputDialog(frame, "Enter A New Filename:", "Rename",
						JOptionPane.PLAIN_MESSAGE);
				if (rename != null) {
					rename = rename.trim();
				}
				else {
					return;
				}
				if (!rename.equals("")) {
					if (tree.getFile().length() >= 5
							&& tree.getFile().substring(tree.getFile().length() - 5).equals(".sbml")
							|| tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".xml")) {
						if (rename.length() > 4) {
							if (!rename.substring(rename.length() - 5).equals(".sbml")
									&& !rename.substring(rename.length() - 4).equals(".xml")) {
								rename += ".sbml";
							}
						}
						else {
							rename += ".sbml";
						}
						if (rename.length() > 4) {
							if (rename.substring(rename.length() - 5).equals(".sbml")) {
								modelID = rename.substring(0, rename.length() - 5);
							}
							else {
								modelID = rename.substring(0, rename.length() - 4);
							}
						}
					}
					else if (tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".gcm")) {
						if (rename.length() > 3) {
							if (!rename.substring(rename.length() - 4).equals(".gcm")) {
								rename += ".gcm";
							}
						}
						else {
							rename += ".gcm";
						}
					}
					else if (tree.getFile().length() >= 4
							&& tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
						if (rename.length() > 3) {
							if (!rename.substring(rename.length() - 4).equals(".grf")) {
								rename += ".grf";
							}
						}
						else {
							rename += ".grf";
						}
					}
					if (rename
							.equals(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
						JOptionPane.showMessageDialog(frame, "Unable to rename file."
								+ "\nNew filename must be different than old filename.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					boolean write = true;
					if (new File(root + separator + rename).exists()) {
						Object[] options = { "Overwrite", "Cancel" };
						int value = JOptionPane.showOptionDialog(frame, "File already exists."
								+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
						if (value == JOptionPane.YES_OPTION) {
							write = true;
							File dir = new File(root + separator + rename);
							if (dir.isDirectory()) {
								deleteDir(dir);
							}
							else {
								System.gc();
								dir.delete();
							}
							for (int i = 0; i < tab.getTabCount(); i++) {
								if (tab.getTitleAt(i).equals(rename)) {
									tab.remove(i);
								}
							}
						}
						else {
							write = false;
						}
					}
					if (write) {
						new File(tree.getFile()).renameTo(new File(root + separator + rename));
						if (modelID != null) {
							SBMLReader reader = new SBMLReader();
							SBMLDocument document = new SBMLDocument();
							document = reader.readSBML(root + separator + rename);
							document.getModel().setId(modelID);
							FileOutputStream out = new FileOutputStream(new File(root + separator + rename));
							SBMLWriter writer = new SBMLWriter();
							String doc = writer.writeToString(document);
							byte[] output = doc.getBytes();
							out.write(output);
							out.close();
						}
						for (int i = 0; i < tab.getTabCount(); i++) {
							if (tab.getTitleAt(i).equals(
									tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
								if (tree.getFile().length() > 4
										&& tree.getFile().substring(tree.getFile().length() - 5).equals(".sbml")
										|| tree.getFile().length() > 3
										&& tree.getFile().substring(tree.getFile().length() - 4).equals(".xml")) {
									((SBML_Editor) tab.getComponentAt(i)).setModelID(modelID);
									((SBML_Editor) tab.getComponentAt(i)).setFile(root + separator + rename);
									tab.setTitleAt(i, rename);
								}
								else if (tree.getFile().length() > 3
										&& tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
									((Graph) tab.getComponentAt(i)).setGraphName(rename);
									tab.setTitleAt(i, rename);
								}
								else {
									for (int j = 0; j < ((JTabbedPane) tab.getComponentAt(i)).getTabCount(); j++) {
										if (((JTabbedPane) tab.getComponentAt(i)).getComponent(j).getName().equals(
												"Simulate")) {
											((Reb2Sac) ((JTabbedPane) tab.getComponentAt(i)).getComponent(j))
													.setSim(rename);
										}
										else if (((JTabbedPane) tab.getComponentAt(i)).getComponent(j).getName()
												.contains("TSD Graph")) {
											if (((JTabbedPane) tab.getComponentAt(i)).getComponent(j) instanceof Graph) {
												Graph g = ((Graph) ((JTabbedPane) tab.getComponentAt(i)).getComponent(j));
												g.setDirectory(root + separator + rename);
											}
										}
									}
									tab.setTitleAt(i, rename);
								}
							}
						}
						refreshTree();
					}
				}
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "Unable to rename selected file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getActionCommand().equals("openGraph")) {
			boolean done = false;
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (tab.getTitleAt(i).equals(
						tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
					tab.setSelectedIndex(i);
					done = true;
				}
			}
			if (!done) {
				if (tree.getFile().split(separator)[tree.getFile().split(separator).length - 1]
						.contains(".grf")) {
					addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
							new Graph(null, "amount", "title", "tsd.printer", root, "time", this, tree.getFile(),
									log, tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
									true, false), "TSD Graph");
				}
				else {
					addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
							new Graph(null, "amount", "title", "tsd.printer", root, "time", this, tree.getFile(),
									log, tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
									false, false), "Probability Graph");
				}
			}
		}
	}

	public int getTab(String name) {
		for (int i = 0; i < tab.getTabCount(); i++) {
			if (tab.getTitleAt(i).equals(name)) {
				return i;
			}
		}
		return -1;
	}

	public void deleteDir(File dir) {
		int count = 0;
		do {
			File[] list = dir.listFiles();
			System.gc();
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					deleteDir(list[i]);
				}
				else {
					list[i].delete();
				}
			}
			count++;
		}
		while (!dir.delete() && count != 100);
		if (count == 100) {
			JOptionPane.showMessageDialog(frame, "Unable to delete.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method adds a new project to recent list
	 */
	public void addRecentProject(String projDir) {
		// boolean newOne = true;
		for (int i = 0; i < numberRecentProj; i++) {
			if (recentProjectPaths[i].equals(projDir)) {
				for (int j = 0; j <= i; j++) {
					String save = recentProjectPaths[j];
					recentProjects[j].setText(projDir.split(separator)[projDir.split(separator).length - 1]);
					file.add(recentProjects[j]);
					recentProjectPaths[j] = projDir;
					projDir = save;
				}
				for (int j = i + 1; j < numberRecentProj; j++) {
					file.add(recentProjects[j]);
				}
				return;
			}
		}
		if (numberRecentProj < 5) {
			numberRecentProj++;
		}
		for (int i = 0; i < numberRecentProj; i++) {
			String save = recentProjectPaths[i];
			recentProjects[i].setText(projDir.split(separator)[projDir.split(separator).length - 1]);
			file.add(recentProjects[i]);
			recentProjectPaths[i] = projDir;
			projDir = save;
		}
	}

	/**
	 * This method refreshes the menu.
	 */
	public void refresh() {
		mainPanel.remove(tree);
		tree = new FileTree(new File(root), this);
		mainPanel.add(tree, "West");
		mainPanel.validate();
	}

	/**
	 * This method refreshes the tree.
	 */
	public void refreshTree() {
		tree.fixTree();
		mainPanel.validate();
		updateGCM();
	}

	/**
	 * This method adds the given Component to a tab.
	 */
	public void addTab(String name, Component panel, String tabName) {
		tab.addTab(name, panel);
		if (tabName != null) {
			tab.getComponentAt(tab.getComponents().length - 1).setName(tabName);
		}
		else {
			tab.getComponentAt(tab.getComponents().length - 1).setName(name);
		}
		tab.setSelectedIndex(tab.getComponents().length - 1);
	}

	/**
	 * This method removes the given component from the tabs.
	 */
	public void removeTab(Component component) {
		tab.remove(component);
	}

	public JTabbedPane getTab() {
		return tab;
	}

	/**
	 * Prompts the user to save work that has been done.
	 */
	public int save(int index) {
		if (tab.getComponentAt(index).getName().contains(("GCM"))) {
			if (tab.getComponentAt(index) instanceof GCM2SBMLEditor) {
				GCM2SBMLEditor editor = (GCM2SBMLEditor) tab.getComponentAt(index);
				if (editor.isDirty()) {
					Object[] options = { "Yes", "No", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "Do you want to save changes to "
							+ tab.getTitleAt(index) + "?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						editor.save("gcm");
						return 1;
					}
					else if (value == JOptionPane.NO_OPTION) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		else if (tab.getComponentAt(index).getName().equals("SBML Editor")) {
			if (tab.getComponentAt(index) instanceof SBML_Editor) {
				if (((SBML_Editor) tab.getComponentAt(index)).hasChanged()) {
					Object[] options = { "Yes", "No", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "Do you want to save changes to "
							+ tab.getTitleAt(index) + "?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						((SBML_Editor) tab.getComponentAt(index)).save(false, "");
						return 1;
					}
					else if (value == JOptionPane.NO_OPTION) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		else if (tab.getComponentAt(index).getName().contains("Graph")) {
			if (tab.getComponentAt(index) instanceof Graph) {
				if (((Graph) tab.getComponentAt(index)).hasChanged()) {
					Object[] options = { "Yes", "No", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "Do you want to save changes to "
							+ tab.getTitleAt(index) + "?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						((Graph) tab.getComponentAt(index)).save();
						return 1;
					}
					else if (value == JOptionPane.NO_OPTION) {
						return 1;
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		else {
			if (tab.getComponentAt(index) instanceof JTabbedPane) {
				for (int i = 0; i < ((JTabbedPane) tab.getComponentAt(index)).getTabCount(); i++) {
					if (((JTabbedPane) tab.getComponentAt(index)).getComponentAt(i).getName().equals(
							"Simulate")) {
						if (((Reb2Sac) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i)).hasChanged()) {
							Object[] options = { "Yes", "No", "Cancel" };
							int value = JOptionPane.showOptionDialog(frame,
									"Do you want to save simulation option changes for " + tab.getTitleAt(index)
											+ "?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
							if (value == JOptionPane.YES_OPTION) {
								((Reb2Sac) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i)).save();
							}
							else if (value == JOptionPane.CANCEL_OPTION) {
								return 0;
							}
						}
					}
					else if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i).getName().equals(
							"SBML Editor")) {
						if (((SBML_Editor) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i))
								.hasChanged()) {
							Object[] options = { "Yes", "No", "Cancel" };
							int value = JOptionPane.showOptionDialog(frame,
									"Do you want to save parameter changes for " + tab.getTitleAt(index) + "?",
									"Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
									null, options, options[0]);
							if (value == JOptionPane.YES_OPTION) {
								((SBML_Editor) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i)).save(
										false, "");
							}
							else if (value == JOptionPane.CANCEL_OPTION) {
								return 0;
							}
						}
					}
					else if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i).getName().equals(
							"Learn")) {
						if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i) instanceof Learn) {
							if (((Learn) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i)).hasChanged()) {
								Object[] options = { "Yes", "No", "Cancel" };
								int value = JOptionPane.showOptionDialog(frame,
										"Do you want to save learn option changes for " + tab.getTitleAt(index) + "?",
										"Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
										null, options, options[0]);
								if (value == JOptionPane.YES_OPTION) {
									if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i) instanceof Learn) {
										((Learn) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i)).save();
									}
								}
								else if (value == JOptionPane.CANCEL_OPTION) {
									return 0;
								}
							}
						}
					}
					else if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i).getName().equals(
							"Data Manager")) {
						if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i) instanceof DataManager) {
							((DataManager) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i))
									.saveChanges(tab.getTitleAt(index));
						}
					}
					else if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i).getName().contains(
							"Graph")) {
						if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i) instanceof Graph) {
							if (((Graph) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i)).hasChanged()) {
								Object[] options = { "Yes", "No", "Cancel" };
								int value = JOptionPane.showOptionDialog(frame,
										"Do you want to save graph changes for " + tab.getTitleAt(index) + "?",
										"Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
										null, options, options[0]);
								if (value == JOptionPane.YES_OPTION) {
									if (((JTabbedPane) tab.getComponentAt(index)).getComponent(i) instanceof Graph) {
										Graph g = ((Graph) ((JTabbedPane) tab.getComponentAt(index)).getComponent(i));
										g.save();
									}
								}
								else if (value == JOptionPane.CANCEL_OPTION) {
									return 0;
								}
							}
						}
					}
				}
			}
			return 1;
		}
	}

	/**
	 * Saves a circuit from a learn view to the project view
	 */
	public void saveGcm(String filename, String path) {
		try {
			boolean write = true;
			if (new File(root + separator + filename).exists()) {
				Object[] options = { "Overwrite", "Cancel" };
				int value = JOptionPane.showOptionDialog(frame, "File already exists."
						+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (value == JOptionPane.YES_OPTION) {
					write = true;
					File dir = new File(root + separator + filename);
					if (dir.isDirectory()) {
						deleteDir(dir);
					}
					else {
						System.gc();
						dir.delete();
					}
					for (int i = 0; i < tab.getTabCount(); i++) {
						if (tab.getTitleAt(i).equals(filename)) {
							tab.remove(i);
						}
					}
				}
				else {
					write = false;
				}
			}
			if (write) {
				FileOutputStream out = new FileOutputStream(new File(root + separator + filename));
				FileInputStream in = new FileInputStream(new File(path));
				int read = in.read();
				while (read != -1) {
					out.write(read);
					read = in.read();
				}
				in.close();
				out.close();
				refreshTree();
			}
		}
		catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, "Unable to save genetic circuit.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Returns the frame.
	 */
	public JFrame frame() {
		return frame;
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.removeAll();
			if (tree.getFile().length() > 4
					&& tree.getFile().substring(tree.getFile().length() - 5).equals(".sbml")
					|| tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".xml")) {
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("sbmlEditor");
				JMenuItem graph = new JMenuItem("View Network");
				graph.addActionListener(this);
				graph.setActionCommand("graph");
				JMenuItem browse = new JMenuItem("View in Browser");
				browse.addActionListener(this);
				browse.setActionCommand("browse");
				JMenuItem simulate = new JMenuItem("Create Analysis View");
				simulate.addActionListener(this);
				simulate.setActionCommand("simulate");
				JMenuItem createLearn = new JMenuItem("Create Learn View");
				createLearn.addActionListener(this);
				createLearn.setActionCommand("createLearn");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(simulate);
				popup.add(createLearn);
				popup.addSeparator();
				popup.add(graph);
				popup.add(browse);
				popup.addSeparator();
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".gcm")) {
				JMenuItem create = new JMenuItem("Create Analysis View");
				create.addActionListener(this);
				create.setActionCommand("createSim");
				JMenuItem createLearn = new JMenuItem("Create Learn View");
				createLearn.addActionListener(this);
				createLearn.setActionCommand("createLearn");
				JMenuItem createSBML = new JMenuItem("Create SBML File");
				createSBML.addActionListener(this);
				createSBML.setActionCommand("createSBML");
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("dotEditor");
				JMenuItem graph = new JMenuItem("View Genetic Circuit");
				graph.addActionListener(this);
				graph.setActionCommand("graphDot");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(create);
				popup.add(createLearn);
				popup.add(createSBML);
				popup.addSeparator();
				popup.add(graph);
				popup.addSeparator();
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("openGraph");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".prb")) {
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("openGraph");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (new File(tree.getFile()).isDirectory() && !tree.getFile().equals(root)) {
				boolean sim = false;
				for (String s : new File(tree.getFile()).list()) {
					if (s.length() > 3 && s.substring(s.length() - 4).equals(".sim")) {
						sim = true;
					}
				}
				JMenuItem open;
				if (sim) {
					open = new JMenuItem("Open Analysis View");
					open.addActionListener(this);
					open.setActionCommand("openSim");
				}
				else {
					open = new JMenuItem("Open Learn View");
					open.addActionListener(this);
					open.setActionCommand("openLearn");
				}
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("deleteSim");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(open);
				popup.addSeparator();
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			if (popup.getComponentCount() != 0) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			if (tree.getFile() != null) {
				if (tree.getFile().length() >= 5
						&& tree.getFile().substring(tree.getFile().length() - 5).equals(".sbml")
						|| tree.getFile().length() >= 4
						&& tree.getFile().substring(tree.getFile().length() - 4).equals(".xml")) {
					try {
						boolean done = false;
						for (int i = 0; i < tab.getTabCount(); i++) {
							if (tab.getTitleAt(i).equals(
									tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
								tab.setSelectedIndex(i);
								done = true;
							}
						}
						if (!done) {
							addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
									new SBML_Editor(tree.getFile(), null, log, this, null, null), "SBML Editor");
						}
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "You must select a valid sbml file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				else if (tree.getFile().length() >= 4
						&& tree.getFile().substring(tree.getFile().length() - 4).equals(".gcm")) {
					try {

						String filename = tree.getFile();
						String directory = "";
						String theFile = "";
						if (filename.lastIndexOf('/') >= 0) {
							directory = filename.substring(0, filename.lastIndexOf('/') + 1);
							theFile = filename.substring(filename.lastIndexOf('/') + 1);
						}
						if (filename.lastIndexOf('\\') >= 0) {
							directory = filename.substring(0, filename.lastIndexOf('\\') + 1);
							theFile = filename.substring(filename.lastIndexOf('\\') + 1);
						}
						File work = new File(directory);
						int i = getTab(theFile);
						if (i != -1) {
							tab.setSelectedIndex(i);
						}
						else {
							addTab(theFile, new GCM2SBMLEditor(work.getAbsolutePath(), theFile, this, log),
									"GCM Editor");
						}
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "Unable to view this gcm file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				else if (tree.getFile().length() >= 4
						&& tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
					boolean done = false;
					for (int i = 0; i < tab.getTabCount(); i++) {
						if (tab.getTitleAt(i).equals(
								tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
							tab.setSelectedIndex(i);
							done = true;
						}
					}
					if (!done) {
						addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
								new Graph(null, "amount", "title", "tsd.printer", root, "time", this, tree
										.getFile(), log, tree.getFile().split(separator)[tree.getFile()
										.split(separator).length - 1], true, false), "TSD Graph");
					}
				}
				else if (tree.getFile().length() >= 4
						&& tree.getFile().substring(tree.getFile().length() - 4).equals(".prb")) {
					boolean done = false;
					for (int i = 0; i < tab.getTabCount(); i++) {
						if (tab.getTitleAt(i).equals(
								tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
							tab.setSelectedIndex(i);
							done = true;
						}
					}
					if (!done) {
						addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1],
								new Graph(null, "amount", "title", "tsd.printer", root, "time", this, tree
										.getFile(), log, tree.getFile().split(separator)[tree.getFile()
										.split(separator).length - 1], false, false), "Probability Graph");
					}
				}
				else if (new File(tree.getFile()).isDirectory() && !tree.getFile().equals(root)) {
					boolean sim = false;
					for (String s : new File(tree.getFile()).list()) {
						if (s.length() > 3 && s.substring(s.length() - 4).equals(".sim")) {
							sim = true;
						}
					}
					if (sim) {
						openSim();
					}
					else {
						openLearn();
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.removeAll();
			if (tree.getFile().length() > 4
					&& tree.getFile().substring(tree.getFile().length() - 5).equals(".sbml")
					|| tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".xml")) {
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("sbmlEditor");
				JMenuItem graph = new JMenuItem("View Network");
				graph.addActionListener(this);
				graph.setActionCommand("graph");
				JMenuItem browse = new JMenuItem("View in Browser");
				browse.addActionListener(this);
				browse.setActionCommand("browse");
				JMenuItem simulate = new JMenuItem("Create Analysis View");
				simulate.addActionListener(this);
				simulate.setActionCommand("simulate");
				JMenuItem createLearn = new JMenuItem("Create Learn View");
				createLearn.addActionListener(this);
				createLearn.setActionCommand("createLearn");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(simulate);
				popup.add(createLearn);
				popup.addSeparator();
				popup.add(graph);
				popup.add(browse);
				popup.addSeparator();
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".gcm")) {
				JMenuItem create = new JMenuItem("Create Analysis View");
				create.addActionListener(this);
				create.setActionCommand("createSim");
				JMenuItem createLearn = new JMenuItem("Create Learn View");
				createLearn.addActionListener(this);
				createLearn.setActionCommand("createLearn");
				JMenuItem createSBML = new JMenuItem("Create SBML File");
				createSBML.addActionListener(this);
				createSBML.setActionCommand("createSBML");
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("dotEditor");
				JMenuItem graph = new JMenuItem("View Genetic Circuit");
				graph.addActionListener(this);
				graph.setActionCommand("graphDot");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(create);
				popup.add(createLearn);
				popup.add(createSBML);
				popup.addSeparator();
				popup.add(graph);
				popup.addSeparator();
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (tree.getFile().length() > 3
					&& tree.getFile().substring(tree.getFile().length() - 4).equals(".grf")) {
				JMenuItem edit = new JMenuItem("View/Edit");
				edit.addActionListener(this);
				edit.setActionCommand("openGraph");
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("delete");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(edit);
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			else if (new File(tree.getFile()).isDirectory() && !tree.getFile().equals(root)) {
				boolean sim = false;
				for (String s : new File(tree.getFile()).list()) {
					if (s.length() > 3 && s.substring(s.length() - 4).equals(".sim")) {
						sim = true;
					}
				}
				JMenuItem open;
				if (sim) {
					open = new JMenuItem("Open Analysis View");
					open.addActionListener(this);
					open.setActionCommand("openSim");
				}
				else {
					open = new JMenuItem("Open Learn View");
					open.addActionListener(this);
					open.setActionCommand("openLearn");
				}
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(this);
				delete.setActionCommand("deleteSim");
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(this);
				copy.setActionCommand("copy");
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(this);
				rename.setActionCommand("rename");
				popup.add(open);
				popup.addSeparator();
				popup.add(copy);
				popup.add(rename);
				popup.add(delete);
			}
			if (popup.getComponentCount() != 0) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void simulate(boolean isDot) throws Exception {
		if (isDot) {
			String simName = JOptionPane.showInputDialog(frame, "Enter Analysis ID:", "Analysis ID",
					JOptionPane.PLAIN_MESSAGE);
			if (simName != null && !simName.trim().equals("")) {
				simName = simName.trim();
				File f = new File(root + separator + simName);
				if (f.exists()) {
					Object[] options = { "Overwrite", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "File already exists."
							+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						File dir = new File(root + separator + simName);
						if (dir.isDirectory()) {
							deleteDir(dir);
						}
						else {
							System.gc();
							dir.delete();
						}
						for (int i = 0; i < tab.getTabCount(); i++) {
							if (tab.getTitleAt(i).equals(simName)) {
								tab.remove(i);
							}
						}
					}
					else {
						return;
					}
				}
				new File(root + separator + simName).mkdir();
				// new FileWriter(new File(root + separator + simName + separator +
				// ".sim")).close();
				String[] dot = tree.getFile().split(separator);
				String sbmlFile = /*
													 * root + separator + simName + separator +
													 */(dot[dot.length - 1].substring(0, dot[dot.length - 1].length() - 3) + "sbml");
				GCMParser parser = new GCMParser(tree.getFile());
				GeneticNetwork network = parser.buildNetwork();
				GeneticNetwork.setRoot(root + File.separator);
				network.mergeSBML(root + separator + simName + separator + sbmlFile);
				try {
					FileOutputStream out = new FileOutputStream(new File(root + separator + simName.trim()
							+ separator + simName.trim() + ".sim"));
					out.write((dot[dot.length - 1] + "\n").getBytes());
					out.close();
				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, "Unable to save parameter file!",
							"Error Saving File", JOptionPane.ERROR_MESSAGE);
				}
				// network.outputSBML(root + separator + sbmlFile);
				refreshTree();
				sbmlFile = root + separator + simName + separator
						+ (dot[dot.length - 1].substring(0, dot[dot.length - 1].length() - 3) + "sbml");
				JTabbedPane simTab = new JTabbedPane();
				Reb2Sac reb2sac = new Reb2Sac(sbmlFile, sbmlFile, root, this, simName.trim(), log, simTab,
						null);
				simTab.addTab("Simulation Options", reb2sac);
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("Simulate");
				simTab.addTab("Abstraction Options", reb2sac.getAdvanced());
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
				simTab.addTab("Advanced Options", reb2sac.getProperties());
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
				SBML_Editor sbml = new SBML_Editor(sbmlFile, reb2sac, log, this, root + separator
						+ simName.trim(), root + separator + simName.trim() + separator + simName.trim()
						+ ".sim");
				reb2sac.setSbml(sbml);
				simTab.addTab("Parameter Editor", sbml);
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("SBML Editor");
				simTab.addTab("TSD Graph", reb2sac.createGraph(null));
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD Graph");
				simTab.addTab("Probability Graph", reb2sac.createProbGraph(null));
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("ProbGraph");
				/*
				 * JLabel noData = new JLabel("No data available"); Font font =
				 * noData.getFont(); font = font.deriveFont(Font.BOLD, 42.0f);
				 * noData.setFont(font);
				 * noData.setHorizontalAlignment(SwingConstants.CENTER);
				 * simTab.addTab("TSD Graph", noData);
				 * simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD
				 * Graph"); JLabel noData1 = new JLabel("No data available"); Font font1 =
				 * noData1.getFont(); font1 = font1.deriveFont(Font.BOLD, 42.0f);
				 * noData1.setFont(font1);
				 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
				 * simTab.addTab("Probability Graph", noData1);
				 * simTab.getComponentAt(simTab.getComponents().length -
				 * 1).setName("ProbGraph");
				 */
				addTab(simName, simTab, null);
			}
		}
		else {
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (tab.getTitleAt(i).equals(
						tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
					tab.setSelectedIndex(i);
					if (save(i) != 1) {
						return;
					}
					break;
				}
			}
			SBMLReader reader = new SBMLReader();
			SBMLDocument document = reader.readSBML(tree.getFile());
			// document.setLevel(2);
			document.setLevelAndVersion(2, 3);
			String simName = JOptionPane.showInputDialog(frame, "Enter analysis id:", "Analysis ID",
					JOptionPane.PLAIN_MESSAGE);
			if (simName != null && !simName.trim().equals("")) {
				simName = simName.trim();
				File f = new File(root + separator + simName);
				if (f.exists()) {
					Object[] options = { "Overwrite", "Cancel" };
					int value = JOptionPane.showOptionDialog(frame, "File already exists."
							+ "\nDo you want to overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						File dir = new File(root + separator + simName);
						if (dir.isDirectory()) {
							deleteDir(dir);
						}
						else {
							System.gc();
							dir.delete();
						}
						for (int i = 0; i < tab.getTabCount(); i++) {
							if (tab.getTitleAt(i).equals(simName)) {
								tab.remove(i);
							}
						}
					}
					else {
						return;
					}
				}
				new File(root + separator + simName).mkdir();
				// new FileWriter(new File(root + separator + simName + separator +
				// ".sim")).close();
				String sbmlFile = tree.getFile();
				String[] sbml1 = tree.getFile().split(separator);
				String sbmlFileProp = root + separator + simName + separator + sbml1[sbml1.length - 1];
				try {
					FileOutputStream out = new FileOutputStream(new File(root + separator + simName.trim()
							+ separator + simName.trim() + ".sim"));
					out.write((sbml1[sbml1.length - 1] + "\n").getBytes());
					out.close();
				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, "Unable to save parameter file!",
							"Error Saving File", JOptionPane.ERROR_MESSAGE);
				}
				new FileOutputStream(new File(sbmlFileProp)).close();
				/*
				 * try { FileOutputStream out = new FileOutputStream(new
				 * File(sbmlFile)); SBMLWriter writer = new SBMLWriter(); String doc =
				 * writer.writeToString(document); byte[] output = doc.getBytes();
				 * out.write(output); out.close(); } catch (Exception e1) {
				 * JOptionPane.showMessageDialog(frame, "Unable to copy sbml file to
				 * output location.", "Error", JOptionPane.ERROR_MESSAGE); }
				 */
				refreshTree();
				JTabbedPane simTab = new JTabbedPane();
				Reb2Sac reb2sac = new Reb2Sac(sbmlFile, sbmlFileProp, root, this, simName.trim(), log,
						simTab, null);
				simTab.addTab("Simulation Options", reb2sac);
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("Simulate");
				simTab.addTab("Abstraction Options", reb2sac.getAdvanced());
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
				simTab.addTab("Advanced Options", reb2sac.getProperties());
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
				SBML_Editor sbml = new SBML_Editor(sbmlFile, reb2sac, log, this, root + separator
						+ simName.trim(), root + separator + simName.trim() + separator + simName.trim()
						+ ".sim");
				reb2sac.setSbml(sbml);
				simTab.addTab("Parameter Editor", sbml);
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("SBML Editor");
				simTab.addTab("TSD Graph", reb2sac.createGraph(null));
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD Graph");
				simTab.addTab("Probability Graph", reb2sac.createProbGraph(null));
				simTab.getComponentAt(simTab.getComponents().length - 1).setName("ProbGraph");
				/*
				 * JLabel noData = new JLabel("No data available"); Font font =
				 * noData.getFont(); font = font.deriveFont(Font.BOLD, 42.0f);
				 * noData.setFont(font);
				 * noData.setHorizontalAlignment(SwingConstants.CENTER);
				 * simTab.addTab("TSD Graph", noData);
				 * simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD
				 * Graph"); JLabel noData1 = new JLabel("No data available"); Font font1 =
				 * noData1.getFont(); font1 = font1.deriveFont(Font.BOLD, 42.0f);
				 * noData1.setFont(font1);
				 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
				 * simTab.addTab("Probability Graph", noData1);
				 * simTab.getComponentAt(simTab.getComponents().length -
				 * 1).setName("ProbGraph");
				 */
				addTab(simName, simTab, null);
			}
		}
	}

	private void openLearn() {
		boolean done = false;
		for (int i = 0; i < tab.getTabCount(); i++) {
			if (tab.getTitleAt(i).equals(
					tree.getFile().split(separator)[tree.getFile().split(separator).length - 1])) {
				tab.setSelectedIndex(i);
				done = true;
			}
		}
		if (!done) {
			JTabbedPane lrnTab = new JTabbedPane();
			// String graphFile = "";
			String open = null;
			if (new File(tree.getFile()).isDirectory()) {
				String[] list = new File(tree.getFile()).list();
				int run = 0;
				for (int i = 0; i < list.length; i++) {
					if (!(new File(list[i]).isDirectory()) && list[i].length() > 4) {
						String end = "";
						for (int j = 1; j < 5; j++) {
							end = list[i].charAt(list[i].length() - j) + end;
						}
						if (end.equals(".tsd") || end.equals(".dat") || end.equals(".csv")) {
							if (list[i].contains("run-")) {
								int tempNum = Integer.parseInt(list[i]
										.substring(4, list[i].length() - end.length()));
								if (tempNum > run) {
									run = tempNum;
									// graphFile = tree.getFile() + separator + list[i];
								}
							}
						}
						else if (end.equals(".grf")) {
							open = tree.getFile() + separator + list[i];
						}
					}
				}
			}

			String lrnFile = tree.getFile() + separator
					+ tree.getFile().split(separator)[tree.getFile().split(separator).length - 1] + ".lrn";
			String lrnFile2 = tree.getFile() + separator + ".lrn";
			Properties load = new Properties();
			String learnFile = "";
			try {
				if (new File(lrnFile2).exists()) {
					FileInputStream in = new FileInputStream(new File(lrnFile2));
					load.load(in);
					in.close();
					new File(lrnFile2).delete();
				}
				if (new File(lrnFile).exists()) {
					FileInputStream in = new FileInputStream(new File(lrnFile));
					load.load(in);
					in.close();
					if (load.containsKey("genenet.file")) {
						learnFile = load.getProperty("genenet.file");
					}
				}
				FileOutputStream out = new FileOutputStream(new File(lrnFile));
				load.store(out, learnFile);
				out.close();

			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(frame(), "Unable to load properties file!",
						"Error Loading Properties", JOptionPane.ERROR_MESSAGE);
			}
			for (int i = 0; i < tab.getTabCount(); i++) {
				if (tab.getTitleAt(i).equals(
						learnFile.split(separator)[learnFile.split(separator).length - 1])) {
					tab.setSelectedIndex(i);
					if (save(i) != 1) {
						return;
					}
					break;
				}
			}
			// if (!graphFile.equals("")) {
			lrnTab.addTab("Data Manager", new DataManager(tree.getFile(), this));
			lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("Data Manager");
			lrnTab.addTab("Learn", new Learn(tree.getFile(), log, this));
			lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("Learn");
			lrnTab.addTab("TSD Graph", new Graph(null, "amount", tree.getFile().split(separator)[tree
					.getFile().split(separator).length - 1]
					+ " data", "tsd.printer", tree.getFile(), "time", this, open, log, null, true, true));
			lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("TSD Graph");
			// }
			/*
			 * else { lrnTab.addTab("Data Manager", new DataManager(tree.getFile(),
			 * this)); lrnTab.getComponentAt(lrnTab.getComponents().length -
			 * 1).setName("Data Manager"); JLabel noData = new JLabel("No data
			 * available"); Font font = noData.getFont(); font =
			 * font.deriveFont(Font.BOLD, 42.0f); noData.setFont(font);
			 * noData.setHorizontalAlignment(SwingConstants.CENTER);
			 * lrnTab.addTab("Learn", noData);
			 * lrnTab.getComponentAt(lrnTab.getComponents().length -
			 * 1).setName("Learn"); JLabel noData1 = new JLabel("No data available");
			 * font = noData1.getFont(); font = font.deriveFont(Font.BOLD, 42.0f);
			 * noData1.setFont(font);
			 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
			 * lrnTab.addTab("TSD Graph", noData1);
			 * lrnTab.getComponentAt(lrnTab.getComponents().length - 1).setName("TSD
			 * Graph"); }
			 */
			addTab(tree.getFile().split(separator)[tree.getFile().split(separator).length - 1], lrnTab,
					null);
		}
	}

	private void openSim() {
		String filename = tree.getFile();
		boolean done = false;
		for (int i = 0; i < tab.getTabCount(); i++) {
			if (tab.getTitleAt(i).equals(filename.split(separator)[filename.split(separator).length - 1])) {
				tab.setSelectedIndex(i);
				done = true;
			}
		}
		if (!done) {
			if (filename != null && !filename.equals("")) {
				if (new File(filename).isDirectory()) {
					if (new File(filename + separator + ".sim").exists()) {
						new File(filename + separator + ".sim").delete();
					}
					String[] list = new File(filename).list();
					String getAFile = "";
					// String probFile = "";
					String openFile = "";
					// String graphFile = "";
					String open = null;
					String openProb = null;
					int run = 0;
					for (int i = 0; i < list.length; i++) {
						if (!(new File(list[i]).isDirectory()) && list[i].length() > 4) {
							String end = "";
							for (int j = 1; j < 5; j++) {
								end = list[i].charAt(list[i].length() - j) + end;
							}
							if (end.equals("sbml")) {
								getAFile = filename + separator + list[i];
							}
							else if (end.equals(".xml") && getAFile.equals("")) {
								getAFile = filename + separator + list[i];
							}
							else if (end.equals(".txt") && list[i].contains("sim-rep")) {
								// probFile = filename + separator + list[i];
							}
							else if (end.equals("ties") && list[i].contains("properties")
									&& !(list[i].equals("species.properties"))) {
								openFile = filename + separator + list[i];
							}
							else if (end.equals(".tsd") || end.equals(".dat") || end.equals(".csv")
									|| end.contains("=")) {
								if (list[i].contains("run-")) {
									int tempNum = Integer.parseInt(list[i].substring(4, list[i].length()
											- end.length()));
									if (tempNum > run) {
										run = tempNum;
										// graphFile = filename + separator + list[i];
									}
								}
								else if (list[i].contains("euler-run.") || list[i].contains("gear1-run.")
										|| list[i].contains("gear2-run.") || list[i].contains("rk4imp-run.")
										|| list[i].contains("rk8pd-run.") || list[i].contains("rkf45-run.")) {
									// graphFile = filename + separator + list[i];
								}
								else if (end.contains("=")) {
									// graphFile = filename + separator + list[i];
								}
							}
							else if (end.equals(".grf")) {
								open = filename + separator + list[i];
							}
							else if (end.equals(".prb")) {
								openProb = filename + separator + list[i];
							}
						}
						else if (new File(filename + separator + list[i]).isDirectory()) {
							String[] s = new File(filename + separator + list[i]).list();
							for (int j = 0; j < s.length; j++) {
								if (s[j].contains("sim-rep")) {
									// probFile = filename + separator + list[i] + separator +
									// s[j];
								}
								else if (s[j].contains(".tsd")) {
									// graphFile = filename + separator + list[i] + separator +
									// s[j];
								}
							}
						}
					}
					if (!getAFile.equals("")) {
						String[] split = filename.split(separator);
						String simFile = root + separator + split[split.length - 1].trim() + separator
								+ split[split.length - 1].trim() + ".sim";
						String pmsFile = root + separator + split[split.length - 1].trim() + separator
								+ split[split.length - 1].trim() + ".pms";
						if (new File(pmsFile).exists()) {
							if (new File(simFile).exists()) {
								new File(pmsFile).delete();
							}
							else {
								new File(pmsFile).renameTo(new File(simFile));
							}
						}
						String sbmlLoadFile = null;
						if (new File(simFile).exists()) {
							try {
								Scanner s = new Scanner(new File(simFile));
								if (s.hasNextLine()) {
									sbmlLoadFile = s.nextLine();
									sbmlLoadFile = sbmlLoadFile.split(separator)[sbmlLoadFile.split(separator).length - 1];
									if (sbmlLoadFile.contains(".gcm")) {
										GCMParser parser = new GCMParser(root + separator + sbmlLoadFile);
										GeneticNetwork network = parser.buildNetwork();
										GeneticNetwork.setRoot(root + File.separator);
										sbmlLoadFile = root + separator + split[split.length - 1].trim() + separator
												+ sbmlLoadFile.replace(".gcm", ".sbml");
										network.mergeSBML(sbmlLoadFile);
									}
									else {
										sbmlLoadFile = root + separator + sbmlLoadFile;
									}
								}
								while (s.hasNextLine()) {
									s.nextLine();
								}
								s.close();
								File f = new File(sbmlLoadFile);
								if (!f.exists()) {
									sbmlLoadFile = root + separator + f.getName();
								}
							}
							catch (Exception e) {
								JOptionPane.showMessageDialog(frame, "Unable to load sbml file.", "Error",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						else {
							sbmlLoadFile = root + separator
									+ getAFile.split(separator)[getAFile.split(separator).length - 1];
							if (!new File(sbmlLoadFile).exists()) {
								sbmlLoadFile = getAFile;
								/*
								 * JOptionPane.showMessageDialog(frame, "Unable to load sbml
								 * file.", "Error", JOptionPane.ERROR_MESSAGE); return;
								 */
							}
						}
						for (int i = 0; i < tab.getTabCount(); i++) {
							if (tab.getTitleAt(i).equals(
									sbmlLoadFile.split(separator)[sbmlLoadFile.split(separator).length - 1])) {
								tab.setSelectedIndex(i);
								if (save(i) != 1) {
									return;
								}
								break;
							}
						}
						JTabbedPane simTab = new JTabbedPane();
						Reb2Sac reb2sac = new Reb2Sac(sbmlLoadFile, getAFile, root, this,
								split[split.length - 1].trim(), log, simTab, openFile);
						simTab.addTab("Simulation Options", reb2sac);
						simTab.getComponentAt(simTab.getComponents().length - 1).setName("Simulate");
						simTab.addTab("Abstraction Options", reb2sac.getAdvanced());
						simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
						simTab.addTab("Advanced Options", reb2sac.getProperties());
						simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
						SBML_Editor sbml = new SBML_Editor(sbmlLoadFile, reb2sac, log, this, root + separator
								+ split[split.length - 1].trim(), root + separator + split[split.length - 1].trim()
								+ separator + split[split.length - 1].trim() + ".sim");
						reb2sac.setSbml(sbml);
						simTab.addTab("Parameter Editor", sbml);
						simTab.getComponentAt(simTab.getComponents().length - 1).setName("SBML Editor");
						// if (open != null) {
						simTab.addTab("TSD Graph", reb2sac.createGraph(open));
						simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD Graph");
						/*
						 * } else if (!graphFile.equals("")) { simTab.addTab("TSD Graph",
						 * reb2sac.createGraph(open));
						 * simTab.getComponentAt(simTab.getComponents().length -
						 * 1).setName("TSD Graph"); } /* else { JLabel noData = new
						 * JLabel("No data available"); Font font = noData.getFont(); font =
						 * font.deriveFont(Font.BOLD, 42.0f); noData.setFont(font);
						 * noData.setHorizontalAlignment(SwingConstants.CENTER);
						 * simTab.addTab("TSD Graph", noData);
						 * simTab.getComponentAt(simTab.getComponents().length -
						 * 1).setName("TSD Graph"); }
						 */
						// if (openProb != null) {
						simTab.addTab("Probability Graph", reb2sac.createProbGraph(openProb));
						simTab.getComponentAt(simTab.getComponents().length - 1).setName("ProbGraph");
						/*
						 * } else if (!probFile.equals("")) { simTab.addTab("Probability
						 * Graph", reb2sac.createProbGraph(openProb));
						 * simTab.getComponentAt(simTab.getComponents().length -
						 * 1).setName("ProbGraph"); } else { JLabel noData1 = new JLabel("No
						 * data available"); Font font1 = noData1.getFont(); font1 =
						 * font1.deriveFont(Font.BOLD, 42.0f); noData1.setFont(font1);
						 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
						 * simTab.addTab("Probability Graph", noData1);
						 * simTab.getComponentAt(simTab.getComponents().length -
						 * 1).setName("ProbGraph"); }
						 */
						addTab(split[split.length - 1], simTab, null);
					}
				}
			}
		}
	}

	/**
	 * Embedded class that allows tabs to be closed.
	 */
	class TabbedPaneCloseButtonUI extends BasicTabbedPaneUI {
		public TabbedPaneCloseButtonUI() {
			super();
		}

		protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
				Rectangle iconRect, Rectangle textRect) {

			super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);

			Rectangle rect = rects[tabIndex];
			g.setColor(Color.black);
			g.drawRect(rect.x + rect.width - 19, rect.y + 4, 13, 12);
			g.drawLine(rect.x + rect.width - 16, rect.y + 7, rect.x + rect.width - 10, rect.y + 13);
			g.drawLine(rect.x + rect.width - 10, rect.y + 7, rect.x + rect.width - 16, rect.y + 13);
			g.drawLine(rect.x + rect.width - 15, rect.y + 7, rect.x + rect.width - 9, rect.y + 13);
			g.drawLine(rect.x + rect.width - 9, rect.y + 7, rect.x + rect.width - 15, rect.y + 13);
		}

		protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
			return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24;
		}

		protected MouseListener createMouseListener() {
			return new MyMouseHandler();
		}

		class MyMouseHandler extends MouseHandler {
			public MyMouseHandler() {
				super();
			}

			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int tabIndex = -1;
				int tabCount = tabPane.getTabCount();
				for (int i = 0; i < tabCount; i++) {
					if (rects[i].contains(x, y)) {
						tabIndex = i;
						break;
					}
				}
				if (tabIndex >= 0 && !e.isPopupTrigger()) {
					Rectangle tabRect = rects[tabIndex];
					y = y - tabRect.y;
					if ((x >= tabRect.x + tabRect.width - 18) && (x <= tabRect.x + tabRect.width - 8)
							&& (y >= 5) && (y <= 15)) {
						if (save(tabIndex) == 1) {
							tabPane.remove(tabIndex);
						}
					}
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public JMenuItem getExitButton() {
		return exit;
	}

	/**
	 * This is the main method. It excecutes the BioSim GUI FrontEnd program.
	 */
	public static void main(String args[]) {
		String varname;

		if (System.getProperty("mrj.version") != null)
			varname = "DYLD_LIBRARY_PATH"; // We're on a Mac.
		else
			varname = "LD_LIBRARY_PATH"; // We're not on a Mac.
		try {
			System.loadLibrary("sbmlj");
			// For extra safety, check that the jar file is in the classpath.
			Class.forName("org.sbml.libsbml.libsbml");
		}
		catch (UnsatisfiedLinkError e) {
			System.err.println("Error: could not link with the libSBML library."
					+ "  It is likely\nyour " + varname + " environment variable does not include\nthe"
					+ " directory containing the libsbml library file.");
			System.exit(1);
		}
		catch (ClassNotFoundException e) {
			System.err.println("Error: unable to load the file libsbmlj.jar." + "  It is likely\nyour "
					+ varname + " environment" + " variable or CLASSPATH variable\ndoes not include"
					+ " the directory containing the libsbmlj.jar file.");
			System.exit(1);
		}
		catch (SecurityException e) {
			System.err.println("Could not load the libSBML library files due to a"
					+ " security exception.");
			System.exit(1);
		}
		new BioSim();
	}

	public void copySim(String newSim) {
		try {
			new File(root + separator + newSim).mkdir();
			// new FileWriter(new File(root + separator + newSim + separator +
			// ".sim")).close();
			String oldSim = tab.getTitleAt(tab.getSelectedIndex());
			String[] s = new File(root + separator + oldSim).list();
			String sbmlFile = "";
			String propertiesFile = "";
			String sbmlLoadFile = null;
			for (String ss : s) {
				if (ss.length() > 4 && ss.substring(ss.length() - 5).equals(".sbml")) {
					SBMLReader reader = new SBMLReader();
					SBMLDocument document = reader.readSBML(root + separator + oldSim + separator + ss);
					FileOutputStream out = new FileOutputStream(new File(root + separator + newSim
							+ separator + ss));
					SBMLWriter writer = new SBMLWriter();
					String doc = writer.writeToString(document);
					byte[] output = doc.getBytes();
					out.write(output);
					out.close();
					sbmlFile = root + separator + newSim + separator + ss;
				}
				else if (ss.length() > 10 && ss.substring(ss.length() - 11).equals(".properties")) {
					FileOutputStream out = new FileOutputStream(new File(root + separator + newSim
							+ separator + ss));
					FileInputStream in = new FileInputStream(new File(root + separator + oldSim + separator
							+ ss));
					int read = in.read();
					while (read != -1) {
						out.write(read);
						read = in.read();
					}
					in.close();
					out.close();
					propertiesFile = root + separator + newSim + separator + ss;
				}
				else if (ss.length() > 3
						&& (ss.substring(ss.length() - 4).equals(".dat")
								|| ss.substring(ss.length() - 4).equals(".sad")
								|| ss.substring(ss.length() - 4).equals(".pms") || ss.substring(ss.length() - 4)
								.equals(".sim")) && !ss.equals(".sim")) {
					FileOutputStream out;
					if (ss.substring(ss.length() - 4).equals(".pms")) {
						out = new FileOutputStream(new File(root + separator + newSim + separator
								+ ss.substring(0, ss.length() - 4) + ".sim"));
					}
					else {
						out = new FileOutputStream(new File(root + separator + newSim + separator + ss));
					}
					FileInputStream in = new FileInputStream(new File(root + separator + oldSim + separator
							+ ss));
					int read = in.read();
					while (read != -1) {
						out.write(read);
						read = in.read();
					}
					in.close();
					out.close();
					if (ss.substring(ss.length() - 4).equals(".pms")) {
						if (new File(root + separator + newSim + separator + ss.substring(0, ss.length() - 4)
								+ ".sim").exists()) {
							new File(root + separator + newSim + separator + ss).delete();
						}
						else {
							new File(root + separator + newSim + separator + ss).renameTo(new File(root
									+ separator + newSim + separator + ss.substring(0, ss.length() - 4) + ".sim"));
						}
						ss = ss.substring(0, ss.length() - 4) + ".sim";
					}
					if (ss.substring(ss.length() - 4).equals(".sim")) {
						try {
							Scanner scan = new Scanner(new File(root + separator + newSim + separator + ss));
							if (scan.hasNextLine()) {
								sbmlLoadFile = scan.nextLine();
								sbmlLoadFile = sbmlLoadFile.split(separator)[sbmlLoadFile.split(separator).length - 1];
								if (sbmlLoadFile.contains(".gcm")) {
									GCMParser parser = new GCMParser(root + separator + sbmlLoadFile);
									GeneticNetwork network = parser.buildNetwork();
									GeneticNetwork.setRoot(root + File.separator);
									sbmlLoadFile = root + separator + newSim + separator
											+ sbmlLoadFile.replace(".gcm", ".sbml");
									network.mergeSBML(sbmlLoadFile);
								}
								else {
									sbmlLoadFile = root + separator + sbmlLoadFile;
								}
							}
							while (scan.hasNextLine()) {
								scan.nextLine();
							}
							scan.close();
						}
						catch (Exception e) {
							JOptionPane.showMessageDialog(frame, "Unable to load sbml file.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
			refreshTree();
			JTabbedPane simTab = new JTabbedPane();
			Reb2Sac reb2sac = new Reb2Sac(sbmlLoadFile, sbmlFile, root, this, newSim, log, simTab,
					propertiesFile);
			simTab.addTab("Simulation Options", reb2sac);
			simTab.getComponentAt(simTab.getComponents().length - 1).setName("Simulate");
			simTab.addTab("Abstraction Options", reb2sac.getAdvanced());
			simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
			simTab.addTab("Advanced Options", reb2sac.getProperties());
			simTab.getComponentAt(simTab.getComponents().length - 1).setName("");
			SBML_Editor sbml = new SBML_Editor(sbmlLoadFile, reb2sac, log, this, root + separator
					+ newSim, root + separator + newSim + separator + newSim + ".sim");
			reb2sac.setSbml(sbml);
			simTab.addTab("Parameter Editor", sbml);
			simTab.getComponentAt(simTab.getComponents().length - 1).setName("SBML Editor");
			simTab.addTab("TSD Graph", reb2sac.createGraph(null));
			simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD Graph");
			simTab.addTab("Probability Graph", reb2sac.createProbGraph(null));
			simTab.getComponentAt(simTab.getComponents().length - 1).setName("ProbGraph");
			/*
			 * JLabel noData = new JLabel("No data available"); Font font =
			 * noData.getFont(); font = font.deriveFont(Font.BOLD, 42.0f);
			 * noData.setFont(font);
			 * noData.setHorizontalAlignment(SwingConstants.CENTER);
			 * simTab.addTab("TSD Graph", noData);
			 * simTab.getComponentAt(simTab.getComponents().length - 1).setName("TSD
			 * Graph"); JLabel noData1 = new JLabel("No data available"); Font font1 =
			 * noData1.getFont(); font1 = font1.deriveFont(Font.BOLD, 42.0f);
			 * noData1.setFont(font1);
			 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
			 * simTab.addTab("Probability Graph", noData1);
			 * simTab.getComponentAt(simTab.getComponents().length -
			 * 1).setName("ProbGraph");
			 */
			tab.setComponentAt(tab.getSelectedIndex(), simTab);
			tab.setTitleAt(tab.getSelectedIndex(), newSim);
			tab.getComponentAt(tab.getSelectedIndex()).setName(newSim);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Unable to copy simulation.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void refreshLearn(String learnName, boolean data) {
		for (int i = 0; i < tab.getComponentCount(); i++) {
			if (tab.getTitleAt(i).equals(learnName)) {
				for (int j = 0; j < ((JTabbedPane) tab.getComponentAt(i)).getComponentCount(); j++) {
					if (((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j).getName().equals("TSD Graph")) {
						// if (data) {
						if (((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j) instanceof Graph) {
							((Graph) ((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j)).refresh();
						}
						else {
							((JTabbedPane) tab.getComponentAt(i)).setComponentAt(j, new Graph(null, "amount",
									learnName + " data", "tsd.printer", root + separator + learnName, "time", this,
									null, log, null, true, true));
							((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j).setName("TSD Graph");
						}
						/*
						 * } else { JLabel noData1 = new JLabel("No data available"); Font
						 * font = noData1.getFont(); font = font.deriveFont(Font.BOLD,
						 * 42.0f); noData1.setFont(font);
						 * noData1.setHorizontalAlignment(SwingConstants.CENTER);
						 * ((JTabbedPane) tab.getComponentAt(i)).setComponentAt(j, noData1);
						 * ((JTabbedPane)
						 * tab.getComponentAt(i)).getComponentAt(j).setName("TSD Graph"); }
						 */
					}
					else if (((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j).getName()
							.equals("Learn")) {
						// if (data) {
						if (((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j) instanceof Learn) {
						}
						else {
							((JTabbedPane) tab.getComponentAt(i)).setComponentAt(j, new Learn(root + separator
									+ learnName, log, this));
							((JTabbedPane) tab.getComponentAt(i)).getComponentAt(j).setName("Learn");
						}
						/*
						 * } else { JLabel noData = new JLabel("No data available"); Font
						 * font = noData.getFont(); font = font.deriveFont(Font.BOLD,
						 * 42.0f); noData.setFont(font);
						 * noData.setHorizontalAlignment(SwingConstants.CENTER);
						 * ((JTabbedPane) tab.getComponentAt(i)).setComponentAt(j, noData);
						 * ((JTabbedPane)
						 * tab.getComponentAt(i)).getComponentAt(j).setName("Learn"); }
						 */
					}
				}
			}
		}
	}

	private void updateGCM() {
		for (int i = 0; i < tab.getTabCount(); i++) {
			if (tab.getTitleAt(i).contains(".gcm")) {
				((GCM2SBMLEditor) tab.getComponentAt(i)).reloadFiles();
				tab.setTitleAt(i, ((GCM2SBMLEditor) tab.getComponentAt(i)).getFilename());
			}
		}
	}

	public void updateViews(String updatedFile) {
		for (int i = 0; i < tab.getTabCount(); i++) {
			String tab = this.tab.getTitleAt(i);
			String properties = root + separator + tab + separator + tab + ".sim";
			String properties2 = root + separator + tab + separator + tab + ".lrn";
			if (new File(properties).exists()) {
				String check = "";
				try {
					Scanner s = new Scanner(new File(properties));
					if (s.hasNextLine()) {
						check = s.nextLine();
						check = check.split(separator)[check.split(separator).length - 1];
					}
					s.close();
				}
				catch (Exception e) {
				}
				if (check.equals(updatedFile)) {
					JTabbedPane sim = ((JTabbedPane) (this.tab.getComponentAt(i)));
					for (int j = 0; j < sim.getTabCount(); j++) {
						if (sim.getComponentAt(j).getName().equals("SBML Editor")) {
							new File(properties).renameTo(new File(properties.replace(".sim", ".temp")));
							boolean dirty = ((SBML_Editor) (sim.getComponentAt(j))).hasChanged();
							((SBML_Editor) (sim.getComponentAt(j))).save(false, "");
							if (updatedFile.contains(".gcm")) {
								GCMParser parser = new GCMParser(root + separator + updatedFile);
								GeneticNetwork network = parser.buildNetwork();
								GeneticNetwork.setRoot(root + File.separator);
								network.mergeSBML(root + separator + tab + separator
										+ updatedFile.replace(".gcm", ".sbml"));
							}
							((SBML_Editor) (sim.getComponentAt(j))).updateSBML(i, j);
							((SBML_Editor) (sim.getComponentAt(j))).setChanged(dirty);
							new File(properties).delete();
							new File(properties.replace(".sim", ".temp")).renameTo(new File(properties));
						}
					}
				}
			}
			if (new File(properties2).exists()) {
				String check = "";
				try {
					Properties p = new Properties();
					FileInputStream load = new FileInputStream(new File(properties2));
					p.load(load);
					load.close();
					if (p.containsKey("genenet.file")) {
						String[] getProp = p.getProperty("genenet.file").split(separator);
						check = getProp[getProp.length - 1];
					}
					else {
						check = null;
					}
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "Unable to load background file.", "Error",
							JOptionPane.ERROR_MESSAGE);
					check = null;
				}
				if (check.equals(updatedFile)) {
					JTabbedPane learn = ((JTabbedPane) (this.tab.getComponentAt(i)));
					for (int j = 0; j < learn.getTabCount(); j++) {
						if (learn.getComponentAt(j).getName().equals("Data Manager")) {
							((DataManager) (learn.getComponentAt(j))).updateSpecies();
						}
						else if (learn.getComponentAt(j).getName().equals("Learn")) {
							((Learn) (learn.getComponentAt(j))).updateSpecies();
						}
						else if (learn.getComponentAt(j).getName().contains("Graph")) {
							((Graph) (learn.getComponentAt(j))).refresh();
						}
					}
				}
			}
			if (this.tab.getComponentAt(i) instanceof GCM2SBMLEditor) {
				GCM2SBMLEditor gcm = (GCM2SBMLEditor) this.tab.getComponentAt(i);
				if (gcm.getSBMLFile().equals(updatedFile)) {
					gcm.save("save");
				}
			}
		}
	}

	public String getRoot() {
		return root;
	}
}
