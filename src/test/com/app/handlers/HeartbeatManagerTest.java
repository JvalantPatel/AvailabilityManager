package test.com.app.handlers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.app.managers.HeartbeatManager;

public class HeartbeatManagerTest {

	@Test
	public void testPing() {
		HeartbeatManager manager = HeartbeatManager.getInstance();
		//manager.ping();
		//manager.pingVirtualMachine("216.58.192.14");
		if(manager.pingVirtualMachine("1.2.3.4")) {
			System.out.println("Ping successful");
		} else {
			System.out.println("Ping NOT successful");
		}
	}

}
