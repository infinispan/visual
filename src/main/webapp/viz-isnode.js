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

/* Utility function */
function round2(x) {
    return Math.round(x * 100) / 100;
}



/*
 * What size dots to use in each range
 */
ISNode.prototype.rangeDotSize = new Array();
ISNode.prototype.rangeDotSize[0] = 3;
ISNode.prototype.rangeDotSize[1] = 7;
ISNode.prototype.rangeDotSize[2] = 12;

/*
 * What orbit for each range
 */
ISNode.prototype.rangeOrbit = new Array();
ISNode.prototype.rangeOrbit[0] = 27;
ISNode.prototype.rangeOrbit[1] = 40;
ISNode.prototype.rangeOrbit[2] = 60;


ISNode.prototype.coreRadius = 20;

/**
 * Build the HTML for a node
 */
ISNode.prototype.buildNodeHTML = function(nodeInfo) {
    return "\n"
        + '<div id="' + nodeInfo.id + '" class="node" '
        /*
        + 'onmouseover="$(\'#title-' + nodeInfo.id + '\').show();" '
        + 'onmouseout="$(\'#title-' + nodeInfo.id + '\').hide();"'
        */
        + '>'
        + '<canvas class="nodecanvas" width="150" height="150"/>'
        + '<div class="nodetitle" id="title-' + nodeInfo.id + '">' + nodeInfo.name + ' [<span class="count">0</span>]</div>'
        + '</div>';
}

/**
 * A node object, representing a node in Infinispan
 */
function ISNode(idIn,colorIn,hilightColorIn,phaseIn) {

    // Record the ID
    this.id = idIn;

    // Its location relative to it's 'ideal' position
    this.x = 0;
    this.y = 0;

    // Its velocity
    this.dx = 0;
    this.dy = 0;

    // Its phase - used to calculate its 'ideal' position
    this.phase = phaseIn;

    // The color
    this.color = colorIn;
    this.hilightColor = hilightColorIn;

    // The count
    this.count = 0;
    
    // The core
    this.core = new Dot(75 - this.coreRadius,75 - this.coreRadius,this.coreRadius,this.color,this.hilightColor,null);


    // The dots to draw
    this.dots = Array();

    // Now set it up to the dom elements
    this.attach();




}



// Attach the node to the elements
ISNode.prototype.attach = function() {
    // The jquery array representing this node
    this.jq = $('#' + this.id);
    // The dom object for this node
    this.dom = this.jq.get()[0];
    // The canvas object contained in this node
    this.canvas = $('#' + this.id + ' canvas').get()[0];
    this.core.attach(this.canvas.getContext('2d'));
    this.core.draw();
    for (var i = 0; i < this.dots.length; i++) {
        this.dots[i].attach(this.canvas.getContext('2d'));
        this.dots[i].draw();
    }
//    this.jq.click(function() {
//        deleteNode(this.id);
//    });
}


// Basic geometry used to position the node
ISNode.prototype.stageCenterWidth = 0;
ISNode.prototype.stageCenterHeight = 0;
ISNode.prototype.orbit = 0;

// Basics of the wobble and wiggle
ISNode.prototype.attractiveForce = 0;
ISNode.prototype.drag = 0;



/**
 * Initialize and show a node
 */
ISNode.prototype.init = function(phaseShift) {
    this.setPosition(phaseShift)
    this.jq.show();
}



/**
 * Kicks a node with a random motion
 */
ISNode.prototype.perturb = function(perturbation) {
    this.dx = (Math.random() * 2 * perturbation) - perturbation;
    this.dy = (Math.random() * 2 * perturbation) - perturbation;
}



ISNode.prototype.desiredPosition = function(phase) {
    var x = 0;
    var y = 0;
    var p = 0;
    p = phase;
    x = this.orbit * Math.sin(p) + this.stageCenterWidth;
    y = this.orbit * -Math.cos(p) + this.stageCenterHeight;
    // $('#title-' + this.id).html(this.name + ' : ' + (Math.round(phase * 100) / 100));
    var result = new Object();
    result.x = x;
    result.y = y;
    return result;
}

/**
 * set it's position given a particular phaseShift
 */
ISNode.prototype.setPosition = function(phaseShift) {
    var desired = this.desiredPosition(this.phase + phaseShift);
    this.dom.style.left = Math.round(desired.x + this.x) + "px";
    this.dom.style.top = Math.round(desired.y + this.y) + "px";
}


/**
 * Called start moveing a node to a new phase
 */
ISNode.prototype.setPhase = function(newPhase, runRadians) {
    // $('#title-' + this.id).html(round2(this.phase) + ' -> ' + round2(newPhase));
    var oldDesired = this.desiredPosition(this.phase + runRadians);
    var newDesired = this.desiredPosition(newPhase + runRadians);

    this.x += oldDesired.x - newDesired.x;
    this.y += oldDesired.y - newDesired.y;

    this.phase = newPhase;
}



/**
 * set it's position given a particular phaseShift
 */
ISNode.prototype.animate = function(phaseShift,timeFactor) {
    // What forces are operating on it
    var deltaVX = ((this.attractiveForce * this.x) + (this.drag * this.dx)) * timeFactor;
    var deltaVY = ((this.attractiveForce * this.y) + (this.drag * this.dy)) * timeFactor;
    this.dx += deltaVX;
    this.dy += deltaVY;
    // TODO - Clamp if deltaV gets to be too big?
    this.x += this.dx * timeFactor;
    this.y += this.dy * timeFactor;
    this.setPosition(phaseShift);
}



ISNode.prototype.debug = function() {
    this.jq.html(Math.round(desiredX + this.x) + "," + Math.round(desiredY + this.y) + '</br>'
        + Math.round(desiredX) + "," + Math.round(desiredY) + '</br>'
        + Math.round(this.x) + "," + Math.round(this.y) + '</br>'
        + (Math.round(this.phase * 100) / 100) + ' : ' + (Math.round(phaseShift * 100) / 100)
    );
}


ISNode.prototype.addDot = function(x,y,r) {
    var dot = new Dot(x,y,r,this.color,this.hilightColor,this.canvas.getContext('2d'));
    this.dots.push(dot);
    dot.draw();
}




ISNode.prototype.setCount = function(countIn) {
    if (countIn == this.count) {
        return;
    }
    this.count = countIn;
    var dotsInRange = new Array();

    // OK, 0 - 10, you get one little dot each
    // 11-100 - you get one big dot for every 10
    // 101 - 1000 - you one igger dot got every 100

    if (this.count == 0) {
        // do nothing
    } else if (this.count <= 10) {
        dotsInRange.push(this.count);
    } else if (this.count <= 100) {
        dotsInRange.push(10);
        dotsInRange.push(Math.floor(this.count / 10));
    } else if (this.count <= 1000) {
        dotsInRange.push(10);
        dotsInRange.push(10);
        dotsInRange.push(Math.floor(this.count / 100));
    } else {
        dotsInRange.push(10);
        dotsInRange.push(10);
        dotsInRange.push(Math.floor(this.count / 100));
    }

    this.erase();
    this.core.draw();
    this.dots = new Array();
    var range;
    for (range = 0; range < 3; range++) {
        if (range >= dotsInRange.length) {
            break;
        }
        var i;
        for (i = 0; i < dotsInRange[range]; i++) {
            var r = this.rangeDotSize[range];
            var phase = (Math.PI * 2 * ((i / 10) + (range / 20)));
            var x = 75 - r + (Math.sin(phase) * this.rangeOrbit[range]);
            var y = 75 - r + (Math.cos(phase) * this.rangeOrbit[range]);
            var dot = new Dot(x,y,r,this.color,this.hilightColor,this.canvas.getContext('2d'));
            this.dots.push(dot);
            dot.draw();
        }
    }
    
    var countSpan = this.jq.find("#title-" + this.id + " span.count");
    countSpan.html(countIn);
}


ISNode.prototype.erase = function() {
    this.canvas.getContext('2d').clearRect(0,0,150,150);
}
