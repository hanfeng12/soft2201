package invaders.projectile;

import invaders.physics.Vector2D;

public abstract class ProjectileFactory {
    public abstract ProjectileStrategyInterface createProjectile(Vector2D position);
}