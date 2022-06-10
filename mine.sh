#!/usr/bin/env bash

set -e -x

mine()
{
    local bc=/opt/bitcoin/src/bitcoin-cli

    $bc --regtest createwallet default
    $bc --regtest unloadwallet default
    $bc --regtest loadwallet default true

    a_mine=`$bc -regtest getnewaddress` && echo $a_mine

    # Advance 101 blocks
    $bc -regtest generatetoaddress 101 $a_mine
}

mine
