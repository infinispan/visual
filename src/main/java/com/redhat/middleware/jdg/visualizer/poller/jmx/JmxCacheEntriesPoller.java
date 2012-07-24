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

import java.util.Map;

import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public abstract class JmxCacheEntriesPoller extends JmxPoller<Integer> {
	private static final String ATTRIBUTE = "numberOfEntries";
	
	private final String cacheName;

	public JmxCacheEntriesPoller(JMXServiceURL jmxUrl, Map<String, Object> jmxEnv, String cacheName) {
		super(jmxUrl, jmxEnv);
		this.cacheName = cacheName;
	}
	
	abstract protected ObjectName generateObjectName() throws Exception;
	
	@Override
	public Integer doPoll() throws Exception {
		return Integer.valueOf(getConnection().getAttribute(generateObjectName(), ATTRIBUTE).toString());
	}
	
	public String getCacheName() {
		return cacheName;
	}
}
