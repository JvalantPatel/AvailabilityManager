package com.app.managers;

import com.app.data.InfrastructureData;
import com.app.handlers.SnapshotHandler;

public class SnapshotManager extends Thread {
	SnapshotHandler snapShothandler;

	public SnapshotManager() {

		snapShothandler = new SnapshotHandler();

	}

	public void run() {

		// create snapshot for vm

		snapShothandler.createSnapShotForVM(InfrastructureData.getInstance());

		// create snapshot for host
		/*
		 * snapShothandler.createSnapShotForHost(InfrastructureData
		 * .getInstance());
		 */

	}

}
