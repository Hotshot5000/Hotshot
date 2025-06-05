/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.testframework;

import java.util.concurrent.locks.ReentrantLock;

public abstract class TestFramework {

    public enum TestState {
        UNPREPARED, PREPARED, TESTING, RESULT_PASSED, RESULT_FAILED
    }

    private TestState testState = TestState.UNPREPARED;
    private final ReentrantLock lock = new ReentrantLock();

    public abstract void test();

    public TestState getTestState() {
        lock.lock();
        try {
            return testState;
        } finally {
            lock.unlock();
        }
    }

    public void setTestState(TestState testState) {
        lock.lock();
        try {
            this.testState = testState;
        } finally {
            lock.unlock();
        }
    }
}
