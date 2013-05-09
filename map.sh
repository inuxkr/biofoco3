HADOOP=/usr/local/hadoop/bin/hadoop

while read input
do
  $HADOOP dfs -copyToLocal $input $input
  $HADOOP dfs -copyToLocal chr1.fa.3283 chr1.fa.3283
  /bio/bowtie/bowtie.sh chr1.fa.3283 $input chr1.fa-align.sam.3283
  $HADOOP dfs -moveFromLocal chr1.fa-align.sam.3283 chr1.fa-align.sam.3283
  rm -f chr1.fa.3283
  rm -f $input 
done
