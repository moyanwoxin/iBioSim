package backend.analysis.dynamicsim;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.xml.stream.XMLStreamException;

import backend.analysis.dynamicsim.flattened.SimulatorSSACR;
import backend.analysis.dynamicsim.flattened.SimulatorSSADirect;
import backend.analysis.dynamicsim.hierarchical.methods.HierarchicalMixedSimulator;
import backend.analysis.dynamicsim.hierarchical.methods.HierarchicalODERKSimulator;
import backend.analysis.dynamicsim.hierarchical.methods.HierarchicalSSADirectSimulator;
import backend.analysis.util.AnalysisException;
import frontend.graph.Graph;
import frontend.main.Gui;
import frontend.main.Log;

public class DynamicSimulation
{

	// simulator type
	private final SimulationType	simulatorType;

	// the simulator object
	private ParentSimulator			simulator;
	private boolean					cancelFlag;
	private boolean					statisticsFlag;

	public static enum SimulationType
	{
		CR, DIRECT, RK, HIERARCHICAL_DIRECT, HIERARCHICAL_HYBRID, HIERARCHICAL_RK, HIERARCHICAL_MIXED;
	}

	/**
	 * constructor; sets the simulator type
	 */
	public DynamicSimulation(SimulationType type)
	{

		simulatorType = type;
	}

	public void simulate(String SBMLFileName, String rootDirectory, String outputDirectory, double timeLimit, double maxTimeStep, double minTimeStep, long randomSeed, JProgressBar progress, double printInterval, int runs, JLabel progressLabel, JFrame running, double stoichAmpValue,
			String[] interestingSpecies, int numSteps, double relError, double absError, String quantityType, Boolean genStats, JTabbedPane simTab, String abstraction, Log log, double initialTime, double outputStartTime)
	{
		String progressText = "";

		if (progressLabel != null)
		{
			progressText = progressLabel.getText();
			statisticsFlag = genStats;
		}
		try
		{

			if (progressLabel != null)
			{
				progressLabel.setText("Generating Model . . .");
				running.setMinimumSize(new Dimension((progressLabel.getText().length() * 10) + 20, (int) running.getSize().getHeight()));
			}

			switch (simulatorType)
			{
			case CR:
				simulator = new SimulatorSSACR(SBMLFileName, outputDirectory, timeLimit, maxTimeStep, minTimeStep, randomSeed, progress, printInterval, stoichAmpValue, running, interestingSpecies, quantityType);

				break;
			case DIRECT:
				simulator = new SimulatorSSADirect(SBMLFileName, outputDirectory, timeLimit, maxTimeStep, minTimeStep, randomSeed, progress, printInterval, stoichAmpValue, running, interestingSpecies, quantityType);

				break;
			case HIERARCHICAL_DIRECT:
				simulator = new HierarchicalSSADirectSimulator(SBMLFileName, rootDirectory, outputDirectory, runs, timeLimit, maxTimeStep, minTimeStep, randomSeed, progress, printInterval, stoichAmpValue, running, interestingSpecies, quantityType, abstraction, initialTime, outputStartTime);

				break;
			case HIERARCHICAL_RK:
				simulator = new HierarchicalODERKSimulator(SBMLFileName, rootDirectory, outputDirectory, runs, timeLimit, maxTimeStep, randomSeed, progress, printInterval, stoichAmpValue, running, interestingSpecies, numSteps, relError, absError, quantityType, abstraction, initialTime,
						outputStartTime);
				break;
			case HIERARCHICAL_HYBRID:
				// simulator = new HierarchicalHybridSimulator(SBMLFileName,
				// rootDirectory, outputDirectory, runs, timeLimit, maxTimeStep,
				// minTimeStep, randomSeed, progress, printInterval,
				// stoichAmpValue, running, interestingSpecies, quantityType,
				// abstraction);
				break;
			case HIERARCHICAL_MIXED:
				simulator = new HierarchicalMixedSimulator(SBMLFileName, rootDirectory, outputDirectory, runs, timeLimit, maxTimeStep, minTimeStep, randomSeed, progress, printInterval, stoichAmpValue, running, interestingSpecies, quantityType, abstraction, initialTime, outputStartTime);
				break;
			default:
				log.addText("The simulation selection was invalid.");

			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		catch (XMLStreamException e)
		{
			e.printStackTrace();
			return;
		}
		catch (AnalysisException
				e)
		{
			e.printStackTrace();
			return;
		}
		double val1 = System.currentTimeMillis();

		Runtime runtime = Runtime.getRuntime();
		double mb = 1024 * 1024;
		// int count = 0, total = 0;
		for (int run = 1; run <= runs; ++run)
		{

			if (cancelFlag == true)
			{
				break;
			}

			if (progressLabel != null && running != null)
			{
				progressLabel.setText(progressText.replace(" (" + (run - 1) + ")", "") + " (" + run + ")");
				running.setMinimumSize(new Dimension((progressLabel.getText().length() * 10) + 20, (int) running.getSize().getHeight()));
			}
			if (simulator != null)
			{
				simulator.simulate();
				// count += ((HierarchicalSSADirectSimulator)
				// simulator).getTopLevelValue("counter");
				// total += ((HierarchicalSSADirectSimulator)
				// simulator).getTopLevelValue("n")
				// * ((HierarchicalSSADirectSimulator)
				// simulator).getTopLevelValue("n");
				if ((runs - run) >= 1)
				{
					simulator.setupForNewRun(run + 1);
				}
			}
		}

		System.gc();
		double mem = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		// TODO: send to log if log not null
		System.out.println("Memory used: " + (mem));

		double val2 = System.currentTimeMillis();

		simulator = null;
		System.gc();
		System.runFinalization();

		if (log == null)
		{
			System.out.println("Simulation Time: " + (val2 - val1) / 1000);
			// System.out.println("Count: " + count + " Total: " + total);
		}
		else
		{
			log.addText("Simulation Time: " + (val2 - val1) / 1000);
		}
		if (cancelFlag == false && statisticsFlag == true)
		{
			if (progressLabel != null && running != null)
			{

				progressLabel.setText("Generating Statistics . . .");
				running.setMinimumSize(new Dimension(200, 100));
			}

			if (simulator != null)
			{
				simulator.printStatisticsTSD();
			}

		}
		if (simTab != null)
		{
			for (int i = 0; i < simTab.getComponentCount(); i++)
			{
				if (simTab.getComponentAt(i).getName().equals("TSD Graph"))
				{
					if (simTab.getComponentAt(i) instanceof Graph)
					{
						((Graph) simTab.getComponentAt(i)).refresh();
					}
				}
			}
		}
	}

	/**
	 * cancels the simulation on the next iteration called from outside the
	 * class when the user closes the progress bar dialog
	 */
	public void cancel()
	{

		if (simulator != null)
		{

			simulator.cancel();

			JOptionPane.showMessageDialog(Gui.frame, "Simulation Canceled", "Canceled", JOptionPane.ERROR_MESSAGE);

			cancelFlag = true;
		}
	}
}