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

import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import com.redhat.middleware.jdg.visualizer.internal.VisualizerRemoteCacheManager;
import com.redhat.middleware.jdg.visualizer.poller.RemoteCachePollerManager;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public abstract class JmxPollerManager<I, T, P extends JmxPoller<T>> extends RemoteCachePollerManager<I> {
	private int jmxHotrodPortOffset;
	private String jmxUsername;
	private String jmxPassword;
	
	public JmxPollerManager(VisualizerRemoteCacheManager cacheManager) {
		super(cacheManager);
	}
	
	abstract protected JMXServiceURL generateServiceURL(SocketAddress address) throws MalformedURLException;
	abstract protected P createPoller(JMXServiceURL url, Map<String, Object> env);
	
	protected P createPoller(SocketAddress address)
			throws Exception {
		
		JMXServiceURL serviceURL = generateServiceURL(address);

		Map<String, Object> env = new HashMap<String, Object>();
		if (jmxUsername != null && jmxPassword != null) {
			env.put(JMXConnector.CREDENTIALS, new String[] { jmxUsername, jmxPassword });
		}

		return createPoller(serviceURL, env);
	}

	public int getJmxHotrodPortOffset() {
		return jmxHotrodPortOffset;
	}

	public void setJmxHotrodPortOffset(int jmxPort) {
		this.jmxHotrodPortOffset = jmxPort;
	}
	
	public String getJmxUsername() {
		return jmxUsername;
	}

	public void setJmxUsername(String jmxUsername) {
		this.jmxUsername = jmxUsername;
	}

	public String getJmxPassword() {
		return jmxPassword;
	}

	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}

}
