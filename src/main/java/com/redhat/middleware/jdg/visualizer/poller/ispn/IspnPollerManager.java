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

package com.redhat.middleware.jdg.visualizer.poller.ispn;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXServiceURL;
import javax.naming.InitialContext;

import com.redhat.middleware.jdg.visualizer.internal.VisualizerRemoteCacheManager;
import com.redhat.middleware.jdg.visualizer.poller.CacheEntriesPollerThread;
import com.redhat.middleware.jdg.visualizer.poller.RMIContextFactory;
import com.redhat.middleware.jdg.visualizer.poller.jmx.JmxCacheEntriesPoller;
import com.redhat.middleware.jdg.visualizer.poller.jmx.JmxCacheEntriesPollerManager;
import com.redhat.middleware.jdg.visualizer.rest.NodeInfo;

/**
 * Infinispan uses JVM's JMX remoting, binds to all available network interfaces (i.e.,
 * it doesn't allow a specific bind address).
 * Hence, each JVM that's running on the same machine would have to specify a different
 * JMX port.  In case the user is running multiple Infinispan servers in the same machine,
 * use setJmxPort(ip, port) to register a JMX port w/ the poller. 
 * 
 * {@link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6425769}
 * 
 * However, there may be further issues when running inside JBoss AS 7. See
 * {@link https://issues.jboss.org/browse/AS7-2138}
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public class IspnPollerManager extends JmxCacheEntriesPollerManager {
	private Map<String, Integer> jmxPorts = new HashMap<String, Integer>();

	public IspnPollerManager(VisualizerRemoteCacheManager cacheManager) {
		super(cacheManager);
	}

	@Override
	protected JMXServiceURL generateServiceURL(SocketAddress address)
			throws MalformedURLException {
		
		InetSocketAddress isa = (InetSocketAddress) address;
		String host = isa.getAddress().getHostAddress();
		String serviceUrl = "service:jmx:rmi:///jndi/rmi://" + host + ":" + getJmxPort(host, isa) + "/jmxrmi";
		System.out.println("IspnPollerManager serviceUrl: " + serviceUrl);
		return new JMXServiceURL(serviceUrl);
	}

	@Override
	protected JmxCacheEntriesPoller createPoller(JMXServiceURL url, Map<String, Object> env) {
		env.put(InitialContext.INITIAL_CONTEXT_FACTORY, RMIContextFactory.class.getName());
		return new IspnJmxCacheEntriesPoller(url, env, getCacheName());
	}
	
	public int getJmxPort(String ip, InetSocketAddress isa) {
		return jmxPorts.containsKey(ip) ? jmxPorts.get(ip) : (isa.getPort() + getJmxHotrodPortOffset());
	}
	
	public void setJmxPort(String ip, Integer port) {
		jmxPorts.put(ip, port);
	}
	
	public void unsetJmxPort(String ip) {
		jmxPorts.remove(ip);
	}

	@Override
	protected CacheEntriesPollerThread createPollerThread(SocketAddress address,
			NodeInfo nodeInfo) throws Exception {
		return new CacheEntriesPollerThread(createPoller(address), nodeInfo);
	}

}
