package com.brownfield.vre.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * The Class VREApplicationConfig.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class VREApplicationConfig extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(VREBaseResource.class);
		return s;
	}
}