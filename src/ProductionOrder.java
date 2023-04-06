package de.htwdd.sim.desmoj.processdemo;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.sim.desmoj.processdemo.Machine.Channel;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;

public class ProductionOrder {
	
	public class Operation extends SimProcess {
		
		public Operation(Model owner, String name, boolean showInTrace, ProductionOrder productionOrder, Machine machine, double duration) {
			super(owner, name, showInTrace);
			this.machine = machine;
			this.duration = duration;
			this.productionOrder = productionOrder;
			this.productionOrder.getOperations().add(this);
		}
		
		@Override
		public void lifeCycle() {
			this.getMachine().getWaitingOperations().insert(this);
			this.sendTraceNote("Number of operations waiting infront of " + machine.getName() + ": " + machine.getWaitingOperations().length() + ".");
			if (!machine.getIdleChannels().isEmpty()) {
				final Channel channel = machine.getIdleChannels().removeFirst();
				channel.activateAfter(this);
			}
			passivate();
			sendTraceNote(this.getName() + " was serviced on " + "[no name available]" + ".");
		}
		
		public boolean isLast(Operation operation) {
			return productionOrder.isLastOperation(this);
		}

		public SimProcess getNext() {
			return productionOrder.getNextOperation(this);
		}
		
		@Override
		public String toString() {
			return new StringBuilder(getName()).append(",").append(getMachine().getName()).append(",").append(duration).toString();
		}

		public ProductionOrder getProductionOrder() {
			return productionOrder;
		}

		public Machine getMachine() {
			return machine;
		}

		public double getDuration() {
			return duration;
		}
		
		private ProductionOrder productionOrder;

		private Machine machine;
		
		private double duration;

	}

	public ProductionOrder(String name) {
		super();
		this.name = name;
	}
	
	public boolean isLastOperation(Operation operation) {
		return operations.indexOf(operation) == operations.size() - 1;
	}
	
	public Operation getNextOperation(Operation operation) {
		return operations.get(operations.indexOf(operation) + 1);
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.getName()).append("\n");
		for (Operation operation:operations) {
			stringBuilder.append(operation.toString()).append("\n");
		}
		return stringBuilder.toString();
	}
	
	public String getName() {
		return name;
	}

	public List<Operation> getOperations() {
		return operations;
	}
	
	private String name;

	private List<Operation> operations = new ArrayList<Operation>();

}
