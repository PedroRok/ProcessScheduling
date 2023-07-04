package com.github.rok.os;

import com.github.rok.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

/*
 * @author Rok, Pedro Lucas N M Machado created on 03/07/2023
 */
public class Memory {

	public ArrayList<Process> processList = new ArrayList<>(10);
	// Um placeholder para preencher o gráfico
	private final Process nullProcess = new Process(0, 0.0001);

	private double generationSpeed = 300;
	private double nextToGenerate = 0;
	private boolean paused = true;
	private Consumer<Double> consumer;

	int lastId = 0;

	public Memory(Consumer<Double> consumer) {
		new Thread(this::generate).start();
		for (int i = 0; i < 10; i++) {
			processList.add(nullProcess);
		}
		this.consumer = consumer;
	}

	public void generate() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (paused) continue;
			nextToGenerate -= 1;
			consumer.accept(nextToGenerate);
			if (nextToGenerate <= 0) {
				addRandomProcessToMemory();
				consumer.accept(nextToGenerate);
				nextToGenerate = generationSpeed;
			}
		}
	}

	public Process createProcess(double waitingTime) {
		return new Process(++lastId, waitingTime);
	}

	public void addRandomProcessToMemory() {
		addProcessToMemory(Utils.generateRandomNumber(1,10));
	}

	public int getNextEmpty() {
		int lastEmpty = 0;
		while (processList.get(lastEmpty) != nullProcess) {
			lastEmpty++;
			if (lastEmpty == 10)
				return -1;
		}
		return lastEmpty;
	}

	public void addProcessToMemory(double waitingTime) {
		if (getNextEmpty() == -1) return; // TODO: tratar erro de memória cheia
		processList.set(getNextEmpty(), createProcess(waitingTime));
	}

	public Process getProcess(int id) {
		for (Process process : processList) {
			if (process.getId() == id)
				return process;
		}
		return null;
	}

	public void removeProcess(int id) {
		for (int i = 0; i < processList.size(); i++) {
			if (processList.get(i).getId() == id) {
				processList.set(i, nullProcess);
				return;
			}
		}
	}

	public void removeProcess(Process process) {
		removeProcess(process.getId());
	}

	@Nullable
	public Process getProcessByListPos(int pos) {
		if (processList.get(pos) == nullProcess)
			return null;
		return processList.get(pos);
	}

	@Nullable
	public Process getFirstProcess() {
		for (Process process : processList) {
			if (process != nullProcess)
				return process;
		}
		return null;
	}

	@Nullable
	public Process getLastProcess() {
		for (int i = processList.size() - 1; i >= 0; i--) {
			if (processList.get(i) != nullProcess)
				return processList.get(i);
		}
		return null;
	}

	public boolean isEmpty() {
		int count = 0;
		while (processList.get(count) == nullProcess) {
			count++;
			if (count == 10)
				return true;
		}
		return false;
	}

	public double getGenerationSpeed() {
		return generationSpeed;
	}

	public boolean pause() {
		paused = !paused;
		return paused;
	}
}
