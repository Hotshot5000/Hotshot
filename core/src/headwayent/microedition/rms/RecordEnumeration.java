/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 4:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;

import headwayent.microedition.rms.RecordStore.DataPart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

public class RecordEnumeration {

    private static class Record {
        public final int recordID;
        public final byte[] record;

        public Record(int recordID, byte[] record) {
            this.recordID = recordID;
            this.record = record;
        }
    }

    private static class Listener implements RecordListener {

        private final RecordEnumeration recEnum;

        public Listener(RecordEnumeration recEnum) {
            this.recEnum = recEnum;
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void recordAdded(RecordStore recordStore, int recordId) {
            
            recEnum.rebuildIfNecessary();
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void recordChanged(RecordStore recordStore, int recordId) {
            
            recEnum.rebuildIfNecessary();
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void recordDeleted(RecordStore recordStore, int recordId) {
            
            recEnum.rebuildIfNecessary();
        }

    }

    private final RecordStore recordStore;
    private final RecordFilter recordFilter;
    private final RecordComparator recordComparator;
    private boolean keepUpdated;

    private Listener listener;

    @SuppressWarnings("unchecked")
    private final TreeSet<Record> enumerator = new TreeSet<>(new Comparator() {

        @SuppressWarnings("synthetic-access")
        @Override
        public int compare(Object object1, Object object2) {
            
            byte[] arg0 = ((Record) object1).record;
            byte[] arg1 = ((Record) object2).record;
            return (recordComparator != null) ?
                    recordComparator.compare(arg0, arg1) :
                    -1;
        }

    });

    private ListIterator<Record> enumeratorIt;
    private int currentPos;

    protected RecordEnumeration(RecordStore recordStore, RecordFilter recordFilter,
                                RecordComparator recordComparator, boolean keepUpdated) {
        this.recordStore = recordStore;
        this.recordFilter = recordFilter;
        this.recordComparator = recordComparator;
        keepUpdated(keepUpdated);
        if (!keepUpdated) {
            rebuild();
        }

    }

    public byte[] nextRecord() throws InvalidRecordIDException {
        try {
        /*	String rec = enumeratorIt.next().record;
			char[] c = new char[rec.length()];
			rec.getChars(0, rec.length(), c, 0);
			byte[] bc = new byte[c.length];
			for (int i = 0; i < c.length; ++i) {
				bc[i] = (byte) c[i];
			}
			byte[] b = rec.getBytes();*/
            return enumeratorIt.next().record;
        } catch (NoSuchElementException e) {
            throw new InvalidRecordIDException();
        }
        //return null;
    }

    public int nextRecordId()
            throws InvalidRecordIDException {
        try {
            return enumeratorIt.next().recordID;
        } catch (NoSuchElementException e) {
            throw new InvalidRecordIDException();
        }
    }

    public byte[] previousRecord() throws InvalidRecordIDException {
        try {
            return enumeratorIt.previous().record;
        } catch (NoSuchElementException e) {
            throw new InvalidRecordIDException();
        }
    }

    public int previousRecordId()
            throws InvalidRecordIDException {
        try {
            return enumeratorIt.previous().recordID;
        } catch (NoSuchElementException e) {
            throw new InvalidRecordIDException();
        }
    }

    public boolean hasNextElement() {
        return enumeratorIt.hasNext();
    }

    public boolean hasPreviousElement() {
        return enumeratorIt.hasPrevious();
    }

    public int numRecords() {
        return enumerator.size();
    }

    public void reset() {
        //	enumeratorIt = enumerator.iterator();
        ArrayList<Record> list = new ArrayList<>();
        Object[] rec = enumerator.toArray();
        for (Object o : rec) {
            list.add((Record) o);
        }
        enumeratorIt = list.listIterator();
    }

    public void destroy() {
        if (listener != null) {
            recordStore.removeRecordListener(listener);
        }
    }

    private void rebuildIfNecessary() {
        if (isKeptUpdated()) {
            rebuild();
        }
    }

    private static class KeyPos implements Comparable<KeyPos> {
        public final String key;
        public final int pos;
        public final int size;

        public KeyPos(String key, int pos, int size) {
            this.key = key;
            this.pos = pos;
            this.size = size;
        }

        @Override
        public int compareTo(KeyPos arg0) {
            
            if (pos < arg0.pos) {
                return -1;
            } else if (pos > arg0.pos) {
                return 1;
            }
            return 0; //Should never get here
        }
    }

    public void rebuild() {
        //	Map<String, ?> m = recordStore.sharedPrefs.getAll();
        enumerator.clear();
        TreeMap<Integer, TreeSet<KeyPos>> map = new TreeMap<>();
        int num = recordStore.sharedPrefs.get().size();
        for (Entry<String, ?> stringEntry : recordStore.sharedPrefs.get().entrySet()) {
            Entry<String, ?> entry = stringEntry;
            String key = entry.getKey();
            int firstCommaPos = key.indexOf(",");
            if (firstCommaPos == -1) {
                //Ignore the version recordID and other crap
                continue;
            }
            String recordIdString = key.substring(0, firstCommaPos);
            String lastElements = key.substring(firstCommaPos + 1);
            int recordId = Integer.parseInt(recordIdString);
            int secondCommaPos = lastElements.indexOf(",");
            String sizeString = lastElements.substring(0, secondCommaPos);
            String currentPosString =
                    lastElements.substring(secondCommaPos + 1);
            int size = Integer.parseInt(sizeString);
            int currentPos = Integer.parseInt(currentPosString);
            TreeSet<KeyPos> data = map.get(recordId);
            if (data == null) {
                map.put(recordId, new TreeSet<>());
                data = map.get(recordId);
            }
            data.add(new KeyPos(key, currentPos, size));
        }
        TreeSet<DataPart> dataSet = new TreeSet<>();
        for (Entry<Integer, TreeSet<KeyPos>> integerTreeSetEntry : map.entrySet()) {
            dataSet.clear();
            Entry<Integer, TreeSet<KeyPos>> entry = integerTreeSetEntry;
            int size = 0;
            for (KeyPos keyPos : entry.getValue()) {
                size = keyPos.size;
                int value = recordStore.sharedPrefs.getInteger(keyPos.key, 0);
                dataSet.add(new DataPart(value, keyPos.pos));
            }
            byte[] b = new byte[size * 4];
            int currentPos = 0;
            for (DataPart dataPart : dataSet) {
                int value = dataPart.value;
                b[currentPos++] = (byte) ((value >>> 24) & 0xff);
                b[currentPos++] = (byte) ((value >>> 16) & 0xff);
                b[currentPos++] = (byte) ((value >>> 8) & 0xff);
                b[currentPos++] = (byte) (value & 0xff);
            }

            if (recordFilter != null) {
                if (recordFilter.matches(b)) {
                    enumerator.add(new Record(entry.getKey(), b));
                }
            } else {
                enumerator.add(new Record(entry.getKey(), b));
            }
        }

        reset();
	/*	Collection<String> values = enumerator.values(); 
		Collections.sort((List<?>) values, new Comparator() {

			@SuppressWarnings("synthetic-access")
			@Override
			public int compare(Object object1, Object object2) {
				
				String arg0 = (String) object1;
				String arg1 = (String) object2;
				return recordComparator.compare(arg0.getBytes(), arg1.getBytes());
			}
			
		});*/
    }

    public void keepUpdated(boolean keepUpdated) {
        this.keepUpdated = keepUpdated;
        if (keepUpdated) {
            rebuild();
            if (listener == null) {
                listener = new Listener(this);
            }
            recordStore.addRecordListener(listener);
        } else {
            if (listener != null) {
                recordStore.removeRecordListener(listener);
            }
        }
    }

    public boolean isKeptUpdated() {
        return keepUpdated;
    }
}
