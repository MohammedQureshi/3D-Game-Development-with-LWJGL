package audio;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL10;

public class Test {
	
	public static void main(String args[]) throws IOException, InterruptedException{
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		int buffer = AudioMaster.loadSound("birds");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		source.setPosition(0, 0, 0);
		float yPos = 0;
		
		
		char c = ' ';
		while(c != 'q'){
		    if(!Keyboard.getEventKeyState()) {
		        if(Keyboard.getEventKey() == Keyboard.KEY_W) { 
		        	yPos = yPos - 0.02f;	
		        }
		    }
			source.setPosition(0, yPos, 0);
			System.out.println(yPos);
			Thread.sleep(10);
		}
		
		source.delete();
		AudioMaster.cleanUp();
	}
	
}
