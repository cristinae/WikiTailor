README file for the lump2 text processing project
-------------------------------------------------

@last_modification Feb 9, 2016
@last_author Cristina


SUMMARY
-------

This project includes classes to extract and manipulate text from files. 
Among the preprocessing facilities, it includes a sentences' detector, 
tokenizer, both character and word n-grams, and stopworder, among other
features.  

SUBPROJECTS
-----------

textprocessing includes the abstract classes for decomposing a text (for 
	instance, by extracting n-grams or tokens).

ngram contains the classes for extracting both word and character n-grams 
	from a text. Note that an extractor for word 1-grams can be used as
	tokenizer. 

sentence has one single class that detects sentences in different languages
	(currently including only Arabic, German, Greek, English and Portuguese; 
	the English models is often used for other languages, such as Spanish).
	TODO create models for other required languages.
	
stopwords contains a class to discard/capture the stopwords in a text. The
	lists of stopwords (in [..]stopwords/lists) are currently available 
	for Arabic, Catalan, German, Greek, English, Spanish, Basque, French, 
	Occitan, Portuguese and Romanian. 
	TODO add more languages.
	
word contains a stemmer factory that relies on snowball stemmers (currently
	available: Dutch, English, French, German, Italian, Spanish, among others)
	and a word decomposer based on ICU4J.
	Stemms for Arabic are obtained with Lucene stemmer
	
DEPENDENCIES
------------

lump2-aq-basics for file's processing, for checking the parameters introduced when calling the 
	classes/method 

THIRDPARTIES:

apache-commons-cli 1.2 for the command-line interface

icu4j 4.8.1.1 is used for transliteration, tokenization, and de-diacritization

opennlp 1.5.3 is required by the sentence detector and NER. The necessary models 
	are in [..]sentence.models and [..]ner.models 

snowball-stemmer is used to stem text in multiple languages.

lucene-3.5 is used to stem text in Arabic

VERSIONS
--------

Version 1.1.1

New models for detecting sentences in Greek and Arabic included.
