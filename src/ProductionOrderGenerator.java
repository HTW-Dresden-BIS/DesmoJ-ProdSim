package de.htwdd.sim.desmoj.processdemo;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class ProductionOrderGenerator extends SimProcess {
	
	private static long productionOrderNumber = 0;
	
	private static final Random RANDOM = new Random();

	/**
	 * \\samba1.htw-dresden.de\munkelt\forschung\simulation\ft10x10UtilizationAnalysis01.xlsx
	 */
	private static final double MEAN_PROD_ORDER_INTER_ARRIVAL_TIME = 74.0d;
	
	public ProductionOrderGenerator(Model owner, String name,
			boolean showInTrace) {
		super(owner, name, showInTrace);
		productionOrderArrivalTimeDistribution = new ContDistExponential(
				owner,
				"ProductionOrderInterArrivalTimeStream",
				MEAN_PROD_ORDER_INTER_ARRIVAL_TIME,
				true,
				true);
		productionOrderArrivalTimeDistribution.setNonNegative(true);
	}

	@Override
	public void lifeCycle() {
		while (true) {
			final long productionOrderNumber = ProductionOrderGenerator.productionOrderNumber++;
			final FT10x10Model model = (FT10x10Model)this.getModel();
			hold(new TimeSpan(productionOrderArrivalTimeDistribution.sample(), TimeUnit.MINUTES));
			final ProductionOrder productionOrder = new ProductionOrder("ProductionOrder" + productionOrderNumber);
			final TaskList taskList = model.getTaskLists().get(RANDOM.nextInt(model.getTaskLists().size()));
			for (TaskList.Task task:taskList.getTasks()) {
				productionOrder.new Operation(
						model,
						"Operation" + productionOrderNumber + "." + taskList.getTasks().indexOf(task),
						true,
						productionOrder,
						task.getMachine(),
						task.getDuration());
			}
//			System.out.println(productionOrder.toString());
			productionOrder.getOperations().get(0).activateAfter(this);
		}
	}

	private ContDistExponential productionOrderArrivalTimeDistribution;

}
