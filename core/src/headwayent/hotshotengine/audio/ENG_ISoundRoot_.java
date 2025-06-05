/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.audio;

/**
 * Main interface through which the sound system is controlled.
 * I allows the user to play asynchronous sounds with different volume levels for
 * each sound.
 * I also provides a way for listeners to be registered with particular sound events.
 *
 * @author Sebi
 */
public interface ENG_ISoundRoot_ {

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

    /**
     * Loads a sound and prepares it for future use
     *
     * @param name The sound name to be loaded
     * @throws IllegalArgumentException if no such file exists or the format is not
     *                                  supported
     */
    void loadSound(String name) throws IllegalArgumentException;

    /**
     * Plays a sound using a PlayType.
     *
     * @param name     Name of sound to be played
     * @param playType The way the sound should be played.
     * @throws IllegalArgumentException if the sound has not been loaded.
     */
    void playSound(String name, PlayType playType)
            throws IllegalArgumentException;

    /**
     * Pauses the playing sound.
     *
     * @param name Name of the sound to be paused. If the sound is not playing or
     *             has not been loaded the request is silently ignored.
     */
    void pauseSound(String name);

    /**
     * Stops the playing sound.
     *
     * @param name Name of the sound to be stopped. If the sound is not playing or
     *             has not been loaded the request is silently ignored.
     */
    void stopSound(String name);

    /**
     * Sets a sound volume
     *
     * @param name   Name of the sound for which the volume will be set.
     *               If the sound is not loaded the request will be silently ignored.
     * @param volume The volume should be in the range of 0 to 100 where 0 is
     *               completely muted and 100 is the maximum sound volume. If the volume is
     *               out of that range then the system will clamp it to the appropiate value.
     */
    void setVolume(String name, int volume);

    /**
     * Returns the current volume in the range of 0 - 100 of a loaded sound.
     *
     * @param name The name of the sound for which the volume will be provided.
     * @return The current volume.
     * @throws IllegalArgumentException if the sound is not loaded.
     */
    int getVolume(String name) throws IllegalArgumentException;

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
    void registerListener(String soundName, ENG_SoundEventListener_ eventListener,
                          EventType eventType) throws IllegalArgumentException;
}
