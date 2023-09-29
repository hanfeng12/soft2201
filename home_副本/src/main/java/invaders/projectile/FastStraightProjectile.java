package invaders.projectile;

import invaders.physics.Vector2D;

public class FastStraightProjectile extends AbstractProjectile {


    FastStraightProjectile(Vector2D position) {
        super(position);
    }

    @Override
    public int getSpeedY() {
        return 10;
    }
}
