/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils;

import java.util.BitSet;

public final class ConverterUtil {
	private ConverterUtil() {}

	public static IntBag toIntBag(BitSet bs, IntBag out) {
		if (bs.isEmpty()) {
			out.setSize(0);
			return out;
		}

		int size = bs.cardinality();
		out.setSize(size);
		out.ensureCapacity(size);

		int[] activesArray = out.getData();
		for (int i = bs.nextSetBit(0), index = 0; i >= 0; i = bs.nextSetBit(i + 1)) {
			activesArray[index++] = i;
		}

		return out;
	}

	public static BitSet toBitSet(IntBag bag, BitSet out) {
		int[] data = bag.getData();
		for (int i = 0, s = bag.size(); s > i; i++) {
			out.set(data[i]);
		}

		return out;
	}
}