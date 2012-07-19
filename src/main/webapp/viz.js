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
ISNode.prototype.attractiveForce = -0.8;

// How much drag to model
ISNode.prototype.drag = -0.6;



// How long to wait between frames, just being kind to the CPU
var snooze = 20;

// How many rotations per minuts
var rpm = 1;

// How long is a cycle of 30 rotations
var cycle = 60000 / rpm;

// How long since the start
var startTime;

// When the last frame was rendered - make the animation similar on faster or slower
// devices by not just assuming frame rendering takes the same time
var lastAnimate = 0;

// Is the animation running?
var running = true;


// Actions to be performed during the next animation cycle
var actions = Array();



// This nodes being displayed
var nodes = Array();


var colors = Array();
colors[0] = '#E82715';
colors[1] = '#F38C29';
colors[2] = '#FDFF25';
colors[3] = '#13B045';
colors[4] = '#0C4FB0';
colors[5] = '#8B0085';
colors[6] = '#EE5D24';
colors[7] = '#FBC22A';
colors[8] = '#74CA42';
colors[9] = '#26B7B2';
colors[10] = '#3D0D8D';
colors[11] = '#B11B6D';

var hilightColors = Array();
hilightColors[0] = '#facbcb';
hilightColors[1] = '#fef3e8';
hilightColors[2] = '#ffffe8';
hilightColors[3] = '#e6f7eb';
hilightColors[4] = '#e5ecf7';
hilightColors[5] = '#f3e4f2';
hilightColors[6] = '#fdeee8';
hilightColors[7] = '#fff9e8';
hilightColors[8] = '#fdfdfd';
hilightColors[9] = '#e8f7f7';
hilightColors[10] = '#eae5f3';
hilightColors[11] = '#f7e7f0';




function initViz() {

    // Set up basic note parameters
    // Many come from the css
    nodeWidth = 100;
    nodeHeight = 100;
    nodeHalfWidth = nodeWidth / 2;
    nodeHalfHeight = nodeHeight / 2;
    nodeRadius = Math.min(nodeHalfWidth,nodeHalfHeight);


    // Setup the stage and it's basic parameters
    stage = $('#stage');
    // We're workgin with top-left coordinates, so allow room for the rest of the nodes
    stageWidth = stage.width() - nodeWidth;
    stageHeight = stage.height() - nodeHeight;
    ISNode.prototype.stageCenterWidth = stageWidth / 2;
    ISNode.prototype.stageCenterHeight = stageHeight / 2;


    // Place the first node
    ISNode.prototype.orbit = Math.min(ISNode.prototype.stageCenterWidth,ISNode.prototype.stageCenterHeight) * 0.75;

    // OK GO
    lastAnimate = (new Date()).getTime();
    startTime = lastAnimate;
    setTimeout('animate()',snooze);
}





function animate() {

    // Take care of the basics
    now = (new Date()).getTime();
    timeFactor = (now - lastAnimate) / 1000;
    if (timeFactor > 0.5) {
        timeFactor = 0.5;
    }
    runtime = (now - startTime);
    lastAnimate = now;

    // let it run forever without overflow problems
    if (rpm != 0 && runtime > cycle) {
        // advance start time an integer number of revolutions
        // so we don't mess up the animation
        startTime += cycle;
    }


    if (!running) {
        setTimeout('animate()',1000);
    }

    // Figure out where we are on the cycle
    runRadians = Math.PI * 2 * rpm * runtime / 60000;
    rounded = runRadians - (2 * Math.PI * Math.floor(rpm * runtime / 60000));

    // OK, now handle actions before doing the animation
    while (actions.length > 0) {
        // $('#status').html('Actions : ' + actions.length + " : " + action[0]);
        action = actions.pop();
        if (action[0] == 'add') {
            animateNewNode(action[1], runRadians);
        }
        if (action[0] == 'delete') {
            animateDeleteNode(action[1], runRadians);
        }
        if (action[0] == 'setCount') {
            animateSetCount(action[1], action[2]);
        }
    }


    // Are there any nodes to animate?
    if (nodes.length != 0) {
        // How many radians have we run with runtime miliseconds
        // and the specified RPM
        // $('#status').html('Animating ' + nodes.length + ' at ' + runtime + ' : ' + runRadians + ' ' + rounded);

        // Adjust all nodes
        for (var i = 0; i< nodes.length; i++) {
            nodes[i].animate(runRadians,timeFactor);
        }
    }
    setTimeout('animate()',snooze);
}


function animateNewNode(nodeInfo,runRadians) {
    var id = nodeInfo.id;
    stage.html(stage.html() + ISNode.prototype.buildNodeHTML(nodeInfo));
    var phaseConstant = Math.PI * 2 / (nodes.length + 1);
    var color = colors[nodeInfo.color % colors.length];
    var hilightColor = hilightColors[nodeInfo.color % colors.length];
    node = new ISNode(id,color,hilightColor,phaseConstant * nodes.length);
    nodes.push(node);
    node.perturb(perturbation);
    node.setPosition(runRadians);
    node.dom.style.border = '1px soild #' + id;
    node.jq.show();
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].attach();
        nodes[i].setPhase(phaseConstant * i, runRadians);
        nodes[i].setCount(nodeInfo.count);
    }
}

function animateDeleteNode(id, runRadians) {
    var newnodes = Array();
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].id == id) {
            nodes[i].jq.hide();
        } else {
            newnodes[newnodes.length] = nodes[i];
        }
    }
    nodes = newnodes;
    var phaseConstant = Math.PI * 2 / (nodes.length);
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].attach();
        nodes[i].setPhase(phaseConstant * i, runRadians)
    }
}

function animateSetCount(id, count) {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].id == id) {
            nodes[i].setCount(count)
        }
    }
}

/**
 * Called by UI events
 */
function addNode(nodeInfo) {
    var cmd = new Array();
    cmd[0] = 'add';
    cmd[1] = nodeInfo;
    actions.push(cmd);
}

/**
 * Called by UI events
 */
function deleteNode(id) {
    var cmd = new Array();
    cmd[0] = 'delete';
    cmd[1] = id;
    actions.push(cmd);
}

/**
 * Called by UI events
 */
function setNodeCount(id,count) {
    var cmd = new Array();
    cmd[0] = 'setCount';
    cmd[1] = id;
    cmd[2] = count;
    actions.push(cmd);
}
