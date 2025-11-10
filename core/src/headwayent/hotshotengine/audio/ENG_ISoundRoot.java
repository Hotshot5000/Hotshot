/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/21, 8:01 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.audio;

import games.rednblack.miniaudio.MAAttenuationModel;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Main interface through which the sound system is controlled.
 * I allows the user to play asynchronous sounds with different volume levels for
 * each sound.
 * I also provides a way for listeners to be registered with particular sound events.
 *
 * @author Sebi
 */
public interface ENG_ISoundRoot {

    /**
     * Enumeration stating the way that sounds can be played.
     *
     * @author Sebi
     */
    enum PlayType {
        PLAY_ONCE,
        PLAY_LOOP
    }

    /**
     * Enumeration to be used for triggering events at particular times in the
     * playing.
     *
     * @author Sebi
     */
    enum EventType {
        SOUND_STARTED,
        SOUND_ENDED,
        SOUND_PAUSED,
        SOUND_STOPPED,
        SOUND_REPEATED
    }

    void enableSounds();

    void disableSounds();

    boolean isEnabled();

    /**
     * Loads a sound and prepares it for future use
     *
     * @param name The sound name to be loaded
     * @param duration
     * @param priority
     * @throws IllegalArgumentException if no such file exists or the format is not
     *                                  supported
     */
    void loadSound(String name, String filename, long duration, int priority);

    /**
     * Specifically for android
     *
     * @param name
     * @param handle
     */
    void loadSound(String name, int handle);

    /**
     * Plays a sound using a PlayType.
     * for MiniAudio3D this is deprecated. You need position for this to work.
     *
     * @param name     Name of sound to be played
     * @param playType The way the sound should be played.
     * @throws IllegalArgumentException if the sound has not been loaded.
     */
    long playSound(String name, PlayType playType);

    long playSound(String name, int volume, PlayType playType);

    long playSound(String name, int volume, float pan, PlayType playType);

    long playSound(String name);

    /**
     * Pauses the playing sound.
     *
     * @param name Name of the sound to be paused. If the sound is not playing or
     *             has not been loaded the request is silently ignored.
     */
    void pauseSound(String name);

    void resumeSound(String name);

    void pauseSound(String name, long id);

    void resumeSound(String name, long id);

    /**
     * Stops the playing sound.
     *
     * @param name Name of the sound to be stopped. If the sound is not playing or
     *             has not been loaded the request is silently ignored.
     */
    void stopSound(String name);

    void stopSound(String name, long id);

//	public void setVolume(String name, int volume);

    /**
     * Sets a sound volume
     *
     * @param name   Name of the sound for which the volume will be set.
     *               If the sound is not loaded the request will be silently ignored.
     * @param volume The volume should be in the range of 0 to 100 where 0 is
     *               completely muted and 100 is the maximum sound volume. If the volume is
     *               out of that range then the system will clamp it to the appropiate value.
     */
//	public void setVolume(String name, int volume, boolean vibrate);
//	public void setVolume(String name, int leftVolume, int rightVolume);
    void setVolume(String name, long id, int volume);

    /**
     * Returns the current volume in the range of 0 - 100 of a loaded sound.
     *
     * @param name The name of the sound for which the volume will be provided.
     * @return The current volume.
     * @throws IllegalArgumentException if the sound is not loaded.
     */
    int getVolume(String name);

    void setPanAndVolume(String name, long id, float pan, int volume);

    // Not possible
//	public float getPan(String name, long id);

    long getSoundDuration(String name);

    void playSoundObject(long id, boolean loop);

    long createSoundObject(String name);

    ENG_Vector4D getListenerPosition();
    void setListenerPosition(ENG_Vector4D position);

    ENG_Vector4D getListenerFrontDirection();

    void setListenerFrontDirection(ENG_Vector4D frontDir);

    ENG_Vector4D getListenerUpDirection();

    void setListenerUpDirection(ENG_Vector4D upDir);

    ENG_Vector4D getListenerVelocity();

    void setListenerVelocity(ENG_Vector4D velocity);

    ENG_Vector4D getSoundVelocity(long id);

    void setSoundVelocity(long id, ENG_Vector4D soundVelocity);

    ENG_Vector4D getSoundPosition(long id);

    void setSoundPosition(long id, ENG_Vector4D position);

    ENG_Vector4D getSoundFrontDirection(long id);

    void setSoundFrontDirection(long id, ENG_Vector4D frontDir);

    float getSoundDopplerFactor(long id);

    void setSoundDopplerFactor(long id, float dopplerFactor);

    boolean isSoundEnded(long id);

    void setSoundCone(long id, float innerAngleInRadians, float outerAngleInRadians, float outerGain);

    void setSoundAttenuationModel(long id, MAAttenuationModel maAttenuationModel);

    void setSoundRolloff(long id, float rolloff);

    void setSoundGainRange(long id, float minGain, float maxGain);

    void setSoundDistanceRange(long id, float minDistance, float maxDistance);

    void updateSoundSystem();

    /**
     * Registers a listener for a particular event. Note that the same listener can
     * be registered for multiple events (The event type for which the event has
     * been triggered will be passes as a parameter to the event), or for multiple
     * sounds (for which the name of the sound will be provided also).
     *
     * @param soundName     The name of the sound to be registered with the event.
     * @param eventListener The event listener object that must extend the abstract
     *                      ENG_SoundEventListener.
     * @param eventType     The EventType for which the listener will be registered
     * @throws IllegalArgumentException if the soundName is not currently loaded.
     */
/*	public void registerListener(String soundName, ENG_SoundEventListener eventListener,
            EventType eventType) throws IllegalArgumentException;*/
    void disposeSound(String name);

    void disposeOfAllSounds();
}
