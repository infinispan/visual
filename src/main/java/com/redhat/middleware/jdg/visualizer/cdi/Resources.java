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

package com.redhat.middleware.jdg.visualizer.cdi;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.redhat.middleware.jdg.visualizer.internal.VisualizerRemoteCacheManager;
import com.redhat.middleware.jdg.visualizer.poller.IspnPollerManager;
import com.redhat.middleware.jdg.visualizer.poller.JdgJmxPollerManager;
import com.redhat.middleware.jdg.visualizer.poller.JmxPollerManager;
import com.redhat.middleware.jdg.visualizer.poller.PollerManager;


/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 * 
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class Resources {
   @Produces
   public Logger produceLog(InjectionPoint injectionPoint) {
      return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
   }
   
   @Produces
   @Visualizer
   @ApplicationScoped
   public VisualizerRemoteCacheManager defaultRemoteCacheManager() {
	   VisualizerRemoteCacheManager cm = new VisualizerRemoteCacheManager();
	   cm.start();
	   
	   return cm;
   }
   
   @Produces
   @Visualizer
   @ApplicationScoped
   public PollerManager pollerManager() {
	   JmxPollerManager manager = new JdgJmxPollerManager(defaultRemoteCacheManager());
	   //JmxPollerManager manager = new IspnPollerManager(defaultRemoteCacheManager());
	   
	   manager.setJmxUsername("admin");
	   manager.setJmxPassword("qwerty");
	   manager.setJmxPort(9999);
	   manager.setCacheName("namedCache");
	   manager.setCacheType("dist_sync");
	   
	   manager.init();
	   
	   return manager;
   }
   
   public void destroyPollerManager(@Disposes @Visualizer PollerManager pollerManager) {
	   pollerManager.destroy();
   }
}
