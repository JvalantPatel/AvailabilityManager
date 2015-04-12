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
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author Jvalant
 *
 */
public class RecoverySnapshot {

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
		ManagedEntity[] mes = new InventoryNavigator(root).searchManagedEntities("Datacenter");
		Datacenter dc = (Datacenter)mes[0];
		
		
		ServiceInstance instanceAdmin = InfrastructureData.getInstance().getAdminServiceInstance();
		Folder rootAdmin = instanceAdmin.getRootFolder();
		ManagedEntity[] mesAdmin = new InventoryNavigator(rootAdmin).searchManagedEntities("ComputeResource");
		for(int index=0;index<mesAdmin.length;index++){
		ComputeResource computeResource = (ComputeResource) mesAdmin[index];
		if(computeResource.getName().toString().equals("130.65.132.61")){
		System.out.println("found Computer Resource " +computeResource.getName());
		ResourcePool rp = computeResource.getResourcePool();
		for(int i=0;i<rp.getResourcePools().length;i++){
			if(rp.getResourcePools()[i].getName().toString().equals("Team04_vHost")){
				ResourcePool myResource = rp.getResourcePools()[i];
				//System.out.println(myResource.getVMs()[2].getName());
				for(int j=0;j<myResource.getVMs().length;j++)
				{
				System.out.println(myResource.getVMs()[j].getName());
				  if(myResource.getVMs()[j].getName().toString().equals("T04-vHost02_132.162"))
				  {
					  VirtualMachine vHostVM = (VirtualMachine) myResource.getVMs()[j];
					  vHostVM.revertToCurrentSnapshot_Task(null);
				  }
				}
		
		   }
		
		}
		
	}
		
	}
		
}

}
