/**
 * 
 */
package test.com.app.handlers;

import java.rmi.RemoteException;




import org.junit.Test;

import com.app.data.InfrastructureData;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;

/**
 * @author Jvalant
 *
 */
public class ReconnectHost{

	/*@Test
	public void getStatusOfAllvHosts() throws InvalidProperty, RuntimeFault, RemoteException {
		
		ServiceInstance instance = InfrastructureData.getInstance().getServiceInstance();
		InfrastructureData.getInstance().updateInfra();
		for(HostSystem system : InfrastructureData.getInstance().getHostSystems()){
			System.out.print("vHost Name: "+system.getName());
			System.out.println(" Status: "+system.getSummary().overallStatus);
		}
		
	}*/
	
	@Test
	public void getStatusOfAllvHosts() throws InvalidProperty, RuntimeFault, RemoteException {
		
		ServiceInstance instance = InfrastructureData.getInstance().getServiceInstance();
		Folder root = instance.getRootFolder();
		ManagedEntity[] mes = new InventoryNavigator(root).searchManagedEntities("HostSystem");
		
		HostSystem hs = ((HostSystem) mes[1]);
		
		System.out.println(hs.getConfig().sslThumbprintInfo.sslThumbprints);
		
		HostConnectSpec hostConnectSpec = new HostConnectSpec();
		hostConnectSpec.setHostName("130.65.132.162");
		hostConnectSpec.setPassword("12!@qwQW");
		hostConnectSpec.setUserName("root");
		hostConnectSpec.setVimAccountName("root");
		hostConnectSpec.setVimAccountPassword("12!2qwQW");
		hostConnectSpec.setSslThumbprint("0F:A1:03:D1:34:1C:A0:EC:90:2B:40:9F:3E:CD:F6:10:36:9A:12:2D");
		
		//hs.reconnectHost_Task(hostConnectSpec);
		
		/*Datacenter dc = (Datacenter)mes[0];
		

		ServiceInstance instanceAdmin = InfrastructureData.getInstance().getAdminServiceInstance();
		Folder rootAdmin = instanceAdmin.getRootFolder();
		ManagedEntity[] mesAdmin = new InventoryNavigator(rootAdmin).searchManagedEntities("ComputeResource");
		ComputeResource computeResource = (ComputeResource) mesAdmin[0];
		System.out.println(computeResource.getName());
		ResourcePool rp = computeResource.getResourcePool();
		for(int index=0;index<rp.getResourcePools().length;index++){
			if(rp.getResourcePools()[index].getName().equals("Team04_vHOSTS")){
				ResourcePool myResource = rp.getResourcePools()[index];
				//System.out.println(myResource.getVMs()[2].getName());
				for(int i=0;i<myResource.getVMs().length;i++)
				System.out.println(myResource.getVMs()[i].getName());
			}
		}
		
		/*HostConnectSpec hostConnectSpec = new HostConnectSpec();
		hostConnectSpec.setHostName("130.65.132.162");
		hostConnectSpec.setPassword("12!@qwQW");
		hostConnectSpec.setUserName("root");
		hostConnectSpec.setVimAccountName("root");
		hostConnectSpec.setVimAccountPassword("12!2qwQW");
		//hostConnectSpec.setSslThumbprint("0F:A1:03:D1:34:1C:A0:EC:90:2B:40:9F:3E:CD:F6:10:36:9A:12:2D");
		
		
		
		Task task = dc.getHostFolder().addStandaloneHost_Task(hostConnectSpec, null, false);*/
		
		/*while(task.getTaskInfo().getState().equals("running")){};
		hostConnectSpec.sslThumbprint = task.getTaskInfo().error.fault.;
		dc.getHostFolder().
		
		hostConnectSpec.vmFolder*/
	}

}
