README file for the lump2 text extraction project
-------------------------------------------------

@last_modification Feb 12, 2015 
@last_author Cristina 


SUMMARY
-------

This project includes classes to extract and manipulate text from files
and sources that do not contain plain text. 
As it is now, text can be extracted from Wikitext (through JWPL and a 
local preprocessed copy of Wikipedia)

TODO including other sources, such as doc, pdf, and the Web. 


VERSIONS
--------

v.0.1	Text can be extracted from more than 8 languages including English, 
	Spanish, German, Russian and Greek. 
v.0.1.1 Issues related to the proper handling (discarding) of sections such 
	as "see also" and "related contents" are fixed. Special kinds of lists, 
	such as Catalan acronyms, are discarded.
v.0.1.2 Romanian 2013 has been added. Now the control on whether a dump for a 
	given language and year exists is made through a text file and the process 
	is cleaner.   
v.0.1.4 Automatised process for extracting plain articles for a given domain
    	in Wikipedia (category)
v.0.1.5	Automatised process for the extraction of comparable fragments


JARS DEPRECATED (Oct, 2014)
---------------

ArticleTextExtractor.v2.jar	The overall jar file created with ant should be used 
	instead (e.g. with the -cp flag). 
	
	This deprecated jar file was generated on Aug. 8. It extracts Wikipedia 
	articles from a given dump (language and year). Alternatively, a list with 
	IDs can be included to extract specific articles (entire Wikipedias are 
	extracted otherwise). In this version, lists and tables within the articles 
	are not extracted. 


SUBPROJECTS
-----------

wikipedia includes an interface to the JWPL library, as well as constants
	related to the languages identifiers and Wikipedia-related keywords
	(e.g. naming of images and categories) 
	

DEPENDENCIES
------------

lump2-aq-check for checking the parameters introduced when calling the 
	classes/method
	
lump2-aq-config to get access to the MySQL database with the Wikipedia
	preprocessed dumps

lump2-aq-io for saving and opening files

lump2-aq-log

lump2-ir-retrievalmodels

lump2-aq-struct for accesing a tf-storing class

lump2-ie-textprocessing for standard text preprocessing

THIRDPARTIES:

commons-cli 1.2 for the creation of CLIs

jwpl 0.9.2 is used for accessing the Wikipedia contents

log4j 2.0 to keep some logs

commons-math3 3.4.1 for using splines 

apache-opennlp 1.5.3

apache-commons-math3 3.4.1

junit-4.11 for unit tests

