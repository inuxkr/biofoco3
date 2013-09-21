#!/bin/sh

HADOOP_DIR=/home/ubuntu/hadoop-1.0.3
HADOOP=/home/ubuntu/hadoop-1.0.3/bin/hadoop
tmp=`echo $$`

# tamanho dos cromossomos do genoma
genome=$1

# nome do tamanho dos cromossomos do genoma no HDFS
Hgenome=`echo $genome | awk -F '/' '{ print $NF }'`
Hgenome=$Hgenome.$tmp

# parametro de intervalo para o genome2interval
interval=$2

# saida com intervalos
int_output=$3
Hint_output=`echo $int_output | awk -F '/' '{ print $NF }'`
Hint_output=$Hint_output.$tmp

# arquivo de entrada para o Hadoop Streaming
input="input.$tmp.txt"
echo $Hgenome >> $input

# subimos para o HDFS todos os arquivos de entrada
$HADOOP dfs -put $genome $Hgenome
$HADOOP dfs -put $input $input

# cria script para ser executado no hadoop
cat > map.sh << EOF
HADOOP=/home/ubuntu/hadoop-1.0.3/bin/hadoop

while read input
do
  \$HADOOP dfs -copyToLocal \$input \$input
  /home/ubuntu/bowtie-0.12.9/genome2interval.sh \$input $interval $Hint_output
  \$HADOOP dfs -moveFromLocal $Hint_output $Hint_output
  rm -f \$input
done
EOF

# executa o job no Hadoop
$HADOOP jar $HADOOP_DIR/contrib/streaming/hadoop-streaming-1.0.3.jar \
        -Dmapred.map.tasks=1 -file map.sh -mapper map.sh -reducer NONE \
        -input $input -output output.$tmp

# baixa a saisa (intervalos) e remove do HDFS
$HADOOP dfs -copyToLocal $Hint_output $int_output
$HADOOP dfs -rm $Hint_output

# remove arquivos usados durante a execucao
$HADOOP dfs -rmr output.$tmp
$HADOOP dfs -rm $input
$HADOOP dfs -rm $Hgenome
rm -f $input
rm -f map.sh