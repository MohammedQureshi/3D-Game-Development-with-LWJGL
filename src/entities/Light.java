package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	
	private float lightValueChange = (float) 0.0019;

	public void increaseColour(Vector3f colorIncrease){
	  colorIncrease = new Vector3f(getColour().x+lightValueChange,getColour().y+lightValueChange,getColour().z+lightValueChange);
	  setColour(new Vector3f(colorIncrease.x,colorIncrease.y,colorIncrease.z));
	 }
	 public void decreaseColour(Vector3f colorDecrease){
	  colorDecrease = new Vector3f(getColour().x-lightValueChange,getColour().y-lightValueChange,getColour().z-lightValueChange);
	  setColour(new Vector3f(colorDecrease.x,colorDecrease.y,colorDecrease.z));
	 }

	private Vector3f positions;
	private Vector3f colour;
	private Vector3f attenuation = new Vector3f(1, 0, 0);

	public Light(Vector3f positions, Vector3f colour) {
		super();
		this.positions = positions;
		this.colour = colour;
	}

	public Light(Vector3f positions, Vector3f colour, Vector3f attenuation) {
		super();
		this.positions = positions;
		this.colour = colour;
		this.attenuation = attenuation;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Vector3f getPositions() {
		return positions;
	}

	public void setPositions(Vector3f positions) {
		this.positions = positions;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}

}
