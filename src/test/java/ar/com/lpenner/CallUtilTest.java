package ar.com.lpenner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import ar.com.lpenner.util.CallUtil;

public class CallUtilTest {

	private int callTime;
	private int min;
	private int max;
	
	@Before
	public void setUp() throws Exception {
		min = 1000 * 5;
		max = 1000 * 10;
	}
	
	@Test(expected = InterruptedException.class)
	public void throwInterruptedExceptionIfCallTimeIsInterrupted() throws InterruptedException {
		CallUtil callUtil = mock(CallUtil.class);
		doThrow(InterruptedException.class).when(callUtil).callTimeToMock();
		callUtil.callTimeToMock();
	}
	
	@Test
	public void callTimeOutRange() {
		try {
			callTime = CallUtil.callTime();
			assertFalse(callTime < min && callTime > max);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void callTimeInRange() {
		try {
			callTime = CallUtil.callTime();
			assertTrue(callTime > min && callTime < max);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
	
}
