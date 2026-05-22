package audio;

import static com.raylib.Raylib.*; // Jaylib drawing and input functions
import java.io.File; // File system operations
import java.util.HashMap; // Key-value data structure
import java.util.Map; // Map interface for collections

/**
 * Manages all game sounds and music using Raylib.
 * Uses null-object pattern: missing files result in silent no-ops.
 * 
 * Sound files should be placed in the 'assets/sounds/' directory:
 *   - background.mp3
 *   - boss_killed.wav
 *   - explosion.wav
 *   - hit_enemy.mp3
 *   - kunai.wav
 *   - menu_select.mp3
 *   - player_dead.wav
 *   - player_hurt.wav
 */
public class SoundManager {
    
    private static SoundManager instance;
    
    // Sound clips cache
    private Map<String, Sound> soundClips;
    
    // Background music
    private Music backgroundMusic;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private boolean startupWarningShown = false;
    
    // Volume controls (0.0 to 1.0)
    private float musicVolume = 0.2f;
    private float soundVolume = 0.85f;
    
    private static final String SOUNDS_DIR = "sounds" + File.separator;
    public static final String SOUND_BACKGROUND = "background";
    public static final String SOUND_HIT_ENEMY = "hit_enemy";
    public static final String SOUND_PLAYER_DEAD = "player_dead";
    public static final String SOUND_PLAYER_HURT = "player_hurt";
    public static final String SOUND_BOSS_KILLED = "boss_killed";
    public static final String SOUND_EXPLOSION = "explosion";
    public static final String SOUND_KUNAI = "kunai";
    public static final String SOUND_MENU_SELECT = "menu_select";
    
    private SoundManager() {
        InitAudioDevice(); // Initialize Raylib audio
        soundClips = new HashMap<>();
        loadAllSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void loadAllSounds() {
        // Load all game sounds - missing files are handled gracefully
        loadSound(SOUND_HIT_ENEMY, SOUNDS_DIR + "hit_enemy.mp3");
        loadSound(SOUND_PLAYER_DEAD, SOUNDS_DIR + "player_dead.wav");
        loadSound(SOUND_PLAYER_HURT, SOUNDS_DIR + "player_hurt.wav");
        loadSound(SOUND_BOSS_KILLED, SOUNDS_DIR + "boss_killed.wav");
        loadSound(SOUND_EXPLOSION, SOUNDS_DIR + "explosion.wav");
        loadSound(SOUND_KUNAI, SOUNDS_DIR + "kunai.wav");
        loadSound(SOUND_MENU_SELECT, SOUNDS_DIR + "menu_select.mp3");
        
        // Load background music separately
        loadBackgroundMusic(SOUNDS_DIR + "background.mp3");
    }
    
    private void loadSound(String name, String filepath) {
        File soundFile = new File(filepath);
        if (!soundFile.exists()) {
            if (!startupWarningShown) {
                System.out.println("[SoundManager] Note: Some sound files are missing. Game will run without audio.");
                startupWarningShown = true;
            }
            return;
        }
        
        Sound sound = LoadSound(filepath);
        soundClips.put(name, sound);
    }
    
    private void loadBackgroundMusic(String filepath) {
        File musicFile = new File(filepath);
        if (!musicFile.exists()) {
            return; // Silent fail - missing music is non-fatal; game runs without audio
        }
        
        backgroundMusic = LoadMusicStream(filepath);
        backgroundMusic.looping(true);
    }
    
    /**
     * Play a sound effect once
     */
    public void playSound(String soundName) {
        if (!soundEnabled) return;
        
        Sound sound = soundClips.get(soundName);
        if (sound != null && sound.stream() != null) {
            SetSoundVolume(sound, soundVolume);
            PlaySound(sound);
        }
    }
    
    /**
     * Play an effect by filename (for dynamic sound loading).
     * Strips the file extension before looking up the clip, so callers can pass either
     * a plain name like "hit_enemy" or a filename like "hit_enemy.wav" interchangeably.
     */
    public void playEffect(String filename) {
        if (!soundEnabled) return;
        
        // Extract name from filename (e.g., "boss_phase.wav" -> "boss_phase")
        String name = filename.replace(".wav", "").replace(".mp3", "").replace(SOUNDS_DIR, "");
        
        // Only play if it is one of our loaded sounds
        if (soundClips.containsKey(name)) {
            playSound(name);
        }
    }
    
    /**
     * Call this every frame to keep music playing
     */
    public void updateMusic() {
        if (musicEnabled && backgroundMusic != null && backgroundMusic.stream() != null) {
            UpdateMusicStream(backgroundMusic);
        }
    }
    
    /**
     * Start playing background music
     */
    public void startBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null || backgroundMusic.stream() == null) return;
        
        SetMusicVolume(backgroundMusic, 0.2f);
        PlayMusicStream(backgroundMusic);
    }
    
    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.stream() != null && IsMusicStreamPlaying(backgroundMusic)) {
            StopMusicStream(backgroundMusic);
        }
    }
    
    /**
     * Pause background music
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.stream() != null && IsMusicStreamPlaying(backgroundMusic)) {
            PauseMusicStream(backgroundMusic);
        }
    }
    
    /**
     * Resume background music from where it paused
     */
    public void resumeBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null || backgroundMusic.stream() == null) return;
        ResumeMusicStream(backgroundMusic);
    }
    
    // Volume controls
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        if (backgroundMusic != null && backgroundMusic.stream() != null) {
            SetMusicVolume(backgroundMusic, musicVolume);
        }
    }
    
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0, Math.min(1, volume));
    }
    
    public float getMusicVolume() { return musicVolume; }
    public float getSoundVolume() { return soundVolume; }
    
    // Enable/disable
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        }
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSoundEnabled() { return soundEnabled; }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        if (backgroundMusic != null && backgroundMusic.stream() != null) {
            UnloadMusicStream(backgroundMusic);
        }
        for (Sound sound : soundClips.values()) {
            if (sound != null && sound.stream() != null) {
                UnloadSound(sound);
            }
        }
        soundClips.clear();
        CloseAudioDevice(); // Close Raylib audio
    }
}
