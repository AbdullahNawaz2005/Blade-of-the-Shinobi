package audio;

import static com.raylib.Raylib.*; 
import java.io.File; 
import java.util.HashMap; 
import java.util.Map; 















public class SoundManager {
    
    private static SoundManager instance;
    
    
    private Map<String, Sound> soundClips;
    
    
    private Music backgroundMusic;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private boolean startupWarningShown = false;
    
    
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
        InitAudioDevice(); 
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
        
        loadSound(SOUND_HIT_ENEMY, SOUNDS_DIR + "hit_enemy.mp3");
        loadSound(SOUND_PLAYER_DEAD, SOUNDS_DIR + "player_dead.wav");
        loadSound(SOUND_PLAYER_HURT, SOUNDS_DIR + "player_hurt.wav");
        loadSound(SOUND_BOSS_KILLED, SOUNDS_DIR + "boss_killed.wav");
        loadSound(SOUND_EXPLOSION, SOUNDS_DIR + "explosion.wav");
        loadSound(SOUND_KUNAI, SOUNDS_DIR + "kunai.wav");
        loadSound(SOUND_MENU_SELECT, SOUNDS_DIR + "menu_select.mp3");
        
        
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
            return; 
        }
        
        backgroundMusic = LoadMusicStream(filepath);
        backgroundMusic.looping(true);
    }
    
    


    public void playSound(String soundName) {
        if (!soundEnabled) return;
        
        Sound sound = soundClips.get(soundName);
        if (sound != null && sound.stream() != null) {
            SetSoundVolume(sound, soundVolume);
            PlaySound(sound);
        }
    }
    
    




    public void playEffect(String filename) {
        if (!soundEnabled) return;
        
        
        String name = filename.replace(".wav", "").replace(".mp3", "").replace(SOUNDS_DIR, "");
        
        
        if (soundClips.containsKey(name)) {
            playSound(name);
        }
    }
    
    


    public void updateMusic() {
        if (musicEnabled && backgroundMusic != null && backgroundMusic.stream() != null) {
            UpdateMusicStream(backgroundMusic);
        }
    }
    
    


    public void startBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null || backgroundMusic.stream() == null) return;
        
        SetMusicVolume(backgroundMusic, 0.2f);
        PlayMusicStream(backgroundMusic);
    }
    
    


    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.stream() != null && IsMusicStreamPlaying(backgroundMusic)) {
            StopMusicStream(backgroundMusic);
        }
    }
    
    


    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.stream() != null && IsMusicStreamPlaying(backgroundMusic)) {
            PauseMusicStream(backgroundMusic);
        }
    }
    
    


    public void resumeBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null || backgroundMusic.stream() == null) return;
        ResumeMusicStream(backgroundMusic);
    }
    
    
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
        CloseAudioDevice(); 
    }
}
