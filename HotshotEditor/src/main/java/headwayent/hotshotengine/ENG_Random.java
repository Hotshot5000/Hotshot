package headwayent.hotshotengine;

import java.util.Random;

/**
 * Created by sebas on 05.10.2015.
 */
public class ENG_Random extends Random {
    private long seed;

    public ENG_Random(long seed) {
        super(seed);
        this.seed = seed;
    }

    public ENG_Random() {

    }

    public ENG_Random getRandom() {
        return null;
    }

    @Override
    public boolean nextBoolean() {
        return nextBoolean(null);
    }

    public boolean nextBoolean(String s) {
        return super.nextBoolean();
    }

    @Override
    public void nextBytes(byte[] bytes) {
        nextBytes(null, bytes);
    }

    public void nextBytes(String s, byte[] bytes) {
        super.nextBytes(bytes);
    }

    @Override
    public double nextDouble() {
        return nextDouble(null);
    }

    public double nextDouble(String s) {
        return super.nextDouble();
    }

    @Override
    public float nextFloat() {
        return nextFloat(null);
    }

    public float nextFloat(String s) {
        return super.nextFloat();
    }

    @Override
    public synchronized double nextGaussian() {
        return nextGaussian(null);
    }

    public double nextGaussian(String s) {
        return super.nextGaussian();
    }

    @Override
    public int nextInt() {
        return nextInt(null);
    }

    public int nextInt(String s) {
        return super.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return nextInt(null, bound);
    }

    public int nextInt(String s, int bound) {
        return super.nextInt(bound);
    }

    @Override
    public long nextLong() {
        return nextLong(null);
    }

    public long nextLong(String s) {
        return super.nextLong();
    }

    public long getSeed() {
        return seed;
    }
}
