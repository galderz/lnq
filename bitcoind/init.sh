#!/usr/bin/env bash

set -x -e

# Create wallet, unload and reload w/ autoload
curl --data-binary '{"jsonrpc":"1.0","id":"1","method":"createwallet","params":["default"]}' \
  http://'foo:qDDZdeQ5vw9XXFeVnXT4PZ--tGN2xNjjR4nrtyszZx0='@127.0.0.1:18443 \
  | jq

curl --data-binary '{"jsonrpc":"1.0","id":"1","method":"unloadwallet","params":["default"]}' \
  http://'foo:qDDZdeQ5vw9XXFeVnXT4PZ--tGN2xNjjR4nrtyszZx0='@127.0.0.1:18443 \
  | jq

curl --data-binary '{"jsonrpc":"1.0","id":"1","method":"loadwallet","params":["default", true]}' \
  http://'foo:qDDZdeQ5vw9XXFeVnXT4PZ--tGN2xNjjR4nrtyszZx0='@127.0.0.1:18443 \
  | jq

a_mine=`curl --data-binary '{"jsonrpc":"1.0","id":"1","method":"getnewaddress","params":[]}' \
  http://'foo:qDDZdeQ5vw9XXFeVnXT4PZ--tGN2xNjjR4nrtyszZx0='@127.0.0.1:18443 \
  | jq .result`

# Advance 101 blocks
curl --data-binary '{"jsonrpc":"1.0","id":"1","method":"generatetoaddress","params":[101, '"$a_mine"']}' \
  http://'foo:qDDZdeQ5vw9XXFeVnXT4PZ--tGN2xNjjR4nrtyszZx0='@127.0.0.1:18443 \
  | jq
