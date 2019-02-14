package com.weechan.asr.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2019/1/31 12:50
 */

public class IOUtils {


    public static void extraFile(InputStream zipFileInputSream, File destFolder) {
        if (!destFolder.exists()) {
            if (!destFolder.mkdir()) {
                throw new RuntimeException("Cannot create file " + destFolder.getAbsolutePath());
            }
        }

        if (destFolder.isFile()) {
            throw new RuntimeException("destPath must be a directory");
        }

        try {
            ZipInputStream zins = new ZipInputStream(zipFileInputSream);
            ZipEntry zipEntry = null;
            int length;

            byte[] buf = new byte[1024 * 1024];
            while ((zipEntry = zins.getNextEntry()) != null) {

                if (zipEntry.isDirectory()) {
                    File dir = new File(destFolder, zipEntry.getName());
                    if (!dir.exists()) {
                        boolean result = dir.mkdir();
                        if (!result)
                            Log.e("ZipERROR", "CANNOT CREATE FOLDER " + dir.getAbsolutePath());
                    }
                    continue;
                }

                File destFile = new File(destFolder, zipEntry.getName());
                if(!destFile.getParentFile().exists()){
                    boolean result = destFile.getParentFile().mkdirs();
                    if(!result)
                        throw new RuntimeException("Cannot Create Parent File " + destFile.getParentFile().getAbsolutePath());
                }

                BufferedOutputStream zout =
                        new BufferedOutputStream(new FileOutputStream(destFile));

                while ((length = zins.read(buf)) != -1) {
                    zout.write(buf, 0, length);
                }
                zout.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extraFile(File zipFile, File destFolder) {
        try {
            extraFile(new BufferedInputStream(new FileInputStream(zipFile)), destFolder);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
