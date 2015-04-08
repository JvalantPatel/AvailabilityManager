package com.app.handlers;

import com.app.data.InfrastructureData;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class SnapshotHandler {

	/**
	 * Responsible for creating snapshot of VMs.
	 * 
	 * @param instance
	 */

	public void createSnapShotForVM(ServiceInstance serviceInstace) {
		// System.out.println("SnapShot created for VM");

		Folder rootFolder = serviceInstace.getRootFolder();
		try {

			// get VMs.
			ManagedEntity[] mes = new InventoryNavigator(rootFolder)
					.searchManagedEntities("VirtualMachine");

			for (int i = 0; i < mes.length; i++) {
				VirtualMachine vm = (VirtualMachine) mes[i];
				if (!vm.getConfig().template) {
					// checking the state of each vm
					System.out.println(vm.getSummary().runtime.powerState.toString());
					System.out.println(vm.getGuest().getIpAddress());
					if ((vm.getSummary().runtime.powerState ==
							vm.getSummary().runtime.powerState.poweredOn)
							&& (vm.getGuest().getIpAddress() != null)) {
						// removing snapshots
						System.out
								.println("Removing exisiting snapshots for vm: "
										+ vm.getName());
						removeSnapShot(vm);

						System.out.println("Now creating snapshots for vm "
								+ vm.getName() + "......");

						createSnapShot(vm);

					} else {
						System.out
								.println("Cannot take snapshot as vm is powered off");
					}
				}

			}
		} catch (Exception e) {
			System.out.println("An exception has occured during vm snapshot");
			e.printStackTrace();

		}

	}

	private void createSnapShot(VirtualMachine vm) {

		Task createTask;
		try {
			createTask = vm.createSnapshot_Task(vm.getName() + "_VM-Snapshot",
					"Creating snapshot for vm", false, false);
			if (createTask.waitForTask() == Task.SUCCESS) {
				System.out.println("Snapshot Created successfully");
			} else {
				System.out.println("Snapshot Creation failed");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void removeSnapShot(VirtualMachine vm) {
		Task removeTask;
		try {
			removeTask = vm.removeAllSnapshots_Task();
			if (removeTask.waitForTask() == Task.SUCCESS) {
				System.out.println("Snapshot removed successfully");
			} else {
				System.out.println("No Snapshots available for VM : "
						+ vm.getName());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Responsible for creating snapshot for Hosts.
	 * 
	 * @param instance
	 */

	public void createSnapShotForHost(InfrastructureData instance) {
		System.out.println("SnapShot created for Host");

	}

}
