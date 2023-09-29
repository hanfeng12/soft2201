package invaders.projectile;

import invaders.physics.Vector2D;

public class SlowFactory extends ProjectileFactory {
    @Override
    public ProjectileStrategyInterface createProjectile(Vector2D position) {
        return new SlowStraightProjetile(position);
    }
}
