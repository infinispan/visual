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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.redhat.middleware.jdg.visualizer.poller.PollerManager;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
@Path("/names")
@ApplicationScoped
public class CacheNameInfoRestService {
	@Inject
	private PollerManager<CacheNameInfo> manager;
	
	@GET
	@Produces("application/json")
	public Set<String> getAllNodeInfo() throws Exception {
		Set<String> names = new HashSet<String>();
		Collection<CacheNameInfo> infos = manager.getAllInfos();
		for (CacheNameInfo info : infos) {
			if (info.getNames() != null) {
				names.addAll(Arrays.asList(info.getNames()));
			}
		}
		
		return names;
	}
}
