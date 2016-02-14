Process necessary to extract the Wikipedia articles from a given area
---------------------------------------------------------------------

Warning: given the "anarchy" in the Wikipedia categories' definition, no 
guarantee that every article selected throughout this process actually belongs 
to the area. Conversely, selecting every article from the area of interest is 
not guaranteed.

The process is as follows.

1. Run DomainKeywords 
---------------------

The objective is extracting the vocabulary associated to the area.
Running it with t=10 (top 10%) should be enough.

Keep the output file for step 3

2. Run CategoryExtractor
------------------------

It extracts all the subcategories from the desired category. At this time, it 
should be called in verbose mode and without defining maxdepth. In that way, the 
entire tree will be generated and all the information will be reported.

Keep the output file for steps 3 and 4

3. Run CategoryNameStats
------------------------

It will give you a figure of the percentage of in-domain category titles per 
level of the tree. You need the dictionary generated in step 1 as well as the 
categories' tree generated in step 2.

4. Run ArticleSelector
----------------------

It will extract a list of the articles associated to a tree of categories up to 
the selected depth. You need the categories' tree generated in step 2.

5. Run  ArticleTextExtractor
----------------------------

It will extract the articles' contents into plain text files. You need the list 
or articles produced in step 4.


Alternatively,

Run Xecutor
-----------

It will run the previous steps without any human intervention. For this, there is a 
new step (CategoryDepth) where given the wanted percentage of in-domain category 
titles in a level, the corresponding depth in the category tree is obtained. 
The Xecutor can be run from a selected step:
		//1. Run DomainKeywords
		//2. Run CategoryExtractor
		//3. Run CategoryNameStats
		//4. Run CategoryDepth
		//5. Run createCategoriesFile
		//6. Run ArticleSelector
		//7. Run ArticleTextExtractor
