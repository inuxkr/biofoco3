#!/bin/sh

HADOOP_DIR=/home/ubuntu/hadoop-1.0.3
HADOOP=/home/ubuntu/hadoop-1.0.3/bin/hadoop
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
coverage_clean=`echo $align | awk -F '/' '{ print $NF }'`
Hcoverage=`echo $coverage | awk -F '/' '{ print $NF }'`
Hcoverage=$Hcoverage.$tmp

# arquivo de entrada para o Hadoop Streaming
input="input.$tmp.txt"
echo $Halign >> $input

# subimos para o HDFS todos os arquivos de entrada
$HADOOP dfs -put $align $Halign
$HADOOP dfs -put $interval $Hinterval
$HADOOP dfs -put $input $input

# cria script para ser executado no hadoop
cat > map.sh << EOF
HADOOP=/home/ubuntu/hadoop-1.0.3/bin/hadoop

while read input
do
  \$HADOOP dfs -copyToLocal \$input \$input
  \$HADOOP dfs -copyToLocal $Hinterval $Hinterval
  /home/ubuntu/bowtie-0.12.9/coverageBed.sh \$input $Hinterval $Hcoverage
  \$HADOOP dfs -moveFromLocal $Hcoverage $Hcoverage
  rm -f \$input
  rm -f $Hinterval
done
EOF

# executa o job no Hadoop
$HADOOP jar $HADOOP_DIR/contrib/streaming/hadoop-streaming-1.0.3.jar \
        -Dmapred.map.tasks=1 -file map.sh -mapper map.sh -reducer NONE \
        -input $input -output output.$tmp

# baixa a saisa (cobertura) e remove do HDFS
$HADOOP dfs -copyToLocal $Hcoverage $coverage
$HADOOP dfs -rm $Hcoverage
$HADOOP fs -copyFromLocal $coverage $coverage_clean

# remove arquivos usados durante a execucao
$HADOOP dfs -rmr output.$tmp
$HADOOP dfs -rm $input
$HADOOP dfs -rm $Halign
$HADOOP dfs -rm $Hinterval
rm -f $input
rm -f map.sh
