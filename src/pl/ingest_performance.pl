#!/usr/bin/perl

#
# Run this from server/logs after a bunch of ingests.
# It produces several .dat files there, which can
# be fed to GNUPlot.
#
# The GNUPlot file for plotting the output data of
# script is in src/plt.  You'll have to change the
# last line of that file to make sure it's pointing
# to the right directory for the .dat files.
#

opendir(D, ".");
@fnames=grep(/log/, readdir(D));
closedir(D);
$c=1;
open(IND, ">xmlindexing.dat");
open(TOT, ">total.dat");
open(REPL, ">replication.dat");
open(VALID1, ">valid1.dat");
open(VALID2, ">valid2.dat");
foreach $fname (@fnames) {
  print "$fname\n";
  open(F, $fname);
  foreach $line (<F>) {
      if ($line=~/XML DB/) {
          @f=split(/\"/, $line);
          $st=@f[1];
      }
      if ($line=~/Exiting update/) {
          @f=split(/\"/, $line);
          $et=@f[1];
          $tt=$et-$st;
          print IND "$c $tt\n";
          $c++;
      }
      if ($line=~/Entered DefaultManagement.ingestObject/) {
          @f=split(/\"/, $line);
          $tot_st=@f[1];          
      }
      if ($line=~/Exiting DefaultManagement.ingestObject/) {
          @f=split(/\"/, $line);
          $tot_et=@f[1];          
          $tot_tt=$tot_et-$tot_st;
          print TOT "$c $tot_tt\n";
      }
      if ($line=~/Attempting/) {
          @f=split(/\"/, $line);
          $repl_st=@f[1];  
          $valid2_et=@f[1];          
          $valid2_tt=$valid2_et-$valid2_st;
          print VALID2 "$c $valid2_tt\n";        
      }
      if ($line=~/Entering update/) {
          @f=split(/\"/, $line);
          $repl_et=@f[1];          
          $repl_tt=$repl_et-$repl_st;
          print REPL "$c $repl_tt\n";
      }
      if ($line=~/Generated/) {
          @f=split(/\"/, $line);
          $valid2_st=@f[1];  
          $valid1_et=@f[1];          
          $valid1_tt=$valid1_et-$tot_st;
          print VALID1 "$c $valid1_tt\n";                
      }
  }
  close(F);
}
close(VALID1);
close(VALID2);
close(REPL);
close(TOT);
close(IND);