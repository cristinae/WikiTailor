README file for the lump2 text extraction project
-------------------------------------------------

@last_modification Feb 17, 2016
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


SUBPROJECTS
-----------

wikipedia includes an interface to the JWPL library, as well as constants
	related to the languages identifiers and Wikipedia-related keywords
	(e.g. naming of images and categories) 
	

DEPENDENCIES
------------

lump-aq-basics for checking the parameters introduced when calling the 
	classes/method, saving and opening files, logging, accesing a tf-storing class
	
lump-aq-wikilink to get access to the MySQL database with the Wikipedia
	preprocessed dumps

lump-ir-retrievalmodels

lump-ie-textprocessing for standard text preprocessing

THIRDPARTIES:

commons-cli 1.2 for the creation of CLIs

jwpl 0.9.2 is used for accessing the Wikipedia contents

log4j 2.0 to keep some logs

commons-math3 3.4.1 for using splines 

apache-opennlp 1.5.3

junit-4.11 for unit tests

