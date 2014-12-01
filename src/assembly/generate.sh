#!/bin/sh

PROTO_DIR=../main/resources/app
SRC_DIR=../main/java

rm -rf $SRC_DIR/com/demo2do/entity/protocol/proto/common
mkdir -p $SRC_DIR/com/demo2do/entity/protocol/proto/common

protoc --proto_path=$PROTO_DIR/common --java_out=$SRC_DIR $PROTO_DIR/common/common.proto
protoc --proto_path=$PROTO_DIR/user --proto_path=$PROTO_DIR/common --java_out=$SRC_DIR $PROTO_DIR/user/user.proto
protoc --proto_path=$PROTO_DIR --proto_path=$PROTO_DIR/user --proto_path=$PROTO_DIR/common --java_out=$SRC_DIR $PROTO_DIR/app.proto
