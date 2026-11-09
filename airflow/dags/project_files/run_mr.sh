#!/bin/bash
INPUT_DIR=$1
OUTPUT_DIR=$2

hadoop fs -rm -r -f "$OUTPUT_DIR"
hadoop jar "$(dirname "$0")/mapreduce.jar" MapReduceApp "$INPUT_DIR" "$OUTPUT_DIR"
