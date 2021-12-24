package org.acme.getting.started.commandmode;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import org.mendrugo.lnq.node.NodeCommand;
import picocli.CommandLine.Command;

import javax.inject.Inject;

@TopCommand
@Command(mixinStandardHelpOptions = true, subcommands = {NodeCommand.class, GoodByeCommand.class})
public class EntryCommand
{
}

@Command(name = "hello", aliases = {"h"}, description = "Greet World!")
class HelloCommand implements Runnable
{
    @Inject
    GreetingService service;

    @Override
    public void run()
    {
        System.out.println("Hello World!");
        System.out.println(service.greeting("Hello"));
    }
}

@Command(name = "goodbye", aliases = {"g"}, description = "Say goodbye to World!")
class GoodByeCommand implements Runnable
{
    @Inject
    GreetingService service;

    @Override
    public void run()
    {
        System.out.println("Goodbye World!");
        System.out.println(service.greeting("Goodbye"));
    }
}
