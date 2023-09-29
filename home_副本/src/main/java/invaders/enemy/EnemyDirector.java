package invaders.enemy;

import invaders.projectile.FastFactory;
import invaders.projectile.SlowFactory;

public class EnemyDirector {
    private EnemyBuilder builder;
    public EnemyDirector(EnemyBuilder builder) {
        this.builder = builder;
    }

    public void buildFastProtile()
    {
        this.builder.product = new FastFactory();
    }

    public void buildSlowProtile()
    {
        this.builder.product = new SlowFactory();
    }

}
