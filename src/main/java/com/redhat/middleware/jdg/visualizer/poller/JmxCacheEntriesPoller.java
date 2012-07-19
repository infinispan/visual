/*
* JBoss, Home of Professional Open Source
* Copyright 2011 Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package com.redhat.middleware.jdg.visualizer.poller;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public abstract class JmxCacheEntriesPoller implements CacheEntriesPoller {
	private static final String ATTRIBUTE = "numberOfEntries";
	
	private volatile boolean connected = false;
	
	private final JMXServiceURL jmxUrl;
	private final Map<String, Object> jmxEnv;
	private final String cacheName;
	private final String cacheType;
	
	private JMXConnector connector;
	
	private ObjectName objName;
	private MBeanServerConnection connection;

	public JmxCacheEntriesPoller(JMXServiceURL jmxUrl, Map<String, Object> jmxEnv, String cacheName, String cacheType) {
		this.jmxUrl = jmxUrl;
		this.jmxEnv = jmxEnv;
		this.cacheName = cacheName;
		this.cacheType = cacheType;
	}

	public ObjectName getObjName() {
		return objName;
	}

	public void setObjName(ObjectName objName) {
		this.objName = objName;
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}
	
	public void setConnection(MBeanServerConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public int numberOfEntries() {
		try {
			if (!connected) {
				connect();
			}
			return Integer.valueOf(connection.getAttribute(objName, ATTRIBUTE).toString());
		} 
		catch (Exception e) {
			disconnect();
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void init() {
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	abstract protected ObjectName generateObjectName() throws Exception;
	
	protected void connect() throws Exception {
		connector = JMXConnectorFactory.connect(jmxUrl, jmxEnv);
		connection = connector.getMBeanServerConnection();
		objName = generateObjectName();
		connected = true;
	}
	
	protected void disconnect() {
		if (connector != null) {
			try {
				connector.close();
			} catch (IOException e) {
			}
		}
		connected = false;
	}

	@Override
	public void destroy() {
		disconnect();
	}

	public String getCacheName() {
		return cacheName;
	}

	public String getCacheType() {
		return cacheType;
	}
	
}
