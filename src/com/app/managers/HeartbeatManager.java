package com.app.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import org.tempuri.Service;
import org.tempuri.ServiceSoap;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.app.data.InfrastructureData;
import com.app.handlers.RecoveryHandler;

public class HeartbeatManager extends Thread {

	ManagedEntity[] mes;
	ServiceInstance serviceinstance;
	private static HeartbeatManager instance;

	public HeartbeatManager() {
		serviceinstance = InfrastructureData.getInstance().getServiceInstance();
	}

	public static HeartbeatManager getInstance() {
		if (instance == null) {
			return new HeartbeatManager();
		}
		return instance;
	}

	public void ping() {
		for (HostSystem hostSystem : InfrastructureData.getInstance()
				.getHostSystems()) {
			VirtualMachine[] vMList;
			try {
				vMList = hostSystem.getVms();

				for (VirtualMachine vM : vMList) {
					if (!vM.getConfig().template) {
						String hostIP = vM.getGuest().ipAddress;
						if(hostIP != null) {
							if(!pingVirtualMachine(hostIP)) {
								RecoveryHandler.recoverVM(vM, hostSystem);
							}
						} else {
							System.out.println("HeartbeatManager: IP not found for VM " + vM.getName());
							RecoveryHandler.recoverVM(vM, hostSystem);
						}
						
					}
				}

			} catch (InvalidProperty e) {
				System.out.println("HeartbeatManager: Ping Invalid Property Excetiption");
			} catch (RuntimeFault e) {
				System.out.println("HeartbeatManager: Ping Runtime Fault Excetiption");
			} catch (RemoteException e) {
				System.out.println("HeartbeatManager: Ping Remote Excetiption");
			}

		}
	}
	
	public boolean pingVirtualMachine(String ip) {
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("ping " + ip);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String inputLine;
            int waitCount = 0;
            boolean failure = true;
            
            if(er.readLine() != null) {
            	in.close();
                er.close();
            	return false;
            }
            
            while ((inputLine = in.readLine()) != null) {
            	if(inputLine.equals("Request timed out.")) {
            		waitCount++;
            		if(waitCount == 2) {
            			failure = false;
            			break;
            		}
            	} 
            }
            in.close();
            er.close();
            return failure;

        } catch (IOException e) {
        	System.out.println("HeartbeatManager: Ping IO Excetion");
        }
        
        return false;
	}

	public void run() {
		while (true) {
			try {
				
				System.out.println("Heartbeat manager checking beats....");
				InfrastructureData.getInstance().updateInfra();
				ping();
				Thread.sleep(1000 * 60 * 3);
			} catch (InterruptedException e) {
				System.out.println("HeartbeatManager: Thread Interrupted Exception");
			}
		}
	}
}
