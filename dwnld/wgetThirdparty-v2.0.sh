#!/bin/bash

# Download all the external libraries used by WikiTailor
#v2.0 download commands added for OsX, with curl
#v1.0 download commands for Linux, with wget


if [ "$(uname)" == "Linux" ]; then
# Apfloat for Java Archive
# GNU Lesser General Public License
wget http://www.apfloat.org/apfloat_java/1.8.1/apfloat.jar

# Apache Commons CLI 1.2
# Apache License, Version 2.
wget http://archive.apache.org/dist/commons/cli/binaries/commons-cli-1.2-bin.tar.gz

# Apache Commons Collections 4.4.0
# Apache License, Version 2.
wget http://archive.apache.org/dist/commons/collections/binaries/commons-collections4-4.0-bin.tar.gz

# Apache Commons IO 2.4
# Apache License, Version 2.
wget http://archive.apache.org/dist/commons/io/binaries/commons-io-2.4-bin.tar.gz

# Apache Commons Lang 3.2.1
# Apache License, Version 2.
wget http://archive.apache.org/dist/commons/lang/binaries/commons-lang3-3.2.1-bin.tar.gz

# Apache Commons Math 3.4.1
# Apache License, Version 2.
wget http://archive.apache.org/dist/commons/math/binaries/commons-math3-3.4.1-bin.tar.gz

# Apache Log4j 2 2.0
# Apache License, Version 2.
wget http://archive.apache.org/dist/logging/log4j/2.0/apache-log4j-2.0-bin.tar.gz

# Apache Lucene 3.5
# Apache License, Version 2.
wget http://archive.apache.org/dist/lucene/java/3.5.0/lucene-3.5.0.tgz

# Apache OpenNLP 1.5.3 
# Apache License, Version 2.
wget http://archive.apache.org/dist/opennlp/opennlp-1.5.3/apache-opennlp-1.5.3-bin.tar.gz

# GNU Trove: High performance collections for Java.
# GNU LESSER GENERAL PUBLIC LICENSE
wget https://bitbucket.org/trove4j/trove/downloads/trove-3.0.3.tar.gz

# ICU 4.8.1.1
# ICU License - free
wget http://repo1.maven.org/maven2/com/ibm/icu/icu4j/4.8.1.1/icu4j-4.8.1.1.jar

# JAMA: A Java Matrix Package 1.0.3
# JAMA License - free
wget http://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar

# JUnit 4.11
# Eclipse Public License 1.0
wget http://search.maven.org/remotecontent?filepath=junit/junit/4.11/junit-4.11.jar

# DKPro JWPL
# Apache License, Version 2.
wget http://search.maven.org/remotecontent?filepath=de/tudarmstadt/ukp/wikipedia/de.tudarmstadt.ukp.wikipedia.parser/0.9.2/de.tudarmstadt.ukp.wikipedia.parser-0.9.2.jar
wget http://search.maven.org/remotecontent?filepath=de/tudarmstadt/ukp/wikipedia/de.tudarmstadt.ukp.wikipedia.datamachine/0.9.2/de.tudarmstadt.ukp.wikipedia.datamachine-0.9.2.jar
wget http://search.maven.org/remotecontent?filepath=de/tudarmstadt/ukp/wikipedia/de.tudarmstadt.ukp.wikipedia.dist/0.9.2/de.tudarmstadt.ukp.wikipedia.dist-0.9.2.jar

# opencsv 
# Apache License, Version 2.
wget https://sourceforge.net/projects/opencsv/files/opencsv/2.3/opencsv-2.3-src-with-libs.tar.gz

# Snowball
# BSD License
wget http://snowball.tartarus.org/dist/libstemmer_java.tgz


elif [ "$(uname)" == "Darwin" ]; then

# Apfloat for Java Archive
# GNU Lesser General Public License
curl "http://www.apfloat.org/apfloat_java/1.8.1/apfloat.jar" -o "apfloat.jar"

# Apache Commons CLI 1.2
# Apache License, Version 2.
curl "http://archive.apache.org/dist/commons/cli/binaries/commons-cli-1.2-bin.tar.gz" -o "commons-cli-1.2-bin.tar.gz"

# Apache Commons Collections 4.4.0
# Apache License, Version 2.
curl "http://archive.apache.org/dist/commons/collections/binaries/commons-collections4-4.0-bin.tar.gz" -o "commons-collections4-4.0-bin.tar.gz"

# Apache Commons IO 2.4
# Apache License, Version 2.
curl "http://archive.apache.org/dist/commons/io/binaries/commons-io-2.4-bin.tar.gz" -o "commons-io-2.4-bin.tar.gz"

# Apache Commons Lang 3.2.1
# Apache License, Version 2.
curl "http://archive.apache.org/dist/commons/lang/binaries/commons-lang3-3.2.1-bin.tar.gz" -o "commons-lang3-3.2.1-bin.tar.gz"

# Apache Commons Math 3.4.1
# Apache License, Version 2.
curl "http://archive.apache.org/dist/commons/math/binaries/commons-math3-3.4.1-bin.tar.gz" -o "commons-math3-3.4.1-bin.tar.gz"

# Apache Log4j 2 2.0
# Apache License, Version 2.
curl "http://archive.apache.org/dist/logging/log4j/2.0/apache-log4j-2.0-bin.tar.gz" -o "apache-log4j-2.0-bin.tar.gz"

# Apache Lucene 3.5
# Apache License, Version 2.
curl "http://archive.apache.org/dist/lucene/java/3.5.0/lucene-3.5.0.tgz" -o "lucene-3.5.0.tgz"

# Apache OpenNLP 1.5.3 
# Apache License, Version 2.
curl "http://archive.apache.org/dist/opennlp/opennlp-1.5.3/apache-opennlp-1.5.3-bin.tar.gz" -o "apache-opennlp-1.5.3-bin.tar.gz"

# GNU Trove: High performance collections for Java.
# GNU LESSER GENERAL PUBLIC LICENSE
curl "https://bitbucket.org/trove4j/trove/downloads/trove-3.0.3.tar.gz" -o "trove-3.0.3.tar.gz"

# ICU 4.8.1.1
# ICU License - free
curl "http://repo1.maven.org/maven2/com/ibm/icu/icu4j/4.8.1.1/icu4j-4.8.1.1.jar" -o "icu4j-4.8.1.1.jar"

# JAMA: A Java Matrix Package 1.0.3
# JAMA License - free
curl "http://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar" -o "Jama-1.0.3.jar"

# JUnit 4.11
# Eclipse Public License 1.0
curl "http://search.maven.org/remotecontent?filepath=junit/junit/4.11/junit-4.11.jar" -o "junit-4.11.jar"

# DKPro JWPL
# Apache License, Version 2.
curl "http://search.maven.org/remotecontent?filepath=de/tudarmstadt/ukp/wikipedia/de.tudarmstadt.ukp.wikipedia.parser/0.9.2/de.tudarmstadt.ukp.wikipedia.parser-0.9.2.jar" -o "de.tudarmstadt.ukp.wikipedia.parser-0.9.2.jar"
curl "http://search.maven.org/remotecontent?filepath=de/tudarmstadt/ukp/wikipedia/de.tudarmstadt.ukp.wikipedia.datamachine/0.9.2/de.tudarmstadt.ukp.wikipedia.datamachine-0.9.2.jar" -o "de.tudarmstadt.ukp.wikipedia.datamachine-0.9.2.jar"
curl "http://search.maven.org/remotecontent?filepath=de/tudarmstadt/ukp/wikipedia/de.tudarmstadt.ukp.wikipedia.dist/0.9.2/de.tudarmstadt.ukp.wikipedia.dist-0.9.2.jar" -o "de.tudarmstadt.ukp.wikipedia.dist-0.9.2.jar"

# opencsv 
# Apache License, Version 2.
curl "https://sourceforge.net/projects/opencsv/files/opencsv/2.3/opencsv-2.3-src-with-libs.tar.gz" -o "opencsv-2.3-src-with-libs.tar.gz"

# Snowball
# BSD License
curl "http://snowball.tartarus.org/dist/libstemmer_java.tgz" -o "libstemmer_java.tgz"


fi

