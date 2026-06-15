package ua.edu.ukma.music;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class AudioManager {

    private MediaPlayer bgMusicPlayer;

    public void playBackgroundMusic(String filePath) {
        try {
            URL resource = getClass().getResource(filePath);
            if (resource == null) {
                System.err.println("No audio file in: " + filePath);
                return;
            }

            Media media = new Media(resource.toString());
            bgMusicPlayer = new MediaPlayer(media);

            bgMusicPlayer.setVolume(0.3);

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
}
