#!/usr/bin/env bash

set -x -e

curl --data-binary '{"jsonrpc":"1.0","id":"1","method":"getnetworkinfo","params":[]}' \
  http://'foo:qDDZdeQ5vw9XXFeVnXT4PZ--tGN2xNjjR4nrtyszZx0='@127.0.0.1:18443 \
  | jq
