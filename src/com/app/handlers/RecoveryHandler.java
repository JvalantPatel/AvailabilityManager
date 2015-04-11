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
				System.out.println("VM has been recovered on vHost - "+hs.getName());
			}
			Task taskVm = vm.powerOnVM_Task(hs);
			while (taskVm.getTaskInfo().state == taskVm.getTaskInfo().state.running) {
			}

			return true;
		}
		else if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOff){
			VirtualMachine vmFromAdmin =getvHostFromAdminVCenter(hs.getName().substring(11, hs.getName().length()));
		//case 4: Try to make VHost alive - 3 attempts
					
			Task task = vmFromAdmin.powerOnVM_Task(null);
			while (task.getTaskInfo().state == task.getTaskInfo().state.running) {
				System.out.print(". ");
			}
			System.out.println("vHost is powered on now..");
			System.out.println("Trying to reconnect vHost...");
			for(int attempt=0;attempt<3;attempt++){
				System.out.println("Attempt no -"+attempt);
				Task reconnectTask = hs.reconnectHost_Task(null);
				while (reconnectTask.getTaskInfo().state == reconnectTask.getTaskInfo().state.running) {
					System.out.print(".");
				}
			if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn){
				System.out.println("VHost is connected now..");
				Task taskVm = vm.revertToCurrentSnapshot_Task(null);
				while (taskVm.getTaskInfo().state == taskVm.getTaskInfo().state.running) {
				}
				if (task.getTaskInfo().getState().success == TaskInfoState.success) {
					System.out.println("VM has been recovered on vHost - "+hs.getName());
					
				}
				return true;
			}	
		}
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

				System.out.println("The current Host is being recovered with the Vm's");
				VirtualMachine vHostVM = getvHostFromAdminVCenter(hs.getName()
						.toString());
				Task taskHost = vHostVM.revertToCurrentSnapshot_Task(null);

				if (taskHost.getTaskInfo().getState().success == TaskInfoState.success) {
					System.out.println("vHost has been recovered on the admin vCenter..");
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
		ServiceInstance instanceAdmin = InfrastructureData.getInstance().getAdminServiceInstance();
		Folder rootAdmin = instanceAdmin.getRootFolder();
		ComputeResource computeResource = null;
		ManagedEntity[] mesAdmin = new InventoryNavigator(rootAdmin).searchManagedEntities("ComputeResource");
		for(int j=0;j<mesAdmin.length;j++){
		if(mesAdmin[j].getName().equals("130.65.132.61")){
			 computeResource = (ComputeResource) mesAdmin[j];
		}
		}
		
		System.out.println(computeResource.getName());
		ResourcePool rp = computeResource.getResourcePool();
		for(int index=0;index<rp.getResourcePools().length;index++){
			if(rp.getResourcePools()[index].getName().equals("Team04_vHost")){
				ResourcePool myResource = rp.getResourcePools()[index];
				//System.out.println(myResource.getVMs()[2].getName());
				for(int i=0;i<myResource.getVMs().length;i++){
					if(myResource.getVMs()[i].getName().contains(vHostName)){
						System.out.println("vm found");
						return myResource.getVMs()[i];
					}
						
				System.out.println(myResource.getVMs()[i].getName());
				}
			}
		}
		
		return null;
	}

}
