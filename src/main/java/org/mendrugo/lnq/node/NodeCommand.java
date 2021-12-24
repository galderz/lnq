package org.mendrugo.lnq.node;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.net.URI;
import java.net.URL;

@Command(name = "node", aliases = {"n"}, description = "Node")
public class NodeCommand implements Runnable
{
    // TODO lnport
    // TODO rpcport
    // TODO datadir
    // TODO config

    @Option(
        names = {
            "-b"
            , "--bitcoin"
        }
        , description = "Bitcoin RPC endpoint"
        , defaultValue = "http://user:pass@localhost:18443"
    )
    URL bitcoinUrl;

    @Inject
    Node node;

    @Override
    public void run()
    {
        System.out.println("Node");

        var userInfo = bitcoinUrl.getUserInfo().split(":");
        var nodeArgs = new NodeArgs(
            userInfo[0]
            , userInfo[1]
            , bitcoinUrl.getHost()
            , bitcoinUrl.getPort()
        );
        node.accept(nodeArgs);
    }
}
