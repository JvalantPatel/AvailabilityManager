package com.app.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class InfrastructureData {

	private  List<HostSystem>  hostSystems ;
	private static InfrastructureData instance ;
	private  ServiceInstance serviceInstance ;
	
	public InfrastructureData() throws MalformedURLException, RemoteException{
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
	
	public  void updateInfra(HostSystem[] newSystems){
		List<HostSystem>  hostSystemsNew = Arrays.asList(newSystems);
		HostSystem system = hostSystemsNew.get(0);
		try {
			VirtualMachine[] vms = system.getVms();
			vms[0].g
			
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
