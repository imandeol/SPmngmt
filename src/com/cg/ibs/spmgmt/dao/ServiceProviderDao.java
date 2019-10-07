package com.cg.ibs.spmgmt.dao;

import java.time.LocalDateTime;
import java.util.TreeMap;
import com.cg.ibs.spmgmt.bean.ServiceProvider;
import com.cg.ibs.spmgmt.exception.IBSException;
import com.cg.ibs.spmgmt.exception.RegisterException;

public interface ServiceProviderDao {

	boolean storeServiceProviderData(ServiceProvider serviceProvider) throws RegisterException;

	TreeMap<LocalDateTime, ServiceProvider> fetchPendingSp();

	ServiceProvider checkLogin(String username, String password) throws IBSException;

	void approveStatus(ServiceProvider serviceProvider) throws IBSException;

	boolean checkUserID(String userId) throws IBSException;

	TreeMap<LocalDateTime, ServiceProvider> fetchApprovedSp();

	boolean checkAdminLogin(String adminID, String adminPassword) throws IBSException;

	ServiceProvider getServiceProvider(String uid) throws IBSException;

	boolean emptyData();

}
