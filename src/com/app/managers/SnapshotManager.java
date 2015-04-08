package com.app.managers;

import com.app.data.InfrastructureData;
import com.app.handlers.SnapshotHandler;
import com.vmware.vim25.mo.ServiceInstance;

public class SnapshotManager extends Thread {
	SnapshotHandler snapShothandler;
	ServiceInstance serviceinstance;
	private static SnapshotManager instance;
	
	public SnapshotManager() {
		serviceinstance = InfrastructureData.getInstance().getServiceInstance();
		snapShothandler = new SnapshotHandler();
	}

	public static SnapshotManager getInstance() {
		if (instance == null) {
			return new SnapshotManager();
		}
		return instance;
	}

	public void run() {
		
		while (true) {
			try {
				Thread.sleep(60000);
				System.out.println("Taking snapshot of VMs...");
				snapShothandler.createSnapShotForVM(serviceinstance);
			} catch (InterruptedException e) {
				System.out.println("HeartbeatManager: Thread Interrupted Exception");
			}
		}

		// create snapshot for vm

		

		// create snapshot for host
		/*
		 * snapShothandler.createSnapShotForHost(InfrastructureData
		 * .getInstance());
		 */

	}

}
