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

package com.redhat.middleware.jdg.visualizer.internal;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * This class maintains a list of servers available in the JDG cluster.  It acts almost like
 * a listener, where a RemoteCacheManager would need to call updateServers(...) with the
 * latest list of the server.
 * 
 * RemoteCacheManager does not do this out of the box, hence a custom implementation is used,
 * see VisualizerRemoteCacheManager. 
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public class ServersRegistry {
	private Collection<SocketAddress> servers;
		
	public void updateServers(Collection<SocketAddress> updatedServers) {
		servers = new HashSet<SocketAddress>(updatedServers);
	}

	public Collection<SocketAddress> getServers() {
		if (servers == null) return Collections.emptySet();
		return servers;
	}

	public void setServers(Collection<SocketAddress> servers) {
		this.servers = servers;
	}
}
