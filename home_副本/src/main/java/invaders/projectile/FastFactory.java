package invaders.projectile;

import invaders.physics.Vector2D;

public class FastFactory extends ProjectileFactory {
    @Override
    public ProjectileStrategyInterface createProjectile(Vector2D position) {
        return new FastStraightProjectile(position);
    }
}
