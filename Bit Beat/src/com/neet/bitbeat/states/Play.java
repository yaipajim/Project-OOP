package com.neet.bitbeat.states;

import static com.neet.bitbeat.handlers.B2DVars.PPM;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.neet.bitbeat.entities.Bitbeat;
import com.neet.bitbeat.entities.Button1;
import com.neet.bitbeat.entities.Button2;
import com.neet.bitbeat.entities.Button3;
import com.neet.bitbeat.entities.Button4;
import com.neet.bitbeat.entities.Button5;
import com.neet.bitbeat.entities.Button6;
import com.neet.bitbeat.entities.Button7;
import com.neet.bitbeat.entities.Coin;
import com.neet.bitbeat.entities.Player;
import com.neet.bitbeat.entities.Pointer;
import com.neet.bitbeat.entities.Pointer2;
import com.neet.bitbeat.entities.Start;
import com.neet.bitbeat.handlers.B2DVars;
import com.neet.bitbeat.handlers.GameStateManager;
import com.neet.bitbeat.handlers.MyContactListener;
import com.neet.bitbeat.handlers.MyInput;
import com.neet.bitbeat.main.Game;

public class Play extends GameState {
	
	public Game game = new Game();
	private boolean debug = false;
	private float tileSize;
	private double lastx;
	private int status = 2, totalCoins = 0, status2 = 6;
	
	private World world;
	private Box2DDebugRenderer b2dr;
	
	private OrthographicCamera b2dCam;
	
	private MyContactListener cl;
	
	private TiledMapTileLayer layer;
	private OrthogonalTiledMapRenderer tmr;
	
	private Pointer pointer;
	private Pointer2 pointer2;
	private Button1 bt1;
	private Button2 bt2;
	private Button3 bt3;
	private Button4 bt4;
	private Button5 bt5;
	private Button6 bt6;
	private Button7 bt7;
	private Player player;
	private Array<Coin> coins;
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
		
		levelSong.setVolume(0.5f);
		levelSong.setLooping(true);
		playMusic();
		
		//set up box2d camera
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
			
	}
	
	public void handleInput(GameStateManager gsm) {
		if (gsm.PLAY == 0) {			//title input
			//push to start
			if(MyInput.isPressed(MyInput.BUTTON1)) {
				gsm.PLAY = 1;
				Play(gsm);
			}
		} else if (gsm.PLAY > 5) {		//state input
			//player jump
			if(MyInput.isPressed(MyInput.BUTTON1)) {
				if (cl.isPlayerOnGround()) {
					player.getBody().applyForceToCenter(0, 420, true);
				}
			}
		} else if (gsm.PLAY == 1) {		//main menu input
			if(MyInput.isPressed(MyInput.BUTTON1)) {
				if (status != 5) {
					gsm.PLAY = status;
					setState();
					Play(gsm);
				} else Gdx.app.exit();
			} else if(MyInput.isPressed(MyInput.BUTTON2)) {
				if (pointer.getBody().getPosition().y != 250 / PPM) {
					pointer.getBody().setTransform(new Vector2(
							pointer.getBody().getPosition().x, 
							pointer.getBody().getPosition().y + 50 / PPM), 
							0
						);
					status--;
				} else {
					pointer.getBody().setTransform(new Vector2(
							pointer.getBody().getPosition().x, 
							100 / PPM), 
							0
						);
					status = 5;
				}
			} else if(MyInput.isPressed(MyInput.BUTTON3)) {
				if (pointer.getBody().getPosition().y != 100 / PPM) {
					pointer.getBody().setTransform(new Vector2(
							pointer.getBody().getPosition().x, 
							pointer.getBody().getPosition().y - 50 / PPM), 
							0
						);
					status++;
				} else {
					pointer.getBody().setTransform(new Vector2(
							pointer.getBody().getPosition().x, 
							250 / PPM), 
							0
						);
					status = 2;
				}
			} else if(MyInput.isPressed(MyInput.BUTTON4)) {
				gsm.PLAY = 0;
				setState();
				Play(gsm);
			}
		} else if (gsm.PLAY == 2) {		//select input
			if(MyInput.isPressed(MyInput.BUTTON1)) {
				gsm.PLAY = status2;
				setState();
				setSong(gsm.PLAY);
				Play(gsm);
			} else if(MyInput.isPressed(MyInput.BUTTON2)) {
				if (pointer2.getBody().getPosition().y != 350 / PPM) {
					pointer2.getBody().setTransform(new Vector2(
							pointer2.getBody().getPosition().x, 
							pointer2.getBody().getPosition().y + 100 / PPM), 
							0
						);
					status2--;
				} else {
					pointer2.getBody().setTransform(new Vector2(
							pointer.getBody().getPosition().x, 
							150 / PPM), 
							0
						);
					status2 = 8;
				}
			} else if(MyInput.isPressed(MyInput.BUTTON3)) {
				if (pointer2.getBody().getPosition().y != 150 / PPM) {
					pointer2.getBody().setTransform(new Vector2(
							pointer2.getBody().getPosition().x, 
							pointer2.getBody().getPosition().y - 100 / PPM), 
							0
						);
					status2++;
				} else {
					pointer2.getBody().setTransform(new Vector2(
							pointer.getBody().getPosition().x, 
							350 / PPM), 
							0
						);
					status2 = 6;
				}
			} else if(MyInput.isPressed(MyInput.BUTTON4)) {
				gsm.PLAY = 1;
				setState();
				Play(gsm);
			}
		} else if (gsm.PLAY == 3) {
			if(MyInput.isPressed(MyInput.BUTTON4)) {
				gsm.PLAY = 1;
				setState();
				Play(gsm);
			}
		} else if (gsm.PLAY == 4) {
			if(MyInput.isPressed(MyInput.BUTTON4)) {
				gsm.PLAY = 1;
				setState();
				Play(gsm);
			}
		}
	}
	
	private void Play(GameStateManager gsm) {
		
		//set up box2d stuff
		if (gsm.PLAY > 5) {
			world = new World(new Vector2(0, -20f), true);
		} else world = new World(new Vector2(0, 0f), true);
		
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		
		if (gsm.PLAY < 6) {
			
			if (levelSong.isPlaying() == false) {
				setSong(gsm.PLAY);
				levelSong.setLooping(true);
				playMusic();
			}
			switch (gsm.PLAY) {
			case 1:
				status = 2;
				
				//create Button
				createButton(1);
				
				//create Pointer
				createPointer(1);
				break;

			case 2:
				status2 = 6;
				
				//create Button
				createButton(2);
				
				//create Pointer
				createPointer(2);
				break;
			}
			
		} else if (gsm.PLAY > 5) {
		
			//create player
			createPlayer();
		
			//create tiles
			createTiles();
		
			//create coins
			createCoins();
		
			playMusic();

		}
		
		//set up box2d camera
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
		
	}

	public void update(float dt) {
		
		//check input
		handleInput(gsm);
		
		//update box2d
		world.step(dt, 6, 2);
		
		if (gsm.PLAY == 0) {
			
			start.update(dt);
			
		} else if (gsm.PLAY > 5) {
			// remove coins
			Array<Body> bodies = cl.getBodiesToRemove();
			for (int i = 0; i < bodies.size; i++) {
				Body b = bodies.get(i);
				coins.removeValue((Coin) b.getUserData(), true);
				world.destroyBody(b);
				player.cellectCrystal();
			}
			bodies.clear();
			
			for (int i = 0; i < coins.size; i++) {
				coins.get(i).update(dt);
			}
			
			player.update(dt);
			
			if (player.getPosition().y < 0 || player.getPosition().x == lastx || player.getPosition().x > 960 * 32) {
				gsm.PLAY = 1;
				setSong(gsm.PLAY);
				setState();
				status = 2;
				Play(gsm);
			} else lastx = player.getPosition().x;
		}
		
	}
	
	public void render() {
		
		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (gsm.PLAY <= 5) {
			//draw background
			sb.begin();
			sb.draw(background, 0, 0, Game.V_WIDTH, Game.V_HEIGHT);
			sb.end();
			
			//set camera
			cam.position.set(Game.V_WIDTH / 2, Game.V_HEIGHT / 2, 0);
			cam.update();
			
			switch (gsm.PLAY) {
			case 0:
				//draw start
				sb.setProjectionMatrix(cam.combined);
				start.render(sb);
				
				//draw bitbeat
				sb.setProjectionMatrix(cam.combined);
				bitbeat.render(sb);
				break;

			case 1:
				//draw bitbeat
				sb.setProjectionMatrix(cam.combined);
				bitbeat.render(sb);
				
				//draw button1
				sb.setProjectionMatrix(cam.combined);
				bt1.render(sb);
				
				//draw button2
				sb.setProjectionMatrix(cam.combined);
				bt2.render(sb);
				
				//draw button3
				sb.setProjectionMatrix(cam.combined);
				bt3.render(sb);
				
				//draw button4
				sb.setProjectionMatrix(cam.combined);
				bt4.render(sb);
				
				//draw pointer
				sb.setProjectionMatrix(cam.combined);
				pointer.render(sb);
				break;
			case 2:
				
				//draw button5
				sb.setProjectionMatrix(cam.combined);
				bt5.render(sb);
				
				//draw button6
				sb.setProjectionMatrix(cam.combined);
				bt6.render(sb);
				
				//draw button7
				sb.setProjectionMatrix(cam.combined);
				bt7.render(sb);
				
				//draw pointer
				sb.setProjectionMatrix(cam.combined);
				pointer2.render(sb);
				break;
			}
			
			
		} else if (gsm.PLAY > 5) {
			
			if (player.getPosition().x * PPM < Game.V_WIDTH / 4) {
				//camera follow player
				cam.position.set(
						Game.V_WIDTH /2, 
						Game.V_HEIGHT / 2,
						0
				);
			} else {
				//camera follow player
				cam.position.set(
						player.getPosition().x * PPM + Game.V_WIDTH / 4, 
						Game.V_HEIGHT / 2,
						0
				);
			}
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
			
			//draw coins
			for (int i = 0; i < coins.size; i++) {
				coins.get(i).render(sb);
			}
			
		}
		
		//draw box2d (object simulator)
		if(debug) {
			b2dr.render(world, b2dCam.combined);
			
		}
		
	}
	
	public void dispose() {}
	
	public void createPointer(int i) {
		if (i == 1) {
			BodyDef bdef = new BodyDef();
			FixtureDef fdef = new FixtureDef();
			PolygonShape shape = new PolygonShape();
			
			bdef.position.set(210 / PPM, 250 / PPM);
			bdef.type = BodyType.DynamicBody;
			Body body = world.createBody(bdef);
			
			shape.setAsBox(24 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef.shape = shape;
			body.createFixture(fdef).setUserData("pointer");
			pointer = new Pointer(body);
		} else if (i == 2) {
			BodyDef bdef = new BodyDef();
			FixtureDef fdef = new FixtureDef();
			PolygonShape shape = new PolygonShape();
			
			bdef.position.set(210 / PPM, 350 / PPM);
			bdef.type = BodyType.DynamicBody;
			Body body = world.createBody(bdef);
			
			shape.setAsBox(24 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef.shape = shape;
			body.createFixture(fdef).setUserData("pointer2");
			pointer2 = new Pointer2(body);
		}
		
	}
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
	
	public void createButton(int i) {
		
		switch (i) {
		case 1:
			BodyDef bdef = new BodyDef();
			FixtureDef fdef = new FixtureDef();
			PolygonShape shape = new PolygonShape();
			
			bdef.position.set(320 / PPM, 250 / PPM);
			bdef.type = BodyType.DynamicBody;
			Body body = world.createBody(bdef);
				
			shape.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef.shape = shape;
			body.createFixture(fdef).setUserData("button1");
			bt1 = new Button1(body);
			
			BodyDef bdef1 = new BodyDef();
			FixtureDef fdef1 = new FixtureDef();
			PolygonShape shape1 = new PolygonShape();
			
			bdef1.position.set(320 / PPM, 200 / PPM);
			bdef1.type = BodyType.DynamicBody;
			Body body1 = world.createBody(bdef1);
				
			shape1.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef1.shape = shape1;
			body1.createFixture(fdef1).setUserData("button2");
			bt2 = new Button2(body1);
			
			BodyDef bdef2 = new BodyDef();
			FixtureDef fdef2 = new FixtureDef();
			PolygonShape shape2 = new PolygonShape();
			
			bdef2.position.set(320 / PPM, 150 / PPM);
			bdef2.type = BodyType.DynamicBody;
			Body body2 = world.createBody(bdef2);
				
			shape2.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef2.shape = shape2;
			body2.createFixture(fdef2).setUserData("button3");
			bt3 = new Button3(body2);
			
			BodyDef bdef3 = new BodyDef();
			FixtureDef fdef3 = new FixtureDef();
			PolygonShape shape3 = new PolygonShape();
			
			bdef3.position.set(320 / PPM, 100 / PPM);
			bdef3.type = BodyType.DynamicBody;
			Body body3 = world.createBody(bdef3);
				
			shape3.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef3.shape = shape;
			body3.createFixture(fdef3).setUserData("button4");
			bt4 = new Button4(body3);
			break;

		case 2:
			BodyDef bdef4 = new BodyDef();
			FixtureDef fdef4 = new FixtureDef();
			PolygonShape shape4 = new PolygonShape();
			
			bdef4.position.set(320 / PPM, 350 / PPM);
			bdef4.type = BodyType.DynamicBody;
			Body body4 = world.createBody(bdef4);
				
			shape4.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef4.shape = shape4;
			body4.createFixture(fdef4).setUserData("button5");
			bt5 = new Button5(body4);
			
			BodyDef bdef5 = new BodyDef();
			FixtureDef fdef5 = new FixtureDef();
			PolygonShape shape5 = new PolygonShape();
			
			bdef5.position.set(320 / PPM, 250 / PPM);
			bdef5.type = BodyType.DynamicBody;
			Body body5 = world.createBody(bdef5);
				
			shape5.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef5.shape = shape5;
			body5.createFixture(fdef5).setUserData("button6");
			bt6 = new Button6(body5);
			
			BodyDef bdef6 = new BodyDef();
			FixtureDef fdef6 = new FixtureDef();
			PolygonShape shape6 = new PolygonShape();
			
			bdef6.position.set(320 / PPM, 150 / PPM);
			bdef6.type = BodyType.DynamicBody;
			Body body6 = world.createBody(bdef6);
				
			shape6.setAsBox(80 / PPM, 24 / PPM , new Vector2(0 , -100 / PPM), 0);
			fdef6.shape = shape6;
			body6.createFixture(fdef6).setUserData("button7");
			bt7 = new Button7(body6);
			break;
		}
		
	}
	
	public void createBitbeat() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		bdef.position.set(320 / PPM, 380 / PPM);
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
	
	private void createCoins() {
		
		coins = new Array<Coin>();
		
		MapLayer layer = tileMap.getLayers().get("coins");
		
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
			body.createFixture(fdef).setUserData("coin");
			
			Coin c = new Coin(body);
			coins.add(c);
			
			body.setUserData(c);
			
		}
		
	}

	@Override
	public void handleInput() {
		
	}
	
}
