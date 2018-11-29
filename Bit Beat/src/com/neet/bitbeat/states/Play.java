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
import com.neet.bitbeat.entities.Bitbeat;
import com.neet.bitbeat.entities.Crystal;
import com.neet.bitbeat.entities.Player;
import com.neet.bitbeat.entities.Start;
import com.neet.bitbeat.handlers.B2DVars;
import com.neet.bitbeat.handlers.GameStateManager;
import com.neet.bitbeat.handlers.MyContactListener;
import com.neet.bitbeat.handlers.MyInput;
import com.neet.bitbeat.main.Game;

public class Play extends GameState {
	
	public Game game = new Game();
	private boolean debug = false;
	
	private World world;
	private Box2DDebugRenderer b2dr;
	
	private OrthographicCamera b2dCam;
	
	private MyContactListener cl;
	
	private TiledMapTileLayer layer;
	private float tileSize;
	private double lastx;
	private OrthogonalTiledMapRenderer tmr;
	
	private Player player;
	private Array<Crystal> crystals;
	private Start start;
	private Bitbeat bitbeat;
	
	public Play(GameStateManager gsm) {
		
		super(gsm);

		//set up box2d stuff
		world = new World(new Vector2(0, 0f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		
		//create start
		createStart();
		
		//create bitbeat
		createBitbeat();
		
		levelSong.setVolume(0.3f);
		levelSong.setLooping(true);
		playMusic();
		
		//set up box2d camera
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
			
	}
	
	public void handleInput(GameStateManager gsm) {
		
		if (gsm.PLAY == 0) {
			//push to start
			if(MyInput.isPressed(MyInput.BUTTON1)) {
				gsm.PLAY = 7;
				setState();
				setSong(gsm.PLAY);
				Play(gsm);
			}
		} else if (gsm.PLAY > 1) {
			//player jump
			if(MyInput.isPressed(MyInput.BUTTON1)) {
				if (cl.isPlayerOnGround()) {
					player.getBody().applyForceToCenter(0, 420, true);
				}
			}
		}
	}
	
	private void Play(GameStateManager gsm) {
		
		if (gsm.PLAY <= 5) {
			//set up box2d stuff
			world = new World(new Vector2(0, 0f), true);
			cl = new MyContactListener();
			world.setContactListener(cl);
			b2dr = new Box2DDebugRenderer();
			
			//create start
			createStart();
			
			//create bitbeat
			createBitbeat();
			
			levelSong.setVolume(0.3f);
			levelSong.setLooping(true);
			playMusic();
			
			//set up box2d camera
			b2dCam = new OrthographicCamera();
			b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

		} else if (gsm.PLAY > 5) {
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
		
			playMusic();
		
			//set up box2d camera
			b2dCam = new OrthographicCamera();
			b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
		}
		
		
	}

	public void update(float dt) {
		
		if (gsm.PLAY <= 5) {
			
			//check input
			handleInput(gsm);
			
			//update box2d
			world.step(dt, 6, 2);
			start.update(dt);
			
		} else if (gsm.PLAY > 5) {
			
			//check input
			handleInput(gsm);
			
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
			
			for (int i = 0; i < crystals.size; i++) {
				crystals.get(i).update(dt);
			}
			
			player.update(dt);
			
			if (player.getPosition().y < 0 || player.getPosition().x == lastx) {
				gsm.PLAY = 0;
				setSong(gsm.PLAY);
				setState();
				Play(gsm);
			} else lastx = player.getPosition().x;
		}
		
	}
	
	public void render() {
		
		if (gsm.PLAY == 0) {
			
			// clear screen
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			//draw background
			sb.begin();
			sb.draw(background, 0, 0, Game.V_WIDTH, Game.V_HEIGHT);
			sb.end();
			
			//set camera
			cam.position.set(Game.V_WIDTH / 2, Game.V_HEIGHT / 2, 0);
			cam.update();
			
			//draw start
			sb.setProjectionMatrix(cam.combined);
			start.render(sb);
			
			//draw bitbeat
			sb.setProjectionMatrix(cam.combined);
			bitbeat.render(sb);
			
		} else if (gsm.PLAY > 5) {
			
			// clear screen
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			//camera follow player
			cam.position.set(
					player.getPosition().x * PPM + Game.V_WIDTH / 4, 
					Game.V_HEIGHT / 2,
					0
			);
			cam.update();
			
			//draw background
			sb.begin();
			for (int i = 0; i <= layer.getWidth() / (Game.V_WIDTH / PPM) ;) {
				for (int j = 0; j < 3; j++) {
					sb.draw(background, Game.V_WIDTH * i, 0, Game.V_WIDTH, Game.V_HEIGHT);
					i++;
					sb.draw(background2, Game.V_WIDTH * i, 0, Game.V_WIDTH, Game.V_HEIGHT);
					i++;
					sb.draw(background3, Game.V_WIDTH * i, 0, Game.V_WIDTH, Game.V_HEIGHT);
					i++;
				}
			}
			sb.end();
			
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
			
		}
		
		//draw box2d (object simulator)
		if(debug) {
			b2dr.render(world, b2dCam.combined);
			
		}
		
	}
	
	public void dispose() {}
	
	public void createStart() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		bdef.position.set(320 / PPM, 180 / PPM);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);
			
		shape.setAsBox(216 / PPM, 10 / PPM , new Vector2(0 , -100 / PPM), 0);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("start");
		start = new Start(body);
		
	}
	
	public void createBitbeat() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		bdef.position.set(320 / PPM, 280 / PPM);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);
			
		shape.setAsBox(430/ PPM, 77 / PPM , new Vector2(0 , -100 / PPM), 0);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("bitbeat");
		bitbeat = new Bitbeat(body);
		
	}
	
	public void createPlayer() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		// create player
		bdef.position.set(0 / PPM, 300 / PPM);

		//Velocity -- walk speed
		bdef.linearVelocity.set(1.7f, 0);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);
			
		shape.setAsBox(12 / PPM, 16 / PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_BODY;
		fdef.filter.maskBits = B2DVars.BIT_LINE | B2DVars.BIT_CRYSTAL;
		body.createFixture(fdef).setUserData("player");
		
		//create foot
		shape.setAsBox(10 / PPM, 0 / PPM, new Vector2(0 , -13 / PPM), 0);
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
		player = new Player(body, gsm.PLAY);
		
	}
	
	public void createTiles() {
		
		//load tile map
		tmr = new OrthogonalTiledMapRenderer(tileMap);
		
		tileSize = (int) tileMap.getProperties().get("tilewidth");
		
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
						(col + 0.7f) * tileSize / PPM,
						(row) * 16 / PPM
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
			fdef.filter.maskBits = B2DVars.BIT_BODY;
			
			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("crystal");
			
			Crystal c = new Crystal(body);
			crystals.add(c);
			
			body.setUserData(c);
			
		}
		
	}

	@Override
	public void handleInput() {
		
	}
	
}
