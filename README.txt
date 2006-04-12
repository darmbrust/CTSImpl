CTS Readme file


Overview:
CTS is split into two parts - MAPI and VAPI.

There is only one implementation of MAPI - and it runs agains the HL7 SQL database - not part of LexGrid.

There are three implementations of VAPI - and all of these are based on LexGrid datastores.  2 of the 
implementations run against SQL servers, and one runs against LDAP.  There are two SQL implementations 
because one runs against the full LexGrid Model - and the other runs against a simplified set of tables
we refer to as SQL-Lite.  The SQL-Lite version doesn't support things like association qualifiers, 
because these are not stored in the database.

The Vapi SQL and LDAP implementations allow you to enable Lucene searching - this vastly enhances the 
power of the search algorithms, and improves performance.  You will need to create the lucene index, 
however, with the LexGrid Converter - http://informatics.mayo.edu/LexGrid/index.php?page=convert


Configuration:
Configuration of CTS is done in the file resources/CTSProperties.prps.  It contains documentation
on its parameters.  The only remaining part when you start up a CTS instance is to make sure that it 
can find the configuration file.  You can do this by setting a VM parameter - 

java -DPropFileLocation=c:/CTS/resources/CTSProperties.prps

Or, if you put the CTS jar file in the following folder:

someFolder/lib/CTSImplAndDepend.jar

It will automatically look for the props file in this folder:

someFolder/resources/CTSProperties.prps

This is to facilitate useage of CTS in a SOAP/web server enviroment.


Performance Notes:
If your SQL or SQLLite database is not using the "constraints" that are created by our converter tool, you will
likely need many more indexes to get reasonable performance.


Support:
You may contact informatics@mayo.edu for support.