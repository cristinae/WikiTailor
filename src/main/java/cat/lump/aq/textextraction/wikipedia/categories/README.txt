CLI reference sheet
-------------------

DomainKeywords
--------------

SUMMARY

This program extracts the "associated" vocabulary to a given category in Wikipedia. 
In brief, it extracts the text from every article belonging to a category and 
returns the top k% of their vocabulary weighted with tf and after standard 
pre-processing (stopwording, stemming). Additional preprocessing includes removing 
numbers and words shorter than 4 characters. 

PARAMETERS

Mandatory

	-l,--language <arg>   Language of interest (e.g., en, es, ca)
 	
 	-y,--year <arg>       Wikipedia year edition (2010, 2012, 2013)
 	
 	-c,--category <arg>   Category (e.g., "Mitología")
 	-i,--id <arg>         Category as numerical id (e.g., "49204")
 						Only one of c or i are necessary. The text-based call 
 						requires the exact name of the category as in Wikipedia 
 						(spaces are replaced by underscores).	
 
 Optional	
 	
 	-f,--file <arg>    Output file. A file in the current directory will
                       be generated if not provided 
 	
 	-t,--top <arg>     Top-k number of words required (in pctge.). If not
                       provided, 100% is assumed

OUTPUT

A tab-separated file with tf and the word.

CategoryExtractor
-----------------

SUMMARY 

It extracts all the subcategories of a given category up to the desired depth.

PARAMETERS

Mandatory

	-l,--language <arg>   Language of interest (e.g., en, es, ca)
 	
 	-y,--year <arg>       Wikipedia year edition (2010, 2012, 2013)
 	
 	-c,--category <arg>   Category (e.g., "Mitología")
 	-i,--id <arg>         Category as numerical id (e.g., "49204")
 						Only one of c or i are necessary. The text-based call 
 						requires the exact name of the category as in Wikipedia 
 						(spaces are replaced by underscores).	
Optional

	-f,--file <arg>    Output file. A file in the current directory will
                       be generated if not provided 
	
	-m,--maxdepth <arg>   Maximum depth of the categories' tree to explore at
                       (all if not provided)
 	-v,--verbose          Include extra features in the output	

OUTPUT

Normal: 

		depth, category ID, category name		
		
Verbose: depth, category ID, category name, articles associated,
		 number of children, number of parents, parent id (from which we
		 reached this category)
		 
		 For instance,
		 
			 1	122975	Finanzas	102	25	3	-1
			 
		where depth is 1, the id is 122975, with name "Finanzas", 
		it has 102 articles associated, 25 children, 3 parents, and -1 
		means this is the root of the subgraph explored.
		 
 	   
 	   
CategoryNameStats
-----------------

SUMMARY 

The input is composed of the domain keywords dictionary generated with 
DomainKeywords and the category tree file generated CategoryExtractor.
It estimates the (approximate) extent of in-domain categories per tree
level, given the domain vocabulary.


PARAMETERS

Mandatory


 -c,--categories <arg>   File with the per-depth categories (generated
                         with CategoryExtractor)
 -d,--dictionary <arg>   File with the dictionary (generated with
                         DomainKeywords)
 -l,--language <arg>     Language of interest (e.g., en, es, ca)
 
OUTPUT 

Tab-separated file with level (1 is the root) and a value in the range 
[0,1] for the percentage.

ArticleSelector
---------------

SUMMARY

The input is composed of the category tree file generated with 
CategoryExtractor. It selects the Wikipedia articles associated to the 
category and subcategories according to the desired depth. 

PARAMETERS

Mandatory

 -l,--language <arg>   Language of interest (e.g., en, es, ca)
 -y,--year <arg>       Wikipedia year edition (2010, 2012, 2013)
 -f,--file <arg>       Input file with the categories and their
                       depth(generated with CategoryExtractor)

Optional 

 -d,--depth <arg>      Maximum depth to consider (or take the entire tree


OUTPUT

Tab-separated file with article ID and and article title


Preprocessor
------------

SUMMARY

Extracts the article from a given Wikipedia (either the entirely or some 
specific ids)


PARAMERERS

Mandatory

 -l,--language <arg>    Language of interest (e.g., en, es, ca)
 -y,--year <arg>        Wikipedia year edition (2010, 2012, 2013)
 -d,--directory <arg>   Output directory

Optional

 -f,--file <arg>        Input file with the articles' (generated with
						ArticleSelector)

OUTPUT

The set of articles, one per id (right now in plain text only)

