# Lightning Network Quarkus

A lightning network node implementation with Lightning Dev Kit and Quarkus.

## Testing

This section explains how to test things,
assuming that you've already done the first time setup:

Start bitcoind in regtest mode.

```bash
bitcoind -regtest -daemon
```

Verify that the blockchain is in the tip,
as per the initial setup:

```bash
bitcoin-cli getblockchaininfo
```

### Test 1: lnq to lnrod connectivity

Start a `lnrod` node:

```bash
lnrod --regtest --datadir ./data2 --rpcport 8802 --lnport 9902
```

Start `lnq` in quarkus dev mode:

```bash
./mvnw clean quarkus:dev
```

Check `lnq` can establish a peer connection to `lnrod`:

```bash
make peers
```

## First Time Setup

Build
[bitcoin][https://github.com/bitcoin/bitcoin]
with wallet support.
See [macOS step-by-step guide](https://blog.coincorner.com/bitcoin-core-development-on-macos-a-step-by-step-guide-5ecf8b17eb49).

Build
[lnrod][https://gitlab.com/lightning-signer/lnrod]
having installed Rust via `rustup`:

```bash
git clone git@gitlab.com:lightning-signer/lnrod.git
cd lnrod
cargo build
```

Add `bitcoind` configuration to your system's `bitcoin.conf`.
In linux, this is located in `~/.bitcoin/bitcoin.conf`,
but on macOS, this is in `~/Library/Application Support/Bitcoin/bitcoin.conf`.
Though you can instruct bitcoin daemon and bitcoin cli to use a different data directory,
things are smoother if you stick to your environment's default location:

```
rpcuser=user
rpcpassword=pass
fallbackfee=0.0000001
```

Start bitcoind in regtest mode:

```bash
bitcoind -regtest -daemon
```

Create wallet, unload and reload with autoload:

```bash
bitcoin-cli --regtest createwallet default
bitcoin-cli --regtest unloadwallet default
bitcoin-cli --regtest loadwallet default true
```

Mine initial blocks:

```bash
a_mine=`bitcoin-cli -regtest getnewaddress` && echo $a_mine
bitcoin-cli -regtest generatetoaddress 101 $a_mine
```
