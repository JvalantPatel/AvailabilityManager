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
	private  ServiceInstance adminServiceInstance ;
	
	private InfrastructureData() throws MalformedURLException, RemoteException{
		hostSystems = new ArrayList<HostSystem>();
		URL url = new URL("https://130.65.132.104/sdk");
		URL urlAdmin = new URL("https://130.65.132.19/sdk");
		serviceInstance = new ServiceInstance(url, "administrator", "12!@qwQW", true);
		adminServiceInstance = new ServiceInstance(urlAdmin, "student@vsphere.local", "12!@qwQW", true);
	}
	
	public  ServiceInstance getServiceInstance(){
		return serviceInstance;
	}
	
	public  ServiceInstance getAdminServiceInstance(){
		return adminServiceInstance;
	}
	
	public static  InfrastructureData getInstance(){
		if(instance==null){
			try {
				instance = new InfrastructureData();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Malformed URL Exception");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Remote Exception while connecting");
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
		} catch (Exception e) {
			System.out.println("Infrastructure Data: Exception occured while getting HostSystem form inventory");
		}
		System.out.println("InfrastructureData: Updating Infrastructure data .... ");
		System.out.println(" ");
		if(checkAndUpdateAlerts(hostSystems)){
			System.out.println("InfrastructureData: Infrastructure data updated successfully ....");
			System.out.println(" ");
		}
		else{
			System.out.println("InfrastructureData: Infrastructure data update failed ....");
			System.out.println(" ");
		}
	}
	
	private boolean checkAndUpdateAlerts(List<HostSystem> vHosts){
		System.out.println("InfrastructureData: List of vHost and their VM's");
		for(HostSystem vHost:vHosts){
			System.out.println("InfrastructureData: vHost Name: "+vHost.getName());
			//System.out.println(" ");
			try {
				for(VirtualMachine vm:vHost.getVms()){
				if(!vm.getConfig().template){
					System.out.println("InfrastructureData: VM Name: "+vm.getName());
					System.out.println("InfrastructureData: Chekcing if Alarm is created or not ...");
					AlarmHandler.createAlarm(vm.getName());}				
				}
				
			} catch (Exception e){
				System.out.println("Infrastructure Data: Exception occured while creating alerts");
			}
					
		}
		return true;
		
	}
	
	
}
