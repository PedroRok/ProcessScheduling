package com.github.rok;

import com.github.rok.interfaces.*;
import com.github.rok.os.*;
import com.github.rok.os.Process;
import com.github.rok.os.interfaces.ICPU;
import com.github.rok.os.interfaces.IMemory;
import com.github.rok.panel.ChartsFrame;
import com.github.rok.panel.Frame;
import com.github.rok.utils.Utils;

import javax.swing.*;

/*
 * @author Rok, Pedro Lucas N M Machado created on 03/07/2023
 */
public class MainPanel implements IController, IMainController {

	private static final int WINDOW_WIDTH = 900;
	private static final int WINDOW_HEIGHT = 500;

	private Memory memory;
	private CPU cpu;
	private Counter counter;

	private final Frame frame;
	private ChartsFrame chartsFrame;

	private AlgorithmInterface algorithm;

	private boolean running = false;

	public MainPanel() {
		//Cria os modulos do sistema
		this.cpu = new CPU(this);
		this.memory = new Memory(this);
		this.counter = new Counter(this);
		chartsFrame = new ChartsFrame(this, memory);
		frame = new Frame(this, chartsFrame);
	}

	public void importAlgorithms() {
		JComboBox<String> algorithmBox = (JComboBox<String>) frame.getComponent("algorithm");
		for (String algorithm : Main.getAlgorithmsNameList()) {
			algorithmBox.addItem(algorithm);
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void addProcessToCPU(Process process, double timeProcessing) {
		cpu.addProcessToCPU(process, timeProcessing);
	}

	@Override
	public void removeControlButtons() {
		frame.removeControlButtons();
	}
	@Override
	public void setRunning(boolean running) {
		this.running = running;
		memory.pause(!running);
		memory.clearMemory();
		if (running) {
			memory.addRandomProcessToMemory();
			counter.setCounter(frame.getComponentInt("simulation_min"), frame.getComponentInt("simulation_sec"));
		}
		cpu.endProcess();
		cpu.pause(!running);
		updateCenterBar(0, "Feito por Pedro Lucas Nascimento, Caio Lapa, Kaio Stefan e Joao Victor Mascarenhas.");

		chartsFrame.updateCPUChartColor(true);
		updateMemoryBar(0);
		updateCPUBar(0);
		updateMemoryChart();
		updateCPUChart();
	}

	public void pause(boolean pause) {
		memory.pause(pause);
		cpu.pause(pause);
	}

	// Toda atualização na lista de processos, esse método deve ser chamado para atualizar o gráfico
	public void updateProperties() {
		// Memory
		memory.setGenerationSpeed(frame.getComponentInt("process_delay") * 100);
		int processMax = frame.getComponentInt("process_max");
		int processMin = frame.getComponentInt("process_min");
		memory.setProcessSize(processMin, processMax);
		chartsFrame.getMemoryChart().getStyler().setYAxisMax((double) processMax);

		// CPU
		cpu.setScalingDelay(frame.getComponentDouble("scaling_delay"));
		cpu.setProcessSpeed(frame.getComponentDouble("cpu_speed"));
		algorithm = Main.getAlgorithm((String) ((JComboBox<?>) frame.getComponent("algorithm")).getSelectedItem());
	}

	private void updateMemoryBar(int value) {
		chartsFrame.updateMemoryBar(value);
	}

	private void updateCPUBar(int value) {
		chartsFrame.updateCpuBar(value);
	}

	private void updateCenterBar(int value, String text) {
		frame.updateCenterBar(value, text);
	}

	private void updateMemoryChart() {
		chartsFrame.updateMemoryChart();
	}

	private Process lastProcess = null;

	public void updateCPUChart() {
		boolean processRunning = cpu.getRunningProcess() != null;
		chartsFrame.updateCPUChart(processRunning, counter.isRunning());
		if (processRunning) {
			lastProcess = cpu.getRunningProcess();
		} else {
			// TODO: TESTANDO OUTROS METODOS APAGAR DEPOIS
			useCPUWithAlgorithm();
			updateMemoryChart();
		}
	}

	public void useCPUWithAlgorithm() {
		algorithm.execute();
	}

	public CPU getCpu() {
		return cpu;
	}

	public Memory getMemory() {
		return memory;
	}

	public int getWindowWidth() {
		return WINDOW_WIDTH;
	}

	public int getWindowHeight() {
		return WINDOW_HEIGHT;
	}


	@Override
	public void updateTick() {
		if (cpu.getRunningProcess() != null) {
			updateCPUBar((int) ((int) Utils.getPercentageToValue(cpu.getInitialTime(), cpu.getTimeProcessing()) * cpu.getProcessSpeed()));
			updateCPUChart();
			return;
		}
		updateCPUChart();
	}

	@Override
	public void scalingTick(boolean toMemory, double completePercentage, CPU.STATE isRunning) {
		if (isRunning == CPU.STATE.COMPUTING) {
			updateCenterBar(100, "Aguardando...");
		}
		if (isRunning == CPU.STATE.WAITING) {
			updateCenterBar(0, "Aguardando...");
		}
		if (cpu.isRunning()) {
			chartsFrame.updateCPUChartColor(true);
		}
		if (isRunning != CPU.STATE.SCALING) return;
		chartsFrame.updateCPUChartColor(false);
		if (toMemory) {
			updateCenterBar((int) (100 - completePercentage), "Escalonando para Memória...");
			updateCPUChart();
			return;
		}
		updateCenterBar((int) ((int) completePercentage), "Escalonando para CPU...");
		updateCPUChart();
	}

	@Override
	public void memoryTick(double nextGen) {

		if (counter.isRunning()) {
			frame.updateTime(counter.getTime());
		}
		if (nextGen <= 0) {
			updateMemoryChart();
		}
		if (!cpu.isRunning() && memory.getFirstProcess() != null) {
			useCPUWithAlgorithm();
		}
		updateMemoryBar(100 - (int) (nextGen / (memory.getGenerationSpeed() / 100)));
	}

	@Override
	public void sendToMemory(Process process) {
		if (process.getWaitingTime() <= 0)
			getMemory().removeProcess(process);
		updateMemoryChart();
		if (getMemory().isEmpty()) {
			chartsFrame.clearCPUChart();
			updateCPUBar(0);
		}
		updateCPUChart();
	}

	@Override
	public IMemory getIMemory() {
		return memory;
	}

	@Override
	public ICPU getICPU() {
		return cpu;
	}
}
