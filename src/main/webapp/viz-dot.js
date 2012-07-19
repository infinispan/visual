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

