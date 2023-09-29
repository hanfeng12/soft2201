package invaders.projectile;

import invaders.physics.Vector2D;

public class SlowStraightProjectilePlayer  extends AbstractProjectile {

    public SlowStraightProjectilePlayer(Vector2D position) {
        super(position);
    }

    @Override
    public int getSpeedY() {
        return -5;
    }
}
