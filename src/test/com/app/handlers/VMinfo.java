/**
 * 
 */
package test.com.app.handlers;

import java.rmi.RemoteException;






import org.junit.Test;

import sun.misc.VM;

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
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author Jvalant
 *
 */
public class VMinfo{

	
	
	@Test
	public void getStatusOfAllvHosts() throws InvalidProperty, RuntimeFault, RemoteException {
		
		ServiceInstance instance = InfrastructureData.getInstance().getServiceInstance();
		Folder root = instance.getRootFolder();
		ManagedEntity[] mes = new InventoryNavigator(root).searchManagedEntities("VirtualMachine");
		
		for( int i = 0 ; i < mes.length; i++)
		{
			VirtualMachine vm = ((VirtualMachine) mes[i]);
			if(vm.getRuntime().powerState.toString().equals("poweredOn"))
			{
				
				
				System.out.println("VM Name:"+vm.getName()); 	
				
				System.out.println("Guest OS:"+vm.getSummary().getConfig().guestFullName); 
			     System.out.println("VM Version:"+vm.getConfig().version); 
			     System.out.println("CPU:"+vm.getConfig().getHardware().numCPU+" vCPU"); 
			     System.out.println("Memory:"+vm.getConfig().getHardware().memoryMB+" MB"); 
			     //System.out.println("Memory Overhead:"+(long)vm.getConfig().initialOverhead.initialMemoryReservation/1000000f+" MB"); 
			     System.out.println("VMware Tools:"+vm.getGuest().toolsRunningStatus); 
			     System.out.println("IP Addresses:"+vm.getSummary().getGuest().getIpAddress()); 
			     System.out.println("State:"+vm.getGuest().guestState); 
			     
			     System.out.println("consumed memory " +vm.getResourcePool().getSummary().getQuickStats().guestMemoryUsage);
			     System.out.println("CPU Usage " +vm.getResourcePool().getSummary().getQuickStats().overallCpuUsage);
			     System.out.println("====================*********=============");
			}
			
		}
		
		
	}

}
