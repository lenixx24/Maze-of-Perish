package ua.edu.ukma.entity.player;

import ua.edu.ukma.annotation.PlayerFrame;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public enum PlayerFrameType {

    @PlayerFrame(direction = Direction.LEFT, state = PlayerState.CARD, x = 55, y = 95, width = 285, height = 340)
    CARD_LEFT,

    @PlayerFrame(direction = Direction.LEFT, state = PlayerState.WALKING, x = 465, y = 95, width = 260, height = 340)
    WALK_LEFT_1,

    @PlayerFrame(direction = Direction.LEFT, state = PlayerState.IDLE, x = 785, y = 95, width = 250, height = 340)
    IDLE_LEFT,

    @PlayerFrame(direction = Direction.LEFT, state = PlayerState.WALKING, x = 1120, y = 95, width = 260, height = 340)
    WALK_LEFT_2,


    @PlayerFrame(direction = Direction.RIGHT, state = PlayerState.CARD, x = 55, y = 595, width = 285, height = 340)
    CARD_RIGHT,

    @PlayerFrame(direction = Direction.RIGHT, state = PlayerState.WALKING, x = 465, y = 595, width = 260, height = 340)
    WALK_RIGHT_1,

    @PlayerFrame(direction = Direction.RIGHT, state = PlayerState.IDLE, x = 785, y = 595, width = 250, height = 340)
    IDLE_RIGHT,

    @PlayerFrame(direction = Direction.RIGHT, state = PlayerState.WALKING, x = 1120, y = 595, width = 260, height = 340)
    WALK_RIGHT_2;

    private PlayerFrame annotation() {
        try {
            Field field = PlayerFrameType.class.getField(name());
            PlayerFrame annotation = field.getAnnotation(PlayerFrame.class);

            if (annotation == null) {
                throw new IllegalStateException("Missing PlayerFrame annotation for: " + name());
            }

            return annotation;
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Cannot read player frame: " + name(), exception);
        }
    }

    public Direction direction() {
        return annotation().direction();
    }

    public PlayerState state() {
        return annotation().state();
    }

    public int x() {
        return annotation().x();
    }

    public int y() {
        return annotation().y();
    }

    public int width() {
        return annotation().width();
    }

    public int height() {
        return annotation().height();
    }

    public static PlayerFrameType idle(Direction direction) {
        Direction horizontalDirection = normalizeDirection(direction);

        return Arrays.stream(values())
                .filter(frame -> frame.direction() == horizontalDirection)
                .filter(frame -> frame.state() == PlayerState.IDLE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No idle frame for direction: " + horizontalDirection));
    }

    public static PlayerFrameType card(Direction direction) {
        Direction horizontalDirection = normalizeDirection(direction);

        return Arrays.stream(values())
                .filter(frame -> frame.direction() == horizontalDirection)
                .filter(frame -> frame.state() == PlayerState.CARD)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No card frame for direction: " + horizontalDirection));
    }

    public static List<PlayerFrameType> walkingFrames(Direction direction) {
        Direction horizontalDirection = normalizeDirection(direction);

        return Arrays.stream(values())
                .filter(frame -> frame.direction() == horizontalDirection)
                .filter(frame -> frame.state() == PlayerState.WALKING)
                .toList();
    }

    public static List<PlayerFrameType> walkingCycle(Direction direction) {
        Direction horizontalDirection = normalizeDirection(direction);

        PlayerFrameType idle = idle(horizontalDirection);
        List<PlayerFrameType> walkingFrames = walkingFrames(horizontalDirection);

        if (walkingFrames.size() < 2) {
            return List.of(idle);
        }

        return List.of(walkingFrames.get(0), idle, walkingFrames.get(1), idle);
    }

    private static Direction normalizeDirection(Direction direction) {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            return direction;
        }

        return Direction.RIGHT;
    }
}