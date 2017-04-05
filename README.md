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

INFINISPAN-VISUALIZER
=====================

What is it?
-----------

This is a graphical Infinispan visualizer based on the 2011 JBoss World Keynote presented 
by Burr Sutter (https://vimeo.com/25258416).

This visualizer works with out of the box with Infinispan JMX and Management User configured (see below for instructions).

# 1 Configuring the Demo
This demo application uses a single instance of Wildfly server, and a cluster of Infinispan servers. The following steps document how to configure the servers whether you plan to build the demo from source or deploy a precompiled demo app.

### 1.1 System requirements
 * Infinispan 8
 * Wildfly 10
 * Maven 3

### 1.2 Add Management User to JBoss Data Grid

Use the example shown below to add an user

    cd $ISPN_HOME/bin
     ./add-user.sh -u admin -p p455w0rd

Follow instructions to add a user with your username/password of your choice.  _ALL_ nodes should have the exact same login credentials in order for this visualizer to run correctly.

### 1.5 Create Instance Profiles
Each JBoss Data Grid instance should have its own instance directory. Make four copies of the `$ISPN_HOME/standalone` directory. When starting up Infinispan the `-Djboss.server.base.dir=<instance-directory-name>` will be used to select the instance's execution directory.

	cp -r $ISPN_HOME/standalone $ISPN_HOME/standalone1
	cp -r $ISPN_HOME/standalone $ISPN_HOME/standalone2
	cp -r $ISPN_HOME/standalone $ISPN_HOME/standalone3 

### 1.6 Configuring the Network Interface
For this visualizer to work in a clustered environment, each Infinispan node must use the standalone-ha.xml configuration (for clustering), 
and each Infinispan node must be running on a dedicated network interface. This can be achieved through IP aliasing or port offsets.
This can be achieved through: 

a. Running each data grid instance on the same IP address, but with port offsets.

b. The use of IP Aliasing. You may need to look up more documentation regarding IP Aliasing for your Operating System. 

c. Running each Infinispan node in a virtual machine instance, each with a different IP address.

#### 1.6a Using Port Offsets
Further configuration is not necessary. Port offsets may be specified on the command line startup, e.g.:

	./standalone.sh -c clustered.xml -b 127.0.0.1 -bmanagement=127.0.0.1 -Djboss.node.name=ispn-1 -Djboss.socket.binding.port-offset=1 ...
	./standalone.sh -c clustered.xml -b 127.0.0.1 -bmanagement=127.0.0.1 -Djboss.node.name=ispn-2 -Djboss.socket.binding.port-offset=2 ...

#### 1.6b Using IP Aliases
An example to create local IP Aliases in Mac OS X:

	sudo ifconfig lo0 alias 127.0.0.2 255.0.0.0
	sudo ifconfig lo0 alias 127.0.0.3 255.0.0.0
 
Each Infinispan instance must bind to a specific IP address, e.g.:

	./clustered.sh -b 127.0.0.2 -bmanagement=127.0.0.2 -Djboss.node.name=ispn-2 ...
	./clustered.sh -b 127.0.0.3 -bmanagement=127.0.0.3 -Djboss.node.name=ispn-3 ...
 
#### 1.6c Using Virtual Machines 

If running Infinispan inside a VM, please make it is bound to the non-local network interface. (Infinispan binds to localhost unless otherwise specified), e.g.:
	
	./standalone.sh -c clustered.xml -b 192.168.100.101 -bmanagement=192.168.100.101 -Djboss.node.name=ispn-1 ...

#### 1.7 Deploy the Visualizer

+ Copy the Visualizer to the Wildfly deployments directory: 
	
	```
	cp infinispan-visualizer.war $WILDFLY_HOME/standalone/deployments```
	
## 2 Running the Demo

### 2.1 Starting the Data Grid Instances

Open a command line and navigate to the root of the JBoss Data Grid server directory **$ISPN_HOME** for each instance you want to start.

Start each instance, ensuring the address and ports will not conflict with each other or the JBoss EAP instance.

	bin/standalone.sh -c clustered.xml -b localhost -bmanagement=localhost -Djboss.server.base.dir=standalone1 -Djboss.socket.binding.port-offset=1 -Djboss.node.name=ispn1

	bin/standalone.sh -c clustered.xml -b localhost -bmanagement=localhost -Djboss.server.base.dir=standalone2 -Djboss.socket.binding.port-offset=2 -Djboss.node.name=ispn2

	bin/standalone.sh -c clustered.xml -b localhost -bmanagement=localhost -Djboss.server.base.dir=standalone3 -Djboss.socket.binding.port-offset=3 -Djboss.node.name=ispn3

+ use `bin/standalone.sh` for Linux/Unix and `bin\standalone.bat` for Windows
+ **-c <filename>** specifies the configuration file to use; `standalone-ha.xml` must be used to form a cluster. 
+ **-b** and **-bmanagement** specify the IP Address to bind the data grid to. If running the the demo on an isolated host then specify `localhost`.  If running the demo with other hosts on a network the specify the IP Address of the network interface you would like to bind to. Both the NIC configuration and the network must support multicast for the data grid to be dynamically formed.
+ **-Djboss.node.name** must be unique for each Infinispan instance
+ **-Djboss.server.base.dir** should point to a profile dirctory tree under the **$ISPN_HOME** directory. This should not be shared between instances.
+ **-Djboss.socket.binding.port-offset** specifies the offset from the default port bindings. `11222+offset` for Hotrod. `9990+offset` for JMX

### 2.2 Start the Application Server

Open a command line and navigate to the root of the JBoss EAP server directory **$WILDFLY_HOME**.

Start the application server and specify the visualizer's demo parameters.

	bin/standalone.sh -b 192.168.1.101 -bmanagement=192.168.1.101 -Dinfinispan.visualizer.jmxUser=admin -Dinfinispan.visualizer.jmxPass=p455w0rd -Dinfinispan.visualizer.serverList=localhost:11223

+ use `bin/standalone.sh` for Linux/Unix and `bin\standalone.bat` for Windows
+ **-b** and **-bmanagement** should be set to the external IP address of your host
+ **infinispan.visualizer.jmxUser** and **infinispan.visualizer.jmxPass** should be set to the credentials of the JBoss Data Grid servers you configured in step '1.2 Add Management User to JBoss Data Grid'  
+ **infinispan.visualizer.serverList** must set to the `<IPAddress>:<HotRodPort>` combination of at least one of the Infinispan servers you started in step '2.1 Starting the Data Grid Instances'. To specify multiple addresses wrap with quotes and delimit with a semicolon, e.g.: `-Dinfinispan.visualizer.serverList='localhost:11223;localhost:11224'`
+ **infinispan.visualizer.refreshRate** This is refresh rate determines how often Visualizer should poll data from Infinispan servers.  If onset, this defaults to 2000 - which means 2000ms delay between data polling.

### 2.3 View the Demo Application

The application will be running at the following URL: <http://localhost:8080/infinispan-visualizer/>.

_NOTE: Before accessing the application, please ensure an Infinispan server is up and running!_

### 2.4 Load Data into the Grid

Use the hotrod-demo application to load data into the grid: <https://github.com/saturnism/hotrod-demo/>

# 3 Building from Source

### 3.1 Building the Application

To build the application, invoke.

	mvn clean package

### 3.2 Build and Deploy from Source

_NOTE: The following build command assumes you have configured your Maven user settings.

a. Make sure you have started the Infinispan Server as described above.

b. Open a command line and navigate to the root directory of this quickstart.

c. Type this command to build and deploy the archive:

	mvn clean package wildfly:deploy

d. This will deploy `target/infinispan-visualizer.war` to the running instance of the server.

