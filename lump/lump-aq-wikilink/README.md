README file for the lump wikilink project
-----------------------------------------

@last_modification Feb 9, 2016 
@last_author Cristina


SUMMARY
-------

This project includes classes to access configuration data of the database, to get 
connected to the MySQL Wikipedias databases and manage the connection. Includes an 
interface to the JWPL library, as well as constants related to the languages 
identifiers and Wikipedia-related keywords (e.g. naming of images and categories). 
General methods to query the database are also included here.

SUBPROJECTS
-----------

config - includes the class to access the Wikipedia-db-related configuration

connection - includes a class to manage the connection with the MySQL database

jwpl - includes methods to initialise the database and the language-dependant 
keywords associated to Wikipedia
 
	
DEPENDENCIES
------------

THIRDPARTIES:

jwpl 0.9.2 is used for accessing the Wikipedia contents
