README file for the lump basics project
---------------------------------------

@last_modification Feb 9, 2016 
@last_author Cristina


SUMMARY
-------

This project includes several subprojects to do some basic and
general operations for text acquisition. 

SUBPROJECTS
-----------

algebra - includes the class for operating over vectors and matrix. 

check - classes to check whether a given parameter (or any object) is 
what is expected; e.g. it is not empty, not null, true, etc.
It includes a few methods to throw errors.

io-files - includes classes to read and write files in different ways 
as well as a fool CSV reader and writer.
	
log - includes classes and configuration files to keep logs. 
Reporter is set as Deprecated as LumpLogger should be used instead.

structure - includes classes with basic data structures used by more
especific packages
	
	
DEPENDENCIES
------------

THIRDPARTIES:

thirdparty-junit-4.11 for junit testing

log4j 2.0 to keep generate the logs

