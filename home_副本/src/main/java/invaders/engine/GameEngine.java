package invaders.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.util.Random;

import invaders.GameObject;
import invaders.bunker.BunkerState1;
import invaders.bunker.BunkerState2;
import invaders.bunker.BunkerState3;
import invaders.enemy.EnemyBuilder;
import invaders.enemy.EnemyDirector;
import invaders.entities.Player;
import invaders.physics.BoxCollider;
import invaders.physics.Vector2D;
import invaders.projectile.AbstractProjectile;
import invaders.projectile.FastStraightProjectile;
import invaders.projectile.SlowStraightProjectilePlayer;
import invaders.projectile.SlowStraightProjetile;
import invaders.rendering.Renderable;
import javafx.scene.control.Label;
import org.json.simple.JSONObject;

import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;

import org.json.simple.parser.ParseException;


/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine {

	private List<GameObject> gameobjects;
	private List<Renderable> renderables;
	private List<Renderable> removedRenderables;
	private Player player;

	private boolean left;
	private boolean right;

	public int screen_width;
	public int screen_height;

	Random random = new Random();

	public Label label = new Label("");

	public int aliensDestroyed = 0;

	public GameEngine(String config){
		// read the config here
		gameobjects = new ArrayList<GameObject>();
		renderables = new ArrayList<Renderable>();
		removedRenderables = new ArrayList<Renderable>();

		analysis_json(config);
	}

	private void analysis_json(String config) {
		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource(config).getFile());

		String filePath = file.getAbsolutePath();
		try {
			JSONParser parser = new JSONParser();

			FileReader reader = new FileReader(filePath);
			Object obj = parser.parse(reader);
			reader.close();

			if (obj instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) obj;
				JSONObject gameObj = (JSONObject) jsonObject.get("Game");
				JSONObject sizeObj = (JSONObject) gameObj.get("size");
				this.screen_width = ((Number) sizeObj.get("x")).intValue();
				this.screen_height = ((Number) sizeObj.get("y")).intValue();


				JSONObject playerObj = (JSONObject) jsonObject.get("Player");
				JSONObject positionObj = (JSONObject) playerObj.get("position");


				double x = ((Number) positionObj.get("x")).intValue();
				double y = ((Number) positionObj.get("y")).intValue();
				player = new Player(new Vector2D(x, y));
				int speed = ((Number) playerObj.get("speed")).intValue();
				player.setSpeed(speed);
				int lives = ((Number) playerObj.get("lives")).intValue();
				player.setHealth(lives);

				renderables.add(player);


				JSONArray enemyObjs = (JSONArray) jsonObject.get("Enemies");
				for(int i=0;i<enemyObjs.size();i++)
				{
					JSONObject enemyobj = (JSONObject) enemyObjs.get(i);
					String projectileStr = (String) enemyobj.get("projectile");
					positionObj = (JSONObject) enemyobj.get("position");
					x = ((Number) positionObj.get("x")).intValue();
					y = ((Number) positionObj.get("y")).intValue();
					EnemyBuilder enemyBuilder = new EnemyBuilder(new Vector2D(x, y));
					if(projectileStr.equals("fast_straight"))
					{
						new EnemyDirector(enemyBuilder).buildFastProtile();
					}
					else
					{
						new EnemyDirector(enemyBuilder).buildSlowProtile();
					}
					renderables.add(enemyBuilder);
					gameobjects.add(enemyBuilder);
				}


				JSONArray bunkerObjs = (JSONArray) jsonObject.get("Bunkers");
				for(int i=0;i<bunkerObjs.size();i++)
				{
					positionObj = (JSONObject)((JSONObject) bunkerObjs.get(i)).get("position");
					x = ((Number) positionObj.get("x")).intValue();
					y = ((Number) positionObj.get("y")).intValue();
					BunkerState1 bunkerState1 = new BunkerState1(new Vector2D(x, y));
					renderables.add(bunkerState1);
					gameobjects.add(bunkerState1);
				}


			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the game/simulation
	 */
	public void update(){

		if(this.player.getHealth() <= 0)
		{
			return;
		}

		if(this.getNumEnemy() == 0)
		{
			return;
		}

		movePlayer();
		for(GameObject go: gameobjects) {
			go.update();
		}

		// enemy shoot
		AbstractProjectile newlyCreatedProjectile = null;
		for(GameObject go: gameobjects) {
			if(go instanceof EnemyBuilder)
			{
				if(random.nextInt(500) == 1)
				{
					if(this.getNumEnemyProjectiles() < 3)
					{
						EnemyBuilder enemy = (EnemyBuilder) go;
						Vector2D newPosition = new Vector2D(enemy.getPosition().getX() + enemy.getWidth() / 2, enemy.getPosition().getY() + enemy.getHeight() + 1);
						newlyCreatedProjectile = (AbstractProjectile) enemy.product.createProjectile(newPosition);
						break;
					}
				}
			}
		}
		if(newlyCreatedProjectile != null)
		{
			renderables.add(newlyCreatedProjectile);
			gameobjects.add(newlyCreatedProjectile);
		}

		for(GameObject go: gameobjects) {
			if(go instanceof AbstractProjectile)
			{
				AbstractProjectile projectile = (AbstractProjectile) go;
				if(projectile.getPosition().getX() > this.screen_width - 4
						|| projectile.getPosition().getX() < 4
						|| projectile.getPosition().getY() > this.screen_height - 4
						|| projectile.getPosition().getY() < 4)
				{
					renderables.remove(go);
					gameobjects.remove(go);
					removedRenderables.add((Renderable) go);
					break;
				}
			}
		}


		// collide AbstractProjectile speed < 0 with enemy
		{
			GameObject g1 = null;
			GameObject g2 = null;
			boolean collidged = false;
			for(GameObject go: gameobjects)
			{
				if(go instanceof AbstractProjectile)
				{
					for(GameObject other : gameobjects)
					{
						if(other instanceof EnemyBuilder)
						{
							AbstractProjectile projectile = (AbstractProjectile) go;
							EnemyBuilder enemy = (EnemyBuilder) other;
							if(projectile.getSpeedY() < 0)
							{
								BoxCollider box1 = new BoxCollider(projectile.getWidth(), projectile.getHeight(), projectile.getPosition());
								BoxCollider box2 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
								if (box1.isColliding(box2)) {
									collidged = true;
									g1 = go;
									g2 = other;
									break;
								}
							}
						}
					}
				}
				if(collidged)
				{
					break;
				}
			}
			if(collidged)
			{
				renderables.remove(g1);
				gameobjects.remove(g1);
				removedRenderables.add((Renderable) g1);
				renderables.remove(g2);
				gameobjects.remove(g2);
				removedRenderables.add((Renderable) g2);
				aliensDestroyed += 1;
			}
		}

		// collide AbstractProjectile with BunkerState1
		{
			GameObject g1 = null;
			GameObject g2 = null;
			boolean collidged = false;
			for(GameObject go: gameobjects)
			{
				if(go instanceof AbstractProjectile)
				{
					for(GameObject other : gameobjects)
					{
						if(other instanceof BunkerState1)
						{
							AbstractProjectile projectile = (AbstractProjectile) go;
							BunkerState1 enemy = (BunkerState1) other;
							BoxCollider box1 = new BoxCollider(projectile.getWidth(), projectile.getHeight(), projectile.getPosition());
							BoxCollider box2 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
							if(box1.isColliding(box2))
							{
								collidged = true;
								g1 = go;
								g2 = other;
								break;
							}
						}
					}
				}
				if(collidged)
				{
					break;
				}
			}
			if(collidged)
			{
				renderables.remove(g1);
				gameobjects.remove(g1);
				removedRenderables.add((Renderable) g1);
				renderables.remove(g2);
				gameobjects.remove(g2);
				removedRenderables.add((Renderable) g2);

				BunkerState2 bunkerState2 = new BunkerState2(((BunkerState1) g2).getPosition());
				renderables.add(bunkerState2);
				gameobjects.add(bunkerState2);
			}
		}

		// collide AbstractProjectile with BunkerState2
		{
			GameObject g1 = null;
			GameObject g2 = null;
			boolean collidged = false;
			for(GameObject go: gameobjects)
			{
				if(go instanceof AbstractProjectile)
				{
					for(GameObject other : gameobjects)
					{
						if(other instanceof BunkerState2)
						{
							AbstractProjectile projectile = (AbstractProjectile) go;
							BunkerState2 enemy = (BunkerState2) other;
							BoxCollider box1 = new BoxCollider(projectile.getWidth(), projectile.getHeight(), projectile.getPosition());
							BoxCollider box2 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
							if(box1.isColliding(box2))
							{
								collidged = true;
								g1 = go;
								g2 = other;
								break;
							}
						}
					}
				}
				if(collidged)
				{
					break;
				}
			}
			if(collidged)
			{
				renderables.remove(g1);
				gameobjects.remove(g1);
				removedRenderables.add((Renderable) g1);
				renderables.remove(g2);
				gameobjects.remove(g2);
				removedRenderables.add((Renderable) g2);

				BunkerState3 bunkerState3 = new BunkerState3(((BunkerState2) g2).getPosition());
				renderables.add(bunkerState3);
				gameobjects.add(bunkerState3);
			}
		}


		// collide AbstractProjectile with BunkerState3
		{
			GameObject g1 = null;
			GameObject g2 = null;
			boolean collidged = false;
			for(GameObject go: gameobjects)
			{
				if(go instanceof AbstractProjectile)
				{
					for(GameObject other : gameobjects)
					{
						if(other instanceof BunkerState3)
						{
							AbstractProjectile projectile = (AbstractProjectile) go;
							BunkerState3 enemy = (BunkerState3) other;
							BoxCollider box1 = new BoxCollider(projectile.getWidth(), projectile.getHeight(), projectile.getPosition());
							BoxCollider box2 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
							if(box1.isColliding(box2))
							{
								collidged = true;
								g1 = go;
								g2 = other;
								break;
							}
						}
					}
				}
				if(collidged)
				{
					break;
				}
			}
			if(collidged)
			{
				renderables.remove(g1);
				gameobjects.remove(g1);
				removedRenderables.add((Renderable) g1);
				renderables.remove(g2);
				gameobjects.remove(g2);
				removedRenderables.add((Renderable) g2);
			}
		}

		// collide AbstractProjectile with player
		{
			GameObject g1 = null;
			GameObject g2 = null;
			boolean collidged = false;
			for(GameObject go: gameobjects) {
				if (go instanceof AbstractProjectile) {
					AbstractProjectile projectile = (AbstractProjectile) go;
					BoxCollider box1 = new BoxCollider(projectile.getWidth(), projectile.getHeight(), projectile.getPosition());
					BoxCollider box2 = new BoxCollider(player.getWidth(), player.getHeight(), player.getPosition());
					g1 = go;
					if (box1.isColliding(box2)) {
						player.takeDamage(1);
						collidged = true;
						break;
					}
				}
			}
			if(collidged)
			{
				renderables.remove(g1);
				gameobjects.remove(g1);
				removedRenderables.add((Renderable) g1);
			}
		}

		// collide enemy with bunker
		{
			GameObject g1 = null;
			GameObject g2 = null;
			boolean collidged = false;
			for(GameObject go: gameobjects)
			{
				if(go instanceof EnemyBuilder)
				{
					for(GameObject other : gameobjects)
					{
						if(other instanceof BunkerState1)
						{
							EnemyBuilder enemy = (EnemyBuilder) go;
							BunkerState1 bunker = (BunkerState1) other;
							BoxCollider box1 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
							BoxCollider box2 = new BoxCollider(bunker.getWidth(), bunker.getHeight(), bunker.getPosition());
							if(box1.isColliding(box2))
							{
								collidged = true;
								g1 = go;
								g2 = other;
								break;
							}
						}
						if(other instanceof BunkerState2)
						{
							EnemyBuilder enemy = (EnemyBuilder) go;
							BunkerState2 bunker = (BunkerState2) other;
							BoxCollider box1 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
							BoxCollider box2 = new BoxCollider(bunker.getWidth(), bunker.getHeight(), bunker.getPosition());
							if(box1.isColliding(box2))
							{
								collidged = true;
								g1 = go;
								g2 = other;
								break;
							}
						}
						if(other instanceof BunkerState3)
						{
							EnemyBuilder enemy = (EnemyBuilder) go;
							BunkerState3 bunker = (BunkerState3) other;
							BoxCollider box1 = new BoxCollider(enemy.getWidth(), enemy.getHeight(), enemy.getPosition());
							BoxCollider box2 = new BoxCollider(bunker.getWidth(), bunker.getHeight(), bunker.getPosition());
							if(box1.isColliding(box2))
							{
								collidged = true;
								g1 = go;
								g2 = other;
								break;
							}
						}
					}
				}
				if(collidged)
				{
					break;
				}
			}
			if(collidged)
			{
				renderables.remove(g1);
				gameobjects.remove(g1);
				removedRenderables.add((Renderable) g1);
				renderables.remove(g2);
				gameobjects.remove(g2);
				removedRenderables.add((Renderable) g2);
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= screen_width) {
				ro.getPosition().setX(screen_width - 1 -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(1);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= screen_height) {
				ro.getPosition().setY(screen_height - 1 - ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(1);
			}
		}

		// set label
		label.setText("Lives: " + (int)this.player.getHealth() + "  Aliens destroyed: " + aliensDestroyed);
	}

	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<Renderable> getRemovedRenderables() {
		return removedRenderables;
	}

	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}
	public void rightPressed(){
		this.right = true;
	}

	public int getNumPlayerProjectiles()
	{
		int cnt = 0;
		for(GameObject obj : gameobjects)
		{
			if(obj instanceof SlowStraightProjectilePlayer)
			{
				SlowStraightProjectilePlayer projectile = (SlowStraightProjectilePlayer) obj;
				if(projectile.getSpeedY() == -5)
				{
					cnt++;
				}
			}
		}
		return cnt;
	}

	public int getNumEnemyProjectiles()
	{
		int cnt = 0;
		for(GameObject obj : gameobjects)
		{
			if((obj instanceof FastStraightProjectile)
				|| (obj instanceof SlowStraightProjetile))
			{
					cnt++;
			}
		}
		return cnt;
	}

	public int getNumEnemy()
	{
		int cnt = 0;
		for(GameObject obj : gameobjects)
		{
			if((obj instanceof EnemyBuilder))
			{
				cnt++;
			}
		}
		return cnt;
	}

	public boolean shootPressed(){
		player.shoot();
		if(this.getNumPlayerProjectiles() < 1)
		{
			SlowStraightProjectilePlayer projectile = new SlowStraightProjectilePlayer(new Vector2D(player.getPosition().getX() + player.getWidth() / 2, player.getPosition().getY() - 5));
			renderables.add(projectile);
			gameobjects.add(projectile);
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}
}
