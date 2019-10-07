package com.cg.ibs.spmgmt.service;

import java.util.TreeMap;
import java.math.BigInteger;
import java.time.LocalDateTime;
import com.cg.ibs.spmgmt.bean.ServiceProvider;
import com.cg.ibs.spmgmt.dao.ServiceProviderDao;
import com.cg.ibs.spmgmt.dao.ServiceProviderDaoImpl;
import com.cg.ibs.spmgmt.exception.IBSException;
import com.cg.ibs.spmgmt.exception.*;

public class ServiceProviderServiceImpl implements ServiceProviderService, IBSPortal {
	ServiceProviderDao serviceProviderDao = new ServiceProviderDaoImpl();
	ServiceProvider serviceProvider = new ServiceProvider();
	static BigInteger spi = new BigInteger("1000000");
	
	//Function to generate User ID and Password of Service Provider
	@Override
	public ServiceProvider generateIdPassword(ServiceProvider serviceprovider) throws RegisterException, IBSException {
		String temp = serviceprovider.getNameOfCompany();
		int length = temp.length();
		String userId;
		boolean b = false;
		int count = 5; // make it unique
		do {
			userId = temp.substring(0, count);
				b = serviceProviderDao.checkUserID(userId);
			count++;
		} while (b != true && count < length);

		String passwordSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"
				+ "!@#$%^&*_=+-/.?<>)";
		StringBuilder password = new StringBuilder(8);

		for (int i = 0; i < 8; i++) {
			int index = (int) (passwordSet.length() * Math.random());
			password.append(passwordSet.charAt(index));
		}

		String result = password.toString();
		serviceprovider.setUserId(userId);
		serviceprovider.setPassword(result);
		return serviceprovider;
	}
	
	//Function to store details of Service Provider
	@Override
	public boolean storeSPDetails(ServiceProvider sp) throws RegisterException {
		boolean result = false;
		boolean storeResult = serviceProviderDao.storeServiceProviderData(sp); // used to store the SP details after registration
		sp.setRequestDate(LocalDateTime.now());
		if (storeResult == true)
			result = true;
		return result;
	}
	
	//Function to validate Login of Service Provider
	@Override
	public ServiceProvider validateLogin(String username, String password) throws IBSException { // Make catch block for
																									// the exceptions
		ServiceProvider sp = new ServiceProvider();
		try {
			sp = serviceProviderDao.checkLogin(username, password);
		} catch (IBSException e) {
			System.out.println(e.getMessage());
		} // Exception catch block to be made in UI
		return sp;
	}
	
	//Function to get the details of Service Provider from DAO Layer where the details are stored
	@Override
	public ServiceProvider getServiceProvider(String userid) throws IBSException { 
		serviceProvider = serviceProviderDao.getServiceProvider(userid);
		return serviceProvider;
	}

	//Function to show all the Pending Request of Service Providers to the Bank Administrative
	@Override
	public TreeMap<LocalDateTime, ServiceProvider> showPending() {
		TreeMap<LocalDateTime, ServiceProvider> pendingMap = serviceProviderDao.fetchPendingSp();
		return pendingMap;
	}

	//Function to approve or disapprove the request of Service Provider by the Bank Administrative 
	@Override
	public void approveSP(ServiceProvider sp, boolean decision) throws IBSException {
			if (decision == true) {
			sp.setStatus("Approved");
			generateSpi(sp);
			sp.setSpi(spi);}
		else if (decision == false) {
			sp.setStatus("Rejected");
		}
		serviceProviderDao.approveStatus(sp);
	}

	//Function to get the details of all the Approved Service Providers to be used by Remittance Management
	@Override
	public TreeMap<LocalDateTime,ServiceProvider> getApprovedDetails() {
		TreeMap<LocalDateTime, ServiceProvider> approvedSP;
	 	approvedSP= serviceProviderDao.fetchApprovedSp();
	 	return approvedSP;
	}
	
	//Function to generate Unique SPI ID after the Service Provider gets approved
	private BigInteger generateSpi(ServiceProvider sp) { // Logic for counter to be made
		spi = spi.add(BigInteger.valueOf(1));
		return spi;
	}
	
	//Function to validate the login of bank Administrative
	@Override
	public boolean validateAdminLogin(String adminID, String adminPassword) throws IBSException {
		return serviceProviderDao.checkAdminLogin(adminID, adminPassword);
	}
	
	//Default empty data function
	@Override
	public boolean emptyData() {
		return serviceProviderDao.emptyData();
	}

}
