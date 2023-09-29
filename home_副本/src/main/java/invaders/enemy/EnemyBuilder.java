package invaders.enemy;

import invaders.GameObject;
import invaders.logic.Damagable;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.projectile.ProjectileFactory;
import invaders.rendering.Renderable;
import javafx.scene.image.Image;

import java.io.File;

public class EnemyBuilder implements Moveable, Damagable, Renderable, GameObject {

    public ProjectileFactory product = null;

    private final Vector2D position;
    private final Image image;

    private final double width = 25;
    private final double height = 30;
    public EnemyBuilder(Vector2D position)
    {
        this.image = new Image(new File("src/main/resources/enemy.png").toURI().toString(), width, height, true, true);
        this.position = position;
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
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
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

    int cnt = 20;
    int dx = 1;


    @Override
    public void update() {
        this.position.setX(this.position.getX() + dx);
        cnt++;
        if(cnt >= 40)
        {
            cnt = 0;
            dx *= -1;
            this.position.setY(this.position.getY() + 1);
        }
    }
}
