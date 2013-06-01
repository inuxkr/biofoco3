#!/bin/sh

HADOOP_DIR=/usr/local/hadoop
tmp=`echo $$`

# alinhamento no formato SAM
align=$1

# nome do genoma de referencia no HDFS
Halign=`echo $align | awk -F '/' '{ print $NF }'`
Halign=$Halign.$tmp

# saida com alinhamento no formato BED
bedfile=$2
Hbedfile=`echo $bedfile | awk -F '/' '{ print $NF }'`
Hbedfile=$Hbedfile.$tmp

# arquivo de entrada para o Hadoop Streaming
input="input.$tmp.txt"
echo $Halign >> $input

# subimos para o HDFS todos os arquivos de entrada
hadoop dfs -put $align $Halign
hadoop dfs -put $input $input

# cria script para ser executado no hadoop
cat > map.sh << EOF
HADOOP=/usr/local/hadoop-0.20.203.0/bin/hadoop

while read input
do
  \$HADOOP dfs -copyToLocal \$input \$input
  /usr/local/bowtie/sam2bed.sh \$input $Hbedfile
  \$HADOOP dfs -moveFromLocal $Hbedfile $Hbedfile
  rm -f \$input 
done
EOF

# executa o job no Hadoop
hadoop jar $HADOOP_DIR/contrib/streaming/hadoop-streaming-0.20.203.0.jar \
	-Dmapred.map.tasks=1 -file map.sh -mapper map.sh -reducer NONE \
	-input $input -output output.$tmp

# baixa a saida (alinhamento em BED) e remove do HDFS
hadoop dfs -copyToLocal $Hbedfile $bedfile
hadoop dfs -rm $Hbedfile

# remove arquivos usados durante a execucao
hadoop dfs -rmr output.$tmp
hadoop dfs -rm $input
hadoop dfs -rm $Halign
rm -f $input
rm -f map.sh

