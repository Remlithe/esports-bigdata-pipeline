set -e

echo "1. Tworzenie struktury katalogow Airflow..."
mkdir -p ~/airflow/dags/project_files

echo "2. Kompilacja MapReduceApp.java i budowanie mapreduce.jar..."
javac -cp $(hadoop classpath) src/MapReduceApp.java -d .
jar cf ~/airflow/dags/project_files/mapreduce.jar MapReduceApp*.class
rm -f MapReduceApp*.class

echo "3. Kopiowanie DAG-a oraz skryptow Hive..."
cp airflow/dags/projekt1.py ~/airflow/dags/
cp airflow/dags/project_files/* ~/airflow/dags/project_files/

echo "4. Ladowanie probki danych (matches00-10 + players) do HDFS..."
hadoop fs -rm -r -f /tmp/input
hadoop fs -mkdir -p /tmp
hadoop fs -put data/input /tmp/

echo "Gotowe! Mozesz uruchomic DAG 'project1-workflow' w interfejsie Airflow."