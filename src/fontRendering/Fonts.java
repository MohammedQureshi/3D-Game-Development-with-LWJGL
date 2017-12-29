package fontRendering;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.Loader;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import mainGame.Launcher;

public class Fonts extends Launcher{
	
	public static void LoadFonts(){
		Loader loader = new Loader();
		FontType font = new FontType(loader.loadTexture("candara"), "candara");
		GUIText text = new GUIText("You just have to read this!", 1.3f, font, new Vector2f(-0.35f, 0.026f), 1f, true);
		text.setColour(1.0f, 1.0f, 1.0f);
		GUIText text2 = new GUIText(Launcher.userEntered, 1.0f, font, new Vector2f(0, 0.42f), 1f, true);
		text2.setColour(1.0f, 1.0f, 1.0f);
	}
	
}
