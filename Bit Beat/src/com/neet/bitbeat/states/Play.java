package com.neet.bitbeat.states;

import static com.neet.bitbeat.handlers.B2DVars.PPM;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.neet.bitbeat.entities.Crystal;
import com.neet.bitbeat.entities.Player;
import com.neet.bitbeat.handlers.B2DVars;
import com.neet.bitbeat.handlers.GameStateManager;
import com.neet.bitbeat.handlers.MyContactListener;
import com.neet.bitbeat.handlers.MyInput;
import com.neet.bitbeat.main.Game;

public class Play extends GameState {
	
	private boolean debug = false;
	
	private World world;
	private Box2DDebugRenderer b2dr;
	
	private OrthographicCamera b2dCam;
	
	private MyContactListener cl;
	
	private TiledMap tileMap;
	private float tileSize;
	private OrthogonalTiledMapRenderer tmr;
	
	private Player player;
	private Array<Crystal> crystals;
	
	public Play(GameStateManager gsm) {
		
		super(gsm);
		
		//set up box2d stuff
		world = new World(new Vector2(0, -20f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		
		//create player
		createPlayer();
		
		//create tiles
		createTiles();
		
		//create crystals
		createCrystals();
		
		//set up box2d camera
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
			
	}
	
	public void handleInput() {
		
		//player jump
		if(MyInput.isPressed(MyInput.BUTTON1)) {
			if (cl.isPlayerOnGround()) {
				player.getBody().applyForceToCenter(0, 270, true);
			}
		}
		
	}
	
	public void update(float dt) {
		
		//check input
		handleInput();
		
		//update box2d
		world.step(dt, 6, 2);
		
		// remove crystals
		Array<Body> bodies = cl.getBodiesToRemove();
		for (int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			crystals.removeValue((Crystal) b.getUserData(), true);
			world.destroyBody(b);
			player.cellectCrystal();
		}
		bodies.clear();
		
		player.update(dt);
		
		for (int i = 0; i < crystals.size; i++) {
			crystals.get(i).update(dt);
		}
		
	}
	
	public void render() {
		
		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//camera follow player
		cam.position.set(
				player.getPosition().x * PPM + Game.V_WIDTH / 4, 
				Game.V_HEIGHT / 2,
				0
		);
		sb.begin();					// #5
		sb.draw(background, 0, 0, Game.V_WIDTH, Game.V_HEIGHT);	// #6
		sb.end();
		cam.update();
		
		//draw bg
		
		
		//draw tiled map
		tmr.setView(cam);
		tmr.render();
		
		//draw player
		sb.setProjectionMatrix(cam.combined);
		player.render(sb);
		
		//draw crystal
		for (int i = 0; i < crystals.size; i++) {
			crystals.get(i).render(sb);
		}
		
		//draw box2d (object simulator)
		if(debug) {
			b2dr.render(world, b2dCam.combined);
		}
		
	}
	
	public void dispose() {}
	
	public void createPlayer() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		// create player
		bdef.position.set(180 / PPM, 300 / PPM);
		bdef.type = BodyType.DynamicBody;
		//Velocity -- walk speed
		bdef.linearVelocity.set(1.5f, 0);
		Body body = world.createBody(bdef);
			
		shape.setAsBox(18 / PPM, 32 / PPM, new Vector2(0 , 20 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_BODY;
		fdef.filter.maskBits = B2DVars.BIT_LINE | B2DVars.BIT_CRYSTAL;
		body.createFixture(fdef).setUserData("player");
		
		//create foot
		shape.setAsBox(18 / PPM, 1 / PPM, new Vector2(0 , -12 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_LINE | B2DVars.BIT_CRYSTAL;
		body.createFixture(fdef).setUserData("stand");

		//create foot sensor
		shape.setAsBox(8 / PPM, 2 / PPM, new Vector2(0 , -13 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_LINE;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");
		
		//create player
		player = new Player(body);
	}
	
	public void createTiles() {
		//load tile map
		tileMap = new TmxMapLoader().load("res/map/test2.tmx");
		tmr = new OrthogonalTiledMapRenderer(tileMap);
		
		tileSize = (int) tileMap.getProperties().get("tilewidth");
		
		TiledMapTileLayer layer;
		
		layer = (TiledMapTileLayer) tileMap.getLayers().get("line");
		createLayer(layer, B2DVars.BIT_LINE);
	}
	
	private void createLayer(TiledMapTileLayer layer, short bits) {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		//go through all the cells in the layer
		//y
		for (int row = 0; row < layer.getHeight(); row++) {
			//x
			for (int col = 0; col < layer.getWidth(); col++) {
				
				// get cell
				Cell cell = layer.getCell(col, row);
				
				//check if cell exists
				if(cell == null) continue;
				if (cell.getTile() == null) continue;
				
				//create body & fixture from cell
				bdef.type = BodyType.StaticBody;
				bdef.position.set(
						(col + 0.5f) * tileSize / PPM,
						(row + 0.5f) * 15 / PPM
				);
				
				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[2];
				v[0] = new Vector2(tileSize / 2 / PPM, tileSize / 2 / PPM);
				v[1] = new Vector2(-tileSize / 2 / PPM, tileSize / 2 / PPM);
				cs.createChain(v);
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2DVars.BIT_PLAYER;
				fdef.isSensor = false;
				world.createBody(bdef).createFixture(fdef);
			}
		}
	}
	
	private void createCrystals() {
		
		crystals = new Array<Crystal>();
		
		MapLayer layer = tileMap.getLayers().get("crystals");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		for(MapObject mo : layer.getObjects()) {
			
			bdef.type = BodyType.StaticBody;
			
			float x = (float) mo.getProperties().get("x") / PPM;
			float y = (float) mo.getProperties().get("y") / PPM;
			
			bdef.position.set(x, y);
			
			CircleShape cshape = new CircleShape();
			cshape.setRadius(8 / PPM);
			
			fdef.shape = cshape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;
			
			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("crystal");
			
			Crystal c = new Crystal(body);
			crystals.add(c);
			
			body.setUserData(c);
			
		}
		
	}
	
}
