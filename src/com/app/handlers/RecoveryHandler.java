package com.app.handlers;

import java.rmi.RemoteException;
import java.util.List;

import com.app.data.InfrastructureData;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.HostSystem;
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
		// if()
		if (hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn) {
			Task task = vm.revertToCurrentSnapshot_Task(null);
			while(task.getTaskInfo().state == task.getTaskInfo().state.running){}
			if (task.getTaskInfo().getState().success == TaskInfoState.success) {
				System.out.println("VM has been recovered..");
			}
			Task taskVm = vm.powerOnVM_Task(hs);
			while(taskVm.getTaskInfo().state == taskVm.getTaskInfo().state.running){}
			
			return true;
		}
		
		else {
			List<HostSystem> vHosts = InfrastructureData.getInstance()
					.getHostSystems();
			if (vHosts.size() != 1) {
				for (HostSystem vHost : vHosts) {
					if (vHost.getSummary().runtime.powerState == vHost
							.getSummary().runtime.powerState.poweredOn) {
						Task task = vm.revertToCurrentSnapshot_Task(vHost);
						if (task.getTaskInfo().getState().success == TaskInfoState.success) {
							System.out.println("VM has been recovered..");
						}
						return true;
					}
				}
			} else {
				/*HostSystem newHost = addvHostFromAdminvCenter();
				InfrastructureData.getInstance().getHostSystems().add(newHost);
				if (newHost.getSummary().runtime.powerState == newHost
						.getSummary().runtime.powerState.poweredOn) {
					Task task = vm.revertToCurrentSnapshot_Task(newHost);
					if (task.getTaskInfo().getState().success == TaskInfoState.success) {
						System.out.println("VM has been recovered..");
					}
					return true;
				}*/
				
			}
		}

		return true;
	}

	private static HostSystem addvHostFromAdminvCenter() {
		
		ServiceInstance adminService = InfrastructureData.getInstance().getAdminServiceInstance();
		return null;
	}

}
