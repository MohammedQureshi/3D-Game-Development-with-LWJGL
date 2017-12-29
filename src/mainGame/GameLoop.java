package mainGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import audio.AudioMaster;
import audio.Source;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.movingEntity;
import fontRendering.Fonts;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

/*
 * Creator Mohammed Faisal Qureshi
 * 
 * Creation Date 25th July 2015 
 * 
 * Version 8.2
 * 
 * Latest Update 3th November 2017
 * 
 * OpenGL Version 3.1
 * 
 * Max FPS 60
 * 
 * Library LWJGL
 * 
 * Language Java
 * 
 * Editor Eclipse
 * 
 * Frame Size 1280 x 720
 */


/* Main Game Class */


public class GameLoop {
	
	private static int waterLevel = 8; /* Sets Overall Water Level */ 
	private static Loader loader = new Loader(); /* Loads up the Loader Class */
	private static void print(String text){System.out.println(text+" Loaded."); /* Basic Print Statement To Show Everything Loaded */}
	private static RawModel recordData(ModelData dataRecord){
		return loader.loadToVAO(dataRecord.getVertices(), dataRecord.getTextureCoords(), dataRecord.getNormals(), dataRecord.getIndices());
		/* Easier Way Of Getting Model Data */ }
	private static TexturedModel textureModel(RawModel modelData, String textureName) {
		return new TexturedModel(modelData , new ModelTexture(loader.loadTexture(textureName)));
	}
	private static TerrainTexture terrainTextures(String textureName) { return new TerrainTexture(loader.loadTexture(textureName));}
	public static void sub() { /* Starts Loading Game */
		/******************************************Setting Up*******************************************/
		DisplayManager.createDisplay(); /* Creates Display */
		/*****************************************Splash Screen*****************************************/
		List<GuiTexture> splashscreen = new ArrayList<GuiTexture>();
		GuiTexture splashScreen = new GuiTexture(loader.loadGuiTexture("SplashScreen"),new Vector2f(0.0f, -0.3f), new Vector2f(1.0f,2.0f));
		splashscreen.add(splashScreen);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		guiRenderer.render(splashscreen);
		DisplayManager.updateDisplay();
		/*****************************************Load Audio*****************************************/
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		int buffer = AudioMaster.loadSound("birds");
		Source source = new Source();
		source.setLooping(true);
		/************************************************************************************************/
		TextMaster.init(loader); /* Loads Text Reader Into Game */
		/***************************************Load Player Model****************************************/
		ModelData playerData = OBJFileLoader.loadOBJ("player");
		RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
		TexturedModel playerTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("player")));
		Player player = new Player(playerTexture, new Vector3f(91, 0, 85), 0, 45, 0, 0.8f);
		/***************************************Moving Entity Model**************************************/
		ModelData gravityObject = OBJFileLoader.loadOBJ("player");
		RawModel gravityModel = recordData(gravityObject);
		TexturedModel gravityTexture = new TexturedModel(gravityModel, new ModelTexture(loader.loadTexture("player")));
		movingEntity gravityEntity = new movingEntity(gravityTexture, new Vector3f(100, 25, 100), 0, 0, 0, 1);
		/**********************************************Loaders*******************************************/
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		Fonts.LoadFonts();
		/*****************************************Simplified Controls*************************************/	
		/* Grass Texture Pack */
		String backgroundTextureGrass = "grass2";
		String redTextureGrass = "dirt";
		String greenTextureGrass = "pinkFlowers";
		String blueTextureGrass = "grassPath";
		/**************************************Terrain Texture Packs**************************************/
		/* Grass Biome Texture */
		TerrainTexture backgroundTexture = terrainTextures(backgroundTextureGrass);
		TerrainTexture rTexture = terrainTextures(redTextureGrass);
		TerrainTexture gTexture = terrainTextures(greenTextureGrass);
		TerrainTexture bTexture = terrainTextures(blueTextureGrass);
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		print("Grass Bioms");
		/* Sand Biome Texture */
		TerrainTexture backgroundTextureSand = backgroundTexture;
		TerrainTexture rTextureSand = terrainTextures("sand");
		TerrainTexture gTextureSand = terrainTextures("woodchip");
		TerrainTexture bTextureSand = terrainTextures("stone");
		TerrainTexturePack sandTexturePack = new TerrainTexturePack(backgroundTextureSand, rTextureSand, gTextureSand, bTextureSand);
		print("Sand Biome");
		/* Snow Biome Texture */
		TerrainTexture backgroundTextureSnow = backgroundTextureSand;
		TerrainTexture rTextureSnow = terrainTextures("snow");
		TerrainTexture gTextureSnow = terrainTextures("woodchip");
		TerrainTexture bTextureSnow = terrainTextures("stone");
		TerrainTexturePack snowTexturePack = new TerrainTexturePack(backgroundTextureSnow, rTextureSnow, gTextureSnow, bTextureSnow);
		print("Snow Biome");
		/********************************************Blend Maps********************************************/
		TerrainTexture blendMap = terrainTextures("blendMap");
		TerrainTexture blendMap2 = terrainTextures("blendMap2");
		//TerrainTexture blendMap4 = terrainTextures("blendMap4");
		//TerrainTexture blendMap5 = terrainTextures("blendMap5");
		//TerrainTexture blendMap6 = terrainTextures("blendMap6");
		print("Blend Maps");
		/*********************************************Terrains*********************************************/
		Terrain[][] terrains = new Terrain[4][4]; //Grid For Terrain 4x4
		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap2,"heightmap");//fixed
		Terrain terrain2 = new Terrain(0, 1, loader, sandTexturePack,blendMap2,"heightmap"); //fixed
		Terrain terrain3 = new Terrain(0, 2, loader, texturePack,blendMap,"heightmap"); //fixed
		Terrain terrain4 = new Terrain(0, 3, loader, texturePack, blendMap,"heightmap");//fixed
		Terrain terrain5 = new Terrain(1, 0, loader, sandTexturePack,blendMap2,"heightmap");
		Terrain terrain6 = new Terrain(1, 1, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain7 = new Terrain(1, 2, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain8 = new Terrain(1, 3, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain9 = new Terrain(2, 0, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain10 = new Terrain(2, 1, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain11 = new Terrain(2, 2, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain12 = new Terrain(2, 3, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain13 = new Terrain(3, 0, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain14 = new Terrain(3, 1, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain15 = new Terrain(3, 2, loader, snowTexturePack,blendMap2,"heightmap");
		Terrain terrain16 = new Terrain(3, 3, loader, snowTexturePack,blendMap2,"heightmap");
		/*Collision For The Terrains */
		terrains[0][0] = terrain;
		terrains[0][1] = terrain2;
		terrains[0][2] = terrain3;
		terrains[0][3] = terrain4;
		terrains[1][0] = terrain5;
		terrains[1][1] = terrain6;
		terrains[1][2] = terrain7;
		terrains[1][3] = terrain8;
		terrains[2][0] = terrain9;
		terrains[2][1] = terrain10;
		terrains[2][2] = terrain11;
		terrains[2][3] = terrain12;
		terrains[3][0] = terrain13;
		terrains[3][1] = terrain14;
		terrains[3][2] = terrain15;
		terrains[3][3] = terrain16;
		print("Terrains");	
		/*************************************List Set Up************************************************/
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		Random random = new Random(676452);
		/**************************************Models and Textures***************************************/
		entities.add(gravityEntity); /* Add Moving Object Into Game */
		entities.add(player);
		ModelData data = OBJFileLoader.loadOBJ("gate"); /* Mohammed's Model - Gate */
		RawModel houseModel = recordData(data);
		Entity houseEntity = new Entity(textureModel(houseModel, "bigWall"), new Vector3f(315, 15, 629), 0f, 90f, 0f, 8f);
		print("Gate Model");
		ModelData well = OBJFileLoader.loadOBJ("wellModel"); /* Mohammed's Model - Well Model */
		RawModel wellModel = recordData(well);
		TexturedModel wellTextured = new TexturedModel(wellModel, new ModelTexture(loader.loadTexture("wellTexture")));
		Entity wellEntity = new Entity(wellTextured, new Vector3f(129, 12, 135), 0f , 0f, 0f, 8f);
		entities.add(wellEntity);
		
		ModelData basicHouse = OBJFileLoader.loadOBJ("BasicHouse"); /* Mohammed's Model - Basic House */
		RawModel basicHouseModel = recordData(basicHouse);
		Entity basicHouseEntity = new Entity(textureModel(basicHouseModel, "BasicHouseTexture"), new Vector3f(391, 8, 269), 0f, 10f, 0f, 12f);
		entities.add(basicHouseEntity);
		
		//Mohammed's Model - Two Story House
		ModelData TwoStoryHouse = OBJFileLoader.loadOBJ("TwoStoryHouse"); /* Mohammed's Model - Basic House */
		RawModel TwoStoryHouseouseModel = recordData(TwoStoryHouse);
		TexturedModel TwoStoryHouseTexture = new TexturedModel(TwoStoryHouseouseModel, new ModelTexture(loader.loadTexture("BasicHouseTexture")));
		TwoStoryHouseTexture.getTexture().setExtraInfoMap(loader.loadTexture("VillageHouseGlowMap"));
		Entity twoStoryHouse = new Entity(TwoStoryHouseTexture, new Vector3f(318, 9, 306), 0f, 130f, 0f, 12f);
		entities.add(twoStoryHouse);
		
		//Mohammed's Model - PentaHouse
		ModelData PentaHouse = OBJFileLoader.loadOBJ("PentaHouse");
		RawModel PentaHouseModel = recordData(PentaHouse);
		TexturedModel PentaHouseTexture = new TexturedModel(PentaHouseModel, new ModelTexture(loader.loadTexture("BasicHouseTexture")));
		PentaHouseTexture.getTexture().setExtraInfoMap(loader.loadTexture("VillageHouseGlowMap"));
		Entity PentaHouseEntity = new Entity(PentaHouseTexture, new Vector3f(353, 12, 435), 0f, 0f, 0f, 12f);
		entities.add(PentaHouseEntity);
		
		//Mohammed's Model - Village House
		ModelData VillageHouse = OBJFileLoader.loadOBJ("VillageHouse");
		RawModel VillageHouseModel = recordData(VillageHouse);
		TexturedModel VillageHouseTexture = new TexturedModel(VillageHouseModel, new ModelTexture(loader.loadTexture("BasicHouseTexture")));
		VillageHouseTexture.getTexture().setExtraInfoMap(loader.loadTexture("VillageHouseGlowMap"));
		Entity VillageHouseEntiy = new Entity(VillageHouseTexture, new Vector3f(41, 19, 102),0, 0, 0, 12f);
		entities.add(VillageHouseEntiy);
		Entity VillageHouseEntity2 = new Entity(VillageHouseTexture, new Vector3f(41, 18, 172),0, 0, 0, 12f);
		entities.add(VillageHouseEntity2);
		Entity VillageHouseEntity3 = new Entity(VillageHouseTexture, new Vector3f(41, 15, 242),0, 0, 0, 12f);
		entities.add(VillageHouseEntity3);
		Entity VillageHouseEntity4 = new Entity(VillageHouseTexture, new Vector3f(41, 15, 312),0, 0, 0, 12f);
		entities.add(VillageHouseEntity4);
		Entity VillageHouseEntity5 = new Entity(VillageHouseTexture, new Vector3f(41, 15, 382),0, 0, 0, 12f);
		entities.add(VillageHouseEntity5);
		
		//New Method of Loading Entities
		ModelData farmPentData = OBJFileLoader.loadOBJ("farmpent");
		RawModel farmPentModel = recordData(farmPentData);
		Entity farmPentEntity = new Entity(textureModel(farmPentModel, "box"), new Vector3f(460, 8 ,107), 0f,150f,0f,6f);
		entities.add(farmPentEntity);		
		print("FarmPent Model");

		/* Board Model */
		TexturedModel exampleModel = new TexturedModel(OBJLoader.loadObjModel("exampleOBJ", loader), new ModelTexture(loader.loadTexture("example")));
		ModelTexture exampleTexture = exampleModel.getTexture();
		exampleTexture.setShineDamper(10);
		exampleTexture.setReflectiviry(1);
		print("example Model");
		/* Pine Tree Model */
		TexturedModel treeModel = new TexturedModel(OBJLoader.loadObjModel("pine", loader), new ModelTexture(loader.loadTexture("pine")));
		ModelTexture treeTexture = treeModel.getTexture();
		treeTexture.setShineDamper(20);
		treeTexture.setReflectiviry(1);
		print("treeModel");
		/* Grass Model */
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		print("grassModel");
		/* Lamp Model */
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",loader), new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);
		print("lampModel");
		/* Fern Model */
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		print("fernAtlas");
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader), fernTextureAtlas);
		fern.getTexture().setHasTransparency(true);
		fern.getTexture().setShineDamper(8);
		print("fermModel");
		/*Cherry Model*/
		TexturedModel cherry = new TexturedModel(OBJLoader.loadObjModel("cherry", loader), new ModelTexture(loader.loadTexture("cherry")));
		cherry.getTexture().setHasTransparency(true);
		cherry.getTexture().setShineDamper(10);
		cherry.getTexture().setReflectiviry(0.5f);
		cherry.getTexture().setExtraInfoMap(loader.loadTexture("cherryS"));
		print("cherryModel");
		/*Willow*/
		TexturedModel willow = new TexturedModel(OBJLoader.loadObjModel("willow", loader),new ModelTexture(loader.loadTexture("willow")));
		print("wilowModel");
		
		TexturedModel lantern = new TexturedModel(OBJLoader.loadObjModel("lantern", loader), new ModelTexture(loader.loadTexture("lantern")));
		lantern.getTexture().setExtraInfoMap(loader.loadTexture("lanternS"));
		print("lantern");
		entities.add(new Entity(lantern, new Vector3f(52,19,136),0,0,0,1));
		/***********************************************************************************************/
		//                                    Normal Map Models
		/***********************************************************************************************/
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectiviry(0.5f);
		print("barrelModel");
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectiviry(0.5f);
		print("crateModel");
		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectiviry(0.5f);
		print("boulderModel");
		/***********************************************************************************************/
		//                                    Entities
		/***********************************************************************************************/
		Entity entity = new Entity(barrelModel, new Vector3f(290, 0, 490), 0,0, 0, 1f);
		Entity entity2 = new Entity(boulderModel, new Vector3f(310, 0, 510), 0,0, 0, 1f);
		Entity entity3 = new Entity(crateModel, new Vector3f(330, 0, 530), 0,0, 0, 0.04f);
		normalMapEntities.add(entity);
		normalMapEntities.add(entity2);
		normalMapEntities.add(entity3);
		/***********************************************************************************************/
		//                                    Fern Random Location
		/***********************************************************************************************/
		for (int i = 0; i < 125; i++) {
			float x = random.nextFloat() * 780;
			float z = random.nextFloat() * 780;
			int gridXModels = (int) (x / Terrain.SIZE);
			int gridZModels = (int) (z / Terrain.SIZE);
			Terrain currentModel = terrains[gridXModels][gridZModels];
			float y = currentModel.getHeightOfTerrain(x, z);
			entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
		}
		/***********************************************************************************************/
		//                                    Grass Random Location
		/***********************************************************************************************/
		for (int i = 0; i < 50; i++) {
			float x = random.nextFloat() * 780;
			float z = random.nextFloat() * 780;
			int gridXModels = (int) (x / Terrain.SIZE);
			int gridZModels = (int) (z / Terrain.SIZE);
			Terrain currentModel = terrains[gridXModels][gridZModels];
			float y = currentModel.getHeightOfTerrain(x, z);
			entities.add(new Entity(grass, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 3f));
		}
		/***********************************************************************************************/
		//                                    Tree Random Location
		/***********************************************************************************************/
		for (int i = 0; i < 50; i++) {
			float x = random.nextFloat() * 780;
			float z = random.nextFloat() * 780;
			int gridXModels = (int) (x / Terrain.SIZE);
			int gridZModels = (int) (z / Terrain.SIZE);
			Terrain currentModel = terrains[gridXModels][gridZModels];
			float y = currentModel.getHeightOfTerrain(x, z);
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1f));
		}
		/***********************************************************************************************/
		//                                   Random Stall Model
		/***********************************************************************************************/
		//                                   Extra Single Models
		/***********************************************************************************************/
		//Board
		entities.add(new Entity(exampleModel, new Vector3f(115, 14, 112), 0, 140, 0, 3));
		//Cherry
		entities.add(new Entity(cherry, new Vector3f(200, 5, 200), 0, 0, 0, 4));
		entities.add(new Entity(cherry, new Vector3f(375, -4, 587), 0, 0, 0, 4));
		entities.add(new Entity(cherry, new Vector3f(252, -7, 770), 0, 0, 0, 4));
		//Willow
		entities.add(new Entity(willow, new Vector3f(300, 5, 200), 0, 0, 0, 4));
		entities.add(new Entity(willow, new Vector3f(474, 5, 342), 0, 0, 0, 4));
		entities.add(new Entity(willow, new Vector3f(546, 1, 131), 0, 0, 0, 4));
		entities.add(new Entity(willow, new Vector3f(114, 5, 667), 0, 0, 0, 4));
		//entities.add(new Entity(lantern, new Vector3f(30,10,30),0,0,0,1));
		entities.add(houseEntity);
		print("Entities Added To Game All");
		/***********************************************************************************************/
		//                                    Lights
		/***********************************************************************************************/
		List<Light> lights = new ArrayList<Light>();
		float lightcolourwhite = 1.7f;
		Light sun = new Light(new Vector3f(1000000, 1000000, 1000000), new Vector3f(lightcolourwhite, lightcolourwhite, lightcolourwhite));
		lights.add(sun);
		lights.add(new Light(new Vector3f(169, 15, 163), new Vector3f(2, 0, 0),new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(281, 15, 331), new Vector3f(0, 2, 2),new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(316, 14, 412), new Vector3f(2, 2, 0),new Vector3f(1, 0.01f, 0.002f)));
		print("Lights");
		// Lamp Entity
		entities.add(new Entity(lamp, new Vector3f(169, 12, 163), 0, 0, 0, 1)); // Red
		entities.add(new Entity(lamp, new Vector3f(281, 12, 331), 0, 0, 0, 1));// Green
		entities.add(new Entity(lamp, new Vector3f(316, 12, 412), 0, 0, 0, 1)); // Yellow
		/***********************************************************************************************/
		//                                    Game GUI's
		/***********************************************************************************************/
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture GameGUI = new GuiTexture(loader.loadGuiTexture("GameGUI"),new Vector2f(0.60f, -0.42f), new Vector2f(1.60f, 1.42f));
		guis.add(GameGUI);
		print("GameGUI");
		GuiTexture Crosshair = new GuiTexture(loader.loadGuiTexture("crosshair"),new Vector2f(0, 0.070f), new Vector2f(0.01f, 0.017f));
		guis.add(Crosshair);
		print("Crosshair");
		/***********************************************************************************************/
		//                                    Paricle System 1
		/***********************************************************************************************/
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("smoke"), 8, false);
		print("smoke");
		ParticleTexture particleTexture1 = new ParticleTexture(loader.loadTexture("cosmic"), 4, true);
		print("cosmic");
		ParticleSystem system = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(30.8f);
		/***********************************************************************************************/
		//                                    Paricle System 2
		/***********************************************************************************************/
		//float pps, float speed, float gravityComplient, float lifeLength, float scale
		ParticleSystem system2 = new ParticleSystem(particleTexture1, 50, 25, 0.3f, 4, 1);
		system2.randomizeRotation();
		system2.setDirection(new Vector3f(0, -1, 0), 2.9f);
		system2.setLifeError(0.1f);
		system2.setSpeedError(0.9f);
		system2.setScaleError(0.3f);
		
		//FBOs
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		print("FBO");
		
		//Water
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(150, 150, waterLevel); waters.add(water);
		WaterTile water9 = new WaterTile(150, 250, waterLevel); waters.add(water9);
		WaterTile water10 = new WaterTile(150, 350, waterLevel); waters.add(water10);
		WaterTile water15 = new WaterTile(250, 150, waterLevel); waters.add(water15);
		WaterTile water16 = new WaterTile(250, 250, waterLevel); waters.add(water16);
		WaterTile water17 = new WaterTile(250, 350, waterLevel); waters.add(water17);
		print("Water");
		/*************************************Final Changes************************************************/
		int particleMover = 302;
		sun.setColour(new Vector3f(0.3f,0.3f,0.3f));
		source.setPosition(0, 0, 0);
		source.play(buffer);
		print("Game");
/**************************************Running Game Loop***************************************************************/
	while (!Display.isCloseRequested()&& !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {		
		/************************************Day/Night Cycle********************************************/
		if(SkyboxRenderer.time >= 0 && SkyboxRenderer.time < 5150){
		    sun.decreaseColour(new Vector3f(0.3f,0.3f,0.3f));
		    MasterRenderer.RED = 0.01f; MasterRenderer.GREEN = 0.01f; MasterRenderer.BLUE = 0.01f;
	   }else if(SkyboxRenderer.time >= 5150 && SkyboxRenderer.time < 8000){
		    sun.increaseColour(new Vector3f(0.0001f,0.0001f,0.0001f));
		    MasterRenderer.RED += 0.00157f; MasterRenderer.GREEN += 0.00157f; MasterRenderer.BLUE += 0.0018f;
	   }else if(SkyboxRenderer.time >= 8000 && SkyboxRenderer.time < 21000){
			sun.setColour(new Vector3f(1f,1f,1f));
		    MasterRenderer.RED = 0.5444f; MasterRenderer.GREEN = 0.62f; MasterRenderer.BLUE = 0.69f;
	   }else{
			sun.decreaseColour(new Vector3f(0.0001f,0.0001f,0.0001f));
		    MasterRenderer.RED -= 0.002f; MasterRenderer.GREEN -= 0.002f; MasterRenderer.BLUE -= 0.002f;
		   }
		/*************************************Print Players Location************************************/
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
		System.out.println("Player Cords "+"X =" + player.getPosition().x + "  "+ "Y =" + player.getPosition().y + " " + "Z ="+ player.getPosition().z);
		System.out.println("  ");
		System.out.println("Player Rot "+"X=" + player.getRotX() + "  "+ "Y =" + player.getRotY() + " " + "Z ="+ player.getRotZ());
		System.out.println("  ");
		System.out.println("Object Cords "+"X =" + gravityEntity.getPosition().x + "  "+ "Y =" + gravityEntity.getPosition().y + " " + "Z ="+ gravityEntity.getPosition().z);
		System.out.println("  "); }
		/*************************************Render Particles******************************************/
		system2.generateParticles(player.getPosition()); /* Renders Particles On Player */
		//This moves the particles 	
		particleMover = particleMover + 1; /* Moves Particles */
		if(particleMover == 500){ particleMover = particleMover - 198; } /* Sets Particles Location */
		system.generateParticles(new Vector3f(900, 10, 900));
		system2.generateParticles(new Vector3f(particleMover, 35, 235));
		ParticleMaster.update(camera);
		/************************************Player Collision*******************************************/
		int gridX = (int) (player.getPosition().x / Terrain.SIZE);
		int gridZ = (int) (player.getPosition().z / Terrain.SIZE);
		Terrain currentTerrain = terrains[gridX][gridZ];
		camera.move();
		player.move(currentTerrain);
		camera.move();
		gravityEntity.move(currentTerrain);
		/***********************************************************************************************/
		//                                   Start Renderering Game
		/***********************************************************************************************/
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
				
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights,camera, new Vector4f(0, 1, 0, -water.getHeight()+1));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights,camera, new Vector4f(0, -1, 0, water.getHeight()+1));
			
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights,camera, new Vector4f(0, -1, 0, 20));
			ParticleMaster.renderParticle(camera);
			waterRenderer.render(waters, camera, sun);
			multisampleFbo.unbindFrameBuffer();
			
			//multisampleFbo.resolveToScreen(); //DONT ENABLE AT THE SAME TIME AS BOTTOM
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			
			renderer.renderShadowMap(entities, normalMapEntities , sun);
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		System.out.println("Game Closed - Cleaning Up!");
		/*************************************Clean Up Methods******************************************/
		source.delete();
		AudioMaster.cleanUp();
		PostProcessing.cleanUp();
		buffers.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		waterShader.cleanUp();
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		System.out.println("Game Fully Cleaned Up!"); } }

