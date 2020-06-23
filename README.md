# WikiTailor 

WikiTailor is temporarily out of business as it is refurbishing to use up-to-date Lucene libraries. We are currently working on it and hoping it will be ready soon. 

### Your à-la-carte corpora extraction tool

WikiTailor is a tool for extracting in-domain corpora from Wikipedia. A domain must be defined as an existing category in Wikipedia (or in Vikipèdia, or in ويكيبيديا or in Βικιπαίδεια) and the articles belonging to that domain are extracted even if they are not tagged as such. Two extraction methods are implemented: the main system is based on the exploration of Wikipedia's category graph and a secondary one based information retrieval techniques is also included.

### WikiTailor 1.0 functionalities

- Monolingual in-domain corpora extraction
- Multilingual in-domain comparable corpora extraction
- Multilingual in-domain parallel corpora extraction build with the articles' titles

Available languages: Arabic, Basque, Catalan, English, French, German, Greek, Romanian, Portuguese and Spanish.

### Upcoming
- New available languages: Czech and Bulgarian
- Bilingual in-domain parallel corpora extraction build with the articles' content
- Evaluation of the quality of the extractions

### Usage

For the main functionality, that is, the extraction of a corpus of a specific domain do:

```
java -jar wikiTailor.v1.0.0.jar [-c <arg> | -n <arg>] [-d <arg>] [-e <arg>] [-h]
          -i <FILE> -l <arg> [-m <arg>]  [-o <arg>] [-s <arg>] [-t <arg>] -y <arg>

where the arguments are:
 -c,--category <arg>      Name of the main category (with '_' instead of ' ';
                          you can use -n instead)
 -d,--depth <arg>         depth obtained in a previous execution
                          (default: 0)
 -e,--end <arg>           Last step for the process
                          (default: 7)
 -h,--help                This help
 -i,--ini <FILE>          Global config file for WikiTailor
 -l,--language <arg>      Language of interest (e.g., en, es, ca)
 -m,--model <arg>         Percentage of in-domain categories
                          (default: 0.5)
 -n,--numcategory <arg>   Numerical identifier of the category (you can use -c instead)
 -o,--outpath <arg>       Save the output into this directory
                          (default: current)
 -s,--start <arg>         Initial step for the process
                          (default: 1)
 -t,--top <arg>           Number of vocabulary terms within the 10%
                          (default: 100, all: -1)
 -y,--year <arg>          Wikipedia year edition (2013, 2015, 2016)

Ex: java -jar wikiTailor.v1.0.0.jar -l en -y 2015 -i wikiTailor.ini -c Science

```

For other uses see [the manual](http://cristinae.github.io/WikiTailor/dwnld/wikiTailorTechnicalManual_v1.0.pdf) and [the project webpage](http://cristinae.github.io/WikiTailor).

### References
For a complete analysis of the methods implemented see: 

* Cristina España-Bonet, Alberto Barrón-Cedeño and Lluís Màrquez.
**Tailoring Wikipedia for in-Domain Comparable Corpora Extraction**.
[Preprint arXiv:2005.01177](https://arxiv.org/abs/2005.01177)

* Alberto Barrón-Cedeño, Cristina España-Bonet, Josu Boldoba and Lluís Màrquez.
**A Factory of Comparable Corpora from Wikipedia**.
In Proceedings of the 8th Workshop on Building and Using Comparable Corpora 
(BUCC 2015), pages 3-13, July 2015, Beijing, China


