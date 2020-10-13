#!/bin/bash
#
# Copies the selected files from a Wikitailor plain folder 
# into a desired new folder. Useful to extract in-domain articles
# from global extractions
# Author: cristinae
# Date: 13.10.2020
# #################################################################

lan=$2
output=$3
size=100000

mkdir -p $output
while read id; do
  folder=$((id / size))
  cp $lan/$folder/$id.$lan.txt $output/.
done < $1
