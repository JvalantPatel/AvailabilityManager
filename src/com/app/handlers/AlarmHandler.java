package com.app.handlers;

import java.rmi.RemoteException;

import com.app.data.InfrastructureData;
import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/***
 * Provides functionality to create power status alarm and check if the power
 * status alarm is triggered.
 * 
 */
public class AlarmHandler {
	static String alarmName = "VmPowerStatus";

	/**
	 * Creates Power status alarm for given VM
	 * 
	 * @param vmName
	 */
	static public void createAlarm(String vmName) {
		
		ServiceInstance serviceInstance = InfrastructureData.getInstance().getServiceInstance();
		InventoryNavigator inv = new InventoryNavigator(serviceInstance.getRootFolder());

		try {
			VirtualMachine vm = (VirtualMachine) inv.searchManagedEntity("VirtualMachine", vmName);
			if (vm == null) {
				System.out.println("AlarmManager: Cannot find the VM - "+ vmName);
				return;
			} else if (vm.getConfig().template) {
				System.out.println("AlarmManager: " + vmName
						+ " is a template. Cannot create alarm for a template");
				return;
			}
			AlarmManager alarmMgr = serviceInstance.getAlarmManager();

			Alarm[] alarms = alarmMgr.getAlarm(vm);

			Alarm vmAlarm = null;
			for (Alarm alarm : alarms) {
				if (alarm.getAlarmInfo().name.equals(alarmName + "-"
						+ vm.getName())) {
					vmAlarm = alarm;
				}
			}

			if (vmAlarm != null) {
				// System.out.println("AlarmManager: "+ alarmName +
				// " is already set for the VM");
				return; // need not set a new alarm.
			}

			AlarmSpec spec = new AlarmSpec();
			spec.setExpression(createAlarmExpression());
			spec.setName(alarmName + "-" + vm.getName());
			spec.setDescription("Monitor VM State - triggers when VM powers off");
			spec.setEnabled(true);

			AlarmSetting as = new AlarmSetting();
			as.setReportingFrequency(0); // as often as possible
			as.setToleranceRange(0);

			spec.setSetting(as);

			alarmMgr.createAlarm(vm, spec);
			System.out.println("AlarmManager: Alarm created successfully for " +vm.getName());

		} catch (InvalidProperty e) {
			System.out.println("AlarmManager: Invalid Property");
			// e.printStackTrace();

		} catch (RuntimeFault e) {
			System.out.println("AlarmManager: Run time fault");
			// e.printStackTrace();

		} catch (RemoteException e) {
			System.out.println("AlarmManager: Remote Connection error" + e);
			// e.printStackTrace();
		}
	}

	/**
	 * Checks if power status alarm is triggered for given VM
	 * 
	 * @param vmName
	 * @return true if alarm is triggered false if alarm is not triggered or not
	 *         set
	 */
	public static boolean checkAlarm(String vmName) {
		ServiceInstance serviceInstance = InfrastructureData.getInstance()
				.getServiceInstance();
		InventoryNavigator inv = new InventoryNavigator(
				serviceInstance.getRootFolder());

		try {
			VirtualMachine vm = (VirtualMachine) inv.searchManagedEntity(
					"VirtualMachine", vmName);
			if (vm == null) {
				System.out.println("AlarmManager: Cannot find the VM - "
						+ vmName);
				return false;
			} else if (vm.getConfig().template) {
				System.out.println("AlarmManager: " + vmName
						+ " is a template. Cannot check alarm for a template");
				return false;
			}

			AlarmManager alarmMgr = serviceInstance.getAlarmManager();
			Alarm[] alarms = alarmMgr.getAlarm(vm);

			Alarm vmAlarm = null;
			for (Alarm alarm : alarms) {
				if (alarm.getAlarmInfo().name.equals(alarmName)) {
					vmAlarm = alarm;
				}
			}

			if (vmAlarm == null) {
				System.out.println("AlarmManager: " + alarmName
						+ " is not set for the VM");
				return false;
			}

			AlarmState[] alarmStates = vm.getTriggeredAlarmState();

			if (alarmStates.length == 0) {
				System.out.println("AlarmManager: No alarm triggered");
				return false;
			} else {
				for (AlarmState alarm : alarmStates) {
					if (alarm.getAlarm().getVal()
							.equals(vmAlarm.getMOR().getVal())
							&& alarm.overallStatus.name().equals("red")) {
						return true;
					}
				}
			}

		} catch (InvalidProperty e) {
			System.out.println("AlarmManager: Invalid Property");
			// e.printStackTrace();
		} catch (RuntimeFault e) {
			System.out.println("AlarmManager: Run time fault");
			// e.printStackTrace();
		} catch (RemoteException e) {
			System.out.println("AlarmManager: Remote Connection error");
			// e.printStackTrace();
		}

		return false;
	}

	private static StateAlarmExpression createAlarmExpression() {
		StateAlarmExpression expression = new StateAlarmExpression();
		expression.setType("VirtualMachine");
		expression.setStatePath("runtime.powerState");
		expression.setOperator(StateAlarmOperator.isEqual);
		expression.setRed("poweredOff");
		return expression;
	}
}
