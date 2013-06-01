#!/bin/sh

HADOOP_DIR=/usr/local/hadoop
tmp=`echo $$`

# genoma de referencia
genome=$1

# nome do genoma de referencia no HDFS
Hgenome=`echo $genome | awk -F '/' '{ print $NF }'`
Hgenome=$Hgenome.$tmp

# arquivo com reads para serem alinhadas
reads=$2

# nome do arquivo com reads no HDFS
Hreads=`echo $reads | awk -F '/' '{ print $NF }'`
Hreads=$Hreads.$tmp

# saida com alinhamentos no formato SAM
align=$3
Halign=`echo $align | awk -F '/' '{ print $NF }'`
Halign=$Halign.$tmp

# arquivo de entrada para o Hadoop Streaming
input="input.$tmp.txt"
echo $Hreads >> $input

# subimos para o HDFS todos os arquivos de entrada
hadoop dfs -put $genome $Hgenome
hadoop dfs -put $reads $Hreads
hadoop dfs -put $input $input

# cria script para ser executado no hadoop
cat > map.sh << EOF
HADOOP=/usr/local/hadoop-0.20.203.0/bin/hadoop

while read input
do
  \$HADOOP dfs -copyToLocal \$input \$input
  \$HADOOP dfs -copyToLocal $Hgenome $Hgenome
  /usr/local/bowtie/bowtie.sh $Hgenome \$input $Halign
  \$HADOOP dfs -moveFromLocal $Halign $Halign
  rm -f $Hgenome
  rm -f \$input 
done
EOF

# executa o job no Hadoop
hadoop jar $HADOOP_DIR/contrib/streaming/hadoop-streaming-0.20.203.0.jar \
	-Dmapred.map.tasks=1 -file map.sh -mapper map.sh -reducer NONE \
	-input $input -output output.$tmp

# baixa a saida (alinhamento) e remove do HDFS
hadoop dfs -copyToLocal $Halign $align
hadoop dfs -rm $Halign

# remove arquivos usados durante a execucao
hadoop dfs -rmr output.$tmp
hadoop dfs -rm $input
hadoop dfs -rm $Hgenome
hadoop dfs -rm $Hreads
rm -f $input
rm -f map.sh

