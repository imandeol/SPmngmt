package com.cg.ibs.spmgmt.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.cg.ibs.spmgmt.bean.BankAdmin;
import com.cg.ibs.spmgmt.bean.ServiceProvider;
import com.cg.ibs.spmgmt.exception.IBSException;
import com.cg.ibs.spmgmt.exception.IBSExceptionInterface;
import com.cg.ibs.spmgmt.exception.RegisterException;

public class ServiceProviderDaoImpl implements ServiceProviderDao {
	
	//Bank Administrative Database
	BankAdmin admin1 = new BankAdmin("company@ID", "65F43S6");
	BankAdmin admin2 = new BankAdmin("company@ID2", "65F43S8");

	// DataBase Initialization
	private static Map<String, ServiceProvider> spMap = new HashMap<>();
	private static Map<String, BankAdmin> bankMap = new HashMap<>();
	{
		bankMap.put(admin1.getAdminID(), admin1);
		bankMap.put(admin2.getAdminID(), admin2);
	}

	// Storing Service Provider Details in HashMap
	@Override
	public boolean storeServiceProviderData(ServiceProvider serviceProvider) throws RegisterException {
		if (serviceProvider != null && !(spMap.containsKey(serviceProvider.getUserId()))) {
			spMap.put(serviceProvider.getUserId(), serviceProvider);
			return true;
		} else {
			throw new RegisterException(IBSExceptionInterface.alreadyExistMessage);//
		}
	}
	
	//All the pending Service Providers being stored in TreeMap sorted on the basis of Date and Time of registration
	@Override
	public TreeMap<LocalDateTime, ServiceProvider> fetchPendingSp() {
		TreeMap<LocalDateTime, ServiceProvider> tempPendingList = new TreeMap<>();
		for (ServiceProvider serviceProvider : spMap.values()) {
			if (serviceProvider.getStatus().equalsIgnoreCase("pending")) {
				tempPendingList.put(serviceProvider.getRequestDate(), serviceProvider);
			}
		}
		return tempPendingList;
	}

	//Function to check the Login credentials of User
	@Override
	public ServiceProvider checkLogin(String userId, String password) throws IBSException {
		for (ServiceProvider serviceProvider : spMap.values()) {
			if (serviceProvider.getUserId().equals(userId)) {
				if (serviceProvider.getPassword().equals(password)) {
					return serviceProvider;
				}
				throw new IBSException(IBSExceptionInterface.incorrectPasswordMessage);//
			}
		}
		throw new IBSException(IBSExceptionInterface.incorrectUserIdMessage);//
	}
	
	//Changing the status of User based on the Bank Administrative's decision
	@Override
	public void approveStatus(ServiceProvider serviceProvider) throws IBSException {
		spMap.replace(serviceProvider.getUserId(), serviceProvider);
	}
	
	//Function to see if the userID is present in Map or nor
	@Override
	public boolean checkUserID(String userId) throws IBSException {
		boolean result;
		if (spMap.containsKey(userId)) {
			result = false;
		} else
			result = true;
		return result;
	}
	
	//All the Approved Service Provider Details for Remittance Management
	@Override
	public TreeMap<LocalDateTime, ServiceProvider> fetchApprovedSp() {
		TreeMap<LocalDateTime, ServiceProvider> tempApprovedList = new TreeMap<>();
		for (ServiceProvider serviceProvider : spMap.values()) {
			if (serviceProvider.getStatus().equalsIgnoreCase("Approved")) {
				tempApprovedList.put(serviceProvider.getRequestDate(), serviceProvider);
			}
		}
		return tempApprovedList;
	}
	
	//Administrative Login credentials check
	@Override
	public boolean checkAdminLogin(String adminID, String adminPassword) throws IBSException {
		boolean result = false;
		for (BankAdmin bankAdmin : bankMap.values()) {
			if (bankAdmin.getAdminID().equals(adminID) && bankAdmin.getAdminPassword().equals(adminPassword)) {
				result = true;
			}
		}
		return result;
	}
	
	//Function to get details of Service Provider stored in HashMap
	@Override
	public ServiceProvider getServiceProvider(String uid) throws IBSException {
		if (spMap.containsKey(uid)) {
			return spMap.get(uid);
		}
		return null;
	}

	//Default empty Data Function
	@Override
	public boolean emptyData() {
		return spMap.isEmpty();
	}
}
