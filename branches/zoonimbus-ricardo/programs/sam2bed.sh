#!/bin/sh

HADOOP_DIR=/home/ubuntu/hadoop-1.0.3
HADOOP=/home/ubuntu/hadoop-1.0.3/bin/hadoop
tmp=`echo $$`

# alinhamento no formato SAM
align=$1

# nome do genoma de referencia no HDFS
Halign=`echo $align | awk -F '/' '{ print $NF }'`
Halign=$Halign.$tmp

# saida com alinhamento no formato BED
bedfile=$2
bedfile_clean=`echo $bedfile | awk -F '/' '{ print $NF }'`
Hbedfile=`echo $bedfile | awk -F '/' '{ print $NF }'`
Hbedfile=$Hbedfile.$tmp

# arquivo de entrada para o Hadoop Streaming
input="input.$tmp.txt"
echo $Halign >> $input

# subimos para o HDFS todos os arquivos de entrada
$HADOOP dfs -put $align $Halign
$HADOOP dfs -put $input $input

# cria script para ser executado no hadoop
cat > map.sh << EOF
HADOOP=/home/ubuntu/hadoop-1.0.3/bin/hadoop

while read input
do
  \$HADOOP dfs -copyToLocal \$input \$input
  /home/ubuntu/bowtie-0.12.9/sam2bed.sh \$input $Hbedfile
  \$HADOOP dfs -moveFromLocal $Hbedfile $Hbedfile
  rm -f \$input
done
EOF

# executa o job no Hadoop
$HADOOP jar $HADOOP_DIR/contrib/streaming/hadoop-streaming-1.0.3.jar \
        -Dmapred.map.tasks=1 -file map.sh -mapper map.sh -reducer NONE \
        -input $input -output output.$tmp

# baixa a saida (alinhamento em BED) e remove do HDFS
$HADOOP dfs -copyToLocal $Hbedfile $bedfile
$HADOOP dfs -rm $Hbedfile
$HADOOP fs -copyFromLocal $bedfile $bedfile_clean

# remove arquivos usados durante a execucao
$HADOOP dfs -rmr output.$tmp
$HADOOP dfs -rm $input
$HADOOP dfs -rm $Halign
rm -f $input
rm -f map.sh
