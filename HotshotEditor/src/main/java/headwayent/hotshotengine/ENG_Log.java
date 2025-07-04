package headwayent.hotshotengine;

import headwayent.hotshotengine.vfs.ENG_FileUtils;

import java.io.*;
import java.text.DateFormat;
import java.util.Calendar;

public class ENG_Log {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_NOTIFICATION = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_FATAL_ERROR = 4;
    private static final ENG_Log log = new ENG_Log(true);
    private static final String TYPE_MESSAGE_STRING = "Message";
    private static final String TYPE_NOTIFICATION_STRING = "Notification";
    private static final String TYPE_WARNING_STRING = "Warning";
    private static final String TYPE_ERROR_STRING = "Error";
    private static final String TYPE_FATAL_ERROR_STRING = "Fatal error";
    private static final String LOG_PATH = "";//APP_Game.FOLDER_COMPANY + "/" + APP_Game.FOLDER_GAME;
    private static final String LOG_FILENAME = "log";
    private static final boolean DEV = true;
    private PrintWriter /*FileOutputStream*/ file;
    private boolean logActive;

    private ENG_Log(boolean logActive) {
//		setLogActive(logActive);

    }

    private boolean createLog() {
//		boolean ok = true;
        file = ENG_FileUtils.createOutputWriter(LOG_PATH + File.separator + LOG_FILENAME, ENG_FileUtils.FileCreationMode.APPEND, ENG_FileUtils.Compression.NONE, true);
        return file != null;
//		try {
////			File path = new File(Environment.getExternalStorageDirectory() +
////					File.separator + LOG_PATH + File.separator + LOG_FILENAME);
//			File path = Gdx.files.local(LOG_PATH + File.separator + LOG_FILENAME).file();
//			if (!path.exists()) {
//				if (!path.getParentFile().mkdirs()) {
//					return false;
//				}
//				if (!path.createNewFile()) {
//					return false;
//				}
//			} else {
//				if (path.isDirectory()) {
//					if (!path.delete()) {
//						return false;
//					}
//					if (!path.createNewFile()) {
//						return false;
//					}
//				}
//			}
////			f.createNewFile();
//		/*	File filename = new File(path, LOG_FILENAME);
//			if (!filename.isDirectory()) {
//				filename.delete();
//				if (!filename.createNewFile()) {
//					return false;
//				}
//			}*/
//			file = new PrintWriter(new BufferedWriter(new FileWriter(
//					path, true)));//MainActivity.getInstance().openFileOutput(LOG_FILENAME, Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
//			if (file.checkError()) {
//				ok = false;
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
////			logActive = false;
//			MainApp.setFatalError();
//			ok = false;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			ok = false;
////			logActive = false;
//		}
//		return ok;
    }

    public void log(String message, int type) {
        if (!logActive) {
            return;
        }
        String typeString;
        switch (type) {
            case TYPE_MESSAGE:
                typeString = TYPE_MESSAGE_STRING;
                break;
            case TYPE_NOTIFICATION:
                typeString = TYPE_NOTIFICATION_STRING;
                break;
            case TYPE_WARNING:
                typeString = TYPE_WARNING_STRING;
                break;
            case TYPE_ERROR:
                typeString = TYPE_ERROR_STRING;
                break;
            case TYPE_FATAL_ERROR:
                typeString = TYPE_FATAL_ERROR_STRING;
                break;
            default:
                //Should never get here
                throw new IllegalArgumentException();
        }
        String text = typeString +
                " (" + DateFormat.getInstance().format(Calendar.getInstance().getTime()) + "): " +
                message + "\n";
        file.write(text);
        if (file.checkError()) {

        }
        if (DEV) {
            System.out.println(text);
        }
    }

    public boolean close() {
        file.flush();
        file.close();
        return true;
    }

    public void setLogActive(boolean logActive) {
        boolean ok = true;
        if (this.logActive != logActive) {
            if (logActive) {
                ok = createLog();
            } else {
                ok = close();
            }
        }
        if (ok) {
            this.logActive = logActive;
        }

    }

    public void writeException(Throwable t) {
        if (logActive) {
            t.printStackTrace(file);
        }
    }

    public boolean isLogActive() {
        return logActive;
    }

    public static ENG_Log getInstance() {
        return log;
    }
}
