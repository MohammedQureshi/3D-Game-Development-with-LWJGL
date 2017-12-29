package entities;

import models.TexturedModel;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import terrains.Terrain;

public class movingEntity extends Entity {
	
	public static float RUN_SPEED = 15;
	public static float TURN_SPEED = 45;
	public static final float GRAVITY = -45;
	private static final float JUMP_POWER = 30;
	private boolean isInAir = false;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float currentSidewaysSpeed = 0;
	private float upwardsSpeed = 0;

	public movingEntity(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(Terrain terrain) {	
		checkInput();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float distanceSideways = currentSidewaysSpeed * DisplayManager.getFrameTimeSeconds();
		
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz); 
		
		float dxSideways = (float) (distanceSideways * Math.sin(Math.toRadians(super.getRotY()+90)));
		float dzSideways = (float) (distanceSideways * Math.cos(Math.toRadians(super.getRotY()+90)));
		super.increasePosition(dxSideways, 0, dzSideways); 
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0,
				upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			//isInAir = true;
		}
	}
	
	private void checkInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
			jump();
		}
		Random randomNum = new Random();
		this.currentSpeed = RUN_SPEED;
		this.currentTurnSpeed = (-360 + randomNum.nextFloat() * (360 - (-360)));
	}

	
}
