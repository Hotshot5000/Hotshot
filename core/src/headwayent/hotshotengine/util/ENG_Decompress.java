/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/27/21, 12:04 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Gdx;

public class ENG_Decompress {
    private final String _zipFile;
    private final String _location;

    public ENG_Decompress(String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;

        _dirChecker("");
    }

    public boolean unzip() {
        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            byte[] buffer = new byte[1024 * 1024 * 2];
            Gdx.app.log("Decompress", "Decompressing from " + _zipFile);
            while ((ze = zin.getNextEntry()) != null) {
                Gdx.app.log("Decompress", "Unzipping " + _location + ze.getName());
                //Log.v("Decompress", "Unzipping " + ze.getName());

                if (ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    File file = new File(_location + ze.getName());
                    boolean delete = file.delete();
                    boolean newFile = file.createNewFile();
                    System.out.println("file deleted: " + delete + " newFile: " + newFile);
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    int size;


                    BufferedOutputStream bufferOut = new BufferedOutputStream(fout, buffer.length);

                    while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
                        bufferOut.write(buffer, 0, size);
                    }

                    bufferOut.flush();
                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
//			Log.e("Decompress", "unzip", e);
            Gdx.app.log("Decompress", "unzip", e);
            return false;
        }
        return true;

    }

    private void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}
