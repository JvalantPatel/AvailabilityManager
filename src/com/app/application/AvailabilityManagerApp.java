package com.app.application;

import java.rmi.RemoteException;

import com.app.data.InfrastructureData;
import com.app.handlers.RecoveryHandler;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class AvailabilityManagerApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		InfrastructureData infra = InfrastructureData.getInstance();
		ServiceInstance si = infra.getServiceInstance();
		Folder rootFolder = si.getRootFolder();
		try {
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
			for(int i=0;i<mes.length;i++){
				if(mes[i].getName().equals("T04-VM01-Ubu-ABH")){
					VirtualMachineSummary  host = ((VirtualMachine)mes[i]).getSummary();
					System.out.println(host.toString());
					//RecoveryHandler.recoverVM((VirtualMachine)mes[i]);	
				}
			}
		
		} catch (InvalidProperty e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
