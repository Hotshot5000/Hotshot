/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/24/18, 10:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.util;

import com.badlogic.gdx.files.FileHandle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ENG_Compress {

    private final FileHandle fileToCompress;
    private final FileHandle destination;

    public ENG_Compress(FileHandle fileToCompress, FileHandle destination) {
        this.fileToCompress = fileToCompress;
        this.destination = destination;
    }

    public boolean zip() {
        byte[] buffer = new byte[1024 * 1024 * 2];

        try {

            FileOutputStream fos = new FileOutputStream(destination.file());
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(fileToCompress.name());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(fileToCompress.file());

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
