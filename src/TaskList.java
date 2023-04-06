package de.htwdd.sim.desmoj.processdemo;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
	
	public class Task {
		
		public Task(TaskList taskList, String name, Machine machine, double duration) {
			super();
			this.name = name;
			this.machine = machine;
			this.duration = duration;
			taskList.getTasks().add(this);
		}
		
		@Override
		public String toString() {
			return new StringBuilder(name).append(",").append(machine.getName()).append(",").append(duration).toString();
		}
		
		public String getName() {
			return name;
		}

		public Machine getMachine() {
			return machine;
		}

		public double getDuration() {
			return duration;
		}

		private String name;
		
		private Machine machine;
		
		private double duration;
		
	}
	
	public TaskList(String id) {
		super();
		this.name = id;
	}
	
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(name).append("\n");
		for (TaskList.Task task:tasks) {
			stringBuilder.append(task.toString()).append("\n");
		}
		return stringBuilder.toString();
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	private String name;

	private List<Task> tasks = new ArrayList<Task>();

}
