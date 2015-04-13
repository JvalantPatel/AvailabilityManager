package com.app.handlers;


import com.app.data.InfrastructureData;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class SnapshotHandler {

	/**
	 * Responsible for creating snapshot of VMs.
	 * 
	 * @param instance
	 */
	public void createSnapShotForVM(ServiceInstance serviceInstace) {
		// System.out.println("SnapShot created for VM");

		Folder rootFolder = serviceInstace.getRootFolder();
		try {

			// get VMs.
			ManagedEntity[] mes = new InventoryNavigator(rootFolder)
					.searchManagedEntities("VirtualMachine");

			for (int i = 0; i < mes.length; i++) {
				VirtualMachine vm = (VirtualMachine) mes[i];
				if (!vm.getConfig().template) {
					// checking the state of each vm
					System.out.println("Snapshot Handler: " +vm.getSummary().runtime.powerState.toString());
					System.out.println("Snapshot Handler: " +vm.getGuest().getIpAddress());
					if ((vm.getSummary().runtime.powerState ==
							vm.getSummary().runtime.powerState.poweredOn)
							&& (vm.getGuest().getIpAddress() != null)) {
						// removing snapshots
						System.out
								.println("Snapshot Handler: Removing exisiting snapshots for vm: "
										+ vm.getName());
						removeSnapShot(vm);

						System.out.println("Snapshot Handler: Now creating snapshots for vm "
								+ vm.getName() + "......");

						createSnapShot(vm);

					} else {
						System.out
								.println("Snapshot Handler: Cannot take snapshot as vm is powered off");
					}
				}

			}
		} catch (Exception e) {
			System.out.println("Snapshot Handler: An exception has occured during vm snapshot");
			//e.printStackTrace();

		}

	}

	/**
	 * Responsible for creating snapshot for Hosts.
	 * 
	 * @param instance
	 */
	public void createSnapShotForHOST(ServiceInstance adminServiceInstance, ServiceInstance serviceInstance) 
	{
	System.out.println("Snapshot Handler: SnapShot creation system for Hosts \n");
	Folder rootFolderAdmin = adminServiceInstance.getRootFolder();
        Folder rootFoldervCenter = serviceInstance.getRootFolder();
        
        
        try {
        	ManagedEntity[] mesAdmin = new InventoryNavigator(rootFolderAdmin).searchManagedEntities("VirtualMachine");
			ManagedEntity[] mesvCenter = new InventoryNavigator(rootFoldervCenter).searchManagedEntities("HostSystem");
			
			for (int i = 0; i < mesvCenter.length; i++) {
				
				HostSystem hs = (HostSystem) mesvCenter[i];
				
				for (int j = 0; j < mesAdmin.length; j++) {
					
					VirtualMachine vm = (VirtualMachine) mesAdmin[j];
					if (!vm.getConfig().template) {
						
						// checking the state of each vm
						if (vm.getName().toString().contains(hs.getName().substring(7)) 
								&& (vm.getSummary().runtime.powerState ==
								vm.getSummary().runtime.powerState.poweredOn)
								) {
													
							// Displaying status of the corresponding hosts
							System.out.println("Snapshot Handler: The current status of the host " + vm.getName().toString()+ " is: " 
							+ vm.getSummary().runtime.powerState.toString() + "\n");
							
							// removing snapshots
							System.out
									.println("Snapshot Handler: Removing exisiting snapshots for the host: "
											+ vm.getName());
						removeSnapShot(vm);
						
	
							// Creating snapshots
							System.out.println("Snapshot Handler: Now creating snapshots for the host "
									+ vm.getName() + "......");

						createSnapShot(vm);

						} 
						
						else if(!vm.getName().toString().contains(hs.getName().substring(7))) 
						{
							//System.out.println("Not our Host dude !!")
							;
						}
						
						else {
							System.out.println("Snapshot Handler: Cannot take snapshot as the host is powered off \n");
						}
					}
	
				}
 
			}
        	
		} catch (Exception e) {
			System.out.println("Snapshot Handler: An exception has occured during snapshot creation");
			//e.printStackTrace();

		}
		
	}

	/**
	 * Responsible for creating snapshot.
	 * 
	 * @param instance
	 */
	private void createSnapShot(VirtualMachine vm) {

		Task createTask;
		try {
			createTask = vm.createSnapshot_Task(vm.getName() + "_VM-Snapshot",
					"Creating snapshot for vm", false, false);
			if (createTask.waitForTask() == Task.SUCCESS) {
				System.out.println("Snapshot Handler: Snapshot Created successfully \n");
			} else {
				System.out.println("Snapshot Handler: Snapshot Creation failed \n");
			}
		} catch (Exception e) {
			System.out.println("Snapshot Handler: An exception has occured during snapshot creation");
			//e.printStackTrace();
		}

	}

	/**
	 * Responsible for deleting snapshot for Hosts.
	 * 
	 * @param instance
	 */
	private void removeSnapShot(VirtualMachine vm) {
		Task removeTask;
		try {
			removeTask = vm.removeAllSnapshots_Task();
			if (removeTask.waitForTask() == Task.SUCCESS) {
				System.out.println("Snapshot Handler: Snapshot removed successfully \n");
			} else {
				System.out.println("Snapshot Handler: No Snapshots available for VM : "
						+ vm.getName());
			}
		} catch (Exception e) {
			System.out.println("Snapshot Handler: An exception has occured during snapshot removal");
			//e.printStackTrace();
		}

	}

	public void createSnapShotForHost(InfrastructureData instance) {
		System.out.println("Snapshot Handler: SnapShot created for Host");

	}
	

}
