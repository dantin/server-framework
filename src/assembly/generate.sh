#!/bin/sh

PROTO_DIR=../main/resources/proto
SRC_DIR=../main/java

rm -rf $SRC_DIR/com/demo2do/entity/protocol/proto/common
mkdir -p $SRC_DIR/com/demo2do/entity/protocol/proto/common

protoc --proto_path=$PROTO_DIR --java_out=$SRC_DIR $PROTO_DIR/Common.proto
protoc --proto_path=$PROTO_DIR --java_out=$SRC_DIR $PROTO_DIR/User.proto
protoc --proto_path=$PROTO_DIR --java_out=$SRC_DIR $PROTO_DIR/AppPb.proto
