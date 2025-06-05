package headwayent.hotshotengine.resource;

//import headwayent.blackholedarksun.MainActivity;

import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;

//import com.badlogic.gdx.Gdx;

public class ENG_Resource {

    public static final String RES_ATTR = "attr";
    public static final String RES_DRAWABLE = "drawable";
    public static final String RES_LAYOUT = "layout";
    public static final String RES_RAW = "raw";
    public static final String RES_STRING = "string";

    private static final String RES_PATH = "headwayEnt.Blackhole_Darksun.R";
    private static Class[] subClass;

    private final String name;
    private String filename;


    private String path;
    private int id;

    public ENG_Resource(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public ENG_Resource(String name, String filename) {
        this.name = name;
        this.filename = filename;
        this.path = FilenameUtils.getFullPath(filename);
    }

    public ENG_Resource(String name, String filename, String path) {
        this.name = name;
        this.filename = filename;
        this.path = path;
    }

    /**
     * @return Full filename including path
     */
    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

//	public static int getFileHandle(String fileName, String path) {
//		String fullPath = RES_PATH + '$' + path;
//		if (subClass == null) {
//			Class<headwayEnt.Blackhole_Darksun.R> c = null;		
//			try {
//				c = (Class<R>)Class.forName(RES_PATH);
//			} catch(ClassNotFoundException e) {
//				int i = 3;
//			} catch(ClassCastException e) {
//				
//			}
//			subClass = c.getDeclaredClasses();
//		}
//		int i = 0;
//		for (; i < subClass.length; ++i) {
//			String name = subClass[i].getName();
//			if (subClass[i].getName().equals(fullPath)) {
//				break;
//			}
//		}
//		Field[] f = subClass[i].getDeclaredFields();
//		for (i = 0; i < f.length; ++i) {
//			if (f[i].getName().equals(fileName)) {
//				break;
//			}
//		}
//		int fileHandle = 0;
//		try {
//			fileHandle = f[i].getInt(null);
//		} catch(IllegalAccessException e) {
//			
//		}
//		return fileHandle;
//	}


    private static ArrayList<ENG_Resource> error() {
//        if (MainActivity.isDebugmode()) {
        throw new ENG_InvalidFormatParsingException();
//        } else {
//            return null;
//        }
    }

    public static File getFile(String filename, String path) {
        return getFile(filename, path, true);
    }

    public static File getFile(String filename, String path, boolean SDCard) {
//		return new File(path, filename);
        if (SDCard) {
            String f;
            if (path == null || path.isEmpty()) {
                f = filename;
            } else {
                f = path + File.separator + filename;
            }
//            return Gdx.files.local(f).file();
            return new File(f);
        } else {
            // Ignore the path since we don't have one on android.
//            return Gdx.files.internal(filename).file();
        }
        throw new IllegalStateException();
    }

    public enum RandomAccessMode {
        READ, READ_WRITE, READ_WRITE_SYNC_META_OR_CONTENT,
        READ_WRITE_SYNC_CONTENT
    }

    public static String getRandomAccessMode(RandomAccessMode mode) {
        switch (mode) {
            case READ:
                return "r";
            case READ_WRITE:
                return "rw";
            case READ_WRITE_SYNC_META_OR_CONTENT:
                return "rws";
            case READ_WRITE_SYNC_CONTENT:
                return "rwd";
            default:
                throw new IllegalArgumentException(mode + " is not supported");
        }
    }

    public static RandomAccessFile getRandomAccessFile(
            String filename, String path, RandomAccessMode mode)
            throws FileNotFoundException {
        return new RandomAccessFile(getFile(filename, path),
                getRandomAccessMode(mode));
    }

    public static BufferedReader getFileAsBufferedReader(String fileName,
                                                         String path, boolean fromSDCard) {
        if (fromSDCard) {
            try {
                return new BufferedReader(new InputStreamReader(
                        new FileInputStream(getFile(fileName, path))));
            } catch (FileNotFoundException e) {

                e.printStackTrace();
                return null;
            }
        } else {
            throw new UnsupportedOperationException();
//			return getFileAsBufferedReader(getFileHandle(fileName, path));
        }
    }

    public static BufferedReader getFileAsBufferedReader(String fileName, String path) {
        if (getFile(fileName, path).exists()) {
            return getFileAsBufferedReader(fileName, path, true);
        }
        return getFileAsBufferedReader(FilenameUtils.removeExtension(fileName), path, false);
    }

    public static DataInputStream getFileAsStream(String fileName, String path,
                                                  boolean fromSDCard) {
//        if (fromSDCard) {
        try {
//                File file = getFile("badlogic.jpg", path, fromSDCard);
//                boolean exists = file.exists();
            return new DataInputStream(new BufferedInputStream(
                    new FileInputStream(getFile(fileName, path, fromSDCard))));
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            return null;
        }
//        } else {
//            throw new UnsupportedOperationException();
//			return getFileAsStream(getFileHandle(fileName, path));
//        }
    }

    public static DataInputStream getFileAsStream(String fileName, String path) {
        if (getFile(fileName, path).exists()) {
            return getFileAsStream(fileName, path, true);
        }
        return getFileAsStream(FilenameUtils.removeExtension(fileName), path, false);
    }

//	public static BufferedReader getFileAsBufferedReader(int resourceHandle) {
//		return new BufferedReader(
//				new InputStreamReader(
//						MainActivity.getInstance().getApplicationContext()
//						.getResources().openRawResource(
//								resourceHandle)));
//	}

//	public static DataInputStream getFileAsStream(int resourceHandle) {
//		return new DataInputStream(
//				new BufferedInputStream(
//						MainActivity.getInstance().getApplicationContext()
//						.getResources().openRawResource(
//								resourceHandle)));
//	}

    public static DataInputStream getGZIPFileAsStream(
            String fileName, String path, boolean fromSDCard)
        /*throws NotFoundException,*/ throws IOException {
        if (fromSDCard) {
            return new DataInputStream(
                    new BufferedInputStream(new GZIPInputStream(
                            new FileInputStream(getFile(fileName, path)))));
        } else {
            throw new UnsupportedOperationException();
//			return getGZIPFileAsStream(getFileHandle(fileName, path));
        }
    }

    public static DataInputStream getGZIPFileAsStream(
            String fileName, String path)
        /*throws NotFoundException,*/ throws IOException {
        return getGZIPFileAsStream(fileName, path, false);
    }

//	public static DataInputStream getGZIPFileAsStream(int resourceHandle) 
//			/*throws NotFoundException,*/ IOException {
//		return new DataInputStream(
//				new BufferedInputStream(new GZIPInputStream(
//						MainActivity.getInstance().getApplicationContext()
//						.getResources().openRawResource(
//								resourceHandle))));
//	}

//	public static ArrayList<ENG_Resource> getResourceID(
//			String fileName, String path) {				
//		ArrayList<ENG_Resource> handleList = new ArrayList<ENG_Resource>();
//		ArrayList<String> fileList = ENG_CompilerUtil.loadListFromFile(fileName, path);
//		if (!fileList.isEmpty()) {			
//			String temp = null;
//			String[] list = null;
//			for (int i = 0; i < fileList.size(); ++i) {
//				temp = fileList.get(i);
//				list = ENG_CompilerUtil.getPathAndFileName(temp);
//				handleList.add(new ENG_Resource(list[1],
//						getFileHandle(ENG_CompilerUtil.trimFile(list[1]), list[0])));
//			}
//		
//			return handleList;
//		}
//		return error();
//	
//	}
}
