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

package org.infinispan.visualizer.poller;


import org.infinispan.visualizer.internal.VisualizerRemoteCacheManager;

/**
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 */
public abstract class RemoteCachePollerManager<T> extends PollerManager<T> {
   private VisualizerRemoteCacheManager cacheManager;

   public RemoteCachePollerManager(VisualizerRemoteCacheManager cacheManager) {
      this.cacheManager = cacheManager;
   }

   @Override
   public void updateClusterList() {
      updateClusterList(cacheManager.getRegistry().getServers());
   }

   public VisualizerRemoteCacheManager getCacheManager() {
      return cacheManager;
   }

   public void setCacheManager(VisualizerRemoteCacheManager cacheManager) {
      this.cacheManager = cacheManager;
   }

}
