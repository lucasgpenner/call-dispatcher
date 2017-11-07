package ar.com.lpenner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.com.lpenner.enums.EmployeeType;
import ar.com.lpenner.model.Call;
import ar.com.lpenner.service.Dispatcher;
import ar.com.lpenner.util.CallUtil;

public class DispatcherTest {

	private Dispatcher dispatcher;
	private int calls;
	private int concurrentCalls;
	private int operators;
	private int supervisors;
	private int directors;
	private int callTime;

	@Before
	public void setUp() throws Exception {
		concurrentCalls = 1;
		operators = 1;
		supervisors = 1;
		directors = 1;
		dispatcher = new Dispatcher(concurrentCalls, operators, supervisors, directors);
		callTime = CallUtil.callTime();
	}
	
	@Test
	public void addCallToHoldOnQueue() {
		calls = 1;
		dispatcher.addCallToHoldOn(new Call(calls));
		assertEquals(calls, dispatcher.getHoldOnQueue().size());
	}

	@Test
	public void addCallToAnOperatorQueue() {
		calls = 1;
		for (int i = 1; i <= calls; i++) {
			dispatcher.addCallToAnEmployee(new Call(i));
		}
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(calls, dispatcher.callPendings());
	}

	@Test
	public void addCallsToAnSupervisorQueue() {
		calls = 2;
		for (int i = 1; i <= calls; i++) {
			dispatcher.addCallToAnEmployee(new Call(i));
		}
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(supervisors, dispatcher.getSupervisorQueue().size());
		assertEquals(calls, dispatcher.callPendings());
	}

	@Test
	public void addCallsToAnDirectorQueue() {
		calls = 3;
		for (int i = 1; i <= calls; i++) {
			dispatcher.addCallToAnEmployee(new Call(i));
		}
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(supervisors, dispatcher.getSupervisorQueue().size());
		assertEquals(directors, dispatcher.getDirectorQueue().size());
		assertEquals(calls, dispatcher.callPendings());
	}
	
	@Test
	public void addCallsToHoldOnQueue() {
		calls = 4;
		List<EmployeeType> employeeTypeList = new LinkedList<>();
		for (int i = 1; i <= calls; i++) {
			employeeTypeList.add(dispatcher.addCallToAnEmployee(new Call(i)));
		}
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(supervisors, dispatcher.getSupervisorQueue().size());
		assertEquals(directors, dispatcher.getDirectorQueue().size());
		assertNotEquals(calls, dispatcher.callPendings());
		assertTrue(employeeTypeList.contains(EmployeeType.HOLD_ON));
	}
	
	@Test
	public void assignOperatorToCall() throws InterruptedException {
		calls = 1;
		EmployeeType employeeType = dispatcher.addCallToAnEmployee(new Call(calls));
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(calls, dispatcher.callPendings());
		dispatcher.assignEmployeeToCall(employeeType, callTime);
		assertTrue(dispatcher.getOperatorQueue().isEmpty());
		assertEquals(0, dispatcher.callPendings());
	}
	
	@Test
	public void assignSupervisorToCall() throws InterruptedException {
		calls = 2;
		List<EmployeeType> employeeTypeList = new LinkedList<>();
		for (int i = 1; i <= calls; i++) {
			employeeTypeList.add(dispatcher.addCallToAnEmployee(new Call(i)));
		}
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(supervisors, dispatcher.getSupervisorQueue().size());
		assertEquals(calls, dispatcher.callPendings());
		for (EmployeeType employeeType : employeeTypeList) {
			dispatcher.assignEmployeeToCall(employeeType, callTime);
		}
		assertTrue(dispatcher.getOperatorQueue().isEmpty());
		assertTrue(dispatcher.getSupervisorQueue().isEmpty());
		assertEquals(0, dispatcher.callPendings());
	}
	
	@Test
	public void assignDirectorToCall() throws InterruptedException {
		calls = 3;
		List<EmployeeType> employeeTypeList = new LinkedList<>();
		for (int i = 1; i <= calls; i++) {
			employeeTypeList.add(dispatcher.addCallToAnEmployee(new Call(i)));
		}
		assertEquals(operators, dispatcher.getOperatorQueue().size());
		assertEquals(supervisors, dispatcher.getSupervisorQueue().size());
		assertEquals(directors, dispatcher.getDirectorQueue().size());
		assertEquals(calls, dispatcher.callPendings());
		for (EmployeeType employeeType : employeeTypeList) {
			dispatcher.assignEmployeeToCall(employeeType, callTime);
		}
		assertTrue(dispatcher.getOperatorQueue().isEmpty());
		assertTrue(dispatcher.getSupervisorQueue().isEmpty());
		assertTrue(dispatcher.getDirectorQueue().isEmpty());
		assertEquals(0, dispatcher.callPendings());
	}
}
