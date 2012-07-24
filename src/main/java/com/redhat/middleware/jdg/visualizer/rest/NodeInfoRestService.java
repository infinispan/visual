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

package com.redhat.middleware.jdg.visualizer.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.redhat.middleware.jdg.visualizer.cdi.Resources;
import com.redhat.middleware.jdg.visualizer.internal.VisualizerRemoteCacheManager;
import com.redhat.middleware.jdg.visualizer.poller.PollerManager;
import com.redhat.middleware.jdg.visualizer.poller.jmx.JmxCacheEntriesPollerManager;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
@Path("/nodes")
@ApplicationScoped
public class NodeInfoRestService {
	@Inject
	private VisualizerRemoteCacheManager cacheManager;
	
	@Inject
	private Resources resources;
	
	private Map<String, PollerManager<NodeInfo>> pollerManagers = new HashMap<String, PollerManager<NodeInfo>>();
	
	@GET
	@Path("/{cacheName}")
	@Produces("application/json")
	public Collection<NodeInfo> getAllNodeInfo(@PathParam(value="cacheName") String cacheName) throws Exception {
		if (cacheName == null || "".equals(cacheName)) {
			cacheName = "default(dist_sync)";
		}
		if (!pollerManagers.containsKey(cacheName)) {
			JmxCacheEntriesPollerManager manager = resources.cacheEntriesPollerManager();
			manager.setCacheName(cacheName);
			manager.init();
			pollerManagers.put(cacheName, manager);
		}
		
		PollerManager<NodeInfo> manager = pollerManagers.get(cacheName);
		return manager.getAllInfos();
	}
	
	@PreDestroy
	protected void preDestroy() {
		for (PollerManager<NodeInfo> manager: pollerManagers.values()) {
			manager.destroy();
		}
	}
}
