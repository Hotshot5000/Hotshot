/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.vfs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by sebas on 15.09.2015.
 */
public class ENG_FileUtils {

    public enum FileCreationMode {
        NEW,
        OVERWRITE,
        APPEND,
        INCREMENT // add a number after the last character of the filename until a filename with that specific name is available in the folder
    }

    public enum FilePermission {
        READ, WRITE, READ_WRITE
    }

    public enum Compression {
        NONE, GZIP
    }

    private static OutputStream createOutputStream(String pathAndFilename, FileCreationMode creationMode, Compression compression, boolean virtualized) {
        boolean ok = true;
        OutputStream file = null;
        try {
//			File path = new File(Environment.getExternalStorageDirectory() +
//					File.separator + LOG_PATH + File.separator + LOG_FILENAME);
            File path;

            CreationParamsRet creationParamsRet = new CreationParamsRet(pathAndFilename, creationMode, virtualized).invoke();
            path = creationParamsRet.getPath();
            boolean append = creationParamsRet.isAppend();
            if (compression == Compression.NONE) {
                file = new FileOutputStream(path, append);
//                file = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileWriter(path, append), StandardCharsets.UTF_8)));
                //MainActivity.getInstance().openFileOutput(LOG_FILENAME, Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            } else if (compression == Compression.GZIP) {
                file = new GZIPOutputStream(new FileOutputStream(path, append));
            }
//        } catch (FileNotFoundException e) {
//
////			logActive = false;
//            MainApp.setFatalError();
//            ok = false;
        } catch (IOException e) {

            e.printStackTrace();
            //			logActive = false;
        }
        return file;
    }

    public static PrintWriter createOutputWriter(String path, String filename, FileCreationMode creationMode, Compression compression) {
        return createOutputWriter(path, filename, creationMode, compression, false);
    }

    public static PrintWriter createOutputWriter(String path, String filename, FileCreationMode creationMode, Compression compression, boolean virtualized) {
        return createOutputWriter(path + File.separator + filename, creationMode, compression, virtualized);
    }

    public static PrintWriter createOutputWriter(String pathAndFilename, FileCreationMode creationMode, Compression compression) {
        return createOutputWriter(pathAndFilename, creationMode, compression, false);
    }

    public static PrintWriter createOutputWriter(String pathAndFilename, FileCreationMode creationMode, Compression compression, boolean virtualized) {
        OutputStream outputStream = createOutputStream(pathAndFilename, creationMode, compression, virtualized);
        if (outputStream != null) {
            return new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));
        }
        return null;

    }

    public static DataOutputStream createBufferedOutputStream(String path, String filename, FileCreationMode creationMode, Compression compression) {
        return createBufferedOutputStream(path, filename, creationMode, compression, false);
    }

    public static DataOutputStream createBufferedOutputStream(String path, String filename, FileCreationMode creationMode, Compression compression, boolean virtualized) {
        return createBufferedOutputStream(path + File.separator + filename, creationMode, compression, virtualized);
    }

    public static DataOutputStream createBufferedOutputStream(String pathAndFilename, FileCreationMode creationMode, Compression compression) {
        return createBufferedOutputStream(pathAndFilename, creationMode, compression, false);
    }

    public static DataOutputStream createBufferedOutputStream(String pathAndFilename, FileCreationMode creationMode, Compression compression, boolean virtualized) {
        OutputStream outputStream = createOutputStream(pathAndFilename, creationMode, compression, virtualized);
        if (outputStream != null) {
            return new DataOutputStream(new BufferedOutputStream(outputStream));
        }
        return null;
    }

    public static void createNewFile(File path) throws IOException {
        path = new File(getVirtualizedFile(path.getPath()));
        if (path.getParentFile().isDirectory() || !path.getParentFile().exists()) {
            if (!path.getParentFile().mkdirs()) {
//                    return false;
//                throw new IOException("Cannot create dirs");
            }
        }
        if (!path.createNewFile()) {
//                    return false;
            throw new IOException("Cannot create new file");
        }
    }

    private static String getVirtualizedFile(String path) {
//        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            // each file is virtualized
//            ServerConnectionRequest serverConnectionRequest = MainApp.getMainThread().getApplicationSettings().serverConnectionRequest;
//            String baseName = FilenameUtils.getBaseName(path);
//            String extension = FilenameUtils.getExtension(path);
//            String finalName = baseName + "_" + serverConnectionRequest.getSessionName() + "_" +
//                    serverConnectionRequest.getSessionId() + "." + extension;
//            path = FilenameUtils.getPath(path) + finalName;
//        }
        return path;
    }

    private static InputStream createInputStream(String pathAndFilename, Compression compression, boolean virtualized) {
        File path = getFile(pathAndFilename, virtualized);
        if (!path.exists()) {
            return null;
        }
        try {
            if (compression == Compression.NONE) {
//                return new BufferedReader(new FileReader(path));
                return new FileInputStream(path);
            } else if (compression == Compression.GZIP) {
                return new GZIPInputStream(new FileInputStream(path));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader createInputReader(String path, String filename, Compression compression) {
        return createInputReader(path, filename, compression, false);
    }

    public static BufferedReader createInputReader(String path, String filename, Compression compression, boolean virtualized) {
        return createInputReader(path + File.separator + filename, compression, virtualized);
    }

    public static BufferedReader createInputReader(String pathAndFilename, Compression compression) {
        return createInputReader(pathAndFilename, compression, false);
    }

    public static BufferedReader createInputReader(String pathAndFilename, Compression compression, boolean virtualized) {
        InputStream inputStream = createInputStream(pathAndFilename, compression, virtualized);
        if (inputStream != null) {
            return new BufferedReader(new InputStreamReader(inputStream));
        }
        return null;

    }

    public static DataInputStream createBufferedInputStream(String path, String filename, Compression compression) {
        return createBufferedInputStream(path, filename, compression, false);
    }

    public static DataInputStream createBufferedInputStream(String path, String filename, Compression compression, boolean virtualized) {
        return createBufferedInputStream(path + File.separator + filename, compression, virtualized);
    }

    public static DataInputStream createBufferedInputStream(String pathAndFilename, Compression compression) {
        return createBufferedInputStream(pathAndFilename, compression, false);
    }

    public static DataInputStream createBufferedInputStream(String pathAndFilename, Compression compression, boolean virtualized) {
        InputStream inputStream = createInputStream(pathAndFilename, compression, virtualized);
        if (inputStream != null) {
            return new DataInputStream(new BufferedInputStream(inputStream));
        }
        return null;
    }

    public static File checkFilenameAndGenerateFile(String path, boolean allowOverwrite) {
        int num = 1;
        String newPath = path;
        int lastIndexOfExtensionDot = path.lastIndexOf(".");
        String pathWithoutExtension = lastIndexOfExtensionDot != -1 ? path.substring(0, lastIndexOfExtensionDot) : path;
        String extension = lastIndexOfExtensionDot != -1 ? path.substring(lastIndexOfExtensionDot) : "";
        while (true) {
            File file = getFile(newPath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } else {
                if (allowOverwrite) {
                    file.delete();
                } else {
                    newPath = pathWithoutExtension + "(" + (num++) + ")" + extension;
                }
            }
        }
    }

    private static class CreationParamsRet {
        private final boolean virtualized;
        private final String pathAndFilename;
        private final FileCreationMode creationMode;
        private File path;
        private boolean append;

        public CreationParamsRet(String pathAndFilename, FileCreationMode creationMode, boolean virtualized) {
            this.pathAndFilename = pathAndFilename;
            this.creationMode = creationMode;
            this.virtualized = virtualized;
        }

        public File getPath() {
            return path;
        }

        public boolean isAppend() {
            return append;
        }

        public CreationParamsRet invoke() throws IOException {
            if (creationMode == FileCreationMode.NEW) {
                path = getFile(pathAndFilename, virtualized);
                if (!path.exists()) {
                    createNewFile(path);
                } else {
                    throw new IOException("File already exists");
//                    if (path.isDirectory()) {
//                        if (!path.delete()) {
////                        return false;
//                            throw new IOException("Cannot delete directory");
//                        }
//                        if (!path.createNewFile()) {
////                        return false;
//                            throw new IOException("Cannot create new file");
//                        }
//                    }
                }
            } else if (creationMode == FileCreationMode.OVERWRITE) {
                path = getFile(pathAndFilename);
                if (path.exists()) {
                    if (!path.delete()) {
//                        return false;
                        throw new IOException("Cannot delete directory");
                    }

                }
                createNewFile(path);
            } else if (creationMode == FileCreationMode.APPEND) {
                path = getFile(pathAndFilename);
                if (!path.exists()) {
                    createNewFile(path);
                }
            } else if (creationMode == FileCreationMode.INCREMENT) {
                path = checkFilenameAndGenerateFile(pathAndFilename, false);
            } else {
                throw new IllegalArgumentException();
            }
//			f.createNewFile();
        /*	File filename = new File(path, LOG_FILENAME);
			if (!filename.isDirectory()) {
				filename.delete();
				if (!filename.createNewFile()) {
					return false;
				}
			}*/
            append = creationMode == FileCreationMode.APPEND;
            return this;
        }
    }

    public static File getFile(String pathAndFilename) {
        return getFile(pathAndFilename, false);
    }

    public static File getFile(String pathAndFilename, boolean virtualized) {
        if (virtualized) {
            pathAndFilename = getVirtualizedFile(pathAndFilename);
        }
        return Gdx.files.local(pathAndFilename).file();
    }

    public static boolean isLocalStorageAvailable() {
        return Gdx.files.isLocalStorageAvailable();
    }

    public static ArrayList<FileHandle> getFilesFromLocalFolder(final String startsWith, boolean sortByDate) {
        return getFilesFromLocalFolder(startsWith, null, sortByDate);
    }

    public static ArrayList<FileHandle> getFilesFromLocalFolder(final String startsWith, final String extension, boolean sortByDate) {
        FileHandle root = Gdx.files.local("");
        FileHandle[] printlnOutputs = root.list((file, s) -> {
            boolean found = s.startsWith(startsWith);
            if (!found) {
                return false;
            }
            if (extension != null && !extension.isEmpty()) {
                String ext = FilenameUtils.getExtension(s);
                return extension.equalsIgnoreCase(ext);
            }
            return true;
        });
        ArrayList<FileHandle> printlnOutputsList = new ArrayList<>();
        Collections.addAll(printlnOutputsList, printlnOutputs);
        // Now sort them by date.
        if (sortByDate) {
            Collections.sort(printlnOutputsList, (fileHandle, t1) -> Long.compare(fileHandle.lastModified(), t1.lastModified()));
        }
        return printlnOutputsList;
    }

    /**
     *
     * @param sortByDate from oldest to newest.
     * @return
     */
    public static ArrayList<FileHandle> getRedirectOutputFiles(boolean sortByDate) {
        return getFilesFromLocalFolder("println_output_", sortByDate);

    }

    public static ArrayList<FileHandle> getRedirectOutputFiles() {
        return getRedirectOutputFiles(true);
    }
}
