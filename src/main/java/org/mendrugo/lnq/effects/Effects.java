package org.mendrugo.lnq.effects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

@ApplicationScoped
public class Effects
{
    @PostConstruct
    public void onStartup()
    {
        System.out.println("Effects.onStartup()");
    }

    public byte[] seed()
    {
        byte[] seed = new byte[32];
        new Random().nextBytes(seed);
        return seed;
    }

    public long time()
    {
        return System.currentTimeMillis();
    }

    public void persist(final byte[] data, Path path, Path dataDir) throws IOException
    {
        final File file = dataDir.resolve(path).toFile();
        if (file.exists() || file.getParentFile().mkdirs() && file.createNewFile())
        {
            try(var out = new FileOutputStream(file))
            {
                out.write(data);
            }
        }
    }

    public byte[][] readDirectory(String dirName, Path dataDir)
    {
        final File directory = dataDir.resolve(dirName).toFile();
        if (directory.exists() && directory.isDirectory()) {
            return Arrays.stream(directory.listFiles())
                .map(File::toPath)
                .map(path -> {
                    try {
                        return (Files.readAllBytes(path));
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(byte[][]::new);
        }
        return new byte[][]{};
    }

    public byte[] readAllBytes(String fileName, Path dataDir)
    {
        final Path filePath = dataDir.resolve(fileName);
        final File file = filePath.toFile();
        if (file.exists() && file.isFile()) {
            try
            {
                return Files.readAllBytes(filePath);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
        }

        return new byte[]{};
    }
}
