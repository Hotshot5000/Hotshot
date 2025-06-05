/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/21, 10:17 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.entitydata.WeaponData.WeaponType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import com.artemis.Component;

public class WeaponProperties extends Component {

    private final transient ArrayList<WeaponType> weaponTypeList = new ArrayList<>();
    private final TreeMap<WeaponType, Integer> weaponAmmo = new TreeMap<>();
    // The current id for the unique projectile name generation
    // We do this whole complication in order to make sure we can never
    // get two projectiles with the same name (it's almost impossible anyway but
    // for extra safety)
    private final TreeMap<WeaponData.WeaponType, Long> nextIdList = new TreeMap<>();
    private final TreeMap<WeaponType, LinkedList<Long>> currentIds = new TreeMap<>();

    private transient WeaponType currentWeaponType;
    private transient int currentIndex;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public WeaponProperties() {

    }

    public WeaponProperties(ArrayList<WeaponType> weaponTypeList) {
        addWeapon(weaponTypeList);
    }

    public WeaponType getCurrentWeaponType() {
        if (currentWeaponType == null) {
            throw new NullPointerException("weapons not initialized");
        }
        return currentWeaponType;
    }

    public void decrementWeaponAmmo(WeaponType wpn, int dec) {
        if (WeaponType.getDefaultMissileNumber(wpn) == WeaponData.INFINITE_AMMO) {
            return;
        }
        Integer integer = weaponAmmo.get(wpn);
        if (integer == null) {
            throw new IllegalArgumentException(wpn + " is not contained in this list");
        }
        int num = integer - dec;
        if (num < 0) {
            num = 0;
        }
        weaponAmmo.put(wpn, num);
    }

    public void incrementWeaponAmmo(WeaponType wpn, int inc) {
        if (WeaponData.WeaponType.getDefaultMissileNumber(wpn) == WeaponData.INFINITE_AMMO) {
            return;
        }
        Integer integer = weaponAmmo.get(wpn);
        if (integer == null) {
            throw new IllegalArgumentException(wpn + " is not contained in this list");
        }
        int num = integer + inc;
        weaponAmmo.put(wpn, num);
    }

    public void incrementCurrentWeaponAmmo(int inc) {
        incrementWeaponAmmo(currentWeaponType, inc);
    }

    public void decrementCurrentWeaponAmmo(int dec) {
        decrementWeaponAmmo(currentWeaponType, dec);
    }

    public void incrementCurrentWeaponAmmo() {
        incrementWeaponAmmo(currentWeaponType, 1);
    }

    public void decrementCurrentWeaponAmmo() {
        decrementWeaponAmmo(currentWeaponType, 1);
    }

    public int getCurrentWeaponAmmo() {
        if (currentWeaponType == null) {
            throw new NullPointerException("weapons not initialized");
        }
        return weaponAmmo.get(currentWeaponType);
    }

    public boolean hasCurrentWeaponAmmo() {
        if (currentWeaponType == null) {
            throw new NullPointerException("weapons not initialized");
        }
        return WeaponType.hasInfiniteAmmo(currentWeaponType) || weaponAmmo.get(currentWeaponType) > 0;
    }

    public void setCurrentWeaponAmmo(int num) {
        if (currentWeaponType == null) {
            throw new NullPointerException("weapons not initialized");
        }
        weaponAmmo.put(currentWeaponType, num);
    }

    public int getWeaponAmmo(WeaponType wpn) {
        Integer integer = weaponAmmo.get(wpn);
        if (integer == null) {
            throw new IllegalArgumentException(wpn + " is not contained in this list");
        }
        return integer;
    }

    public void setWeaponAmmo(WeaponType wpn, int num) {
        Integer integer = weaponAmmo.get(wpn);
        if (integer == null) {
            throw new IllegalArgumentException(wpn + " is not contained in this list");
        }
        weaponAmmo.put(wpn, num);
    }

    public long getNextId() {
        long nextId = nextIdList.get(currentWeaponType);

        LinkedList<Long> linkedList = currentIds.get(currentWeaponType);
        while (linkedList.contains(nextId)) {
            ++nextId;
        }
        linkedList.add(nextId);
        long inc = nextId + 1;
        nextIdList.put(currentWeaponType, inc);
        //	System.out.println("weapon created with id " + nextId);
        return nextId;
    }

    public String getUniqueName() {
        return WeaponType.getWeapon(currentWeaponType) + getNextId();
    }

    public void removeId(WeaponType wpn, long id) {
        //	System.out.println("removing weapon id " + id);
        LinkedList<Long> linkedList = currentIds.get(wpn);
        if (linkedList == null) {
            throw new IllegalArgumentException(wpn + " is not a valid weapon for the " +
                    "current weapon list");
        }
        linkedList.remove(id);
//        if (!linkedList.remove(Long.valueOf(id))) {
            // No idea why we get here
            // INVESTIGATE LATER!!! MIGHT BE IMPORTANT
            //	throw new IllegalArgumentException(id + " is not a valid projectile id");
//        }
    }

    public void addWeapon(ArrayList<WeaponType> weaponTypeList) {
        for (WeaponType type : weaponTypeList) {
            addWeapon(type);
        }
    }

    public void addWeapon(WeaponType weaponType) {
        addWeapon(weaponType, weaponTypeList.size(), WeaponType.getDefaultMissileNumber(weaponType));
    }

    public void addWeapon(WeaponType weaponType, int position, int ammo) {
        if (weaponTypeList.contains(weaponType)) {
            throw new IllegalArgumentException(weaponType + " already exists in this weaponType list");
        }
        if (position >= 0 && position < weaponTypeList.size()) {
            weaponTypeList.add(position, weaponType);
        } else {
            weaponTypeList.add(weaponType);
        }
        weaponAmmo.put(weaponType, ammo);
        if (nextIdList.get(weaponType) == null) {
            nextIdList.put(weaponType, (long) 0);
            currentIds.put(weaponType, new LinkedList<>());
        }
    }

    public void removeWeapon(int position) {
        if (position < 0 || position >= weaponTypeList.size()) {
            throw new IllegalArgumentException(position + " is not between 0 and " +
                    weaponTypeList.size());
        }
        weaponTypeList.remove(position);
        // Don't remove the weapon from id list. We may add it again sometime
        // and need to keep track of the id (there still might be projectiles
        // already launched).
    }

    public void removeWeapon(WeaponData.WeaponType weaponType) {
        boolean remove = weaponTypeList.remove(weaponType);
        if (!remove) {
            throw new IllegalArgumentException(weaponType + " does not exist in this " +
                    "weaponType list");
        }
    }

    public void removeAllWeapons() {
        weaponTypeList.clear();
        weaponAmmo.clear();
        currentWeaponType = null;
    }

    public void setCurrentWeaponType(WeaponData.WeaponType wpn) {
        boolean found = false;
        for (WeaponType weaponType : weaponTypeList) {
            if (weaponType == wpn) {
                found = true;
                break;
            }
        }
        if (found) {
            currentWeaponType = wpn;
        } else {
            throw new IllegalArgumentException(wpn + " is not a valid weapon");
        }
    }

    public void setCurrentWeapon(int i) {
        if (i < 0 || i >= weaponTypeList.size()) {
            throw new IllegalArgumentException(i + " is not between 0 and " +
                    weaponTypeList.size());
        }
        currentWeaponType = weaponTypeList.get(i);
        currentIndex = i;
    }

    public void previousWeapon() {
        if ((--currentIndex) < 0) {
            currentIndex = weaponTypeList.size() - 1;
        }
        setCurrentWeapon(currentIndex);
    }

    public void nextWeapon() {
        if ((++currentIndex) >= weaponTypeList.size()) {
            currentIndex = 0;
        }
        setCurrentWeapon(currentIndex);
    }

    public boolean hasWeapons() {
        return !weaponTypeList.isEmpty();
    }

    public TreeMap<WeaponType, Integer> getWeaponAmmo() {
        return weaponAmmo;
    }
}
