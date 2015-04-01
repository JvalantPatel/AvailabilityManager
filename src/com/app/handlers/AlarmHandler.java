package com.app.handlers;

import java.rmi.RemoteException;

import com.app.data.InfrastructureData;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class AlarmHandler {
	String alarmName = "VmPowerStatus";

	public void createAlarm(String vmName) {
		ServiceInstance serviceInstance = InfrastructureData.getInstance()
				.getServiceInstance();
		InventoryNavigator inv = new InventoryNavigator(serviceInstance.getRootFolder());
		try {
			VirtualMachine vm = (VirtualMachine)inv.searchManagedEntity("VirtualMachine", vmName);
			if(vm == null) {
				System.out.println("AlarmManager: Cannot find the VM - " + vmName);
			}
			AlarmManager.
			
			
		} catch (InvalidProperty e) {
			System.out.println("AlarmManager: Invalid Property");
			e.printStackTrace();
		} catch (RuntimeFault e) {
			System.out.println("AlarmManager: Run time fault");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.out.println("AlarmManager: Remote Connection error");
			//e.printStackTrace();
		}

	}

	private StateAlarmExpression createAlarmExpression() {
		StateAlarmExpression expression = new StateAlarmExpression();
		expression.setType("VirtualMachine");
		expression.setStatePath("runtime.powerState");
		expression.setOperator(StateAlarmOperator.isEqual);
		expression.setRed("poweredOff");
		return expression;
	}
}
