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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.RemoteCacheManagerNotStartedException;

/**
 * This class attempts to "ping" JDG server in order to retrieve topology information,
 * which in turn triggers an update to HotRod Client's internal server list.  This
 * server list is used for visualization purposes.
 * 
 * Currently, there is no public method to invoke a "ping" operation via the RemoteCache
 * interface.  This code currently uses "stats" operation.
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 */
public class PingThread extends Thread {
	private Logger logger = Logger.getLogger(PingThread.class.getName());
	private static final long DEFAULT_REFRESH_RATE = 2000L;

	private volatile boolean running;
	
	private final RemoteCacheManager cacheManager;

	private long refreshRate = DEFAULT_REFRESH_RATE;

	public PingThread(RemoteCacheManager cacheManager) {
		this.cacheManager = cacheManager;
		setDaemon(true);
	}

	public void abort() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				cacheManager.getCache().stats();
			} catch (IllegalStateException e) {
				logger.log(Level.SEVERE, "illegal state exception, aborting", e);
				abort();
			} catch (RemoteCacheManagerNotStartedException e) {
				logger.log(Level.SEVERE, "not started exception, aborting", e);
				abort();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "error when retrieving stats", e);
			}
			try {
				Thread.sleep(refreshRate);
			} catch (InterruptedException e) {
			}
		}
	}

	public long getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(long refreshRate) {
		this.refreshRate = refreshRate;
	}
}
