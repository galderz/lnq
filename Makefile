SHELL := bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DELETE_ON_ERROR:
MAKEFLAGS += --warn-undefined-variables
MAKEFLAGS += --no-builtin-rules

ifeq ($(origin .RECIPEPREFIX), undefined)
  $(error This Make does not support .RECIPEPREFIX. Please use GNU Make 4.0 or later)
endif
.RECIPEPREFIX = >

BITCOIN_HOME = /opt/bitcoin/src
# Release config considerably faster than fastdebug for quarkus dev mode
JAVA_HOME = /opt/java-17
LR_HOME = /opt/lnrod/target/debug

print = echo '$(1)'
comma := ,

#ifdef DEBUG
#  log_level = debug
#endif

bc := $(BITCOIN_HOME)/bitcoin-cli
bc += -regtest
bd += $(BITCOIN_HOME)/bitcoind
#byteman_jar := $(HOME)/.m2/repository/org/jboss/byteman/byteman/4.0.18/byteman-4.0.18.jar
java += $(JAVA_HOME)/bin/java
#java += -javaagent:$(byteman_jar)=boot:$(byteman_jar)$(comma)script:$(resources_dir)/$(log_level).btm
mvn += LD_PRELOAD=/usr/lib64/clang/13/lib/libclang_rt.asan-x86_64.so
mvn += ASAN_OPTIONS=detect_leaks=0
mvn += JAVA_HOME=$(JAVA_HOME)
mvn += /opt/maven/bin/mvn
resources_dir := src/main/resources

ifdef TRACE
  mvn += -X
  mvn += -Dquarkus.log.level=TRACE
  mvn += -Dquarkus.log.min-level=TRACE
  java += -Dorg.jboss.byteman.verbose=true
endif

dev:
> rm -drf data
> $(mvn) --batch-mode clean compile quarkus:dev -Dquarkus.console.color=false -DdebugHost=0.0.0.0
.PHONY: dev

run:
> $(java) -jar target/quarkus-app/quarkus-run.jar
.PHONY: run

jar:
> $(mvn) -DskipTests package
.PHONY: jar

mine:
> ./mine.sh
.PHONY: mine

lnrod1:
> cd $(LR_HOME)
> rm -drf data
> ./lnrod --regtest
.PHONY: lnrod1

lnrod2:
> cd $(LR_HOME)
> rm -drf data2
> ./lnrod --regtest --datadir ./data2 --rpcport 8802 --lnport 9902
.PHONY: lnrod2

info:
> curl -w "\n" http://localhost:8080/node/info
.PHONY: info

connect: info
> curl -X POST http://localhost:8080/peers/connect/${shell $(LR_HOME)/lnrcli -c http://127.0.0.1:8802 node info | jq -r .node_id}/127.0.0.1/9902
.PHONY: connect

peers:
> curl -w "\n" http://localhost:8080/peers
.PHONY: peers

channel: peers
> curl -v -X POST http://localhost:8080/channels/new/${shell $(LR_HOME)/lnrcli -c http://127.0.0.1:8802 node info | jq -r .node_id}/1000000
.PHONY: channel

stop:
> $(bc) stop
.PHONY: stop

# Building LDK Java bindings
#
# If any issues, check:
# https://github.com/lightningdevkit/ldk-garbagecollected/blob/main/.github/workflows/build.yml
#
# It Requires cbindgen, e.g.
# $ cargo install cbindgen

LDK_BIND_C_HOME := /opt/ldk-c-bindings
LDK_BIND_GC_HOME := /opt/ldk-garbagecollected
RUST_LN_HOME := /opt/matt-rust-lightning

LDK_C_BRANCH ?= 0.0.104
LDK_GC_BRANCH ?= main
BINDINGS_BRANCH ?= 2021-03-java-bindings-base

ldk_jni_so = $(LDK_GC_HOME)/liblightningjni.so

$(LDK_BIND_C_HOME):
> cd /opt
> git clone https://github.com/lightningdevkit/ldk-c-bindings

$(LDK_BIND_GC_HOME):
> cd /opt
> git clone https://github.com/lightningdevkit/ldk-garbagecollected

$(RUST_LN_HOME):
> cd /opt
> git clone https://git.bitcoin.ninja/rust-lightning matt-rust-lightning

update-bindings: update-rust-ln update-ldk-bind-c update-ldk-bind-gc build-bindings
.PHONY: update-bindings

build-bindings: build-ldk-bind-c build-ldk-bind-gc
.PHONY: build-bindings

build-ldk-bind-gc:
> cd $(LDK_BIND_GC_HOME)
> PATH=$(JAVA_HOME)/bin:$(PATH) ./genbindings.sh $(LDK_BIND_C_HOME) "-I/opt/java-11/include/ -I/opt/java-11/include/linux/" true false
> $(mvn) -DskipTests install
> ln -s liblightningjni_debug_x86_64-redhat-linux-gnu.so liblightningjni.so
.PHONY: build-ldk-bind-gc

build-ldk-bind-c:
> cd $(LDK_BIND_C_HOME)
> ./genbindings.sh $(RUST_LN_HOME) true
.PHONY: build-ldk-bind-c

update-rust-ln: $(RUST_LN_HOME)
> cd $(RUST_LN_HOME)
> git fetch origin
> git checkout origin/$(BINDINGS_BRANCH)
.PHONY: update-rust-ln

update-ldk-bind-c: $(LDK_BIND_C_HOME)
> cd $(LDK_BIND_C_HOME)
> git fetch --all --tags -f
> git checkout origin/$(LDK_C_BRANCH)
.PHONY: update-ldk-bind-c

update-ldk-bind-gc: $(LDK_BIND_GC_HOME)
> cd $(LDK_BIND_GC_HOME)
> git fetch --all --tags -f
> git checkout origin/$(LDK_GC_BRANCH)
.PHONY: update-ldk-bind-gc

reset-bindings:
> cd $(LDK_BIND_C_HOME)
> git checkout -f
> git reset --hard HEAD
> git clean -f -d
> cd $(LDK_BIND_GC_HOME)
> git checkout -f
> git reset --hard HEAD
> git clean -f -d
> cd $(RUST_LN_HOME)
> git checkout -f
> git reset --hard HEAD
> git clean -f -d
.PHONY: reset-bindings

clean-bindings:
> rm -drf $(LDK_BIND_C_HOME)
> rm -drf $(LDK_BIND_GC_HOME)
> rm -drf $(RUST_LN_HOME)
.PHONY: clean-bindings

get-ldk-jars:
> scp -r $(REMOTE):.m2/repository/org/lightningdevkit $(HOME)/.m2/repository/org
.PHONY: get-ldk-jars
