package com.app.application;

import com.app.managers.HeartbeatManager;
import com.app.managers.SnapshotManager;


public class AvailabilityManagerApp {

	public static void main(String[] args) {
		
		HeartbeatManager.getInstance().start();
		
		SnapshotManager.getInstance().start();

	}

}
