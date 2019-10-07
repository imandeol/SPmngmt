package com.cg.ibs.spmgmt.service;

import java.time.LocalDateTime;
import java.util.TreeMap;

import com.cg.ibs.spmgmt.bean.ServiceProvider;
import com.cg.ibs.spmgmt.exception.*;

public interface ServiceProviderService {

	ServiceProvider generateIdPassword(ServiceProvider serviceprovider) throws RegisterException, IBSException;

	boolean storeSPDetails(ServiceProvider sp) throws RegisterException;

	ServiceProvider validateLogin(String username, String password) throws IBSException;

	ServiceProvider getServiceProvider(String userid) throws IBSException;

	TreeMap<LocalDateTime, ServiceProvider> showPending();

	void approveSP(ServiceProvider sp, boolean decision) throws IBSException;

	boolean validateAdminLogin(String adminID, String adminPassword) throws IBSException;

	boolean emptyData();

}