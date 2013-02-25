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

import java.lang.reflect.Field;
import java.util.Properties;

import javax.enterprise.inject.Alternative;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

import com.redhat.middleware.jdg.visualizer.poller.jdg.JdgJmxCacheNamesPollerManager;

/**
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 * 
 */
@Alternative
public class VisualizerRemoteCacheManager extends RemoteCacheManager {
	public static final String TRANSPORT_FACTORY = "com.redhat.middleware.jdg.visualizer.internal.VisualizerTcpTransportFactory";
	
	
	private ServersRegistry registry;
	private PingThread pingThread;

	public VisualizerRemoteCacheManager() {
		super(getCacheProperties());
	}
	
	public ServersRegistry getRegistry() {
		return registry;
	}

	@Override
	public void start() {
		super.start();
		
		this.registry = new ServersRegistry();
		
		VisualizerTcpTransportFactory factory = getTransportFactoryViaReflection();
		if (factory != null) {
			factory.setRegistry(registry);
			factory.updateServerRegistry();
		}

		pingThread = new PingThread(this);
		pingThread.start();
	}

	protected VisualizerTcpTransportFactory getTransportFactoryViaReflection() {
		try {
			Field transportFactoryField = RemoteCacheManager.class.getDeclaredField("transportFactory");
			transportFactoryField.setAccessible(true);
			return (VisualizerTcpTransportFactory) transportFactoryField.get(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void stop() {
		if (pingThread != null) {
			pingThread.abort();
			pingThread.interrupt();
		}
		super.stop();
	}

	
	public static Properties getCacheProperties() {
		Properties props = new Properties();
		props.setProperty("infinispan.client.hotrod.server_list", System.getProperty("jdg.visualizer.serverList"));
		props.setProperty("infinispan.client.hotrod.transport_factory", TRANSPORT_FACTORY);
		return props;
	}
}
