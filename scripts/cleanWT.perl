#!/usr/bin/perl

###############################################################################
# Purpose: Cleaning WikiTailor extractions
#          (provisional, WikiTailor parser to be actualised)
# Author:  Cristina
# Date:    14/05/2020
###############################################################################

use strict;
use warnings;

#binmode(STDIN, ":utf8");
#binmode(STDOUT, ":utf8");

# Command line arguments
my $num_args = $#ARGV + 1;
if ($num_args != 1) {
    print "\nUsage: $0 <inputFile> \n";
    exit;
}

my $inFile=$ARGV[0];
my $outFile=$inFile.".clean";

# Load lines to clean
open LINES, "< $inFile" or die "could not open $inFile\n";
open CLEANWP, "> $outFile" or die "could not create $outFile\n";

my $counter=0;
while (<LINES>) {
   my $line=$_;
   chomp($line);
   #print STDERR "$line\n";
   $counter++;
   print STDERR "." if $counter % 10000 == 0;

   next if $line =~ /\.jpe*g|\.png|\.tif|\.svg|\.gif/;
   next if $line =~ /http|\.pdf/;
   next if $line =~ /scope\s*=|align\s*=|style\s*=|rowspan\s*=|colspan\s*=/;
   next if $line =~ /caption\s*=|title\s*=|href\s*=|class\s*=|alt\s*=/;
   next if $line =~ /text\s*=|type\s*=|width\s*=|heights*\s*=|mini\||thumb\|/;
   next if $line =~ /\\begin\s*\{|\\end\s*\{|\\frac\s*\{/;

   # Clean WP external links
   ## {{xxx}} all the cases seem to be in removable sentences
   next if $line =~ /\{\{/;
   next if $line =~ /\}\}/;
   # Clean WP internal links
   ## category related, removed completely
   $line =~ s/\[\[[^]]+:.+\]\]//g;
   ## general, extract the first link
   my @strings=$line =~ /\[\[([^]]+)\]\]/g;
   foreach (@strings) {
       my $terms=$_;
       #my @terms = split(/\|/,$_);
       my $term = $terms;
       if ($terms =~ /\|/) {
           $term = substr($terms, 0, index($terms, "\|"));
       } 
       #print "${terms}:::$term\n\n";
       $line =~ s/\Q$terms\E/$term/;
   }
   $line =~ s/\[\[//g;
   $line =~ s/\]\]//g;
   ## Sections, subsections and lower deepths that are not cleaned
   ### usually, they are preappended to a valid sentence
   $line =~ s/==+[^=]+==+//g;
   
   print CLEANWP "$line\n";
}

close LINES;
close CLEANWP;


