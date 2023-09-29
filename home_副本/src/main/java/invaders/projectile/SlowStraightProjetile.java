package invaders.projectile;

import invaders.physics.Vector2D;

public class SlowStraightProjetile extends AbstractProjectile {


    SlowStraightProjetile(Vector2D position) {
        super(position);
    }

    @Override
    public int getSpeedY() {
        return 5;
    }
}
