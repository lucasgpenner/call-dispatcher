package ar.com.lpenner.util;

import java.util.Random;
import java.util.ResourceBundle;

import ar.com.lpenner.constants.AppResources;

public class CallUtil {
	
	private static final String BUNDLE_NAME = "app";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private static int minTime;
	private static int maxTime;
	
	
	
	/**
	 * Simulator of a call with a random time configured in properties file
	 * @return
	 * @throws InterruptedException
	 */
	public static int callTime() throws InterruptedException {
		CallUtil.callConfig();
		Random random = new Random();
		int msToSleep = random.nextInt(maxTime - minTime) + minTime;
		Thread.sleep(msToSleep);
		
		return msToSleep;
	}
	
	/**
	 * Simulator of a call with a random time configured in properties file
	 * @return
	 * @throws InterruptedException
	 */
	public int callTimeToMock() throws InterruptedException {
		return CallUtil.callTime();
	}
	
	private static void callConfig() {
		minTime = 1000 * Integer.parseInt(RESOURCE_BUNDLE.getString(AppResources.MIN_TIME_WAITING));
		maxTime = 1000 * Integer.parseInt(RESOURCE_BUNDLE.getString(AppResources.MAX_TIME_WAITING));
	}

}
