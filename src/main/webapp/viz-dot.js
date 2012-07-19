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
* 
* @author Andrew Sacamano<andrew.sacamano@amentra.com>
* @author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
*/
/* 
 * Javascript to handle drawing dots on a canvas
 */



/**
 * A dot object, a single dot on the a canvas
 */
function Dot(xIn,yIn,rIn,colorIn,backgroundIn,contextIn) {
  this.x = xIn;
  this.y = yIn;
  this.r = rIn;
  this.dx = 0;
  this.dy = 0;
  this.color = colorIn;
  this.background = backgroundIn;
  if (contextIn != null) {
      this.attach(contextIn);
  }
}

Dot.prototype.attach = function(contextIn) {
  this.context = contextIn;
  this.fill = this.context.createRadialGradient(this.r, this.r, this.r, this.r * .7, this.r * .7, this.r / 10);
  this.fill.addColorStop(0, this.color);
  this.fill.addColorStop(1, this.background);
}

/**
 * A function that draws a dot d on a canvas c
 */
Dot.prototype.draw = function() {
  this.context.save();
  this.context.fillStyle = this.fill;
  this.context.translate(this.x,this.y);
  this.context.beginPath();
  this.context.arc(this.r,this.r,this.r,0,Math.PI * 2,true);
  this.context.fill();
  this.context.restore();
}

