package com.cg.ibs.spmgmt.service;

import java.time.LocalDateTime;
import java.util.TreeMap;

import com.cg.ibs.spmgmt.bean.ServiceProvider;

public interface IBSPortal {
	public TreeMap<LocalDateTime,ServiceProvider> getApprovedDetails();

}