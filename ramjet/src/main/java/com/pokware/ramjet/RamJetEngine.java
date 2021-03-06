package com.pokware.ramjet;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RamJet Engine controller.
 * 
 * @author Fabien Benoit <fabien.benoit@pokware.com>
 * 
 * @param <E>
 */
public class RamJetEngine<E extends CommandTarget> implements CommandManager<E> {

	private static final int INITIAL_SIZE = 1;

	private final ArrayList<Pipeline<E>> pipelineList = new ArrayList<Pipeline<E>>();

	private TargetLocator<E> targetLocator;

	private Logger logger = LoggerFactory.getLogger(RamJetEngine.class);

	public RamJetEngine(TargetLocator<E> targetLocator) {
		for (int i = 0; i < INITIAL_SIZE; i++) {
			logger.debug("Creating pipeline #{} ...", i);

			pipelineList.add(new Pipeline<E>(i, this));
		}
		this.targetLocator = targetLocator;
	}

	@Override
	public void submitCommand(Command<E> command) {
		// TODO: Serialization

		logger.debug("Queuing command {}", command);

		long targetId = command.getTargetId();
		int pipelineListSize = pipelineList.size();
		int index = (int) (targetId % pipelineListSize);
		Pipeline<E> pipeline = pipelineList.get(index);
		pipeline.putCommand(command);
	}

	@Override
	public void schedule(Command<E> command, long delay, TimeUnit timeUnit) {

		logger.debug("Scheduling command {} in {} {}", new Object[] { command, delay, timeUnit });

		long targetId = command.getTargetId();
		int pipelineListSize = pipelineList.size();
		int index = (int) (targetId % pipelineListSize);
		Pipeline<E> pipeline = pipelineList.get(index);
		pipeline.scheduleCommand(command, delay, timeUnit);
	}

	@Override
	public boolean isScheduled(Command<E> command) {
		return false;
	}

	public void setTargetLocator(TargetLocator<E> targetLocator) {
		this.targetLocator = targetLocator;
	}

	@Override
	public TargetLocator<E> getTargetLocator() {
		return targetLocator;
	}

	@Override
	public void unscheduleAll(Long targetId, Class<? extends Command<E>> commandClazz) {

		logger.debug("Unscheduling all commands of type {} for target #{}", commandClazz, targetId);

		int pipelineListSize = pipelineList.size();
		int index = (int) (targetId % pipelineListSize);
		Pipeline<E> pipeline = pipelineList.get(index);
		pipeline.unscheduleAll(targetId);
	}

	/**
	 * Wait for all the pipeline to empty.
	 */
	public void sync() {
		for (Pipeline<E> pipeline : pipelineList) {
			pipeline.sync();
		}
	}

}
