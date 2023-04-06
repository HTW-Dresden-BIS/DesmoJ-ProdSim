package de.htwdd.sim.desmoj.processdemo;

import java.util.concurrent.TimeUnit;

import de.htwdd.sim.desmoj.processdemo.ProductionOrder.Operation;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class Machine {
	
	public class Channel extends SimProcess {

		public Channel(Model owner, String name, boolean showInTrace, Machine machine) {
			super(owner, name, showInTrace);
			this.machine = machine;
		}
		
		@Override
		public void lifeCycle() {
			while (true) {
				if (machine.getWaitingOperations().isEmpty()) {
					machine.getIdleChannels().insert(this);
					passivate();
				} else {
					final Operation operation = waitingOperations.removeFirst();
					hold(new TimeSpan(operation.getDuration(), TimeUnit.MINUTES));
					// activate next operation if there is one
					if (!operation.isLast(operation)) {
						operation.getNext().activate(new TimeSpan(0.0d));
					}
					// let the current operation finish its life cycle
					operation.activate();
				}
			}
		}
		
		@Override
		public String toString() {
			return getName();
		}
		
		public Machine getMachine() {
			return machine;
		}

		private Machine machine;

	}
	
	public Machine(Model owner, String name, int channelCount) {
		super();
		this.name = name;
		waitingOperations = new ProcessQueue<ProductionOrder.Operation>(
				owner,
				name + "WaitingOperations",
				true, // show in report
				true); // show in trace
		idleChannels = new ProcessQueue<Channel>(
				owner,
				name + "IdleChannels",
				true, // show in report
				true); // show in trace
	}
	
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(this.getName()).append("\n");
		for (Channel channel:idleChannels) {
			stringBuilder.append(channel.toString());
		}
		return stringBuilder.toString();
	}

	public String getName() {
		return name;
	}

	public ProcessQueue<ProductionOrder.Operation> getWaitingOperations() {
		return waitingOperations;
	}

	public ProcessQueue<Channel> getIdleChannels() {
		return idleChannels;
	}
	
	private String name;

	private ProcessQueue<ProductionOrder.Operation> waitingOperations;
	
	private ProcessQueue<Channel> idleChannels;

}
