package com.app.handlers;

import java.rmi.RemoteException;
import java.util.List;

import com.app.data.InfrastructureData;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class RecoveryHandler {

	@SuppressWarnings("static-access")
	public static boolean recoverVM(VirtualMachine vm, HostSystem hs)
			throws VmConfigFault, SnapshotFault, TaskInProgress, InvalidState,
			InsufficientResourcesFault, NotFound, RuntimeFault, RemoteException {

		System.out.println("Name of vHost: " + hs.getName());
		System.out.println("Name of vHost Status: "
				+ hs.getHealthStatusSystem().toString());
		System.out.println("VM name: " + vm.getName());

		System.out.println("Recovering VM from current snapshot....");
		// Case 1 : To recover the VM on the same Host
		if (hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn) {
			Task task = vm.revertToCurrentSnapshot_Task(null);
			while (task.getTaskInfo().state == task.getTaskInfo().state.running) {
			}
			if (task.getTaskInfo().getState().success == TaskInfoState.success) {
				System.out.println("VM has been recovered..");
			}
			Task taskVm = vm.powerOnVM_Task(hs);
			while (taskVm.getTaskInfo().state == taskVm.getTaskInfo().state.running) {
			}

			return true;
		}
		
		//Case 2 : To move the VMs on other available host and to recover the current vHost
		else {
			List<HostSystem> vHosts = InfrastructureData.getInstance()
					.getHostSystems();
			if (vHosts.size() != 1) {
				for (HostSystem vHost : vHosts) {
					if (vHost.getSummary().runtime.powerState == vHost
							.getSummary().runtime.powerState.poweredOn) {
						Task task = vm.revertToCurrentSnapshot_Task(vHost);
						if (task.getTaskInfo().getState().success == TaskInfoState.success) {
							System.out
									.println("VM has been recovered on other Host..");
						}

						return true;
					}

					else {

						System.out.println("Host is being recovered");
						VirtualMachine vHostVM = getvHostFromAdminVCenter(hs
								.getName().toString());
						Task taskHost = vHostVM
								.revertToCurrentSnapshot_Task(null);

						if (taskHost.getTaskInfo().getState().success == TaskInfoState.success) {
							System.out
									.println("vHost has been recovered on the admin vCenter..");
						}

					}
				}
			} 
			
			//Case 3 : To recover the Host and the current VM on the Host
			else {

				System.out
						.println("The current Host is being recovered with the Vm's");
				VirtualMachine vHostVM = getvHostFromAdminVCenter(hs.getName()
						.toString());
				Task taskHost = vHostVM.revertToCurrentSnapshot_Task(null);

				if (taskHost.getTaskInfo().getState().success == TaskInfoState.success) {
					System.out
							.println("vHost has been recovered on the admin vCenter..");
				}

				System.out.println("Revovering the Vm's from the Host");

				if (vHostVM.getRuntime().powerState == vHostVM.getSummary().runtime.powerState.poweredOn) {
					Task taskVM = vm.revertToCurrentSnapshot_Task(null);
					if (taskVM.getTaskInfo().getState().success == TaskInfoState.success) {
						System.out
								.println("VM has been recovered on other Host..");
					}
				}

			}
		}

		return true;
	}

	/*
	 * To Add a Host private static HostSystem addvHostFromAdminvCenter() {
	 * 
	 * ServiceInstance adminService = InfrastructureData.getInstance()
	 * .getAdminServiceInstance(); return null; }
	 */

	private static VirtualMachine getvHostFromAdminVCenter(String vHostName)
			throws InvalidProperty, RuntimeFault, RemoteException {
		ServiceInstance instanceAdmin = InfrastructureData.getInstance()
				.getAdminServiceInstance();
		Folder rootAdmin = instanceAdmin.getRootFolder();
		ManagedEntity[] mesAdmin = new InventoryNavigator(rootAdmin)
				.searchManagedEntities("ComputeResource");
		for (int index = 0; index < mesAdmin.length; index++) {
			ComputeResource computeResource = (ComputeResource) mesAdmin[index];
			if (computeResource.getName().toString().equals("130.65.132.61")) {
				// System.out.println(computeResource.getName());
				ResourcePool rp = computeResource.getResourcePool();
				for (int i = 0; i < rp.getResourcePools().length; i++) {
					if (rp.getResourcePools()[i].getName().equals(
							"Team04_vHOSTS")) {
						ResourcePool myResource = rp.getResourcePools()[i];
						// System.out.println(myResource.getVMs()[2].getName());
						for (int j = 0; j < myResource.getVMs().length; j++) {
							if (myResource.getVMs()[j].getName()
									.equalsIgnoreCase(vHostName)) {
								return myResource.getVMs()[j];
							}
						}
					}
				}
			}
		}
		return null;
	}
}
