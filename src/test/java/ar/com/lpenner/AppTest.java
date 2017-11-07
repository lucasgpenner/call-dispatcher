package ar.com.lpenner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.concurrent.RejectedExecutionException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ar.com.lpenner.model.Call;
import ar.com.lpenner.service.Dispatcher;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private Dispatcher dispatcher;
	private int calls;
	private int concurrentCalls;
	private int operators;
	private int supervisors;
	private int directors;
	
	@Before
	public void setUp() throws Exception {
		operators = 2;
		supervisors = 1;
		directors = 1;
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionIfConcurrentCallsAreNotPositive() {
		dispatcher = new Dispatcher(0, operators, supervisors, directors);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionIfOperatorsIsNotPositive() {
		dispatcher = new Dispatcher(concurrentCalls, 0, supervisors, directors);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionIfSupervisorsIsNotPositive() {
		dispatcher = new Dispatcher(concurrentCalls, operators, 0, directors);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionIfDirectorsIsNotPositive() {
		dispatcher = new Dispatcher(concurrentCalls, operators, supervisors, 0);
	}
	
	@Test(expected = RejectedExecutionException.class)
	public void throwRejectedExecutionExceptionIfDispatcherIsInterrupted() {
		calls = 10;
		concurrentCalls = 1;
		dispatcher = new Dispatcher(concurrentCalls, operators, supervisors, directors);
		int interruptedCall = new Random().nextInt(calls - 0) + 0;
		for (int i = 1; i <= calls; i++) {
			Call call = new Call(i);
			dispatcher.dispatchCall(call);
			if (i == interruptedCall) {
				dispatcher.getExecutor().shutdownNow();
			}
		}
	}
	
	@Test
	public void dispatcherWithOneCall() {
		calls = 1;
		concurrentCalls = 1;
		dispatcher = new Dispatcher(concurrentCalls, operators, supervisors, directors);
		dispatcher.dispatchCall(new Call(calls));
		dispatcher.terminateDispatcher();
		
		assertTrue(dispatcher.getHoldOnQueue().isEmpty());
		assertTrue(dispatcher.getOperatorQueue().isEmpty());
		assertTrue(dispatcher.getSupervisorQueue().isEmpty());
		assertTrue(dispatcher.getDirectorQueue().isEmpty());
		assertEquals(calls, dispatcher.getAnsweredCalls());
	}
	
	@Test
	public void dispatcherWithTenConcurrentCalls() {
		calls = 10;
		concurrentCalls = 10;
		dispatcher = new Dispatcher(concurrentCalls, operators, supervisors, directors);

		for (int i = 1; i <= calls; i++) {
			Call call = new Call(i);
			dispatcher.dispatchCall(call);
		}
		
		dispatcher.terminateDispatcher();
		
		assertTrue(dispatcher.getHoldOnQueue().isEmpty());
		assertTrue(dispatcher.getOperatorQueue().isEmpty());
		assertTrue(dispatcher.getSupervisorQueue().isEmpty());
		assertTrue(dispatcher.getDirectorQueue().isEmpty());
		assertEquals(calls, dispatcher.getAnsweredCalls());
	
	}
	
}
