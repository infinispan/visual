<%--
JBoss, Home of Professional Open Source
Copyright 2011 Red Hat Inc. and/or its affiliates and other
contributors as indicated by the @author tags. All rights reserved.
See the copyright.txt in the distribution for a full listing of
individual contributors.

This is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation; either version 2.1 of
the License, or (at your option) any later version.

This software is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this software; if not, write to the Free
Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
02110-1301 USA, or see the FSF site: http://www.fsf.org.

@author <a href="mailto:rtsang@redhat.com">Ray Tsang</a>
 --%>

<html>
<head>
<title>JDG Visualizer</title>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
<link rel="stylesheet" type="text/css" href="viz.css" media="screen" />
<script src="jquery-1.5.1.min.js" type="text/Javascript"></script>
<script src="viz-dot.js" type="text/Javascript"></script>
<script src="viz-isnode.js" type="text/Javascript"></script>
<script src="viz.js" type="text/Javascript"></script>
<script type="text/Javascript">
	var addrs = Array();

	var infoUpdateTimeout = 500;

	function startMainLoop() {
		$("#clear").click(function() {
			var cacheName = jQuery("#cacheName").val();
			if (cacheName) {
				jQuery.getJSON('rest/nodes/' + cacheName, {clear: true}, function(data) {
					
				});
			}
		});
		$("#refresh").click(function() {
			var cacheName = jQuery("#cacheName").val();
			if (cacheName) {
				jQuery.getJSON('rest/nodes/'+ cacheName, {refresh: true}, function(data) {
					
				});
			}
		});		
		updateNames();
	}

	function updateNames() {
		var $select = jQuery("#cacheName");
		jQuery.getJSON('rest/names', null, function(data) {
			if (data.length == 0) {
				setTimeout('updateNames()', infoUpdateTimeout);
				return;
			}
			for (var i in data) {
				var name = data[i];
				var $option = jQuery("<option>");
				$option.attr("name", name);
				$option.val(name);
				$option.text(name);
				$select.append($option);
			}

			setTimeout('updateAddrs()', 50);
		});
	}

	function updateAddrs() {		
		var newNodes = Array();
		var deadNodes = Array();

		deadNodes = deadNodes.concat(addrs);
		var cacheName = jQuery("#cacheName").val();
		if (!cacheName) { cacheName = "default(dist_sync)"; }
		jQuery.getJSON('rest/nodes/' + cacheName, null, function(data) {
			if (data) {
				for ( var i in data) {
					nodeInfo = data[i];
					var nodeid = nodeInfo.id;
					var idx = $.inArray(nodeid, deadNodes);
					if (idx != -1) {
						deadNodes.splice(idx, 1);
						setNodeCount(nodeid, nodeInfo.count);
					} else {
						newNodes.push(nodeInfo);
					}
				}
				for ( var j in newNodes) {
					addrs.push(newNodes[j].id);
					addNode(newNodes[j])
				}
				for (j in deadNodes) {
					i = $.inArray(deadNodes[j], addrs);
					if (i != -1) {
						addrs.splice(i, 1);
					}
					deleteNode(deadNodes[j]);
				}

			}
			setTimeout('updateAddrs()', infoUpdateTimeout);
		});
	}
</script>
</head>
<body>
	<div id="controls">
		<select id="cacheName" name="cacheName"></select>
		<button id="clear">Clear Cache</button>
		<button id="refresh">Refresh Cache</button>
	</div>
	<div id="stage"></div>

	<script type="text/javascript">
		$(function() {
			initViz();
			startMainLoop();
		});
	</script>

</body>
</html>
