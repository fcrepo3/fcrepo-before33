
$memtotal=99942400;

opendir(D, ".");
@fnames=grep(/log/, readdir(D));
closedir(D);
$c=1;
$memtotal=99942400;
open(IND, ">xmlindexing.dat");
open(TOT, ">total.dat");
open(REPL, ">replication.dat");
open(VALID1, ">valid1.dat");
open(VALID2, ">valid2.dat");
open(MEM, ">memory.dat");
$max=0;
$maxmemused=0;
$mst=0;
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
          $mst+=$tt;
          if ($tt>$max) {
            $max=$tt;
          }
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
          $mst+=$tot_tt;
          if ($tot_tt>$max) {
            $max=$tot_tt;
          }
      }
      if ($line=~/Attempting/) {
          @f=split(/\"/, $line);
          $repl_st=@f[1];  
          $valid2_et=@f[1];          
          $valid2_tt=$valid2_et-$valid2_st;
          print VALID2 "$c $valid2_tt\n";        
          $mst+=$valid2_tt;
          if ($valid2_tt>$max) {
            $max=$valid2_tt;
          }
      }
      if ($line=~/Entering update/) {
          @f=split(/\"/, $line);
          $repl_et=@f[1];          
          $repl_tt=$repl_et-$repl_st;
          print REPL "$c $repl_tt\n";
          $mst+=$repl_tt;
          if ($repl_tt>$max) {
            $max=$repl_tt;
          }
      }
      if ($line=~/Generated/) {
          @f=split(/\"/, $line);
          $valid2_st=@f[1];  
          $valid1_et=@f[1];          
          $valid1_tt=$valid1_et-$tot_st;
          print VALID1 "$c $valid1_tt\n";
          $mst+=$valid1_tt;
          if ($valid1_tt>$max) {
            $max=$valid1_tt;
          }                
      }
      if ($line=~/Memory/) {
          @f=split(/Memory: /, $line);
          $s=@f[1];
          @f=split(/ /, $s);
          $freemem=@f[0];
          $memused=$memtotal-$freemem;
          print MEM "$c $memused\n";                
          if ($memused>$maxmemused) {
            $maxmemused=$memused;
          }
      }

  }
  close(F);
}
close(VALID1);
close(VALID2);
close(REPL);
close(TOT);
close(IND);
close(MEM);

# do a re-scaled memory .dat file based on avg ($mst/$c)/4 from all others
$mult=(($mst/$c)/4)/$maxmemused;
open(SCMEM, ">memoryscaled.dat");
open(MEM, "memory.dat");
foreach $line (<MEM>) {
    $line=~s/\n//g;
    ($docNum, $bytes)=split(/ /, $line);
    $scaledBytes=$bytes*$mult;
    print SCMEM "$docNum $scaledBytes\n";
}
close(MEM);
close(SCMEM);
