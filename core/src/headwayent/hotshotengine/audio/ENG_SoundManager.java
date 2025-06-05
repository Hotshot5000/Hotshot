/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.audio;

import games.rednblack.miniaudio.MAAttenuationModel;
import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * This class loads all the sounds into a HashMap and communicates directly with
 * Android sound classes
 *
 * @author Alexandru
 */

public class ENG_SoundManager {

    private static class SoundInternalMiniAudio {
        private final String name; // For debugging
        private final String filename; // This is used for creating MASound by MiniSound.
        private final long duration;
        private final int priority; // 0 is the highest priority. 1, 2, 3 are lower priorities.
//        public float leftVolume = 1.0f, rightVolume = 1.0f; // These concepts are now obsolete.
//        public float volume; // 0.0f - 1.0f.

        public SoundInternalMiniAudio(String name, String filename, long duration, int priority) {
            this.name = name;
            this.filename = filename;
            this.duration = duration;
            this.priority = priority;
        }

        public String getName() {
            return name;
        }

        public String getFilename() {
            return filename;
        }

        public long getDuration() {
            return duration;
        }

        public int getPriority() {
            return priority;
        }
    }

    private static class ActiveSoundMiniAudio {
        private final long playId;
        private long startTime;
        private final SoundInternalMiniAudio sound;
        private final MASound soundMiniAudio;
        private final ENG_Vector4D playablePosition = new ENG_Vector4D(true);
        private final ENG_Vector4D playableFrontDirection = new ENG_Vector4D();
        private final ENG_Vector4D playableSoundVelocity = new ENG_Vector4D();
        private float playableDopplerFactor = 1.0f;
        private float innerAngleInRadians;
        private float outerAngleInRadians;
        private float outerGain;
        private int currentPriority; // This is derived based on SoundInternal priority and volume at sound start.
        private boolean looping;

        public ActiveSoundMiniAudio(long playId, long startTime, SoundInternalMiniAudio sound, MASound soundMiniAudio, int currentPriority, boolean isLooping) {
            this.playId = playId;
            this.startTime = startTime;
            this.sound = sound;
            this.soundMiniAudio = soundMiniAudio;
            this.currentPriority = currentPriority;
            this.looping = isLooping;
        }

        public ActiveSoundMiniAudio(long playId, SoundInternalMiniAudio sound, MASound soundMiniAudio) {
            this.playId = playId;
            this.sound = sound;
            this.soundMiniAudio = soundMiniAudio;
        }

        public boolean isStarted() {
            return startTime != 0;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public void setCurrentPriority(int currentPriority) {
            this.currentPriority = currentPriority;
        }

        public void setLooping(boolean looping) {
            this.looping = looping;
        }

        public float getPlayableDopplerFactor() {
            return playableDopplerFactor;
        }

        public void setPlayableDopplerFactor(float playableDopplerFactor) {
            this.playableDopplerFactor = playableDopplerFactor;
        }

        public long getPlayId() {
            return playId;
        }

        public long getStartTime() {
            return startTime;
        }

        public SoundInternalMiniAudio getSound() {
            return sound;
        }

        public MASound getSoundMiniAudio() {
            return soundMiniAudio;
        }

        public ENG_Vector4D getPlayableSoundVelocity() {
            return playableSoundVelocity;
        }

        public void setPlayableSoundVelocity(ENG_Vector4D playableSoundVelocity) {
            this.playableSoundVelocity.set(playableSoundVelocity);
        }

        public ENG_Vector4D getPlayablePosition() {
            return playablePosition;
        }

        public void setPlayablePosition(ENG_Vector4D position) {
            playablePosition.set(position);
        }

        public ENG_Vector4D getPlayableFrontDirection() {
            return playableFrontDirection;
        }

        public void setPlayableFrontDirection(ENG_Vector4D frontDir) {
            playableFrontDirection.set(frontDir);
        }

        public int getCurrentPriority() {
            return currentPriority;
        }

        public boolean isLooping() {
            return looping;
        }

        public float getInnerAngleInRadians() {
            return innerAngleInRadians;
        }

        public float getOuterAngleInRadians() {
            return outerAngleInRadians;
        }

        public float getOuterGain() {
            return outerGain;
        }

        public void setInnerAngleInRadians(float innerAngleInRadians) {
            this.innerAngleInRadians = innerAngleInRadians;
        }

        public void setOuterAngleInRadians(float outerAngleInRadians) {
            this.outerAngleInRadians = outerAngleInRadians;
        }

        public void setOuterGain(float outerGain) {
            this.outerGain = outerGain;
        }

        public void setCone(float innerAngleInRadians, float outerAngleInRadians, float outerGain) {
            this.innerAngleInRadians = innerAngleInRadians;
            this.outerAngleInRadians = outerAngleInRadians;
            this.outerGain = outerGain;
        }
    }

    private static class SoundInternal {

        private final String name; // For debugging
        private final Sound soundID;
        private final long duration;
        private final int priority; // 0 is the highest priority. 1, 2, 3 are lower priorities.
//        public float leftVolume = 1.0f, rightVolume = 1.0f; // These concepts are now obsolete.
//        public float volume; // 0.0f - 1.0f.

        public SoundInternal(String name, Sound soundID, long duration, int priority) {
            this.name = name;
            this.soundID = soundID;
            this.duration = duration;
            this.priority = priority;
        }

        public String getName() {
            return name;
        }

        public Sound getSoundID() {
            return soundID;
        }

        public long getDuration() {
            return duration;
        }

        public int getPriority() {
            return priority;
        }
    }

    private static class ActiveSound {
        private final long playId;
        private final long startTime;
        private final SoundInternal sound;
        private final int currentPriority; // This is derived based on SoundInternal priority and volume at sound start.
        private final boolean looping;

        public ActiveSound(long playId, long startTime, SoundInternal sound, int currentPriority, boolean isLooping) {
            this.playId = playId;
            this.startTime = startTime;
            this.sound = sound;
            this.currentPriority = currentPriority;
            this.looping = isLooping;
        }

        public long getPlayId() {
            return playId;
        }

        public long getStartTime() {
            return startTime;
        }

        public SoundInternal getSound() {
            return sound;
        }

        public int getCurrentPriority() {
            return currentPriority;
        }

        public boolean isLooping() {
            return looping;
        }
    }

    public enum SoundEngine {
        LIBGDX,
        MINIAUDIO,
        MINIAUDIO_3D // Let MiniAudio do the 3D panning, doppler etc. based on 3D positions of the objects
    }

    public static final int MAX_CONCURRENT_SOUNDS = 32;
    public static final int MAX_SOUND_VOLUME = 100;
    public static final int SOUND_LOWEST_PRIORITY = 999;
    public static final float MINIAUDIO_3D_SOUND_SPEED_ATTENUATION = 10.0f;
    private static final boolean DEBUG = false;
    public static final boolean IGNORE_SOUND = false; // For faster loading on mobile
    public static final boolean USE_MINI_AUDIO = true;
    private static SoundEngine soundEngine = SoundEngine.MINIAUDIO_3D;
    public static final boolean FORCE_PLAY = true;
    private final HashMap<String, SoundInternal> sounds = new HashMap<>();
    private final ArrayList<ActiveSound> activeSounds = new ArrayList<>(MAX_CONCURRENT_SOUNDS);
    //	private Context context;
    private MiniAudio miniAudio;
    private final HashMap<String, SoundInternalMiniAudio> soundsMiniAudio = new HashMap<>();
    private final ArrayList<ActiveSoundMiniAudio> activeSoundsMiniAudio = new ArrayList<>(MAX_CONCURRENT_SOUNDS);
    private final HashMap<Long, ActiveSoundMiniAudio> addressToSoundMiniAudioMap = new HashMap<>();
    private final ArrayList<ActiveSoundMiniAudio> endedSounds = new ArrayList<>(MAX_CONCURRENT_SOUNDS);
    private final ENG_Vector4D listenerPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D listenerFrontDirection = new ENG_Vector4D();
    private final ENG_Vector4D listenerUpDirection = new ENG_Vector4D(0.0f, 1.0f, 0.0f, 0.0f);
    private final ENG_Vector4D listenerVelocity = new ENG_Vector4D();

    private final boolean soundAvailable = true;

    /**
     * The constructor with arguments instantiates the audioManager object
     *
     * @param aContext The Context in which the stream will be played
     */
    public ENG_SoundManager(/*Context aContext*/) {

        if (IGNORE_SOUND) {
            return;
        }
        if (isMiniAudioBased()) {
            if (MainApp.PLATFORM != MainApp.Platform.IOS) {
//                throw new IllegalStateException("MiniAudio only supported on iOS");
            }
            //Create only one MiniAudio object!
            miniAudio = new MiniAudio();
        } else {

//		context = aContext;
            //	private SoundPool player;
//        Audio audioManager = Gdx.audio;
//				(AudioManager) context
//				.getSystemService(Context.AUDIO_SERVICE);
        }
    }


    /**
     * Instantiates the HashMap and the SoundPool fields Loads all the sounds
     * into the HashMap
     */
    public void loadSounds() {

	/*	player = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        if (player == null) {
			if (MainActivity.isDebugmode()) {
				throw new NullPointerException(
						"Sound player could not be initialized");
			} else {
				// Set sound not supported
				soundAvailable = false;
			}
		}*/
        //	sounds = new HashMap<String, Integer>();

        // sounds.put("explosion", player.load(context, R.raw.dulce, 1));
        // sounds.put("collision", player.load(context, R.raw.omfg, 1));
        // sounds.put("launch", player.load(context, R.raw.sursa, 1));

    }

    public long playSound(String name, int volume) {
        return playSound(name, volume, false);
    }

    /**
     * @param name Name of the stream to be played
     */
    public long playSound(String name) {
		
	/*	if (soundAvailable) {

		//	float volume = (float) getVolume(name) / 10;
			SoundInternal sound = sounds.get(name);
			if (sound == null) {
				throw new IllegalArgumentException(name + " is not a valid sound name");
			}
	
			player.play(sound.soundID, sound.leftVolume, sound.rightVolume, 10, 0, 1);
		}*/
        return playSound(name, 100, false);

    }

    /**
     * @param name Name of the stream to be played
     * @param loop If is -1 the stream will be looping
     *             returning -1 means invalid sound
     */
    public long playSound(String name, int volume, boolean isLooping) {

        return playSound(name, volume, 0.0f, isLooping);

    }

    public long playSound(String name, int volume, float pan, boolean isLooping) {
        if (IGNORE_SOUND) {
            return -1;
        }

        if (volume == 0) {
            return -1;
        }

        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("volume sent is: " + volume);
        }
        long playID = -1;
        if (soundAvailable) {
            if (soundEngine == SoundEngine.MINIAUDIO) {
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                if (!shouldPlaySound(sound)) {
                    return playID;
                }
                float vol = (float) volume / (float) MAX_SOUND_VOLUME;
                vol = ENG_Math.clamp(vol, 0.0f, 1.0f);

                pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
                MASound soundID = miniAudio.createSound(sound.getFilename());
                playID = soundID.getAddress();
                soundID.setVolume(vol);
                soundID.setPan(pan);
                soundID.setLooping(isLooping);
                soundID.play();
                ActiveSoundMiniAudio activeSoundMiniAudio = new ActiveSoundMiniAudio(playID, ENG_Utility.currentTimeMillis(),
                        sound, soundID, volume == 0 ? SOUND_LOWEST_PRIORITY : sound.getPriority(), isLooping);
                activeSoundsMiniAudio.add(activeSoundMiniAudio);
                ActiveSoundMiniAudio put = addressToSoundMiniAudioMap.put(playID, activeSoundMiniAudio);
                if (put != null) {
                    // Should this situation ever happen?
                    if (DEBUG) {
                        System.out.println("playID: " + playID + " already exists and it's use by: " + put.getSound().getName());
                    }
                }
            } else if (soundEngine == SoundEngine.LIBGDX) {
                //	float volume = (float) getVolume(name) / 10;
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                if (!shouldPlaySound(sound)) {
                    return playID;
                }
		/*	player.play(sound.soundID, sound.leftVolume, sound.rightVolume, 10,
					isLooping, 1);*/
                float vol = (float) volume / (float) MAX_SOUND_VOLUME;
                vol = ENG_Math.clamp(vol, 0.0f, 1.0f);

                pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
                if (isLooping) {
                    playID = sound.getSoundID().loop(vol, 1.0f, pan);
                } else {
                    playID = sound.getSoundID().play(vol, 1.0f, pan);
                }
                if (playID == -1) {
//				throw new ENG_SoundException("Could not play sound: " + name);
                    System.out.println("Could not play sound: " + name);
                } else {
                    activeSounds.add(new ActiveSound(playID, ENG_Utility.currentTimeMillis(),
                            sound, volume == 0 ? SOUND_LOWEST_PRIORITY : sound.getPriority(), isLooping));
                }
            }
            if (DEBUG) {
//                System.out.println("Starting sound " + name + " playId " + playID + " sound volume: " + volume + " pan: " + pan);
            }

        }
        return playID;
    }

    private boolean shouldPlaySound(SoundInternalMiniAudio soundToBePlayed) {
        // Remove sounds that have finished playing from the active sounds list.
        removeFinishedSounds();

        if (DEBUG) {
//            System.out.println("Current active sounds num: " + activeSoundsMiniAudio.size());
//            System.out.println("Attempting to play " + soundToBePlayed.getName() + " with default priority " + soundToBePlayed.getPriority());
        }

        // If we still have all sounds playing we need to try to make room for the new
        // sound based on priority.
        if (activeSoundsMiniAudio.size() == MAX_CONCURRENT_SOUNDS) {
            if (DEBUG) {
                System.out.println("Max concurrent sounds " + MAX_CONCURRENT_SOUNDS + " reached");
            }
            ActiveSoundMiniAudio activeSoundWithLowestPriority = null;
            for (ActiveSoundMiniAudio activeSound : activeSoundsMiniAudio) {
                if (activeSoundWithLowestPriority == null ||
                        activeSound.getCurrentPriority() > activeSoundWithLowestPriority.getCurrentPriority()) {
                    activeSoundWithLowestPriority = activeSound;
                }
            }
            // If there were more with the lowest priority in the active sounds list
            // then we selected the one longest playing. If the new sound has the same
            // priority then we evict the one with the same priority but longest playing.
            if (activeSoundWithLowestPriority.getCurrentPriority() >= soundToBePlayed.getPriority()) {
                if (DEBUG) {
                    System.out.println("Evicting sound " +
                            activeSoundWithLowestPriority.getSound().getName() +
                            " with lowest priority " +
                            activeSoundWithLowestPriority.getCurrentPriority() +
                            " to make room for " + soundToBePlayed.getName() +
                            " with priority " + soundToBePlayed.getPriority());
                }
                activeSoundsMiniAudio.remove(activeSoundWithLowestPriority);
                stopSound(activeSoundWithLowestPriority.getSound(), activeSoundWithLowestPriority.getPlayId());
                disposeSound(activeSoundWithLowestPriority.getSoundMiniAudio());
            } else {
                if (DEBUG) {
                    System.out.println("Sound " + soundToBePlayed.getName() + " with default priority " + soundToBePlayed.getPriority() + " could not be played");
                }
                return false;
            }
        }
        return true;
    }

    private boolean shouldPlaySound(SoundInternal soundToBePlayed) {
        // Remove sounds that have finished playing from the active sounds list.
        removeFinishedSounds();

        if (DEBUG) {
//            System.out.println("Current active sounds num: " + activeSounds.size());
//            System.out.println("Attempting to play " + soundToBePlayed.getName() + " with default priority " + soundToBePlayed.getPriority());
        }

        // If we still have all sounds playing we need to try to make room for the new
        // sound based on priority.
        if (activeSounds.size() == MAX_CONCURRENT_SOUNDS) {
            if (DEBUG) {
                System.out.println("Max concurrent sounds " + MAX_CONCURRENT_SOUNDS + " reached");
            }
            ActiveSound activeSoundWithLowestPriority = null;
            for (ActiveSound activeSound : activeSounds) {
                if (activeSoundWithLowestPriority == null ||
                        activeSound.getCurrentPriority() > activeSoundWithLowestPriority.getCurrentPriority()) {
                    activeSoundWithLowestPriority = activeSound;
                }
            }
            // If there were more with the lowest priority in the active sounds list
            // then we selected the one longest playing. If the new sound has the same
            // priority then we evict the one with the same priority but longest playing.
            if (activeSoundWithLowestPriority.getCurrentPriority() >= soundToBePlayed.getPriority()) {
                if (DEBUG) {
                    System.out.println("Evicting sound " +
                            activeSoundWithLowestPriority.getSound().getName() +
                            " with lowest priority " +
                            activeSoundWithLowestPriority.getCurrentPriority() +
                            " to make room for " + soundToBePlayed.getName() +
                            " with priority " + soundToBePlayed.getPriority());
                }
                activeSounds.remove(activeSoundWithLowestPriority);
                stopSound(activeSoundWithLowestPriority.getSound(), activeSoundWithLowestPriority.getPlayId());
            } else {
                if (DEBUG) {
                    System.out.println("Sound " + soundToBePlayed.getName() + " with default priority " + soundToBePlayed.getPriority() + " could not be played");
                }
                return false;
            }
        }
        return true;
    }

    private void removeFinishedSounds() {
        if (soundEngine == SoundEngine.MINIAUDIO) {
            for (Iterator<ActiveSoundMiniAudio> iterator = activeSoundsMiniAudio.iterator(); iterator.hasNext(); ) {
                ActiveSoundMiniAudio activeSound = iterator.next();
                if (!activeSound.isLooping() && ENG_Utility.hasTimePassed(FrameInterval.SOUND_SHOULD_PLAY_SOUND +
                                activeSound.getSound().getName() + " " +
                                activeSound.getPlayId(),
                        activeSound.getStartTime(),
                        activeSound.getSound().getDuration())) {
//                System.out.println("Removing sound that finished playing " + activeSound.getSound().getName() +
//                        " with play id " + activeSound.getPlayId() +
//                        " and priority " + activeSound.getCurrentPriority());
                    iterator.remove();
                    ActiveSoundMiniAudio remove = addressToSoundMiniAudioMap.remove(activeSound.getPlayId());
                    if (remove == null) {
                        if (DEBUG) {
                            System.out.println("Sound " + activeSound.getSound().getName() +
                                    " with default priority " + activeSound.getSound().getPriority() +
                                    " could not be removed after finishind playing");
                        }
                    }
                }
            }
        } else if (soundEngine == SoundEngine.LIBGDX) {
            for (Iterator<ActiveSound> iterator = activeSounds.iterator(); iterator.hasNext(); ) {
                ActiveSound activeSound = iterator.next();
                if (!activeSound.isLooping() && ENG_Utility.hasTimePassed(FrameInterval.SOUND_SHOULD_PLAY_SOUND +
                                activeSound.getSound().getName() + " " +
                                activeSound.getPlayId(),
                        activeSound.getStartTime(),
                        activeSound.getSound().getDuration())) {
//                System.out.println("Removing sound that finished playing " + activeSound.getSound().getName() +
//                        " with play id " + activeSound.getPlayId() +
//                        " and priority " + activeSound.getCurrentPriority());
                    iterator.remove();
                }
            }
        }
    }

    public void playSoundObject(long id, boolean loop) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio != null) {
            MASound soundMiniAudio = activeSoundMiniAudio.getSoundMiniAudio();
            soundMiniAudio.setLooping(loop);
            soundMiniAudio.play();
            if (DEBUG) {
//                System.out.println("playSoundObject playId: " + id + " loop: " + loop);
            }
        } else {
            if (DEBUG) {
                System.out.println("Sound with playId: " + id + " could not be played");
            }
        }
    }

    public long createSoundObject(String name) {
        if (soundEngine == SoundEngine.MINIAUDIO_3D) {
            if (IGNORE_SOUND) {
                return -1;
            }

            long playID = -1;
            if (soundAvailable) {
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                MASound soundID = miniAudio.createSound(sound.getFilename());
                playID = soundID.getAddress();
                ActiveSoundMiniAudio activeSoundMiniAudio = new ActiveSoundMiniAudio(playID, sound, soundID);
                activeSoundsMiniAudio.add(activeSoundMiniAudio);
                ActiveSoundMiniAudio put = addressToSoundMiniAudioMap.put(playID, activeSoundMiniAudio);
                if (put != null) {
                    // Should this situation ever happen?
                    if (DEBUG) {
                        System.out.println("playID: " + playID + " already exists and it's use by: " + put.getSound().getName());
                    }
                }
                if (DEBUG) {
//                    System.out.println("Sound created: " + name + " playId: " + playID);
                }
            }
            return playID;
        } else {
            throw new IllegalStateException("Only works if MiniAudio 3D positioning enabled!");
        }
    }

    private boolean isSoundActive(long playId) {
        if (soundEngine == SoundEngine.MINIAUDIO_3D) {
            return false;
        } else if (soundEngine == SoundEngine.MINIAUDIO) {
            for (ActiveSoundMiniAudio activeSound : activeSoundsMiniAudio) {
                if (activeSound.getPlayId() == playId) {
                    return true;
                }
            }
            return false;
        } else if (soundEngine == SoundEngine.LIBGDX) {
            for (ActiveSound activeSound : activeSounds) {
                if (activeSound.getPlayId() == playId) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public long getSoundDuration(String name) {
        if (IGNORE_SOUND) {
            return 0;
        }
        if (isMiniAudioBased()) {
            SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            return sound.getDuration();
        } else if (soundEngine == SoundEngine.LIBGDX) {
            SoundInternal sound = sounds.get(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            return sound.getDuration();
        }
        throw new IllegalStateException("soundEngine should not be null!");
    }

    /**
     * Loads a sound into the HashMap and to the SoundPool itself
     *  @param name Name of the sound to be loaded for a specified path The path
     *             is specified in the class field "path"
     * @param duration
     * @param priority
     */
    public void loadSound(String name, String filename, long duration, int priority) {

//		throw new UnsupportedOperationException("Must use the id version");

//		String baseName = FilenameUtils.getBaseName(name);
        if (IGNORE_SOUND) {
            return;
        }
        if (isMiniAudioBased()) {
            System.out.println("Loading sound file without raw/: " + filename);
            soundsMiniAudio.put(name, new SoundInternalMiniAudio(name, filename, duration, priority));
        } else {
            sounds.put(name, new SoundInternal(name, Gdx.audio.newSound(Gdx.files.local(filename)), duration, priority));
        }
    }

    public void loadSound(String name, int handle) {
//		sounds.put(name, new SoundInternal(player.load(context, handle, 0)));
        throw new UnsupportedOperationException();
    }

    /**
     * Pauses a stream from playing
     *
     * @param name Name of the sound to be paused
     */
    public void pauseSound(String name) {
        if (IGNORE_SOUND) {
            return;
        }

        if (soundAvailable) {
            if (soundEngine == SoundEngine.MINIAUDIO) {
                // it will have no effect if the stream is not playing
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//			player.pause(sound.soundID);
//			sound.soundID.
			    throw new UnsupportedOperationException("pauseSound(String name) not supported");
//                sound.getFilename().pause();
            } else if (soundEngine == SoundEngine.LIBGDX) {
                // it will have no effect if the stream is not playing
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//			player.pause(sound.soundID);
//			sound.soundID.
//			throw new UnsupportedOperationException();
                sound.getSoundID().pause();
            }
        }

    }

    /**
     * TODO pausing a playing sound will fool the calculation in removeFinishedSounds()
     * as it does not take into account the paused time and calculates it as
     * playing time.
     * @param name
     * @param id
     */
    public void pauseSound(String name, long id) {
        if (IGNORE_SOUND) {
            return;
        }
        if (soundAvailable) {
            if (isMiniAudioBased()) {
                // it will have no effect if the stream is not playing
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//			player.pause(sound.soundID);
//			sound.soundID.
//			throw new UnsupportedOperationException("pauseSound(String name, long id) not supported");
                ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
                if (activeSoundMiniAudio != null) {
                    activeSoundMiniAudio.getSoundMiniAudio().pause();
                } else {
                    if (DEBUG) {
                        System.out.println("Sound name: " + name + " with playId: " + id + " could not be paused");
                    }
                }
            } else if (soundEngine == SoundEngine.LIBGDX) {
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                sound.getSoundID().pause(id);
            }
        }
    }

    public void resumeSound(String name) {
        if (IGNORE_SOUND) {
            return;
        }
        if (soundAvailable) {
            if (isMiniAudioBased()) {
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//                sound.getFilename().play();
                throw new UnsupportedOperationException("resumeSound(String name) not supported");
            } else if (soundEngine == SoundEngine.LIBGDX) {
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                sound.getSoundID().resume();
            }
        }
    }

    public void resumeSound(String name, long id) {
        if (IGNORE_SOUND) {
            return;
        }
        if (soundAvailable) {
            if (soundEngine == SoundEngine.MINIAUDIO) {
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//                sound.getFilename().play();
                throw new UnsupportedOperationException("resumeSound(String name, long id) not supported");
            } else if (soundEngine == SoundEngine.LIBGDX) {
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                sound.getSoundID().resume(id);
            }
        }
    }

    /**
     * Stops a stream from playing
     *
     * @param name Name of the sound to be stopped
     */
    public void stopSound(String name) {

        if (IGNORE_SOUND) {
            return;
        }

        if (soundAvailable) {
            if (isMiniAudioBased()) {
                // it will have no effect if the stream is not playing
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//			player.stop(sound.soundID);
//                sound.getFilename().stop();
                throw new UnsupportedOperationException("stopSound(String name) not supported");
            } else if (soundEngine == SoundEngine.LIBGDX) {
                // it will have no effect if the stream is not playing
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
//			player.stop(sound.soundID);
                sound.getSoundID().stop();
            }
        }

    }

    public void stopSound(String name, long id) {
        if (IGNORE_SOUND) {
            return;
        }
        if (soundAvailable) {
            if (isMiniAudioBased()) {
                SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                for (Iterator<ActiveSoundMiniAudio> iterator = activeSoundsMiniAudio.iterator(); iterator.hasNext(); ) {
                    ActiveSoundMiniAudio activeSound = iterator.next();
                    if (activeSound.getPlayId() == id) {
                        if (DEBUG) {
                            System.out.println("Removing sound by stopping " + activeSound.getSound().getName() +
                                    " with play id " + activeSound.getPlayId() +
                                    " and" +
                                    " priority " + activeSound.getCurrentPriority());
                        }
                        iterator.remove();
                    }
                }

                stopSound(sound, id);
            } else if (soundEngine == SoundEngine.LIBGDX) {
                SoundInternal sound = sounds.get(name);
                if (sound == null) {
                    throw new IllegalArgumentException(name + " is not a valid sound name");
                }
                for (Iterator<ActiveSound> iterator = activeSounds.iterator(); iterator.hasNext(); ) {
                    ActiveSound activeSound = iterator.next();
                    if (activeSound.getPlayId() == id) {
                        if (DEBUG) {
                            System.out.println("Removing sound by stopping " + activeSound.getSound().getName() +
                                    " with play id " + activeSound.getPlayId() +
                                    " and" +
                                    " priority " + activeSound.getCurrentPriority());
                        }
                        iterator.remove();
                    }
                }

                stopSound(sound, id);
            }
        }
    }

    private void stopSound(SoundInternal sound, long id) {
        sound.getSoundID().stop(id);
    }

    private void stopSound(SoundInternalMiniAudio sound, long id) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio != null) {
            activeSoundMiniAudio.getSoundMiniAudio().stop();
            addressToSoundMiniAudioMap.remove(id);
        } else {
            if (DEBUG) {
                System.out.println("Sound name: " + sound.getName() + " with playId: " + id + " could not be stopped");
            }
        }
    }

    public void setVolume(String name, long id, int volume, boolean vibrate) {
        if (IGNORE_SOUND) {
            return;
        }
        volume = ENG_Math.clamp(volume, 0, MAX_SOUND_VOLUME);
        if (soundEngine == SoundEngine.MINIAUDIO) {
            SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            float vol = volume * 0.01f;
            vol = ENG_Math.clamp(vol, 0.0f, 1.0f);
            if (DEBUG) {
                System.out.println(name + " playId " + id + " sound volume: " + volume);
            }
            ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
            if (activeSoundMiniAudio != null) {
                activeSoundMiniAudio.getSoundMiniAudio().setVolume(vol);
            } else {
                if (DEBUG) {
                    System.out.println("Sound name: " + name + " with playId: " + id + " could not have volume set to: " + vol);
                }
            }
//        sound.leftVolume = vol;
//        sound.rightVolume = vol;
        } else if (soundEngine == SoundEngine.LIBGDX) {
            SoundInternal sound = sounds.get(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            float vol = volume * 0.01f;
            vol = ENG_Math.clamp(vol, 0.0f, 1.0f);
            if (DEBUG) {
                System.out.println(name + " playId " + id + " sound volume: " + volume);
            }
            sound.getSoundID().setVolume(id, vol);
//        sound.leftVolume = vol;
//        sound.rightVolume = vol;
        }
    }

    /**
     * Sets the Volume for a specified stream
     *
     * @param name   The name of the stream
     * @param volume The volume
     */
    public void setVolume(String name, int volume, boolean vibrate) {
        if (IGNORE_SOUND) {
            return;
        }
        volume = ENG_Math.clamp(volume, 0, MAX_SOUND_VOLUME);

        SoundInternal sound = sounds.get(name);
        if (sound == null) {
            throw new IllegalArgumentException(name + " is not a valid sound name");
        }
	/*	int streamMaxVolume = audioManager.getStreamMaxVolume(integer);
		// Map volume between 0 - 100 to 0 - streamMaxVolume
		float mapping = ((float) volume) * 0.01f;
		audioManager.setStreamVolume(integer, (int) (mapping * streamMaxVolume),
				AudioManager.FLAG_VIBRATE);
				*/
        float vol = volume * 0.01f;
        vol = ENG_Math.clamp(vol, 0.0f, 1.0f);
        if (DEBUG) {
            System.out.println(name + " sound volume: " + volume);
        }
//		player.setVolume(sound.soundID, vol, vol);
//        sound.leftVolume = vol;
//        sound.rightVolume = vol;
    }

    public void setVolume(String name, int leftVolume, int rightVolume,
                          boolean vibrate) {
        if (IGNORE_SOUND) {
            return;
        }
        leftVolume = ENG_Math.clamp(leftVolume, 0, MAX_SOUND_VOLUME);
        rightVolume = ENG_Math.clamp(rightVolume, 0, MAX_SOUND_VOLUME);

        SoundInternal sound = sounds.get(name);
        if (sound == null) {
            throw new IllegalArgumentException(name + " is not a valid sound name");
        }
	/*	int streamMaxVolume = audioManager.getStreamMaxVolume(integer);
		// Map volume between 0 - 100 to 0 - streamMaxVolume
		float mapping = ((float) volume) * 0.01f;
		audioManager.setStreamVolume(integer, (int) (mapping * streamMaxVolume),
				AudioManager.FLAG_VIBRATE);
				*/
        float lvol = leftVolume * 0.01f;
        float rvol = rightVolume * 0.01f;
        if (DEBUG) {
            System.out.println(name + " sound volume: " + leftVolume);
            System.out.println(name + " sound volume: " + rightVolume);
        }
//		player.setVolume(sound.soundID, lvol, rvol);
//        sound.leftVolume = lvol;
//        sound.rightVolume = rvol;
    }

    /**
     * Returns an int representing the volume of a specified stream @ name The
     * name of the stream
     */
    public int getVolume(String name) {
        return 0;
//        if (IGNORE_SOUND) {
//            return 0;
//        }
//
//        SoundInternal sound = sounds.get(name);
//        if (sound == null) {
//            throw new IllegalArgumentException(name + " is not a valid sound name");
//        }
//        //	int volume = audioManager.getStreamVolume(integer);
//
//        return (int) ((sound.leftVolume + sound.rightVolume) * 0.5f * 100.0f);
    }

    public int getLeftVolume(String name) {
        return 0;
//        if (IGNORE_SOUND) {
//            return 0;
//        }
//        SoundInternal sound = sounds.get(name);
//        if (sound == null) {
//            throw new IllegalArgumentException(name + " is not a valid sound name");
//        }
//        //	int volume = audioManager.getStreamVolume(integer);
//
//        return (int) (sound.leftVolume * 100.0f);
    }

    public int getRightVolume(String name) {
        return 0;
//        if (IGNORE_SOUND) {
//            return 0;
//        }
//        SoundInternal sound = sounds.get(name);
//        if (sound == null) {
//            throw new IllegalArgumentException(name + " is not a valid sound name");
//        }
//        //	int volume = audioManager.getStreamVolume(integer);
//
//        return (int) (sound.rightVolume * 100.0f);
    }

    /**
     * @param name The name of the stream
     * @return If true, it means the stream exists in the hashMap
     */

    public boolean fileExists(String name) {

        if (isMiniAudioBased()) {
            return soundsMiniAudio.containsKey(name);
        } else {
            return sounds.containsKey(name);
        }

    }

    /**
     * Initiates the path variable
     *
     * @param name The path itself
     */
    public void setPath(String name) {

    }

    public void disposeSound(MASound sound) {
        sound.dispose();
    }

    public void disposeSound(String name) {
        
        if (IGNORE_SOUND) {
            return;
        }
        if (isMiniAudioBased()) {
            SoundInternalMiniAudio sound = soundsMiniAudio.remove(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
//            sound.getFilename().dispose();
            throw new UnsupportedOperationException("Cannot dispose without playId.");
        } else {
            SoundInternal sound = sounds.remove(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            sound.getSoundID().dispose();
        }
    }


    public void setPanAndVolume(String name, long id, float pan, int volume) {
        
        if (IGNORE_SOUND) {
            return;
        }

        // Optimization to no longer pan and set volume for stopped sounds.
        removeFinishedSounds();
        if (!isSoundActive(id)) {
            return;
        }

        if (soundEngine == SoundEngine.MINIAUDIO) {
            SoundInternalMiniAudio sound = soundsMiniAudio.get(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            float vol = volume * 0.01f;
            vol = ENG_Math.clamp(vol, 0.0f, 1.0f);
            pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
            if (DEBUG) {
//            System.out.println(name + " playId " + id + " sound volume: " + volume + " pan: " + pan);
            }
//		sound.soundID.setVolume(id, vol);
//        sound.leftVolume = vol;
//        sound.rightVolume = vol;
            pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
            ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
            if (activeSoundMiniAudio != null) {
                MASound soundMiniAudio = activeSoundMiniAudio.getSoundMiniAudio();
                soundMiniAudio.setVolume(vol);
                soundMiniAudio.setPan(pan);
            } else {
                if (DEBUG) {
                    System.out.println("Sound name: " + name + " with playId: " + id + " could not be panned to: " + pan);
                }
            }
        } else if (soundEngine == SoundEngine.LIBGDX) {
            SoundInternal sound = sounds.get(name);
            if (sound == null) {
                throw new IllegalArgumentException(name + " is not a valid sound name");
            }
            float vol = volume * 0.01f;
            vol = ENG_Math.clamp(vol, 0.0f, 1.0f);
            pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
            if (DEBUG) {
//            System.out.println(name + " playId " + id + " sound volume: " + volume + " pan: " + pan);
            }
//		sound.soundID.setVolume(id, vol);
//        sound.leftVolume = vol;
//        sound.rightVolume = vol;
            pan = ENG_Math.clamp(pan, -1.0f, 1.0f);
            sound.getSoundID().setPan(id, pan, vol);
        }
    }


    public ENG_Vector4D getListenerPosition() {
        return listenerPosition;
    }
    public void setListenerPosition(ENG_Vector4D position) {
        listenerPosition.set(position);
        if (DEBUG) {
//            System.out.println("listenerPosition set: " + position);
        }
    }

    public ENG_Vector4D getListenerFrontDirection() {
        return listenerFrontDirection;
    }

    public void setListenerFrontDirection(ENG_Vector4D frontDir) {
        listenerFrontDirection.set(frontDir);
        if (DEBUG) {
//            System.out.println("listener front direction set: " + frontDir);
        }
    }

    public ENG_Vector4D getListenerUpDirection() {
        return listenerUpDirection;
    }

    public void setListenerUpDirection(ENG_Vector4D upDir) {
        listenerUpDirection.set(upDir);
        if (DEBUG) {
//            System.out.println("listener up direction set: " + upDir);
        }
    }

    public ENG_Vector4D getListenerVelocity() {
        return listenerVelocity;
    }

    public void setListenerVelocity(ENG_Vector4D velocity) {
        listenerVelocity.set(velocity);
        if (DEBUG) {
//            System.out.println("listener velocity set: " + velocity);
        }
    }

    public ENG_Vector4D getSoundVelocity(long id) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound velocity for playId: " + id);
                return null;
            }
            throw new IllegalArgumentException("Could not find sound velocity for playId: " + id);
        }
        return activeSoundMiniAudio.getPlayableSoundVelocity();
    }

    public void setSoundVelocity(long id, ENG_Vector4D soundVelocity) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound velocity for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound velocity for playId: " + id);
        }
        activeSoundMiniAudio.setPlayableSoundVelocity(soundVelocity);
        if (DEBUG) {
//            System.out.println("soundVelocity set for playId " + id + " velocity: " + soundVelocity);
        }
    }

    public ENG_Vector4D getPosition(long id) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound position for playId: " + id);
                return null;
            }
            throw new IllegalArgumentException("Could not find sound position for playId: " + id);
        }
        return activeSoundMiniAudio.getPlayablePosition();
    }

    public void setPosition(long id, ENG_Vector4D position) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound position for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound position for playId: " + id);
        }
        activeSoundMiniAudio.setPlayablePosition(position);
        if (DEBUG) {
//            System.out.println("setPosition playId: " + id + " position: " + position);
        }
    }

    public ENG_Vector4D getFrontDirection(long id) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound front dir for playId: " + id);
                return null;
            }
            throw new IllegalArgumentException("Could not find sound front dir for playId: " + id);
        }
        return activeSoundMiniAudio.getPlayableFrontDirection();
    }

    public void setFrontDirection(long id, ENG_Vector4D frontDir) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound front dir for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound front dir for playId: " + id);
        }
        activeSoundMiniAudio.setPlayableFrontDirection(frontDir);
        if (DEBUG) {
//            System.out.println("setFrontDirection playId: " + id + " frontDir: " + frontDir);
        }
    }

    public float getDopplerFactor(long id) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound doppler factor for playId: " + id);
                return 1.0f;
            }
            throw new IllegalArgumentException("Could not find sound doppler factor for playId: " + id);
        }
        return activeSoundMiniAudio.getPlayableDopplerFactor();
    }

    public void setDopplerFactor(long id, float dopplerFactor) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound doppler factor for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound doppler factor for playId: " + id);
        }
        activeSoundMiniAudio.setPlayableDopplerFactor(dopplerFactor);
        if (DEBUG) {
//            System.out.println("setDopplerFactor playId: " + id + " dopplerFactor: " + dopplerFactor);
        }
    }

    public boolean isSoundEnded(long id) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            for (ActiveSoundMiniAudio endedSound : endedSounds) {
                if (endedSound.getPlayId() == id) {
//                    System.out.println("Found ended sound in endedSounds");
                    return true;
                }
            }

            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound ended for playId: " + id);
                return false;
            }
            throw new IllegalArgumentException("Could not find sound ended for playId: " + id);
        }
        return !activeSoundMiniAudio.isLooping() && activeSoundMiniAudio.getSoundMiniAudio().isEnd();
    }

    public void setSoundCone(long id, float innerAngleInRadians, float outerAngleInRadians, float outerGain) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound cone for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound cone for playId: " + id);
        }
        activeSoundMiniAudio.setCone(innerAngleInRadians, outerAngleInRadians, outerGain);
        if (DEBUG) {
//            System.out.println("setSoundCone playId: " + id + " innerAngleInRadians: " + innerAngleInRadians + " outerAngleInRadians:" + outerAngleInRadians + " outerGain: " + outerGain);
        }
    }

    public void setSoundAttenuationModel(long id, MAAttenuationModel maAttenuationModel) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound attenuation model for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound attenuation model for playId: " + id);
        }
        activeSoundMiniAudio.getSoundMiniAudio().setAttenuationModel(maAttenuationModel);
        if (DEBUG) {
//            System.out.println("setSoundAttenuationModel playId: " + id + " maAttenuationModel: " + maAttenuationModel);
        }
    }

    public void setSoundRolloff(long id, float rolloff) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound rolloff for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound rolloff for playId: " + id);
        }
        activeSoundMiniAudio.getSoundMiniAudio().setRolloff(rolloff);
        if (DEBUG) {
//            System.out.println("setSoundRolloff playId: " + id + " rolloff: " + rolloff);
        }
    }

    public void setSoundGainRange(long id, float minGain, float maxGain) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound gain range for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound gain range for playId: " + id);
        }
        activeSoundMiniAudio.getSoundMiniAudio().setGainRange(minGain, maxGain);
        if (DEBUG) {
//            System.out.println("setSoundGainRange playId: " + id + " minGain: " + minGain + " maxGain: " + maxGain);
        }
    }

    public void setSoundDistanceRange(long id, float minDistance, float maxDistance) {
        ActiveSoundMiniAudio activeSoundMiniAudio = addressToSoundMiniAudioMap.get(id);
        if (activeSoundMiniAudio == null) {
            if (isMiniAudioBased() && DEBUG) {
                System.out.println("Could not find sound distance range for playId: " + id);
                return;
            }
            throw new IllegalArgumentException("Could not find sound distance range for playId: " + id);
        }
        activeSoundMiniAudio.getSoundMiniAudio().setDistanceRange(minDistance, maxDistance);
        if (DEBUG) {
//            System.out.println("setSoundDistanceRange playId: " + id + " minDistance: " + minDistance + " maxDistance: " + maxDistance);
        }
    }

    public void updateSoundSystem() {
        endedSounds.clear();
        miniAudio.setListenerPosition(listenerPosition.x, listenerPosition.y, listenerPosition.z);
        miniAudio.setListenerDirection(listenerFrontDirection.x, listenerFrontDirection.y, listenerFrontDirection.z);
        miniAudio.setListenerWorldUp(listenerUpDirection.x, listenerUpDirection.y, listenerUpDirection.z);
        miniAudio.setListenerVelocity(listenerVelocity.x, listenerVelocity.y, listenerVelocity.z);

        for (Iterator<ActiveSoundMiniAudio> iterator = activeSoundsMiniAudio.iterator(); iterator.hasNext(); ) {
            ActiveSoundMiniAudio activeSound = iterator.next();
            if (!activeSound.isLooping() && activeSound.getSoundMiniAudio().isEnd()) {
                if (DEBUG) {
//                    System.out.println("Removing sound that finished playing " + activeSound.getSound().getName() +
//                            " with play id " + activeSound.getPlayId() +
//                            " and priority " + activeSound.getCurrentPriority());
                }
                endedSounds.add(activeSound);
                disposeSound(activeSound.getSoundMiniAudio());
                iterator.remove();
                ActiveSoundMiniAudio remove = addressToSoundMiniAudioMap.remove(activeSound.getPlayId());
                if (remove == null) {
                    if (DEBUG) {
                        System.out.println("Sound " + activeSound.getSound().getName() +
                                " with default priority " + activeSound.getSound().getPriority() +
                                " could not be removed after finishind playing");
                    }
                }
            }
        }

        for (ActiveSoundMiniAudio activeSoundMiniAudio : activeSoundsMiniAudio) {
            ENG_Vector4D soundVelocity = activeSoundMiniAudio.getPlayableSoundVelocity();
            ENG_Vector4D position = activeSoundMiniAudio.getPlayablePosition();
            ENG_Vector4D frontDirection = activeSoundMiniAudio.getPlayableFrontDirection();
            float dopplerFactor = activeSoundMiniAudio.getPlayableDopplerFactor();
            MASound soundMiniAudio = activeSoundMiniAudio.getSoundMiniAudio();
            soundMiniAudio.setVelocity(soundVelocity.x, soundVelocity.y, soundVelocity.z);
            soundMiniAudio.setPosition(position.x, position.y, position.z);
            soundMiniAudio.setDirection(frontDirection.x, frontDirection.y, frontDirection.z);
            soundMiniAudio.setDopplerFactor(dopplerFactor);
//            soundMiniAudio.setCone(activeSoundMiniAudio.getInnerAngleInRadians(), activeSoundMiniAudio.getOuterAngleInRadians(), activeSoundMiniAudio.getOuterGain());
        }

    }

    public void disposeOfAllSounds() {
        
        if (IGNORE_SOUND) {
            return;
        }
        if (isMiniAudioBased()) {
            for (ActiveSoundMiniAudio sound : activeSoundsMiniAudio) {
                sound.getSoundMiniAudio().dispose();
            }
            activeSoundsMiniAudio.clear();
            addressToSoundMiniAudioMap.clear();
            soundsMiniAudio.clear();
        } else {
            for (SoundInternal sound : sounds.values()) {
                sound.getSoundID().dispose();
            }
            sounds.clear();
        }
    }

    public static boolean isMiniAudioBased() {
        return soundEngine == SoundEngine.MINIAUDIO || soundEngine == SoundEngine.MINIAUDIO_3D;
    }

    public static SoundEngine getSoundEngine() {
        return soundEngine;
    }

    /**
     * This needs to be set in each project's specific startup.
     * This cannot be changed later at runtime.
      * @param soundEngine
     */
    public static void setSoundEngine(SoundEngine soundEngine) {
        ENG_SoundManager.soundEngine = soundEngine;
    }
}
