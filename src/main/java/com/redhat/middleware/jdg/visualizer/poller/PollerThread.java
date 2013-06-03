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
package com.redhat.middleware.jdg.visualizer.poller;

/**
 * This thread holds the run loop to call poller on a fixed interval set
 * by <code>refrehRate</code>.
 * 
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 *
 * @param <T>
 */
public abstract class PollerThread<T> extends Thread {
	private volatile boolean running;

	private final Poller<T> poller;
	private long refreshRate = PollerManager.DEFAULT_REFRESH_RATE;

	public PollerThread(Poller<T> poller) {
		super();
		setDaemon(true);
		
		this.poller = poller;
		
		poller.init();
	}

	public void abort() {
		running = false;
		poller.destroy();
	}

	public boolean isRunning() {
		return running;
	}
	
	abstract protected void doRun() throws Exception;

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				doRun();
			} catch (Exception e) {
				e.printStackTrace();
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

	public Poller<T> getPoller() {
		return poller;
	}

}