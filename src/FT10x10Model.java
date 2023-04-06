package de.htwdd.sim.desmoj.processdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.htwdd.sim.desmoj.processdemo.Machine.Channel;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

public class FT10x10Model extends Model {
	
	/*
	 * TODO
	 * - DONE put inter-arrival time stream in production order generator?
	 * - DONE remove simulation process from machine?
	 * - DONE remove simulation process from production order?
	 * - toString-methods for channels, operations and tasks
	 * - toString-methods for machines, production orders and task lists using toString-methods for channels, operations and tasks
	 */
	
	private static final int MACHINE_COUNT = 10;
	
	private static final double SIMULATION_TIME = 740000.0d;
	
	/**
	 * http://people.brunel.ac.uk/~mastjjb/jeb/orlib/files/jobshop1.txt
	 */
	private static final String FT10[] = {
			"0 29 1 78 2  9 3 36 4 49 5 11 6 62 7 56 8 44 9 21",
			"0 43 2 90 4 75 9 11 3 69 1 28 6 46 5 46 7 72 8 30",
			"1 91 0 85 3 39 2 74 8 90 5 10 7 12 6 89 9 45 4 33",
			"1 81 2 95 0 71 4 99 6  9 8 52 7 85 3 98 9 22 5 43",
			"2 14 0  6 1 22 5 61 3 26 4 69 8 21 7 49 9 72 6 53",
			"2 84 1  2 5 52 3 95 8 48 9 72 0 47 6 65 4  6 7 25",
			"1 46 0 37 3 61 2 13 6 32 5 21 9 32 8 89 7 30 4 55",
			"2 31 0 86 1 46 5 74 4 32 6 88 8 19 9 48 7 36 3 79",
			"0 76 1 69 3 76 5 51 2 85 9 11 6 40 7 89 4 26 8 74",
			"1 85 0 13 2 61 6  7 8 64 9 76 5 47 3 52 4 90 7 45",
			};
	
	private static final int CHANNEL_COUNTS[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

	public FT10x10Model(Model owner, String name, boolean showInReport,
			boolean showInTrace) {
		super(owner, name, showInReport, showInTrace);
	}

	@Override
	public String description() {
		return "Production system using the production orders from the famous 10x10-Fisher-Thompson-problem as task lists.";
	}

	@Override
	public void doInitialSchedules() {
		for (Machine machine:machines) {
			for (Channel channel:machine.getIdleChannels()) {
				channel.activate(new TimeSpan(0));
			}
		}
		productionOrderGenerator.activate(new TimeSpan(0));
	}

	@Override
	public void init() {
		machines = new ArrayList<Machine>(MACHINE_COUNT);
		for (int i = 0; i < MACHINE_COUNT; ++i) {
			final Machine machine = new Machine(this, "Machine" + i, CHANNEL_COUNTS[i]);
			for (int j = 0; j < CHANNEL_COUNTS[i]; ++j) {
				machine.getIdleChannels().insert(machine.new Channel(
						this,
						"Channel" + i + "." + j,
						true,
						machine));
			}
			machines.add(machine);
//			System.out.println(machines.get(i).toString());
		}
		taskLists = new ArrayList<TaskList>();
		int taskListNumber = 0;
		for (String line:FT10) {
			final TaskList taskList= new TaskList("TaskList" + taskListNumber);
			final String characters[] = line.split("\\s+");
			for (int i = 0; i < MACHINE_COUNT; ++i) {
				final int machineId = Integer.valueOf(characters[i * 2]);
				final int duration = Integer.valueOf(characters[i * 2 + 1]);
				taskList.new Task(
						taskList,
						"" + taskListNumber + "." + i,
						machines.get(machineId),
						duration);
			}
			taskLists.add(taskList);
//			System.out.println(taskLists.get(taskLists.size() - 1).toString());
			taskListNumber++;
		}
		productionOrderGenerator = new ProductionOrderGenerator(this, "ProductionOrderGenerator", true);
	}
	
	public static void main(String[] args) {
		final FT10x10Model model = new FT10x10Model(null, "FT10x10Model", true, true);
		final Experiment experiment = new Experiment("FT10x10Experiment", TimeUnit.SECONDS, TimeUnit.MINUTES, null);
		model.connectToExperiment(experiment);
		experiment.setShowProgressBar(true);
		experiment.stop(new TimeInstant(SIMULATION_TIME, TimeUnit.MINUTES));
		experiment.tracePeriod(new TimeInstant(0.0d), new TimeInstant(1000.0d, TimeUnit.MINUTES));
		experiment.debugPeriod(new TimeInstant(0.0d), new TimeInstant(1000.0d, TimeUnit.MINUTES));
		experiment.start();
		experiment.report();
		experiment.finish();
	}
	
	public ContDistExponential getProductionOrderArrivalTimeDistribution() {
		return productionOrderArrivalTimeDistribution;
	}
	
	public List<Machine> getMachines() {
		return machines;
	}

	public List<TaskList> getTaskLists() {
		return taskLists;
	}

	private ContDistExponential productionOrderArrivalTimeDistribution;
	
	private List<Machine> machines;
	
	private List<TaskList> taskLists;
	
	private ProductionOrderGenerator productionOrderGenerator;

}
