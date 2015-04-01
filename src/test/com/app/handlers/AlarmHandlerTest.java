package test.com.app.handlers;

import org.junit.Test;

import com.app.handlers.AlarmHandler;

public class AlarmHandlerTest {

	/*@Test
	public void testCreateAlarm() {
		AlarmHandler handler = new AlarmHandler();
		handler.createAlarm("T04-VM01-Ubu-ABH");
		
	}*/

	@Test
	public void testCheckAlarm() {
		AlarmHandler handler = new AlarmHandler();
		if(handler.checkAlarm("T04-VM01-Ubu-ABH")) {
			
			System.out.println("Alarm Triggered");
		}else {
			System.out.println("Alarm Not Triggered");
		}
	}

}
