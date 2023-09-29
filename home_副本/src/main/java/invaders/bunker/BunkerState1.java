package invaders.bunker;

import invaders.physics.Vector2D;
import javafx.scene.paint.Color;

public class BunkerState1 extends AbstractStateBunker{
    public BunkerState1(Vector2D position) {
        super(position, Color.GREEN);
    }
}
