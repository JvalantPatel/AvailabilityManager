package com.app.handlers;

import java.rmi.RemoteException;

import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class RecoveryHandler {
	
	
	public static boolean recoverVM(VirtualMachine vm,HostSystem hs) throws VmConfigFault, SnapshotFault, TaskInProgress, InvalidState, InsufficientResourcesFault, NotFound, RuntimeFault, RemoteException{
		
		System.out.println("Name of vHost: "+hs.getName());
		System.out.println("Name of vHost Status: "+hs.getHealthStatusSystem().toString());
		System.out.println("VM name: "+vm.getName());
		System.out.println("Recovering VM from current snapshot....");
		Task task = vm.revertToCurrentSnapshot_Task(null);
		if(task.getTaskInfo().getState().success == TaskInfoState.success){
			System.out.println("VM has been recovered..");
		}
		return true;		
	}
	
	
	

}
