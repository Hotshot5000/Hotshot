/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.MainActivity;

import java.util.HashMap;

public class ENG_BitmapManager {

    private final HashMap<String, ENG_Bitmap> bitmapList = new HashMap<>();

/*	private void add(String s) {
		bitmapList.add(s);
	}
	
	private void remove(String s) {
		bitmapList.remove(s);
	}*/

//	public void loadBitmapListFromFile(String fileName, String path) {
//		ArrayList<ENG_Resource> resList = ENG_Resource.getResourceID(fileName, path);
//		int len = resList.size();
//		ENG_Resource res = null;
//		for (int i = 0; i < len; ++i) {
//			res = resList.get(i);
//		//	ENG_Bitmap bmp = ;
//			add(new ENG_Bitmap(res.getId(), res.getName()));
//		}
//		
//	}

    public void add(ENG_Bitmap bmp) {
        bitmapList.put(bmp.getName(), bmp);
    }

    public void remove(ENG_Bitmap bmp) {
        bitmapList.remove(bmp.getName());
    }

    public void empty() {
        bitmapList.clear();
    }

    public ENG_Bitmap get(String name) {
        ENG_Bitmap bmp = bitmapList.get(name);
        if ((bmp == null) && (MainActivity.isDebugmode())) {
            throw new IllegalArgumentException("Bitmap: " + name + " could not be found");
        }
        return bmp;
    }
/*	public boolean checkIntegrity(boolean repair) {
		boolean correct = true;
		int len = bitmapList.size();
		for (int i = 0; i < len; ++i) {
			for (int j = i + 1; j < len; ++j) {
				if (bitmapList.get(i).getName().equals(bitmapList.get(j).getName())) {
					if (repair) {
						bitmapList.remove(j);
						len = bitmapList.size();
					}
					correct = false;
				}
			}
		}
		return correct;
	}
	
	public ENG_Bitmap get(String name) {
		int len = bitmapList.size();
		for (int i = 0; i < len; ++i) {
			if (bitmapList.get(i).getName().equals(name)) {
				return bitmapList.get(i);
			}
		}
		if (Main.isDebugmode()) {
			throw new IllegalArgumentException();
		} else {
			return null;
		}
	}*/
}
