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

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public class VisualizerTcpTransportFactory extends
		org.infinispan.client.hotrod.impl.transport.tcp.TcpTransportFactory {
	
	private ServersRegistry registry;

	public ServersRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(ServersRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void updateServers(Collection<SocketAddress> newServers, byte[] cacheName) {
        super.updateServers(newServers,cacheName);
        updateServerRegistry();
	}
	
	protected void updateServerRegistry() {
		registry.updateServers(getServers());
	}
}
