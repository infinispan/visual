<!---
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
--->

jdg-visualizer
========================

What is it?
-----------

This is a graphical JBoss Data Grid visualizer based on the 2011 JBoss World Keynote presented by Burr Sutter (https://vimeo.com/25258416).

This visualizer works with out of the box JDG configuration with JMX and Management
User configured (see below for instructions).


System requirements
-------------------
 * JBoss Data Grid 6.0
 * JBoss EAP 6.0
 * Maven 2

Configure Maven
---------------
See here to make sure JBoss Repository is configured - http://www.jboss.org/jdf/quickstarts/jboss-as-quickstart/#mavenconfiguration

In addition, please make sure JDG 6 repository is configured based on JDG 6 Maven Repository installation instructions.  If not using JDG 6, please change the `pom.xml` so that the Infinispan dependency is based on community project.

NOTE: This code hasn't been tested w/ Infinispan community project.

Add Management User to JBoss Data Grid
---------------------------------------
 + `cd $JDG_HOME/bin`
 + `./add-user.sh`
 + Select `<a>` to add a Management User
 + Hit `<Enter>` for default realm (`ManagementRealm`)
 + Follow instruction to add a user with your username/password of your choice.  _ALL_ nodes should have the exact same login credentials in order for this visualizer to run correctly.

Running JBoss Data Grid
------------------------
For this visualizer to work in a clustered environment, each JDG node must use the standalone-ha.xml configuration (for clustering), and each JDG node must be running on a dedicated network interface (i.e., a specific IP).  This can be achieved mostly through the use of IP Aliasing (you may need to look up more documentation regarding IP Aliasing for your Operating System). Optionally, you can also run each JDG node in a virtual machine instance, each with a different IP address.

An example to create local IP Aliases in Mac OS X:

1. `sudo ifconfig lo0 alias 127.0.0.2 255.0.0.0`
2. `sudo ifconfig lo0 alias 127.0.0.3 255.0.0.0`
 
Each JDG instance must bind to a specific IP address, e.g.:

1. `./standalone.sh -c standalone-ha.xml -b 127.0.0.2 -bmanagement=127.0.0.2 -Djboss.node.name=jdg-2`
2. `./standalone.sh -c standalone-ha.xml -b 127.0.0.3 -bmanagement=127.0.0.3 -Djboss.node.name=jdg-3`
 
If running JDG inside a VM, please make sure JDG is bound to the non-local network interface. (JDG binds to localhost unless otherwise specified), e.g.:
1. `./standalone.sh -c standalone-ha.xml -b 192.168.100.101 -bmanagement=192.168.100.101 -Djboss.node.name=jdg-1`

Configure jdg-visualizer
------------------------

### Configure JMX Credentials
1. Open `src/main/java/com/redhat/middleware/jdg/visualizer/cdi/Resources.java`
2. Locate the following lines of code:

	manager.setJmxUsername("admin");  
	manager.setJmxPassword("qwerty");  
	manager.setJmxPort(9999);  
	
3. Replace these with your own values.  _NOTE: JDG JMX port is 9999 by default if you haven't changed it.  Replace "namedCache" with a cache that you are interested in monitoring.  JDG comes pre-configured with "namedCache", use it if you haven't changed it. _

### Configure hotrod-client.properties
1. Open `src/main/resources/hotrod-client.properties`
2. Set `infinispan.client.hotrod.server_list` to at least one of your JDG server IP address, e.g.:

	infinispan.client.hotrod.server_list=127.0.0.2

Start JBoss Enterprise Application Platform 6 or JBoss AS 7.1
--------------------------------------------------------------

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat


Build and Deploy
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.html/#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy

4. This will deploy `target/jdg-visualizer.war` to the running instance of the server.


Access the application 
---------------------
 
The application will be running at the following URL: <http://localhost:8080/jdg-visualizer/>.

_NOTE: Before accessing the application, please ensure a JDG server is up and running!_


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc

