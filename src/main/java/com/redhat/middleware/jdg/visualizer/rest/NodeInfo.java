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

/**
 * This class holds info about a particular infinispan node
 * 
 * @author Andrew Sacamano<andrew.sacamano@amentra.com>
 * @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 */
public class NodeInfo {
	private String id = "";
    private String name = "Unknown";
    private int color = 0;
    private int count = 0;
    
    public NodeInfo(String id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.count = 0;
    }

    @Override
    public String toString() {
        return "(" + name + "|" + color + "|" + count + ")";
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 
}
