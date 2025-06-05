/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.audio;


import games.rednblack.miniaudio.MAAttenuationModel;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * @author Alexandru This class implements the sound engine interface and makes
 *         calls to a ENG_SoundManager object
 */

public class ENG_Sound implements ENG_ISoundRoot {

    /**
     * soundManger - a SoundManger object context - the Context passed in the
     * constructor
     */
    private final ENG_SoundManager soundManager;
    // private Context context;
    private boolean soundsActive = true;

    /**
     * The constructor with arguments instantiates the soundManager object and
     * calls the loadSounds() method
     *
     * @param aContext The Context in which the stream will be played
     */
    public ENG_Sound(/* Context aContext */) {

        // context = aContext;
        soundManager = new ENG_SoundManager(/* context */);
        soundManager.loadSounds();
    }

    public long playSound(String name) {
        return playSound(name, 100);
    }

    /**
     * @param name Name of the stream to be played
     */
    public long playSound(String name, int volume) {

        if (soundManager.fileExists(name)) {

            long id = -1;
            if (soundsActive || ENG_SoundManager.FORCE_PLAY) {
                id = soundManager.playSound(name, volume);
            }

            return id;
        } else {

            if (!ENG_SoundManager.IGNORE_SOUND) {
                throw new IllegalArgumentException("File name not found.");
            } else {
                return -1;
            }
        }

    }

    public long playSound(String name, PlayType playType) {
        return playSound(name, 100, playType);
    }

    /**
     * @param name     Name of the stream to be played
     * @param playType The PlayType value can be PLAY_LOOP or PLAY_ONCE
     */
    public long playSound(String name, int volume, PlayType playType) {
        return playSound(name, volume, 0.0f, playType);

    }

    public long playSound(String name, int volume, float pan, PlayType playType){
        if (soundManager.fileExists(name)) {

            boolean isLooping = playType == PlayType.PLAY_LOOP;

//			if (playType == PlayType.PLAY_LOOP) {
//
//				isLooping = true;
//
//			} else {
//
//				isLooping = 0;
//			}

            long id = -1;
            if (soundsActive || ENG_SoundManager.FORCE_PLAY) {
                id = soundManager.playSound(name, volume, pan, isLooping);
            }
            return id;

        } else {

            if (!ENG_SoundManager.IGNORE_SOUND) {
                throw new IllegalArgumentException("File name not found.");
            } else {
                return -1;
            }

        }
    }

    public long getSoundDuration(String name) {
        return soundManager.getSoundDuration(name);
    }

    @Override
    public ENG_Vector4D getListenerPosition() {
        return soundManager.getListenerPosition();
    }

    @Override
    public void setListenerPosition(ENG_Vector4D position) {
        soundManager.setListenerPosition(position);
    }

    @Override
    public ENG_Vector4D getListenerFrontDirection() {
        return soundManager.getListenerFrontDirection();
    }

    @Override
    public void setListenerFrontDirection(ENG_Vector4D frontDir) {
        soundManager.setListenerFrontDirection(frontDir);
    }

    @Override
    public ENG_Vector4D getListenerUpDirection() {
        return soundManager.getListenerUpDirection();
    }

    @Override
    public void setListenerUpDirection(ENG_Vector4D upDir) {
        soundManager.setListenerUpDirection(upDir);
    }

    @Override
    public ENG_Vector4D getListenerVelocity() {
        return soundManager.getListenerVelocity();
    }

    @Override
    public void setListenerVelocity(ENG_Vector4D velocity) {
        soundManager.setListenerVelocity(velocity);
    }

    @Override
    public ENG_Vector4D getSoundVelocity(long id) {
        return soundManager.getSoundVelocity(id);
    }

    @Override
    public void setSoundVelocity(long id, ENG_Vector4D soundVelocity) {
        soundManager.setSoundVelocity(id, soundVelocity);
    }

    @Override
    public ENG_Vector4D getSoundPosition(long id) {
        return soundManager.getPosition(id);
    }

    @Override
    public void setSoundPosition(long id, ENG_Vector4D position) {
        soundManager.setPosition(id, position);
    }

    @Override
    public ENG_Vector4D getSoundFrontDirection(long id) {
        return soundManager.getFrontDirection(id);
    }

    @Override
    public void setSoundFrontDirection(long id, ENG_Vector4D frontDir) {
        soundManager.setFrontDirection(id, frontDir);
    }

    @Override
    public void playSoundObject(long id, boolean loop) {
        soundManager.playSoundObject(id, loop);
    }

    @Override
    public long createSoundObject(String name) {
        return soundManager.createSoundObject(name);
    }

    @Override
    public float getSoundDopplerFactor(long id) {
        return soundManager.getDopplerFactor(id);
    }

    @Override
    public void setSoundDopplerFactor(long id, float dopplerFactor) {
        soundManager.setDopplerFactor(id, dopplerFactor);
    }

    @Override
    public boolean isSoundEnded(long id) {
        return soundManager.isSoundEnded(id);
    }

    @Override
    public void setSoundCone(long id, float innerAngleInRadians, float outerAngleInRadians, float outerGain) {
        soundManager.setSoundCone(id, innerAngleInRadians, outerAngleInRadians, outerGain);
    }

    @Override
    public void setSoundAttenuationModel(long id, MAAttenuationModel maAttenuationModel) {
        soundManager.setSoundAttenuationModel(id, maAttenuationModel);
    }

    @Override
    public void setSoundRolloff(long id, float rolloff) {
        soundManager.setSoundRolloff(id, rolloff);
    }

    @Override
    public void setSoundGainRange(long id, float minGain, float maxGain) {
        soundManager.setSoundGainRange(id, minGain, maxGain);
    }

    @Override
    public void setSoundDistanceRange(long id, float minDistance, float maxDistance) {
        soundManager.setSoundDistanceRange(id, minDistance, maxDistance);
    }

    @Override
    public void updateSoundSystem() {
        soundManager.updateSoundSystem();
    }

    /**
     * Loads a sound into the HashMap and to the SoundPool itself
     *
     * @param name Name of the sound to be loaded for a specified path The path
     *             must be specified using setPath()
     */
    public void loadSound(String name, String filename, long duration, int priority) {

        soundManager.loadSound(name, filename, duration, priority);
    }

    public void loadSound(String name, int handle) {
        soundManager.loadSound(name, handle);
    }

    /**
     * Pauses a stream from playing
     *
     * @param name Name of the sound to be paused
     */
    public void pauseSound(String name) {

        // it will have no effect if the stream is not playing
        soundManager.pauseSound(name);

    }

    public void resumeSound(String name) {
        soundManager.resumeSound(name);
    }

    /**
     * Stops a stream from playing
     *
     * @param name Name of the sound to be stopped
     */
    public void stopSound(String name) {

        // it will have no effect if the stream is not playing
        soundManager.stopSound(name);

    }

    /**
     * This method changes the volume of a specified stream
     *
     * @param name
     *            The name of the stream
     * @param volume
     *            The new value ( must be between 0-7 )
     */
//	public void setVolume(String name, int volume) {
//
//		soundManager.setVolume(name, volume, false);
//
//	}
//
//	public void setVolume(String name, int leftVolume, int rightVolume) {
//
//		soundManager.setVolume(name, leftVolume, rightVolume, false);
//
//	}

    /**
     * This method changes the volume of a specified stream
     *
     * @param name
     *            The name of the stream
     * @param volume
     *            The new value ( must be between 0-7 )
     */
    /*
	 * public void setVolume(String name, int volume, boolean vibrate) {
	 * 
	 * soundManager.setVolume(name, volume, vibrate);
	 * 
	 * }
	 */

    /**
     * Returns an int representing the volume of a specified stream @ name The
     * name of the stream
     */
    public int getVolume(String name) {

        if (soundManager.fileExists(name)) {

            return soundManager.getVolume(name);

        } else {

            if (!ENG_SoundManager.IGNORE_SOUND) {
                throw new IllegalArgumentException("File name not found.");
            } else {
                return 0;
            }
        }

    }

    /**
     * Sets the path or the sounds
     *
     * @param name The path itself
     */
    public void setPath(String name) {

        soundManager.setPath(name);
    }

    @Override
    public void enableSounds() {
        
        soundsActive = true;
    }

    @Override
    public void disableSounds() {
        
        soundsActive = false;
    }

    @Override
    public boolean isEnabled() {
        
        return soundsActive;
    }

    @Override
    public void pauseSound(String name, long id) {
        
        soundManager.pauseSound(name, id);
    }

    @Override
    public void resumeSound(String name, long id) {
        
        soundManager.resumeSound(name, id);
    }

    @Override
    public void stopSound(String name, long id) {
        
        soundManager.stopSound(name, id);
    }

    @Override
    public void setVolume(String name, long id, int volume) {
        
        soundManager.setVolume(name, id, volume, false);
    }

    @Override
    public void disposeSound(String name) {
        
        soundManager.disposeSound(name);
    }

    @Override
    public void setPanAndVolume(String name, long id, float pan, int volume) {
        
        soundManager.setPanAndVolume(name, id, pan, volume);
    }

    @Override
    public void disposeOfAllSounds() {
        
        soundManager.disposeOfAllSounds();
    }

}
