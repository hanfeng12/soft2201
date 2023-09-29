package invaders.bunker;

import invaders.GameObject;
import invaders.logic.Damagable;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.projectile.ProjectileStrategyInterface;
import invaders.rendering.Renderable;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class AbstractStateBunker implements Renderable, GameObject {

    public Image image;

    private Vector2D position;

    public AbstractStateBunker(Vector2D position, Color color)
    {
        Rectangle rectangle = new Rectangle(position.getX(), position.getY(), 30, 30);
        rectangle.setFill(color);

        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        image = rectangle.snapshot(snapshotParameters, null);

        this.position = position;
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return 30;
    }

    @Override
    public double getHeight() {
        return 30;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

}
