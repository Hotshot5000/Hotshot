/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import headwayent.hotshotengine.ENG_Utility;

public class RecordStore {

    public static final int AUTHMODE_PRIVATE = 0;
    public static final int AUTHMODE_ANY = 1;

    //	private Context context;
//	protected SharedPreferences sharedPrefs;
//	private SharedPreferences.Editor editor;
    protected Preferences sharedPrefs;
    private final String name;
    private int version;
    private int recordID;
    private final AtomicInteger numRecords = new AtomicInteger();
    private long lastModified;
    private final ReentrantLock editLock = new ReentrantLock();
    private final ArrayList<RecordListener> listenerList = new ArrayList<>();
    private boolean recordSetCalled;

    private RecordStore(/*Context context,*/ String name) {
//		this.context = context;
        this.name = name;
    }

    private void incrementNumRecords() {
        numRecords.incrementAndGet();
    }

    private void decrementNumRecords() {
        numRecords.decrementAndGet();
    }

    private void incrementVersion() {
        ++version;
    }

    private void updateLastModified() {
        lastModified = ENG_Utility.currentTimeMillis();
        updateRSData();
    }

    private void updateRSData() {
        sharedPrefs.putInteger("version", version);
        sharedPrefs.putInteger("numRecords", numRecords.get());
        sharedPrefs.putInteger("recordID", recordID);
//		editor.commit();
        sharedPrefs.flush();
    }

    private void beginEdit() {
    /*	editLock.lock();
		try {
			if (editor == null) {
				editor = sharedPrefs.edit();
			}
		} finally {
			editLock.unlock();
		}*/

    }

    private void endEdit() throws RecordStoreException {
	/*	if ((editor != null) && (!editor.commit())) {
			throw new RecordStoreException("commiting data from multiple threads");
		}*/
        if (sharedPrefs != null) {
            sharedPrefs.flush();
        } else {
            throw new RecordStoreException("commiting data from multiple threads");
        }
    }

    public int addRecord(byte[] data,
                         int offset,
                         int numBytes) {
        beginEdit();
        int numInts = data.length / 4;
        if (data.length % 4 != 0) {
            ++numInts;
        }
        int[] list = new int[numInts];
        try {
            for (int i = 0; i < numInts; ++i) {
			/*	int num = ((data[4 * i] & 0xff) << 24);
				num += (4 * i + 1 >= data.length ? 0 : (data[4 * i + 1] & 0xff) << 16);
				num +=  (4 * i + 2 >= data.length ? 0 : (data[4 * i + 2] & 0xff) << 8);
				num += 4 * i + 3 >= data.length ? 0 : (data[4 * i + 3] & 0xff);*/
                list[i] = ((data[4 * i] & 0xff) << 24) |
                        ((4 * i + 1 >= data.length) ? 0 : ((data[4 * i + 1] & 0xff) << 16)) |
                        ((4 * i + 2 >= data.length) ? 0 : ((data[4 * i + 2] & 0xff) << 8)) |
                        ((4 * i + 3 >= data.length) ? 0 : (data[4 * i + 3] & 0xff));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        ++recordID;
//		int numRec = sharedPrefs.getAll().keySet().size();
        for (int i = 0; i < list.length; ++i) {
            String recId = String.valueOf(recordID);
            String len = String.valueOf(list.length);
            //	if (i == 0) {
            //		editor.putInt(recId, list[i]);
            //	} else {
            sharedPrefs.putInteger(recId + "," + len + "," + i, list[i]);
            //		editor.commit();
            //	}
        }
	/*	try {
			editor.putString(String.valueOf(++recordID), new String(data, offset, numBytes, "US-ASCII"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}*/
//		editor.commit();
        sharedPrefs.flush();
        incrementNumRecords();
        if (!recordSetCalled) {
            for (RecordListener recordListener : listenerList) {
                recordListener.recordAdded(this, recordID);
            }
        }
        incrementVersion();
        updateLastModified();
        return recordID;
    }

    public void deleteRecord(int recordId)
            throws
            RecordStoreException {

        //	String ret = sharedPrefs.getString(String.valueOf(recordId), "");
		
	/*	if (ret.equals("")) {
			throw new InvalidRecordIDException("recordID not valid");
		} */

        Map<String, ?> map = sharedPrefs.get();//sharedPrefs.getAll();
        Set<String> set = map.keySet();
        //	for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
        //		System.out.println(it.next());
        //	}
        ArrayList<String> foundStringList = new ArrayList<>();
        String rec = String.valueOf(recordId);
        for (String next : set) {
            if ((next.equals("version")) ||
                    (next.equals("recordID")) ||
                    (next.equals("numRecords"))) {
                continue;
            }
            int pos = next.indexOf(",");
            if (pos == -1) {
                throw new RecordStoreException("Internal method to add must be used!!!");
            }
            if (next.startsWith(rec)) {
                //	String afterFirstComma = next.substring(pos);
                //	int lastCommaPos = afterFirstComma.indexOf(",");
                foundStringList.add(next);
            }
        }
        beginEdit();
        //editor.remove(String.valueOf(recordId));
        for (String s : foundStringList) {
            sharedPrefs.remove(s);
        }
        for (RecordListener recordListener : listenerList) {
            recordListener.recordDeleted(this, recordID);
        }
//		editor.commit();
        sharedPrefs.flush();
        decrementNumRecords();
        incrementVersion();
        updateLastModified();
    }

    public int getRecordSize(int recordId)
            throws
            InvalidRecordIDException,
            RecordStoreException {
	/*	String ret = sharedPrefs.getString(String.valueOf(recordId), "");
		if (ret.equals("")) {
			throw new InvalidRecordIDException("recordID not valid");
		}*/
        Map<String, ?> map = sharedPrefs.get();//sharedPrefs.getAll();
        Set<String> set = map.keySet();
        String rec = String.valueOf(recordId);
        for (String next : set) {
            int pos = next.indexOf(",");
            if (pos == -1) {
                throw new RecordStoreException("Internal method to add must be used!!!");
            }
            if (next.startsWith(rec)) {
                String afterFirstComma = next.substring(pos + 1);
                int lastCommaPos = afterFirstComma.indexOf(",");
                String sizeString = afterFirstComma.substring(0, lastCommaPos);
                return Integer.parseInt(sizeString);
            }
        }
        throw new InvalidRecordIDException("recordID not valid");
        //return ret.length();
    }

    protected static class DataPart implements Comparable<DataPart> {
        public final int value;
        public final int pos;

        public DataPart(int value, int pos) {
            this.value = value;
            this.pos = pos;
        }

        @Override
        public int compareTo(DataPart arg0) {
            
            if (pos < arg0.pos) {
                return -1;
            } else if (pos > arg0.pos) {
                return 1;
            }
            return 0;
        }
    }

    private byte[] extractByteArray(TreeSet<DataPart> intList) {
        int size = intList.size() * 4;
        byte[] b = new byte[size];
        int currentPos = 0;
        for (DataPart dataPart : intList) {
            int value = dataPart.value;
            b[currentPos++] = (byte) ((value >>> 24) & 0xff);
            b[currentPos++] = (byte) ((value >>> 16) & 0xff);
            b[currentPos++] = (byte) ((value >>> 8) & 0xff);
            b[currentPos++] = (byte) (value & 0xff);
        }
        return b;
    }

    private TreeSet<DataPart> extractRecord(int recordId) throws RecordStoreException, InvalidRecordIDException {
        Map<String, ?> map = sharedPrefs.get();//sharedPrefs.getAll();
        Set<String> set = map.keySet();
        String rec = String.valueOf(recordId);
        TreeSet<DataPart> intList = new TreeSet<>();
        int currentSizeFound = 0;
        int size = 0;
        for (String next : set) {
            int pos = next.indexOf(",");
            if (pos == -1) {
                throw new RecordStoreException("Internal method to add must be used!!!");
            }
            if (next.startsWith(rec)) {
                String afterFirstComma = next.substring(pos + 1);
                int lastCommaPos = afterFirstComma.indexOf(",");
                String sizeString = afterFirstComma.substring(0, lastCommaPos);
                String currentElemString = afterFirstComma.substring(
                        lastCommaPos + 2);
                size = Integer.parseInt(sizeString);
                int currentElem = Integer.parseInt(currentElemString);
                Object value = map.get(next);
                if (value instanceof Integer) {
                    intList.add(new DataPart((Integer) value, currentElem));
                    ++currentSizeFound;
                }
            }
        }
        if (currentSizeFound != size) {
            throw new RecordStoreException("Internal storage error");
        }
        if (intList.isEmpty()) {
            throw new InvalidRecordIDException("recordID not valid");
        }
        return intList;
    }

    public int getRecord(int recordId,
                         byte[] buffer,
                         int offset)
            throws
            InvalidRecordIDException,
            RecordStoreException {


        byte[] b = extractByteArray(extractRecord(recordId));
        if ((buffer.length - offset) >= b.length) {
            System.arraycopy(b, 0, buffer, offset, b.length);
        } else {
            throw new ArrayIndexOutOfBoundsException(b.length +
                    " is bigger than the provided buffer");
        }
        return b.length;
	/*	String ret = sharedPrefs.getString(String.valueOf(recordId), "");
		if (ret.equals("")) {
			throw new InvalidRecordIDException("recordID not valid");
		}
		if ((buffer.length - offset) >= ret.length()) {
			byte[] b = ret.getBytes();
			System.arraycopy(b, 0, buffer, offset, ret.length());
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
		return ret.length();*/
    }

    public byte[] getRecord(int recordId)
            throws
            InvalidRecordIDException,
            RecordStoreException {
        return extractByteArray(extractRecord(recordId));
	/*	String ret = sharedPrefs.getString(String.valueOf(recordId), "");
		if (ret.equals("")) {
			throw new InvalidRecordIDException("recordID not valid");
		}
		return ret.getBytes();*/
    }

    public void setRecord(int recordId,
                          byte[] newData,
                          int offset,
                          int numBytes)
            throws
            InvalidRecordIDException,
            RecordStoreException {
        recordSetCalled = true;
        deleteRecord(recordId);
        recordSetCalled = false;
        addRecord(newData, offset, numBytes);
	/*	String ret = sharedPrefs.getString(String.valueOf(recordId), "");
		if (ret.equals("")) {
			throw new InvalidRecordIDException("recordID not valid");
		}
		beginEdit();
		editor.putString(String.valueOf(recordId), new String(newData, offset, numBytes));*/
        for (RecordListener recordListener : listenerList) {
            recordListener.recordChanged(this, recordID);
        }
        //	incrementVersion();
        //	updateLastModified();
    }

    public void closeRecordStore()
            throws
            RecordStoreException {
        endEdit();
    }
	
/*	public static void deleteRecordStore(String recordStoreName)
    throws RecordStoreException,
           RecordStoreNotFoundException {
		
	}*/

    public RecordEnumeration enumerateRecords(RecordFilter filter,
                                              RecordComparator comparator,
                                              boolean keepUpdated) {
        return new RecordEnumeration(this, filter, comparator, keepUpdated);
    }

    public long getLastModified() {
        return lastModified;
    }

    public int getNextRecordID() {
        return recordID + 1;
    }

    public int getNumRecords() {
        return this.enumerateRecords(null, null, false).numRecords();
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    private void setVersion(int version) {
        this.version = version;
    }

    private void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    private void setNumRecords(int numRecords) {
        this.numRecords.set(numRecords);
    }

    public void setMode(int authmode, boolean writable) {
	/*	if ((this.authmode != authmode) || (this.writable != writable)) {
			this.authmode = authmode;
			this.writable = writable;
			int mode = 0;
			if (authmode == AUTHMODE_ANY) {
				if (writable) {
					mode |= Context.MODE_WORLD_WRITEABLE;
				}
				mode |= Context.MODE_WORLD_READABLE;
			}
			
			sharedPrefs = context.getSharedPreferences(name, mode);
		}*/
        sharedPrefs = Gdx.app.getPreferences(name);
    }

    public static RecordStore openRecordStore(/*Context context,*/ String recordStoreName,
                                              boolean createIfNecessary)
            throws RecordStoreException,
            RecordStoreNotFoundException {
        return openRecordStore(//context,
                recordStoreName, createIfNecessary, AUTHMODE_PRIVATE, true);
    }

    public static RecordStore openRecordStore(/*Context context,*/ String recordStoreName,
                                              boolean createIfNecessary,
                                              int authmode, boolean writable)
            throws
            RecordStoreNotFoundException {
        RecordStore rs = new RecordStore(/*context,*/ recordStoreName);
        rs.setMode(authmode, writable);
        if (createIfNecessary) {
            //	SharedPreferences.Editor editor = rs.sharedPrefs.edit();
//			editor.commit();
            rs.sharedPrefs.flush();
        } else {
            int size = rs.sharedPrefs.get().size();//rs.sharedPrefs.getAll().size();
            if (size == 0) {
                throw new RecordStoreNotFoundException();
            }
        }
        int ver = rs.sharedPrefs.getInteger("version", 0);
        int numRec = rs.sharedPrefs.getInteger("numRecords", 0);
        int recID = rs.sharedPrefs.getInteger("recordID", 0);
        rs.setVersion(ver);
        rs.setNumRecords(numRec);
        rs.setRecordID(recID);
        return rs;
    }

    public static void deleteRecordStore(/*Context context,*/ String recordStoreName) {
	/*	SharedPreferences.Editor editor = 
			context.getSharedPreferences(recordStoreName, 0).edit();*/
        Preferences preferences = Gdx.app.getPreferences(recordStoreName);
        preferences.clear();
        preferences.flush();
//		editor.clear();
//		editor.commit();
    }

    public void addRecordListener(RecordListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeRecordListener(RecordListener listener) {
        listenerList.remove(listener);
    }
}
