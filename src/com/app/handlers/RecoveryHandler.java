package com.app.handlers;

import java.rmi.RemoteException;
import java.util.List;

import com.app.data.InfrastructureData;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.MigrationFault;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.Timedout;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
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
			InsufficientResourcesFault, NotFound, RuntimeFault, RemoteException, InterruptedException {

		System.out.println("Recovery Handler: Name of vHost: " + hs.getName());
		//System.out.println("Recovery Handler: Name of vHost Status: "+ hs.getHealthStatusSystem().toString());
		System.out.println("Recovery Handler: VM name: " + vm.getName());
		System.out.println("Recovery Handler: Recovering VM ....");
		
		
		VirtualMachine vmHost = getvHostFromAdminVCenter(hs.getName());
		
		// Case 1 : To recover the VM on the same Host
		
		if (hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn) {
			
			System.out.println("Recovery Handler: case 1");
			System.out.println("Recovery Handler: The Host is available and recovering VM on the current Host");
			recoverVMtoSameHost(vm,hs);
			
			return true;
		}
		
		// Case 2 : Try to make VHost alive - 3 attempts
		
		else if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOff ||
				hs.getSummary().runtime.connectionState == hs.getSummary().runtime.connectionState.disconnected){
			System.out.println("Recovery Handler: case 2");
			System.out.println("Recovery Handler: trying to reconnect the vHost");
			if(reconnectHostandRecoverVM(vm,hs)){
				
				//System.out.println("case 2");
				
				return true;
			}
			//Case 3 : To move the VM on other available host and to recover the current vHost
			else {
				
				System.out.println("Recovery Handler:case 3");
				System.out.println("Recovery Handler: trying to migrate to other available vHosts");
				List<HostSystem> vHosts = InfrastructureData.getInstance().getHostSystems();
				if (vHosts.size() != 1) {
					for (HostSystem vHost : vHosts) {
						if (vHost.getSummary().runtime.powerState == vHost
								.getSummary().runtime.powerState.poweredOn) {
							if(migrateVMandRecover(vm,vHost)){
								System.out.println("Recovery Handler:Migrated sucessfully");
								return true;
							}
							else
								System.out.println("Recovery Handler:Migration unsucessfull ");
						}				
					}				
				} 
				
				recoverHostandVM(hs,vm);	
					
					
				//}
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
		if(mesAdmin[j].getName().equals("130.65.132.65")){
			 computeResource = (ComputeResource) mesAdmin[j];
		}
		}
		
		//System.out.println(computeResource.getName());
		ResourcePool rp = computeResource.getResourcePool();
		for(int index=0;index<rp.getResourcePools().length;index++){
			if(rp.getResourcePools()[index].getName().equals("Team04_vHost")){
				ResourcePool myResource = rp.getResourcePools()[index];
				//System.out.println(myResource.getVMs()[2].getName());
				for(int i=0;i<myResource.getVMs().length;i++){
					if(myResource.getVMs()[i].getName().contains(vHostName)){
						//System.out.println("GetvHostFromAdminVCenter :vm found");
						return myResource.getVMs()[i];
					}
						
				//System.out.println("GetvHostFromAdminVCenter" +myResource.getVMs()[i].getName());
				}
			}
		}
		
		return null;
	}


	private static boolean migrateVMandRecover(VirtualMachine vm,HostSystem hs) throws VmConfigFault, Timedout, FileFault, InvalidState, InsufficientResourcesFault, MigrationFault, RuntimeFault, RemoteException{
		ComputeResource cr = (ComputeResource) hs.getParent();
		if(vm.getSummary().runtime.powerState == vm.getSummary().runtime.powerState.poweredOn ){
			System.out.println("MigrateVMandRecover: Hot migration not possible");
			return false;
		}
		Task taskVm = vm.migrateVM_Task(cr.getResourcePool(), hs, VirtualMachineMovePriority.highPriority, VirtualMachinePowerState.poweredOff);
		
		while (taskVm.getTaskInfo().state == taskVm.getTaskInfo().state.running) {
		}				
		Task revertTask = vm.revertToCurrentSnapshot_Task(null);
		while (revertTask.getTaskInfo().state == revertTask.getTaskInfo().state.running) {
		}
		vm.powerOnVM_Task(null);
		if (revertTask.getTaskInfo().getState() == revertTask.getTaskInfo().getState().success) {
			System.out.println("Recovery Handler:VM has been recovered on vHost - "+hs.getName());
			return true;
		}
		return false;
	}
	
	private static void recoverVMtoSameHost(VirtualMachine vm,HostSystem host) throws InvalidProperty, RuntimeFault, RemoteException{
		Task task = vm.revertToCurrentSnapshot_Task(null);
		while (task.getTaskInfo().state == task.getTaskInfo().state.running) {
		}
		if (task.getTaskInfo().getState().success == TaskInfoState.success) {
			System.out.println("Recovery Handler:VM has been recovered on vHost - "+host.getName());
		}
		Task taskVm = vm.powerOnVM_Task(host);
		while (taskVm.getTaskInfo().state == taskVm.getTaskInfo().state.running) {
		}
	}
	
/*	private static boolean reconnectHostandRecoverVM(VirtualMachine vm,HostSystem hs) throws InvalidProperty, RuntimeFault, RemoteException {
		
		VirtualMachine vmFromAdmin =getvHostFromAdminVCenter(hs.getName().substring(11, hs.getName().length()));					
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
			migrateVMandRecover(vm,hs);
			return true;
		}	
	}
		
		return false;
		
	}*/
	
	@SuppressWarnings({ "static-access", "unused" })
	private static boolean reconnectHostandRecoverVM(VirtualMachine vm,HostSystem hs) throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException{
		
		VirtualMachine vmFromAdmin =getvHostFromAdminVCenter(hs.getName().substring(11, hs.getName().length()));	
		
		System.out.println("Reconnection Host and Recover: Trying to power ON the vHost " +hs.getName());
		if(vmFromAdmin.getSummary().runtime.powerState ==  vmFromAdmin.getSummary().runtime.powerState.poweredOff){
			Task task = vmFromAdmin.powerOnVM_Task(null);
		
			while (task.getTaskInfo().state == task.getTaskInfo().state.running) {
			System.out.print(". ");
			}
		}
		
		if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn){
		System.out.println("Reconnection Host and Recover:vHost "+hs.getName()+"is powered on now..");
		}
		
		System.out.println("Reconnection Host and Recover:waiting for vHost "+hs.getName()+" to be available");
		
		//while(!pingVirtualMachine("130.65.132.162"));
		
		Thread.sleep(1000 *60 *1);
		
		System.out.println("Reconnection Host and REcover:Trying to reconnect vHost...");
		
		for(int i=0;i<15;i++){
			System.out.println(" ");
			System.out.println("Reconnection Host and Recover: Attempt - "+i);
			
			Task taskvHost = hs.reconnectHost_Task(null);
	
		
			while (taskvHost.getTaskInfo().state == taskvHost.getTaskInfo().state.running) {
			System.out.print(".");
			}
		
		
			if(taskvHost.getTaskInfo().state == taskvHost.getTaskInfo().state.success){
				if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn){
					System.out.println("Reconnection Host and Recover: vHost "+hs.getName()+" is connected now..");
					if(!AlarmHandler.checkAlarm(vm.getName()))
						recoverVMtoSameHost(vm,hs);
				}

				break;	
			}
			
		}
			
		return false;
	}
	
	public static void recoverHostandVM(HostSystem hs, VirtualMachine vm) throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		//case 4: if none of the host is alive then recover the current vHost
		System.out.println("Recovery Handler:case 4");
		//System.out.println("Recovery Handler: No other Host is available");
		System.out.println("Recovery Handler:" + hs.getName() +" Host is being recovered... - ");
		VirtualMachine vHostVM = getvHostFromAdminVCenter(hs.getName().substring(11, hs.getName().length()));
		Task taskHost = vHostVM.revertToCurrentSnapshot_Task(null);
		if (taskHost.getTaskInfo().getState() == taskHost.getTaskInfo().getState().success) {
			System.out.println("Recovery Handler: vHost has been recovered on the admin vCenter..");
		}
		
		if(reconnectHostandRecoverVM(vm,hs)){
			System.out.println("Recovery Handler: Host reconnected");
		}
	}
}
