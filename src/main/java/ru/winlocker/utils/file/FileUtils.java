package ru.winlocker.utils.file;

import org.apache.commons.lang.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public class FileUtils {

    public static boolean copyFile(File toCopy, File destFile) {
        try {
            return FileUtils.copyStream(new FileInputStream(toCopy), new FileOutputStream(destFile));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyFilesRecursively(File toCopy, File destDir) {
        assert destDir.isDirectory();

        if (!toCopy.isDirectory()) {
            return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
        } else {
            File newDestDir = new File(destDir, toCopy.getName());

            if (!newDestDir.exists() && !newDestDir.mkdir()) {
                return false;
            }

            for (File child : Objects.requireNonNull(toCopy.listFiles())) {
                if (!FileUtils.copyFilesRecursively(child, newDestDir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyJarResourcesRecursively(File destDir, JarURLConnection jarConnection) throws IOException {
        JarFile jarFile = jarConnection.getJarFile();

        for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
            JarEntry entry = e.nextElement();

            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                String filename = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());

                if (!entry.isDirectory()) {
                    try (InputStream entryInputStream = jarFile.getInputStream(entry)) {
                        copyStream(entryInputStream, new File(destDir, filename));
                    }
                } else {
                    ensureDirectoryExists(new File(destDir, filename));
                }
            }
        }
        return true;
    }

    public static boolean copyResourcesRecursively(URL originUrl, File destination) {
        try {
            URLConnection urlConnection = originUrl.openConnection();

            if (urlConnection instanceof JarURLConnection) {
                return copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
            } else {
                return copyFilesRecursively(new File(originUrl.getPath()), destination);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(final InputStream is, final File f) {
        try {
            return FileUtils.copyStream(is, new FileOutputStream(f));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(final InputStream is, final OutputStream os) {
        try {
            final byte[] buf = new byte[1024];

            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean ensureDirectoryExists(final File f) {
        return f.exists() || f.mkdir();
    }
}
