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
/*
    Document   : viz.js
    Created on : Mar 29, 2011, 3:22:43 PM
    Author     : Andrew Sacamano<andrew.sacamano@amentra.com>
    Description:
    Javscript sources for the Infinispan visualizer.
*/


// Basic parameters of the animation


// How much kick to give when kicked.
var perturbation = 20;

// The basic characteristics of the animation
// changing these can break it very easily
// How much attraction to the cetner is there
var attractiveForce = 0.02;
// How fast things go - higher is faster
var timeDilation = 0.005;
// How long to wait between frames, just kind to the CPU
var snooze = 20;


// When the last frame was rendered - make the animation similar on faster or slower
// devices by not just assuming frame rendering takes the same time
var lastAnimate = 0;

// Is the animation running?
var running = true;


// How far away to bounce as a multiple of the node radius
var bounceDistance = 2.0;

// How far away for repel, as a multiple of the node radius
var repelDistance = 3.0;

function initViz() {


    // Find the nodes
    nodes = $('.node');
    nodeCount = nodes.length;
    if (nodeCount == 0) {
        alert('Nothing to animate.');
        return;
    }

    // Set up basic note parameters
    nodeWidth = nodes.first().width();
    nodeHeight = nodes.first().height();
    nodeHalfWidth = nodeWidth / 2;
    nodeHalfHeight = nodeHeight / 2;
    nodeRadius = Math.min(nodeHalfWidth,nodeHalfHeight);


    // Setup the stage and it's basic parameters
    stage = $('#stage');
    // We're workgin with top-left coordinates, so allow room for the rest of the nodes
    stageWidth = stage.width() - nodeWidth;
    stageHeight = stage.height() - nodeHeight;
    stageCenterWidth = stageWidth / 2;
    stageCenterHeight = stageHeight / 2;

    // Node position and velocity
    x = Array();
    y = Array();
    dx = Array();
    dy = Array();


    // Placing nodes
    initialRadius = Math.min(stageCenterWidth,stageCenterHeight);
    
    for (var i = 0; i < nodeCount; i++) {
        initNode(nodes.slice(i,i+1),i);
    }

    lastAnimate = (new Date()).getTime();
    setTimeout('animate()',snooze);
}

function initNode(node,i) {
    radians = i * 2 * Math.PI / nodeCount;
    x[i] = initialRadius * Math.sin(radians) + stageCenterWidth;
    y[i] = initialRadius * Math.cos(radians) + stageCenterHeight;

    kick = (Math.random() * 2 * perturbation) - perturbation;
    dx[i] = (Math.cos(radians) * kick);
    dy[i] = (Math.sin(radians) * kick);

    domNode = node.get();
    domNode[0].style.left = x[i];
    domNode[0].style.top = y[i];
    node.show();
}


function animate() {

    // Take care of the basics
    now = (new Date()).getTime();
    timeFactor = timeDilation * (now - lastAnimate);
    lastAnimate = now;

    // Some calculations that can happen outside the loops
    bounce = bounceDistance * nodeRadius;
    repel = repelDistance * nodeRadius;

    // Adjust everyone's speed
    for (var i = 0; i < nodeCount; i++) {
        bounced = false;
        for (var j = 0; j < nodeCount; j++) {
            if (i != j) {
                // Lazy math, taxi distance and repulsion and bounce
                distance = Math.abs(x[i] - x[j]) + Math.abs(y[i] - y[j]);
                if (distance < bounce) {
                    // Bounce using Vnew = Vcur - 2 * WallNormal * (WallNormal . Vcur);
                    wallnormalx = x[i] - x[j];
                    wallnormaly = y[i] - y[j];
                    wallnormalmag = Math.sqrt(wallnormalx * wallnormalx + wallnormaly * wallnormaly);
                    wallnormalx = wallnormalx / wallnormalmag;
                    wallnormaly = wallnormaly / wallnormalmag;

                    // Now do i
                    bouncescale = 2 * (wallnormalx * dx[i] + wallnormaly * dy[i]);
                    dx[i] = dx[i] - bouncescale * wallnormalx;
                    dy[i] = dy[i] - bouncescale * wallnormaly;
                    bounced = true;
                    break;
                } else if (distance < repel) {

                }
            }
        }
        if (! bounced) {
            // Come to the center
            dx[i] += (stageCenterWidth - x[i]) * attractiveForce * timeFactor;
            dy[i] += (stageCenterHeight - y[i]) * attractiveForce * timeFactor;
        }
    }
    nodes.each(function(i,node){
        x[i] = x[i] + dx[i] * timeFactor;
        y[i] = y[i] + dy[i] * timeFactor;
        node.style.left = x[i];
        node.style.top = y[i];
        node.innerHTML = '<p>' + i + '<br/>' + x[i] + '<br/>' + y[i];
    });
    setTimeout('animate()',snooze);
}

