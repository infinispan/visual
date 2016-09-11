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

This is a graphical JBoss Data Grid visualizer based on the 2011 JBoss World Keynote presented 
by Burr Sutter (https://vimeo.com/25258416).

This visualizer works with out of the box JDG configuration with JMX and Management
User configured (see below for instructions).

# 1 Configuring the Demo
This demo application uses a single instance of JBoss Enterprise Application server, and a cluster of JBoss Data Grid servers. The following steps document how to configure the servers whether you plan to build the demo from source or deploy a precompiled demo app.

### 1.1 System requirements
 * JBoss Data Grid 6.4.0
 * JBoss EAP 6.3
 * Maven 3

### 1.2 Add Management User to JBoss Data Grid

Use the example shown below to add an user
    $JDG_HOME/bin$ ./add-user.sh -a -u admin -p adminPass9! -r ManagementRealm

Follow instructions to add a user with your username/password of your choice.  _ALL_ nodes should have the exact same login credentials in order for this visualizer to run correctly.

### 1.3 Configure the Java Heap for EAP and JDG servers
Decreasing the default EAP and JDG heap size will allow you to run the necessary JBoss instances on your host without running out of memory. The following java heap settings will typically allow for 1 EAP instance with up to 4 JDG instances on a single host (results may vary). 

Edit the EAP `$EAP_HOME/bin/standalone.conf` file and modify the Java heap size to the following:

	-Xms256m -Xmx1024m -XX:MaxPermSize=128m

Edit the `$JDG_HOME/bin/clustered.conf` file and modify the Java heap size to the following:

	-Xms128m -Xmx384m -Xss2048k -XX:MaxPermSize=128m 

### 1.4 Configure the Lab Cache on JDG server
This demo requires a cache to be configured and exposed through the HotRod interface. Open the clustered JDG configuration file, `$JDG_HOME/standalone/configuration/clustered.xml`, and add a distributed cache to the infinispan configuration section called `labCache`.

	<subsystem xmlns="urn:infinispan:server:core:6.2" default-cache-container="clustered">
		<cache-container name="clustered" default-cache="default" statistics="true">
			<transport executor="infinispan-transport" lock-timeout="60000"/>
			...
			<distributed-cache name="labCache" mode="SYNC" virtual-nodes="1" owners="2" remote-timeout="30000" start="EAGER">
				<locking isolation="READ_COMMITTED" acquire-timeout="30000" concurrency-level="1000" striping="false"/>
				<transaction mode="NONE"/>
			</distributed-cache>
			...
		</cache-container>
	</subsystem>

### 1.5 Create Instance Profiles
Each JBoss Data Grid instance should have its own instance directory. Make four copies of the `$JDG_HOME/standalone` directory. When starting up JDG the `-Djboss.server.base.dir=<instance-directory-name>` will be used to select the instance's execution directory.

	cp -r $JDG_HOME/standalone $JDG_HOME/standalone1
	cp -r $JDG_HOME/standalone $JDG_HOME/standalone2
	cp -r $JDG_HOME/standalone $JDG_HOME/standalone3 

### 1.6 Configuring the Network Interface
For this visualizer to work in a clustered environment, each JDG node must use the standalone-ha.xml configuration (for clustering), 
and each JDG node must be running on a dedicated network interface. This can be achieved through IP aliasing or port offsets.
This can be achieved through: 

a. Running each data grid instance on the same IP address, but with port offsets.

b. The use of IP Aliasing. You may need to look up more documentation regarding IP Aliasing for your Operating System. 

c. Running each JDG node in a virtual machine instance, each with a different IP address.

#### 1.6a Using Port Offsets
Further configuration is not necessary. Port offsets may be specified on the command line startup, e.g.:

	./clustered.sh -b 127.0.0.1 -bmanagement=127.0.0.1 -Djboss.node.name=jdg-1 -Djboss.socket.binding.port-offset=1 ...
	./clustered.sh -b 127.0.0.1 -bmanagement=127.0.0.1 -Djboss.node.name=jdg-2 -Djboss.socket.binding.port-offset=2 ...

#### 1.6b Using IP Aliases
An example to create local IP Aliases in Mac OS X:

	sudo ifconfig lo0 alias 127.0.0.2 255.0.0.0
	sudo ifconfig lo0 alias 127.0.0.3 255.0.0.0
 
Each JDG instance must bind to a specific IP address, e.g.:

	./clustered.sh -b 127.0.0.2 -bmanagement=127.0.0.2 -Djboss.node.name=jdg-2 ...
	./clustered.sh -b 127.0.0.3 -bmanagement=127.0.0.3 -Djboss.node.name=jdg-3 ...
 
#### 1.6c Using Virtual Machines 
If running JDG inside a VM, please make sure JDG is bound to the non-local network interface. (JDG binds to localhost unless otherwise specified), e.g.:
	
	./clustered.sh -b 192.168.100.101 -bmanagement=192.168.100.101 -Djboss.node.name=jdg-1 ...

#### 1.7 Deploy the Visualizer
+ Copy the Visualizer to the EAP deployments directory: 
	
	```
	cp jdg-visualizer.war $EAP_HOME/standalone/deployments```
	
+ Create a marker file that signals EAP 6 to deploy the Web Archive

	```	
	echo "" > $EAP_HOME/standalone/deployments/jdg-visualizer.war.dodeploy```
	
## 2 Running the Demo

### 2.1 Starting the Data Grid Instances
Open a command line and navigate to the root of the JBoss Data Grid server directory **$JDG_HOME** for each instance you want to start.

Start each instance, ensuring the address and ports will not conflict with each other or the JBoss EAP instance.

	bin/clustered.sh -b localhost -bmanagement=localhost -Djboss.server.base.dir=standalone1 -Djboss.socket.binding.port-offset=1 -Djboss.node.name=jdg1

	bin/clustered.sh -b localhost -bmanagement=localhost -Djboss.server.base.dir=standalone2 -Djboss.socket.binding.port-offset=2 -Djboss.node.name=jdg2

	bin/clustered.sh -b localhost -bmanagement=localhost -Djboss.server.base.dir=standalone3 -Djboss.socket.binding.port-offset=3 -Djboss.node.name=jdg3

+ use `bin/standalone.sh` for Linux/Unix and `bin\standalone.bat` for Windows
+ **-c <filename>** specifies the configuration file to use; `standalone-ha.xml` must be used to form a cluster. 
+ **-b** and **-bmanagement** specify the IP Address to bind the data grid to. If running the the demo on an isolated host then specify `localhost`.  If running the demo with other hosts on a network the specify the IP Address of the network interface you would like to bind to. Both the NIC configuration and the network must support multicast for the data grid to be dynamically formed.
+ **-Djboss.node.name** must be unique for each JDG instance
+ **-Djboss.server.base.dir** should point to a profile dirctory tree under the **$JDG_HOME** directory. This should not be shared between instances.
+ **-Djboss.socket.binding.port-offset** specifies the offset from the default port bindings. `11222+offset` for Hotrod. `9999+offset` for JMX

### 2.2 Start the Application Server
Open a command line and navigate to the root of the JBoss EAP server directory **$EAP_HOME**.

Start the application server and specify the visualizer's demo parameters.

	bin/standalone.sh -b 192.168.1.101 -bmanagement=192.168.1.101 -Djdg.visualizer.jmxUser=admin -Djdg.visualizer.jmxPass=p455w0rd -Djdg.visualizer.serverList=localhost:11223

+ use `bin/standalone.sh` for Linux/Unix and `bin\standalone.bat` for Windows
+ **-b** and **-bmanagement** should be set to the external IP address of your host
+ **jdg.visualizer.jmxUser** and **jdg.visualizer.jmxPass** should be set to the credentials of the JBoss Data Grid servers you configured in step '1.2 Add Management User to JBoss Data Grid'  
+ **jdg.visualizer.serverList** must set to the `<IPAddress>:<HotRodPort>` combination of at least one of the JDG servers you started in step '2.1 Starting the Data Grid Instances'. To specify multiple addresses wrap with quotes and delimit with a semicolon, e.g.: `-Djdg.visualizer.serverList='localhost:11223;localhost:11224'`
+ **jdg.visualizer.refreshRate** This is refresh rate determines how often Visualizer should poll data from JDG servers.  If onset, this defaults to 2000 - which means 2000ms delay between data polling.

### 2.3 View the Demo Application
The application will be running at the following URL: <http://localhost:8080/jdg-visualizer/>.

_NOTE: Before accessing the application, please ensure a JDG server is up and running!_

### 2.4 Load Data into the Grid
Use the hotrod-demo application to load data into the grid: <https://github.com/saturnism/hotrod-demo/>

# 3 Building from Source

### 3.1 Configure Maven
See here to make sure JBoss Repository is configured - <http://www.jboss.org/jdf/quickstarts/jboss-as-quickstart/#mavenconfiguration/>

In addition, please make sure JDG 6 repository is configured based on JDG 6 Maven Repository installation instructions. If not using JDG 6, please change the `pom.xml` so that the Infinispan dependency is based on community project.

NOTE: This code hasn't been tested w/ Infinispan community project.

### 3.2 Building the Application
To build the application first make sure you have configured maven to use the JBoss Data Grid reppository or the Community Infinispan respository.

	mvn clean package

### 3.3 Build and Deploy from Source

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.html/#buildanddeploy) for complete instructions and additional options._

a. Make sure you have started the JBoss Server as described above.

b. Open a command line and navigate to the root directory of this quickstart.

c. Type this command to build and deploy the archive:

	mvn clean package jboss-as:deploy

d. This will deploy `target/jdg-visualizer.war` to the running instance of the server.

### 3.4 Undeploy the Archive
a. Make sure you have started the JBoss Server as described above.
b. Open a command line and navigate to the root directory of this quickstart.
c. When you are finished testing, type this command to undeploy the archive:

	mvn jboss-as:undeploy

### 3.5 Debug the Application
If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

	mvn dependency:sources
 	mvn dependency:resolve -Dclassifier=javadoc

