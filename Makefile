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
JAVA_HOME = /opt/adopt-17
LR_HOME = /opt/lnrod

print = echo '$(1)'
comma := ,

#ifdef DEBUG
#  log_level = debug
#endif

bc := $(BITCOIN_HOME)/bitcoin-cli
bc += -regtest
bd += $(BITCOIN_HOME)/bitcoind
bd += -regtest
#byteman_jar := $(HOME)/.m2/repository/org/jboss/byteman/byteman/4.0.18/byteman-4.0.18.jar
java += $(JAVA_HOME)/bin/java
#java += -javaagent:$(byteman_jar)=boot:$(byteman_jar)$(comma)script:$(resources_dir)/$(log_level).btm
mvn += LD_PRELOAD=/usr/lib64/clang/13/lib/libclang_rt.asan-x86_64.so
mvn += ASAN_OPTIONS=detect_leaks=0
mvn += JAVA_HOME=$(JAVA_HOME)
mvn += ./mvnw
resources_dir := src/main/resources

ifdef TRACE
  mvn += -X
  mvn += -Dquarkus.log.level=TRACE
  mvn += -Dquarkus.log.min-level=TRACE
  java += -Dorg.jboss.byteman.verbose=true
endif

dev:
> $(mvn) clean compile quarkus:dev
.PHONY: dev

run:
> $(java) -jar target/quarkus-app/quarkus-run.jar
.PHONY: run

jar:
> $(mvn) -DskipTests package
.PHONY: jar

mine:
> $(bc) createwallet default || true
> $(bc) unloadwallet default
> $(bc) loadwallet default true
> $(bc) generatetoaddress 101 $(shell $(bc) getnewaddress)
.PHONY: mine

daemon:
> $(bd) -daemon -datadir=$(HOME)/.bitcoin
.PHONY: daemon

lnrod1:
> cd $(LR_HOME)
> ./lnrod --regtest
.PHONY: lnrod1

lnrod2:
> cd $(LR_HOME)
> ./lnrod --regtest --datadir ./data2 --rpcport 8802 --lnport 9902
.PHONY: lnrod2

test: info connect peers

info:
> curl -w "\n" http://localhost:8080/node/info
.PHONY: info

connect:
> curl -X POST http://localhost:8080/peers/connect/${shell $(LR_HOME)/lnrcli -c http://127.0.0.1:8802 node info | jq -r .node_id}/127.0.0.1/9902
.PHONY: connect

peers:
> curl -w "\n" http://localhost:8080/peers
.PHONY: peers

stop:
> $(bc) stop
.PHONY: stop
