package ua.edu.ukma.annotation;

import ua.edu.ukma.entity.player.Direction;
import ua.edu.ukma.entity.player.PlayerState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PlayerFrame {
    Direction direction();
    PlayerState state();

    int x();
    int y();
    int width();
    int height();
}