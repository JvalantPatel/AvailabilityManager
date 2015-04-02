/**
 * 
 */
package test.com.app.handlers;

import java.rmi.RemoteException;

import org.junit.Test;

import com.app.data.InfrastructureData;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * @author Jvalant
 *
 */
public class RecoveryHandlerTest {

	@Test
	public void getStatusOfAllvHosts() throws InvalidProperty, RuntimeFault, RemoteException {
		
		ServiceInstance instance = InfrastructureData.getInstance().getServiceInstance();
		InfrastructureData.getInstance().updateInfra();
		for(HostSystem system : InfrastructureData.getInstance().getHostSystems()){
			System.out.print("vHost Name: "+system.getName());
			System.out.println(" Status: "+system.getSummary().overallStatus);
		}
		
	}

}
