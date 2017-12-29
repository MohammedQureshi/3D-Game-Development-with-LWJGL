package entities;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {
	
	public static boolean movePlayerWithMouse = true;
	private static float RUN_SPEED = 45;
	public static final float GRAVITY = -45;
	private static final float JUMP_POWER = 30;
	private static final int SENSITIVITY = 5; 
	private boolean isInAir = false;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float currentSidewaysSpeed = 0;
	private float upwardsSpeed = 0;

	public Player(TexturedModel model, Vector3f position, float rotX,
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
			isInAir = true;
		}
	}
	
	private void checkInput() {
		while(Keyboard.next()) {
			
		    if(!Keyboard.getEventKeyState()) {
		        if(Keyboard.getEventKey() == Keyboard.KEY_TAB) { 
		           movePlayerWithMouse = !movePlayerWithMouse;
		        }
		    }
		}
		
		if(movePlayerWithMouse == true) {
			rotY -= Mouse.getDX() / SENSITIVITY;
			Mouse.setGrabbed(true);
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				this.currentSpeed = RUN_SPEED;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				this.currentSpeed = -RUN_SPEED;
			} else {
				this.currentSpeed = 0;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				RUN_SPEED = 25;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				RUN_SPEED = 75;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_B)) {
				RUN_SPEED = 250; // Disable On Release
			}else{
				RUN_SPEED = 50;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				this.currentSidewaysSpeed = RUN_SPEED;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				this.currentSidewaysSpeed = -RUN_SPEED;
			} else {
				this.currentSidewaysSpeed = 0;
			}
		
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				jump();
			}
			
		}else{
			Mouse.setGrabbed(false);
			this.currentSpeed = 0;
			this.currentTurnSpeed = 0;
		}
		
	}

	
}
