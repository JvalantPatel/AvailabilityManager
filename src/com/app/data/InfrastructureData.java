package com.app.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.app.handlers.AlarmHandler;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class InfrastructureData {

	private  List<HostSystem>  hostSystems ;
	
	public synchronized List<HostSystem> getHostSystems() {
		return hostSystems;
	}

	private static InfrastructureData instance ;
	private  ServiceInstance serviceInstance ;
	
	private InfrastructureData() throws MalformedURLException, RemoteException{
		hostSystems = new ArrayList<HostSystem>();
		URL url = new URL("https://130.65.132.104/sdk");
		serviceInstance = new ServiceInstance(url, "administrator", "12!@qwQW", true);
	}
	
	public  ServiceInstance getServiceInstance(){
		return serviceInstance;
	}
	
	public static  InfrastructureData getInstance(){
		if(instance==null){
			try {
				instance = new InfrastructureData();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public void updateInfra(){
		Folder rootFolder = this.serviceInstance.getRootFolder();
		try {
			this.hostSystems.clear();
			ManagedEntity[] mngEntity = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
			for(int index=0;index<mngEntity.length;index++){
				hostSystems.add((HostSystem)mngEntity[index]);
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
		System.out.println("[InfrastructureData]: Updating Infrastructure data ....");
		if(checkAndUpdateAlerts(hostSystems))
			System.out.println("[InfrastructureData]: Infrastructure data updated successfully ....");
		else
			System.out.println("[InfrastructureData]: Infrastructure data update failed ....");
	}
	
	private boolean checkAndUpdateAlerts(List<HostSystem> vHosts){
		
		for(HostSystem vHost:vHosts){
			try {
				for(VirtualMachine vm:vHost.getVms()){
					
				AlarmHandler.createAlarm(vm.getName());
					
				}
				
			} catch (InvalidProperty e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (RuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
					
		}
		return true;
		
	}
	
	
}
