README file for the lump retrieval models project
-------------------------------------------------

@last_modification Mar 19, 2014 
@last_author Alberto Barrón


SUMMARY
-------

This project includes information-retrieval-related classes. It includes
both document representation and similarity measures. 

TODO actually include the retrieval models
TODO IR evaluation measures

SUBPROJECTS
-----------

document - includes classes to represent a document's contents with different
	characterizations (e.g. BoW and n-grams) 

similarity - contains the classes to compute similarity between two document
	vectors. Now only cosine is included.

similarity.esa (TODEFINE) should include the ESA-related classes
	
	
DEPENDENCIES
------------

lump2-aq-algebra for performing vector operations.
 
lump2-aq-check for checking the parameters introduced when calling the 
	classes/method.

lump2-ie-textprocessing for processing the text and get proper representations.

THIRDPARTIES:

apache-commons-collections 4.4.0 is used to have a BidiMap-based dictionary
	with all the vocabulary in a text
	
snowball-stemmer is used to stem text in multiple languages. 
	(TODO move to textprocessing and avoid calling it here???)
