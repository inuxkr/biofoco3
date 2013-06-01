#!/bin/sh

HADOOP_DIR=/usr/local/hadoop
tmp=`echo $$`

# alinhamento em formato BED
align=$1

# alinhamento em formato BED  no HDFS
Halign=`echo $align | awk -F '/' '{ print $NF }'`
Halign=$Halign.$tmp

# intervalo
interval=$2

# intervalo no HDFS
Hinterval=`echo $interval | awk -F '/' '{ print $NF }'`
Hinterval=$Hinterval.$tmp

# saida com cobertura
coverage=$3
Hcoverage=`echo $coverage | awk -F '/' '{ print $NF }'`
Hcoverage=$Hcoverage.$tmp

# arquivo de entrada para o Hadoop Streaming
input="input.$tmp.txt"
echo $Halign >> $input

# subimos para o HDFS todos os arquivos de entrada
hadoop dfs -put $align $Halign
hadoop dfs -put $interval $Hinterval
hadoop dfs -put $input $input

# cria script para ser executado no hadoop
cat > map.sh << EOF
HADOOP=/usr/local/hadoop-0.20.203.0/bin/hadoop

while read input
do
  \$HADOOP dfs -copyToLocal \$input \$input
  \$HADOOP dfs -copyToLocal $Hinterval $Hinterval
  /usr/local/bowtie/coverageBed.sh \$input $Hinterval $Hcoverage
  \$HADOOP dfs -moveFromLocal $Hcoverage $Hcoverage
  rm -f \$input 
  rm -f $Hinterval 
done
EOF

# executa o job no Hadoop
hadoop jar $HADOOP_DIR/contrib/streaming/hadoop-streaming-0.20.203.0.jar \
	-Dmapred.map.tasks=1 -file map.sh -mapper map.sh -reducer NONE \
	-input $input -output output.$tmp

# baixa a saisa (cobertura) e remove do HDFS
hadoop dfs -copyToLocal $Hcoverage $coverage
hadoop dfs -rm $Hcoverage

# remove arquivos usados durante a execucao
hadoop dfs -rmr output.$tmp
hadoop dfs -rm $input
hadoop dfs -rm $Halign
hadoop dfs -rm $Hinterval
rm -f $input
rm -f map.sh

