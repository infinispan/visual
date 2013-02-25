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
package com.redhat.middleware.jdg.visualizer.poller.jmx;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.redhat.middleware.jdg.visualizer.poller.Poller;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 * @param <T>
 */
public abstract class JmxPoller<T> implements Poller<T> {	
	private volatile boolean connected = false;
	
	private final JMXServiceURL jmxUrl;
	private final Map<String, Object> jmxEnv;
	
	private JMXConnector connector;
	
	private MBeanServerConnection connection;

	public JmxPoller(JMXServiceURL jmxUrl, Map<String, Object> jmxEnv) {
		this.jmxUrl = jmxUrl;
		this.jmxEnv = jmxEnv;
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}
	
	public void setConnection(MBeanServerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void init() {
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	abstract protected T doPoll() throws Exception;
	
	@Override
	public T poll() {
		try {
			if (!connected) {
				connect();
			}
			return doPoll();
		} 
		catch (Exception e) {
			disconnect();
			e.printStackTrace();
			return null;
		}
	}
	
	protected void connect() throws Exception {
		System.out.println("Connecting to jmxUrl:'" + jmxUrl + "', jmxEnv:'" + jmxEnv + "'");
		connector = JMXConnectorFactory.connect(jmxUrl, jmxEnv);
		connection = connector.getMBeanServerConnection();
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
}
