README file for the lump lucene-based retrieval  project
--------------------------------------------------------

@last_modification Feb 9 2016 
@last_author Cristina


SUMMARY
-------

This project includes the classes related to extraction of Wikipedia's articles 
using IR methods, all of them built on top of Lucene.  

SUBPROJECTS
-----------

DEPENDENCIES
------------

lump2-aq-basics for logging the process, for checking the parameters introduced when 
 	calling the classes/method, for saving and opening files

lump2-ie-textprocessing for processing the text and get proper representations

lump2-aq-textextraction for including the preprocess done with Wikipedia


THIRDPARTIES:

apache-commons-cli 1.2 for the command-line interface

apache-commons-io-2.4

apache-commons-lang3 3.2.1 
	
apache-lucene for the search engine

icu4j-4.8.1.1 

junit-4.11 for unit tests
