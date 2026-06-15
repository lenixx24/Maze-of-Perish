package ua.edu.ukma.music;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private double musicVolume=0.3;
    private double soundVolume=0.5;
    private MediaPlayer bgMusicPlayer;
    private final Map<String, AudioClip> soundEffectsCache = new HashMap<>();
    private final Map<String, Long> lastPlayedTime = new HashMap<>();
    private static final long SOUND_COOLDOWN_MS = 100;
    public void playBackgroundMusic(String filePath) {
        try {
            URL resource = getClass().getResource(filePath);
            if (resource == null) {
                System.err.println("No audio file in: " + filePath);
                return;
            }

            Media media = new Media(resource.toString());
            bgMusicPlayer = new MediaPlayer(media);

            bgMusicPlayer.setVolume(musicVolume);

            bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            bgMusicPlayer.play();

        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.stop();
        }
    }
    public void playSoundEffect(String filePath) {
        long currentTime = System.currentTimeMillis();
        if (lastPlayedTime.containsKey(filePath)) {
            long timeSinceLastPlay = currentTime - lastPlayedTime.get(filePath);
            if (timeSinceLastPlay < SOUND_COOLDOWN_MS)
                return;
        }
        try {
            AudioClip clip = soundEffectsCache.get(filePath);
            if (clip == null) {
                URL resource = getClass().getResource(filePath);
                if (resource == null) {
                    System.err.println("No file in: " + filePath);
                    return;
                }
                clip = new AudioClip(resource.toString());
                soundEffectsCache.put(filePath, clip);
            }
            clip.play(soundVolume);

        } catch (Exception e) {
            System.err.println("Error playing effect: " + e.getMessage());
        }
    }
}
