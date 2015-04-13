package com.app.managers;

import com.app.data.InfrastructureData;
import com.app.handlers.SnapshotHandler;
import com.vmware.vim25.mo.ServiceInstance;

public class SnapshotManager extends Thread {
	SnapshotHandler snapShothandler;
	ServiceInstance serviceInstance;
	ServiceInstance adminServiceInstance;
	private static SnapshotManager instance;
	
	public SnapshotManager() {
		serviceInstance = InfrastructureData.getInstance().getServiceInstance();
		adminServiceInstance = InfrastructureData.getInstance().getAdminServiceInstance();
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
				
				System.out.println("Snapshot Manager:Taking snapshot of VMs...");
				snapShothandler.createSnapShotForVM(serviceInstance);
				snapShothandler.createSnapShotForHOST(adminServiceInstance, serviceInstance);
				Thread.sleep(60000 * 10);
			} catch (InterruptedException e) {
				System.out.println("Snapshot Manager: Thread Interrupted Exception");
			}
		}

	}

}
