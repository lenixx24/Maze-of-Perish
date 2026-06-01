package ua.edu.ukma.entity;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ua.edu.ukma.exception.AssetLoadingException;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class SpriteSheet<T> {

    private final Image image;

    private final Function<T, Integer> xResolver;
    private final Function<T, Integer> yResolver;
    private final Function<T, Integer> widthResolver;
    private final Function<T, Integer> heightResolver;

    public SpriteSheet(String path, Function<T, Integer> xResolver, Function<T, Integer> yResolver, Function<T, Integer> widthResolver, Function<T, Integer> heightResolver) {
        this.image = loadImage(path);
        this.xResolver = xResolver;
        this.yResolver = yResolver;
        this.widthResolver = widthResolver;
        this.heightResolver = heightResolver;
    }

    public ImageView createImageView() {
        ImageView imageView = new ImageView(image);
        imageView.setSmooth(false);
        return imageView;
    }

    public void applyFrame(ImageView imageView, T frame) {
        imageView.setViewport(new Rectangle2D(
                xResolver.apply(frame),
                yResolver.apply(frame),
                widthResolver.apply(frame),
                heightResolver.apply(frame)
        ));
    }

    private Image loadImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            throw new AssetLoadingException("Cannot load sprite sheet: " + path);
        }

        return new Image(Objects.requireNonNull(stream));
    }
}