#!/bin/bash
# Copyright (c) Facebook, Inc. and its affiliates.
# All rights reserved.
#
# This source code is licensed under the BSD-style license found in the
# LICENSE file in the root directory of this source tree.
#
# LASER  Language-Agnostic SEntence Representations
# is a toolkit to calculate multilingual sentence embeddings
# and to use them for document classification, bitext filtering
# and mining
#
# --------------------------------------------------------
#
# bash script to mine for bitexts in the BUCC corpus
# Modified for a general context

export LC_ALL=C.UTF-8
export LASER="/raid/bin/daga01/LASER"

if [ -z ${LASER+x} ] ; then
  echo "Please set the environment variable 'LASER'"
  exit
fi

# general config

ddir="$1"        # folder with the input data
edir="$2"        # folder for the output data
corpus="$3"      # root of the filenames

echo "Starting for $corpus"
#ddir=${data_in}	        # raw texts BUCC format
#edir=${data_out} 	# output normalized texts and embeddings
#langs=("it pt ro")
langs=("ro")
ltrg="pt"		# ltrg is always the 2nd language

# encoder
model_dir="${LASER}/models"
encoder="${model_dir}/bilstm.93langs.2018-12-26.pt"
bpe_codes="${model_dir}/93langs.fcodes"

###################################################################
#
# Extract files with 
#
###################################################################

GetData () {
  fn2=$1; lang=$2
  outf="${edir}/${fn2}"
  for ll  in ${ltrg} ${lang} ; do
    inf="${ddir}/${ll}.${corpus}"
    if [ ! -f ${outf}.txt.${ll} ] ; then
      echo " - extract files ${outf} in ${ll}"
      cat ${inf} | cut -f1 > ${outf}.id.${ll}
      cat ${inf} | cut -f2 > ${outf}.txt.${ll}
    fi
  done
}


###################################################################
#
# Tokenize and Embed
#
###################################################################

Embed () {
  ll=$2
  txt="$1.txt.${ll}"
  enc="$1.enc.${ll}"
  if [ ! -s ${enc} ] ; then
    cat ${txt} | python3 ${LASER}/source/embed.py \
      --encoder ${encoder} \
      --token-lang ${ll} \
      --bpe-codes ${bpe_codes} \
      --output ${enc} \
      --verbose 
  fi
}


###################################################################
#
# Mine for bitexts
#
###################################################################

Mine () {
  bn=$1
  l1=$2
  l2=$3
  cand="${bn}.candidates.tsv"
  if [ ! -s ${cand} ] ; then
    python3 ${LASER}/source/mine_bitexts.py \
       $edir${corpus}.txt.${l1} $edir${corpus}.txt.${l2} \
       --src-lang ${l1} --trg-lang ${l2} \
       --src-embeddings $edir$corpus.enc.${l1} --trg-embeddings $edir$corpus.enc.${l2} \
       --unify --mode mine --retrieval max --margin ratio -k 4  \
       --output ${cand} \
       --verbose #--gpu
  fi
}


###################################################################
#
# Main loop
#
###################################################################

echo -e "\nProcessing inout data in ${data_in}"

# create output directories
mkdir -p ${edir}

for lsrc in ${langs[@]} ; do

  GetData  ${corpus} ${lsrc}

  # Tokenize and embed test 
  Embed ${edir}/${corpus} ${lsrc}
  Embed ${edir}/${corpus} ${ltrg}

#  mine for texts in test
  langPair="${lsrc}-${ltrg}"
  prefix="${langPair}.${corpus}"
  Mine ${edir}/${prefix} ${lsrc} ${ltrg}

  sort -n -r ${edir}/${prefix}.candidates.tsv > ${edir}/${prefix}.ranked.tsv

  # extract test bitexts for treshhold optimized on train
  #th=`grep 'best threshold' ${bname}.train.log | sed -e 's/[=:]/ /g' | awk '{print $4}'`
  # low threshold to look at it by hnad:
  th=1.1

  
  extracted="${edir}/${prefix}.extracted.tsv"
  if [ ! -s ${extracted} ] ; then
    python3 ${LASER}/tasks/bucc/bucc.py \
      --src-lang ${lsrc} --trg-lang ${ltrg} \
      --bucc-texts ${edir}/${corpus}.txt \
      --bucc-ids ${edir}/${corpus}.id \
      --candidates ${edir}/${prefix}.candidates.tsv \
      --threshold ${th} --output ${extracted} \
      --verbose
  fi
done
