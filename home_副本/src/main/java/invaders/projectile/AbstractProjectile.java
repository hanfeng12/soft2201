package invaders.projectile;

import invaders.GameObject;
import invaders.logic.Damagable;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class AbstractProjectile implements ProjectileStrategyInterface, Moveable, Damagable, Renderable, GameObject {

    public Image image;

    private Vector2D position;

    public AbstractProjectile(Vector2D position)
    {
        Rectangle rectangle = new Rectangle(position.getX(), position.getY(), 4, 4);
        rectangle.setFill(Color.BLUE);

        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        image = rectangle.snapshot(snapshotParameters, null);

        this.position = position;
    }

    @Override
    public int getSpeedY() {
        return -5;
    }

    @Override
    public void up() {

    }

    @Override
    public void down() {

    }

    @Override
    public void left() {

    }

    @Override
    public void right() {

    }

    @Override
    public void takeDamage(double amount) {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return 4;
    }

    @Override
    public double getHeight() {
        return 4;
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
        this.position.setY(this.position.getY() + this.getSpeedY());
    }
}
