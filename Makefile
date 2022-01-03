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
JAVA_HOME = /opt/java-17

bc := $(BITCOIN_HOME)/bitcoin-cli
bc += -regtest
bd += $(BITCOIN_HOME)/bitcoind
bd += -regtest
mvn += JAVA_HOME=$(JAVA_HOME)
mvn += ./mvnw

ifdef TRACE
  mvn += -Dquarkus.log.level=TRACE
  mvn += -Dquarkus.log.min-level=TRACE
endif

dev:
> $(mvn) clean compile quarkus:dev
.PHONY: dev

mine:
> $(bc) createwallet default || true
> $(bc) unloadwallet default
> $(bc) loadwallet default true
> $(bc) generatetoaddress 101 $(shell $(bc) getnewaddress)
.PHONY: mine

daemon:
> $(bd) -daemon -datadir=$(HOME)/.bitcoin
.PHONY: daemon

stop:
> $(bc) stop
.PHONY: stop
