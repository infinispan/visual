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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.redhat.middleware.jdg.visualizer.rest.NodeInfo;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public abstract class PollerManager {
	private String cacheName;
	private String cacheType;
	
	public PollerManager() {
	}

	/**
	 * Map with the data of the address and the key counts
	 */
	private Map<String, NodeInfo> nodeInfos = new ConcurrentHashMap<String, NodeInfo>();

	/**
	 * Pollers
	 */
	private Map<SocketAddress, PollerThread> pollers = new HashMap<SocketAddress, PollerThread>();

	/**
	 * Keeps track of which color new nodes should be
	 */
	private volatile int colorIndex = 0;
	
	abstract protected CacheEntriesPoller createPoller(SocketAddress address) throws Exception;
	abstract protected void updateClusterList();

	public void init() {
		updateClusterList();
	}

	public void destroy() {
		for (PollerThread p : pollers.values()) {
			p.abort();
			p.interrupt();
		}
	}

	private String generateNodeId(SocketAddress address) {
		InetSocketAddress isa = (InetSocketAddress) address;
		String id = isa.getAddress().getCanonicalHostName() + "-"
				+ isa.getPort();
		id = id.replaceAll("[^\\d]", "-");

		return id;
	}

	protected void updateClusterList(Collection<SocketAddress> addrs) {
		Set<SocketAddress> pollersToStop = new HashSet<SocketAddress>();
		pollersToStop.addAll(pollers.keySet());

		for (SocketAddress addr : addrs) {
			pollersToStop.remove(addr);
			String id = generateNodeId(addr);
			if (!nodeInfos.containsKey(id)) {
				NodeInfo nodeInfo = new NodeInfo(id, addr.toString(),
						colorIndex++);

				PollerThread newThread;
				try {
					newThread = new PollerThread(createPoller(addr), nodeInfo);
					newThread.start();
					nodeInfos.put(id, nodeInfo);
					pollers.put(addr, newThread);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for (SocketAddress addr : pollersToStop) {
			String id = generateNodeId(addr);
			nodeInfos.remove(id);
			PollerThread p = pollers.remove(addr);
			if (p != null) {
				p.abort();
			}
		}
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public Collection<NodeInfo> nodeInfoAsCollection() {
		updateClusterList();
		return nodeInfos.values();
	}
	public String getCacheType() {
		return cacheType;
	}
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}
}
