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
package com.redhat.middleware.jdg.visualizer.poller.jdg;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

import com.redhat.middleware.jdg.visualizer.poller.jmx.JmxPoller;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public class JdgJmxCacheNamesPoller extends JmxPoller<String[]> {
	private static final String ATTRIBUTE = "cacheName";

	public JdgJmxCacheNamesPoller(JMXServiceURL jmxUrl,
			Map<String, Object> jmxEnv) {
		super(jmxUrl, jmxEnv);
	}
	
	@Override
	protected String[] doPoll() throws Exception {
		Set<ObjectName> objectNames = getConnection().queryNames(new ObjectName("jboss.infinispan:type=Cache,name=*,manager=\"clustered\",component=Cache"), null);
		if (objectNames == null) return null;
		
		List<String> names = new LinkedList<String>();
		
		int i = 0;
		for (ObjectName objectName : objectNames) {
		   String name = (String) getConnection().getAttribute(objectName, ATTRIBUTE);
		   if (name == null)
		      continue;
		   
		   // ignore internal caches, such ___hotRodTopologyCache
		   if (name.startsWith("___"))
		      continue;
		   
			names.add(name);
		}
		return names.toArray(new String[]{});
	}

}
