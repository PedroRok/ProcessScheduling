package com.github.rok.os.interfaces;

import com.github.rok.os.Process;
import org.jetbrains.annotations.Nullable;

/*
 * @author Rok, Pedro Lucas N M Machado created on 05/07/2023
 */
/**
 * A interface IMemory representa um sistema de memória que gerencia processos.
 * Ela fornece métodos para acessar e manipular processos dentro da memória.
 */
public interface IMemory {

	/**
	 * Obtém o próximo espaço vazio na memória.
	 *
	 * @return O índice do próximo espaço vazio na memória.
	 */
	int getNextEmptySlot();

	/**
	 * Obtém um processo com o ID especificado na memória.
	 *
	 * @param id O ID do processo a ser obtido.
	 * @return O processo com o ID especificado, ou null se não for encontrado.
	 */
	Process getProcess(int id);

	/**
	 * Obtém o processo na posição especificada na lista da memória.
	 *
	 * @param pos A posição do processo na lista da memória.
	 * @return O processo na posição especificada, ou null se não for encontrado.
	 */
	@Nullable Process getProcessOnListPos(int pos);

	/**
	 * Obtém o primeiro processo na memória.
	 *
	 * @return O primeiro processo na memória, ou null se a memória estiver vazia.
	 */
	@Nullable Process getFirstProcess();

	/**
	 * Obtém o último processo na memória.
	 *
	 * @return O último processo na memória, ou null se a memória estiver vazia.
	 */
	@Nullable Process getLastProcess();

	/**
	 * Obtém o processo com o menor ID na memória.
	 *
	 * @return O processo com o menor ID, ou null se a memória estiver vazia.
	 */
	@Nullable Process getLowestIdProcess();

	/**
	 * Obtém o processo com o maior ID na memória.
	 *
	 * @return O processo com o maior ID, ou null se a memória estiver vazia.
	 */
	@Nullable Process getHighestIdProcess();

	/**
	 * Obtém o processo com o menor tempo de execução na memória.
	 *
	 * @return O processo com o menor tempo de execução, ou null se a memória estiver vazia.
	 */
	@Nullable Process getLowestTimeProcess();

	/**
	 * Obtém o processo com o maior tempo de execução na memória.
	 *
	 * @return O processo com o maior tempo de execução, ou null se a memória estiver vazia.
	 */
	@Nullable Process getHighestTimeProcess();

	/**
	 * Verifica se a memória está vazia.
	 *
	 * @return true se a memória estiver vazia, false caso contrário.
	 */
	boolean isEmpty();
}
