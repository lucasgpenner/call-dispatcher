package ar.com.lpenner.service;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.com.lpenner.enums.EmployeeType;
import ar.com.lpenner.model.Call;
import ar.com.lpenner.util.CallUtil;

public class Dispatcher {

	private final static Logger logger = LogManager.getLogger(Dispatcher.class);
	
	private int answeredCalls = 0;
	private int operators;
	private int supervisors;
	private int directors;
	
	private ExecutorService executor;

	private BlockingQueue<Call> operatorQueue;

    private BlockingQueue<Call> supervisorQueue;

    private BlockingQueue<Call> directorQueue;
    
    private Deque<Call> holdOnQueue;
    
    public Dispatcher(int concurrentCalls, int operators, int supervisors, int directors) {

    	this.operators = operators;
    	this.supervisors = supervisors;
    	this.directors = directors;
    	
    	executor = Executors.newFixedThreadPool(concurrentCalls);
    	
    	operatorQueue = new LinkedBlockingQueue<Call>(this.operators);
		supervisorQueue = new LinkedBlockingQueue<Call>(this.supervisors);
		directorQueue = new LinkedBlockingQueue<Call>(this.directors);
		holdOnQueue = new ConcurrentLinkedDeque<Call>();
	}
    
	/**
	 * Dispatch the calls with producer and consumer callers.
	 * @param dispatcher
	 */
    public void dispatchCall(Call callReceived) {

		// Producer Call
		executor.submit(() -> {
			// Add received call to a holdOn queue
			addCallToHoldOn(callReceived);
			logger.info("Llamada nro: " + callReceived.getId());
		});

		// Consumer Call
		executor.submit(() -> {
			while (!holdOnQueue.isEmpty()) {
				try {
					// Take a first call from a holdOn queue and assign this call to an employee.
					// If all employee are busy, this call return to the queue at the first place.
					Call holdOnCall = this.takeCallToHoldOn();
					EmployeeType employeeType = this.addCallToAnEmployee(holdOnCall);
					if (!employeeType.equals(EmployeeType.HOLD_ON)) {
						int callTime = CallUtil.callTime();
						this.assignEmployeeToCall(employeeType, callTime);
					} else {
						this.insertCallToHoldOnFirst(holdOnCall);
					}
				} catch (InterruptedException ex) {
					throw new InterruptedException("Se interrumpio la llamada inesperadamente");
				}
			}
			return true;
		});
    	
		
	
	}
    
    /**
     * Terminate the dispatcher executor and wait the end of all calls
     */
	public void terminateDispatcher() {
		executor.shutdown();
		while (!executor.isTerminated());
	}
	
	/**
     * Add a call to hold on in first place.
     * @param call
     * @return
     */
	public Call takeCallToHoldOn() {
		return holdOnQueue.pollFirst();
	}
    
	/**
     * Add a call to hold on in first place.
     * @param call
     * @return
     */
	public void insertCallToHoldOnFirst(Call call) {
		holdOnQueue.offerFirst(call);
	}
	
	/**
     * Add a call to hold on.
     * @param call
     * @return
     */
	public void addCallToHoldOn(Call call) {
		holdOnQueue.add(call);
	}
	
	/**
     * Add a call to an employee
     * @param call
     * @return
     */
	public EmployeeType addCallToAnEmployee(Call call) {
		return addCallToAnOperator(call);
	}
	
	private EmployeeType addCallToAnOperator(Call call) {
		try {
			operatorQueue.add(call);
			logger.info("Asignando la llamada: " + call.getId() + " a la cola " + EmployeeType.OPERATOR);
			return EmployeeType.OPERATOR;
		} catch (IllegalStateException ex) {
			logger.debug("En este momento todos nuestros operadores se encuentran ocupados, Se asigna la tarea a la cola de SUPERVISORES.");
			return addCallToASupervisor(call);
		}
	}
    
	private EmployeeType addCallToASupervisor(Call call) {
		try {
			supervisorQueue.add(call);
			logger.info("Asignando la llamada: " + call.getId() + " a la cola " + EmployeeType.SUPERVISOR);
			return EmployeeType.SUPERVISOR;
		} catch (IllegalStateException ex) {
			logger.debug("En este momento todos nuestros supervisores se encuentran ocupados, Se asigna la tarea a la cola de DIRECTORES.");
			return addCallToADirector(call);
		}

	}
	
	private EmployeeType addCallToADirector(Call call) {

		try {
			directorQueue.add(call);
			logger.info("Asignando la llamada: " + call.getId() + " a la cola " + EmployeeType.DIRECTOR);
			return EmployeeType.DIRECTOR;
		} catch (IllegalStateException ex) {
			return EmployeeType.HOLD_ON;
		}
	}
	
	/**
	 * Assign an employee to a call
	 * 
	 * @param employeeType
	 * @param callTime
	 * @throws InterruptedException
	 */
	public void assignEmployeeToCall(EmployeeType employeeType, int callTime) throws InterruptedException {
		switch (employeeType) {
		case OPERATOR:
			takeTheCall(employeeType, operatorQueue, callTime);
			break;
		case SUPERVISOR:
			takeTheCall(employeeType, supervisorQueue, callTime);
			break;
		case DIRECTOR:
			takeTheCall(employeeType, directorQueue, callTime);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Take the current call from the queue
	 * 
	 * @param employeeType
	 * @param queue
	 * @param callTime
	 * @throws InterruptedException
	 */
	private void takeTheCall(EmployeeType employeeType, BlockingQueue<Call> queue, int callTime)
			throws InterruptedException {

		if (!queue.isEmpty()) {
			logger.info("Empleado " + employeeType + " atendio la llamada: " + queue.take().getId() + " - Tiempo: "
					+ callTime + "ms - " + "Hay Disponibles: " + queue.remainingCapacity() + " - Llamadas en cola: "
					+ this.callPendings());
			answeredCalls++;
		}
	}

	/**
	 * Retrieve the number of waiting calls
	 * @return
	 */
	public int callPendings() {
		return operatorQueue.size() + supervisorQueue.size() + directorQueue.size() + holdOnQueue.size();
	}
	
	public int getAnsweredCalls() {
		return answeredCalls;
	}

	public void setAnsweredCalls(int answeredCalls) {
		this.answeredCalls = answeredCalls;
	}
	
	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public BlockingQueue<Call> getOperatorQueue() {
		return operatorQueue;
	}

	public void setOperatorQueue(BlockingQueue<Call> operatorQueue) {
		this.operatorQueue = operatorQueue;
	}

	public BlockingQueue<Call> getSupervisorQueue() {
		return supervisorQueue;
	}

	public void setSupervisorQueue(BlockingQueue<Call> supervisorQueue) {
		this.supervisorQueue = supervisorQueue;
	}

	public BlockingQueue<Call> getDirectorQueue() {
		return directorQueue;
	}

	public void setDirectorQueue(BlockingQueue<Call> directorQueue) {
		this.directorQueue = directorQueue;
	}

	public Deque<Call> getHoldOnQueue() {
		return holdOnQueue;
	}

	public void setHoldOnQueue(Deque<Call> holdOnQueue) {
		this.holdOnQueue = holdOnQueue;
	}

}