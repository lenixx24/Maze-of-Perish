package ua.edu.ukma.renderer;

import javafx.scene.Node;

public interface Renderer<T> {

    Node render(T data);
}