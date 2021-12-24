package org.mendrugo.lnq.node;

import picocli.CommandLine.Command;

import javax.inject.Inject;

@Command(name = "node", aliases = {"n"}, description = "Node")
public class NodeCommand implements Runnable
{
    @Inject
    Node node;

    @Override
    public void run()
    {
        System.out.println("Node");
        node.run();
    }
}
