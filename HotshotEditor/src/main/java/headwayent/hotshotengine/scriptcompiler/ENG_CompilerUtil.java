/**
 *
 */
package headwayent.hotshotengine.scriptcompiler;

//import headwayent.blackholedarksun.MainActivity;
//import headwayent.blackholedarksun.MainApp;

import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.exception.ENG_InvalidPathException;
import headwayent.hotshotengine.exception.ENG_MalformedPathException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.resource.ENG_Resource.RandomAccessMode;

import java.io.Closeable;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Sebi
 */
public class ENG_CompilerUtil {

    private static final String CHANGE_DIR = "cd";
    private static final String CHANGE_DIR_ROOT = "cd\\";

    // We need to implement a hack here since we just realized that we would
    // actually
    // like to have variable number of parameters for each function.
    // But we also need to maintain compatibility with previously written
    // scripts.
    // So we implement a way in which the system can look ahead for one word and
    // if that word is not what we need we just put it in a queue so next time
    // when
    // getNextWord() is called it just returns the already read string.
    private static String lookAhead;

    public static void setLookAhead(String s) {
        lookAhead = s;
    }

    public static String getNextLine(DataInputStream fp) {
        ArrayList<ENG_Byte> list = new ArrayList<>();
        byte b;
        while (true) {
            try {
                b = fp.readByte();
                if (b == '\n') {
                    break;
                }
                if (b == '\r') {
                    fp.readByte();
                    break;
                }
                list.add(new ENG_Byte(b));
            } catch (EOFException e) {
                break;
            } catch (IOException e) {
//                if (MainActivity.isDebugmode()) {
//                    e.printStackTrace();
//                    MainApp.setFatalError();
//                }
                break;
            }
        }
        return fromByteArrayToString(list);
    }

    private static final ArrayList<ENG_Byte> savedChars = new ArrayList<>();

    public static String getNextWord(DataInputStream fp) {
        if (lookAhead != null) {
            String ret = lookAhead;
            lookAhead = null;
            return ret;
        }
        ArrayList<ENG_Byte> list = new ArrayList<>();
        byte b = 0;
        boolean cr = false;
        boolean inWord = false;
        boolean mightBeginComment = false;
        boolean lineCommented = false;
        boolean wordRead = false;
        boolean mightBeginMultiLineComment = false;
        boolean mightEndMultiLineComment = false;
        boolean quotationMarkStarted = false;
        byte prevChar;
        savedChars.clear();
        while (true) {
            try {
                if (cr) {
                    cr = false;
                    b = fp.readByte();
                    if (inWord) {
                        break;
                    }
                }
                prevChar = b;
                b = fp.readByte();
            } catch (EOFException e) {
                int i = 3;
                break;
            } catch (IOException e) {

//                if (MainActivity.isDebugmode()) {
//                    e.printStackTrace();
//                    MainApp.setFatalError();
//                }
                break;
            }
            if ((!inWord) && ((b == '\t') || (b == ' '))) {
                continue;
            }
            if (b == '"') {
                if (prevChar == '\\' && !quotationMarkStarted) {
                    // Remove \
                    list.remove(list.size() - 1);
                } else if (quotationMarkStarted) {
                    // We have reached the end of the the quote
                    break;
                } else {
                    quotationMarkStarted = true;
                    inWord = true;
                    continue;
                }
            }
            if (quotationMarkStarted) {
                list.add(new ENG_Byte(b));
                continue;
            }
            if (b == '\r') {
                if (!wordRead) {
                    if (!inWord) {
                        cr = true;
                        lineCommented = false;
                        continue;
                    }
                } else {
                    if (lineCommented) {
                        cr = true;
                        continue;
                    }
                }
            }
            if (b == '\n') {
                if (!wordRead) {
                    if (!inWord) {
                        lineCommented = false;
                        continue;
                    }
                } else {
                    if (lineCommented) {
                        break;
                    }
                }
            }
            if (b == '#') {
                boolean inVelocityCommand = false;
                int velocityCommandStack = 1;
                String velocityStopCommand = "#end";
                ArrayList<ENG_Byte> velocityCommand = new ArrayList<>();
                while (true) {
                    try {
                        b = fp.readByte();
                    } catch (EOFException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (b == '#') {
                        velocityCommand.clear();
                        inVelocityCommand = true;
                    }
                    if (inVelocityCommand) {
                        velocityCommand.add(new ENG_Byte(b));
                        if (velocityCommand.size() == velocityStopCommand.length()) {
                            if (velocityStopCommand.equals(fromByteArrayToString(velocityCommand))) {
                                --velocityCommandStack;
                            } else {
                                // We probably have another command that we don't care about
                                // but it also needs to have a corresponding end command.
                                ++velocityCommandStack;
                            }
                        }
                        if (velocityCommandStack == 0) {
                            break;
                        }
                    }
                }
            }
            if (b == '/') {
                if (mightBeginComment) {
                    // We have definitely began a comment
                    lineCommented = true;
                    savedChars.clear();
                    // We need to read until the end of the line
                    inWord = false;
                } else {
                    mightBeginComment = true;
                    mightBeginMultiLineComment = true;
                    savedChars.add(new ENG_Byte(b));
                    if (inWord) {
                        wordRead = true; // We already read something
                    }
                    inWord = true;
                }
                continue;
            } else {
                if (mightBeginMultiLineComment) {
                    if (b == '*') {
                        savedChars.clear();
                        // Search for end of multiline comment
                        while (true) {
                            try {
                                b = fp.readByte();
                            } catch (EOFException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (b == '*') {
                                mightEndMultiLineComment = true;
                            } else {
                                if (b == '/') {
                                    if (mightEndMultiLineComment) {
                                        break;
                                    }
                                }
                                mightEndMultiLineComment = false;
                            }

                        }
                        if (!wordRead) {
                            inWord = false;
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        mightBeginMultiLineComment = false;
                    }

                } else {
                    mightBeginComment = false;
                    mightBeginMultiLineComment = false;
                }
            }
            if ((b != '\r') && (b != '\n') && (b != '\0') && (b != ' ')
                    && (b != '\t')) {
                if (!lineCommented) {
                    moveSavedCharacters(list);
                    list.add(new ENG_Byte(b));
                    inWord = true;
                }
            } else {
                moveSavedCharacters(list);
                break;
            }
        }
        return fromByteArrayToString(list);
    }

    private static void moveSavedCharacters(ArrayList<ENG_Byte> list) {
        list.addAll(savedChars);
        savedChars.clear();
    }

    public static String fromByteArrayToString(ArrayList<ENG_Byte> list) {
        if (list.isEmpty()) {
            return null;
        }
        ENG_Byte[] byteList = new ENG_Byte[list.size()];
        list.toArray(byteList);
        byte[] s = new byte[byteList.length];
        for (int i = 0; i < s.length; ++i) {
            s[i] = byteList[i].getValue();
        }
        return new String(s);
    }

    private static String basePath = "";

    public static String getBasePath() {
        return basePath;
    }

    public static void setBasePath(String basePath) {
        if (!basePath.isEmpty() && !basePath.endsWith(File.separator)) {
            basePath += File.separator;
        }
        ENG_CompilerUtil.basePath = basePath;
    }

    public static ArrayList<String> findResourceNames(String resourceGroup,
                                                      final String searchPattern) {
        File[] files = extractFileListMatchingPattern(resourceGroup,
                searchPattern);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < files.length; ++i) {
            list.add(files[i].getName());
        }
        return list;
    }

    public static String[] findResourceNamesAsList(String resourceGroup,
                                                   final String searchPattern) {
        File[] files = extractFileListMatchingPattern(resourceGroup,
                searchPattern);
        String[] list = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            list[i] = files[i].getName();
        }
        return list;
    }

    private static File[] extractFileListMatchingPattern(String resourceGroup,
                                                         final String searchPattern) {
        String dirName = getBasePath() + resourceGroup + File.separator;
        File dir = new File(dirName);
        return dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir1, String filename) {

                return filename.matches(searchPattern);
            }
        });
    }

    public static String[] getPathAndFileName(String s) {
        return getPathAndFileName(s, true);
    }

    public static String[] getPathAndFileName(String s, boolean fromSDCard) {
        int c1 = '/';
        int c2 = '\\';
        if (!fromSDCard) {
            int index;
            int lastIndex;
            // String newPath = null;
            // String newFileName = null;
            index = s.indexOf(c1);
            lastIndex = s.lastIndexOf(c1);
            if (index != lastIndex) {
                throw new ENG_MalformedPathException();
            }
            if (index == -1) {
                index = s.indexOf(c2);
                lastIndex = s.lastIndexOf(c2);
                if (index != lastIndex) {
                    throw new ENG_MalformedPathException();
                }
            }
            if (index == -1) {
                // Could not find the path
                throw new ENG_InvalidPathException();
            }
            String[] list = new String[2];
            list[0] = s.substring(0, index);
            list[1] = s.substring(index + 1);
            if (fromSDCard && !getBasePath().isEmpty()) {
                if (list[0].startsWith(File.separator)) {
                    list[0] = list[0].substring(1);
                }
                list[0] = getBasePath() + list[0];
            }
            return list;
        } else {
            int c = c1;
            int lastIndexFromEnd = s.lastIndexOf(c);
            if (lastIndexFromEnd == -1) {
                c = c2;
                lastIndexFromEnd = s.lastIndexOf(c);
                if (lastIndexFromEnd == -1) {
                    throw new ENG_InvalidPathException();
                }
            }
            String[] list = new String[2];
            list[0] = s.substring(0, lastIndexFromEnd);
            list[1] = s.substring(lastIndexFromEnd + 1);
            // int lastIndexOf = list[0].lastIndexOf(c);
            if (!getBasePath().isEmpty()) {
                // We have a full path
                if (!list[0].startsWith(getBasePath())) {
                    if (list[0].startsWith(File.separator)) {
                        list[0] = list[0].substring(1);
                    }
                    list[0] = getBasePath() + list[0];
                }
            }
            return list;
        }
    }

    public static String checkDirChange(String s, DataInputStream fp0) {
        String dir = null;
        if (s.equals(CHANGE_DIR)) {
            dir = ENG_CompilerUtil.getNextWord(fp0);
            // Old code from paleolithic.
//            if ((!dir.equals(ENG_Resource.RES_ATTR))
//                    && (!dir.equals(ENG_Resource.RES_LAYOUT))
//                    && (!dir.equals(ENG_Resource.RES_DRAWABLE))
//                    && (!dir.equals(ENG_Resource.RES_RAW))
//                    && (!dir.equals(ENG_Resource.RES_STRING))) {
//                throw new ENG_InvalidFormatParsingException();
//            }
            dir = dir.replace("\\", "/");
            dir += '/';

        }
        if (s.equals(CHANGE_DIR_ROOT)) {
            dir = "";

        }
        return dir;
    }

    public static ByteBuffer loadFileAsByteBuffer(String filename) {
        String[] pathAndFileName =
                ENG_CompilerUtil.getPathAndFileName(filename);
        return loadFileAsByteBuffer(pathAndFileName[1], pathAndFileName[0]);
    }

    public static ByteBuffer loadFileAsByteBuffer(String fileName, String path) {
        RandomAccessFile fp0 = null;
        try {
            fp0 = ENG_Resource.getRandomAccessFile(
                    fileName, path, RandomAccessMode.READ);
            FileChannel channel = fp0.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect((int) channel.size());
            channel.read(buffer);
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    public static String loadFileAsString(String fileName, String path) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path);
            String s;
            String dir = null;
            String currentDir = "";
            StringBuilder str = new StringBuilder();
            while ((s = ENG_CompilerUtil.getNextLine(fp0)) != null) {
                str.append(s);
            }
            return str.toString();
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    public static ArrayList<String> loadListFromFileAsLines(String fileName,
                                                            String path) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path);
            String s;
            String dir = null;
            String currentDir = "";
            ArrayList<String> materialList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextLine(fp0)) != null) {
                materialList.add(s);
            }
            return materialList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    public static ArrayList<String> loadListFromFile(String fileName,
                                                     String path) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path);
            String s;
            String dir;
            String currentDir = "";
            ArrayList<String> materialList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                dir = ENG_CompilerUtil.checkDirChange(s, fp0);
                if (dir != null) {
                    currentDir = dir;
                    continue;
                }
                String materialFile = currentDir + s;
                materialList.add(materialFile);
            }
            return materialList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    /*
     * public static String getExtension(String fileName) { int indexOf =
     * fileName.lastIndexOf("\\."); if (fileName.length() - indexOf != 4) {
     * throw new IllegalArgumentException(
     * "Invalid fileName. Sure it has the extension?"); } String[] split =
     * fileName.split("."); if (split.length < 1) { throw new
     * IllegalArgumentException("Sure this is a valid fileName?"); } return
     * split[split.length - 1]; }
     */

    private static String trimExtension(String fileName) {
        if (Pattern.matches(".+\\.[a-zA-Z]{3}", fileName)) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

    public static String trimFile(String fileName) {
        return trimFile(fileName, true);
    }

    public static String trimFile(String fileName, boolean trimExtension) {
        if (fileName.startsWith("./") || fileName.startsWith(".\\")) {
            fileName = fileName.substring(2);
        }
        if (fileName.endsWith("/") || fileName.endsWith("\\")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        if (trimExtension) {
            fileName = trimExtension(fileName);
        }
        return fileName;
    }

    public static String getExtension(String fileName) {
        String[] split = fileName.split("\\.");
        if (split.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid fileName. Maybe to many dots?");
        }
        return split[1];
    }

    public static int byteArrayEndCharacterPos(byte[] b) {
        int endPos = 0;
        for (int c = 0; c < b.length; ++c) {
            if (b[c] == (byte) 0) {
                endPos = c;
                break;
            }
        }
        return endPos;
    }

    public static void close(Closeable str) {
        if (str != null) {
            try {
                str.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
