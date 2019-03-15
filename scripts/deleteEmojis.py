#!/usr/bin/env python
# -*- coding: utf-8 -*-

""" Removes emoji from a file
    Date: 14.03.2019
    Author: cristinae
"""

import sys
import re

def remove_emoji(string):

    emoji_pattern = re.compile("["
                u"\U0001F600-\U0001F64F"  # emoticons
                u"\U0001F300-\U0001F5FF"  # symbols & pictographs
                u"\U0001F680-\U0001F6FF"  # transport & map symbols
                u"\U0001F1E0-\U0001F1FF"  # flags (iOS)
                u"\U00002702-\U000027B0"
                u"\U000024C2-\U0001F251"
                u"\U0001f926-\U0001f937"
                u'\U00010000-\U0010ffff'
                u"\u200d"
                u"\u2640-\u2642"
                u"\u2600-\u2B55"
                u"\u270F"
                u"\u23cf"
                u"\u23e9"
                u"\u231a"
                u"\u3030"
                u"\ufe0f"
                "]+", flags=re.UNICODE)


    return emoji_pattern.sub(u'', unicode(string, "utf-8"))
#(r'', string)


def main(inF):

    outF = inF + '.2'
    fOUT = open(outF, 'w')
    with open(inF) as f:
       count = 0
       for line in f:
           #line = line.strip()  # +"\n"afterwards spoils everything in Wikipedia Page table
           clean = remove_emoji(line)
           fOUT.write(clean.encode("utf-8"))
    fOUT.close() 



if __name__ == "__main__":
    
    if len(sys.argv) is not 2:
        sys.stderr.write('Usage: python3 %s inputFile\n' % sys.argv[0])
        sys.exit(1)
main(sys.argv[1])

