#!/bin/sh
#$ -V
#$ -cwd
#$ -q medium
#$ -m beas
#$ -M jboldoba@lsi.upc.edu
#$ -l h_vmem=18G
# 
# pairs: ID_src\tpath_src\tID_trg\tpath_trg
# out: path al directorio de salida
# type: Tipo de representacion . cng, cog, len, mono
# n: n de los n-gramas. Solo es necesario para cng, en el resto de casos se obvia


#FICHERO DE ENTRADA CON LOS PARES A COMPARAR (input.sim)
pairs=$1

#Directorio de salida
out=$2

#representacion
type=$3

#grado del n-grama
n=$4

echo "Start similarity $type $n.<in: $pairs, out: $out>. `date` "
/usr/local/jdk1.7.0/bin/java -Xmx8g -Xms4g -jar /home/usuaris/jboldoba/wikicardi/jars/ArticleSimilarity.jar $pairs $out $type $n 
echo "End `date`"
