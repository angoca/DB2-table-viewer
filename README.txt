This is a very simple Java applet that allows you to execute a query against a
local DB2 database. The main goal is to execute rapidly a query from a Graphical
tool, without installing an application and configuring many aspects, and just
calling a remote applet that will charge the necessary tools to read the tables
of a local/remote database.

The project is open source to give a level of confidence to the users, because
they will be executing a remote tool against the local/remote data.

The applet could also be installed locally in any server in order to be called
from a trusted internal environment.

The objective is to create a basic way to execute queries agaist a DB2 database,
and prevent the configuration of a bigger tools such as IBM Data Studio. This is
necessary after the Control Center removal in DB2 10. In this version, there is
no-way to execute a query from a provided GUI, the only way is via CLP, and not
in all cases is the best way to visaulize the output. 