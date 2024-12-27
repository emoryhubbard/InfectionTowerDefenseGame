package com.gdsoftworks.infectionbeta;

import javax.microedition.khronos.opengles.GL10;

import android.content.pm.ActivityInfo;

import com.gdsoftworks.androidapp.opengl.Animation;
import com.gdsoftworks.androidapp.opengl.GLApp;
import com.gdsoftworks.androidapp.opengl.Texture;
import com.gdsoftworks.androidapp.opengl.TextureRegion;
import com.gdsoftworks.androidapp.opengl.TintBatcher;
import com.gdsoftworks.app.Audio;
import com.gdsoftworks.app.Music;
import com.gdsoftworks.app.Sound;

public class Assets {
	public static Texture mainMenuBackground;
	public static TextureRegion mainMenuBackgroundRegion;
	public static Texture menuAtlas;
	public static TextureRegion logo;
	public static TextureRegion mainMenu;
	public static TextureRegion soundOn;
	public static TextureRegion soundOff;
	public static TextureRegion options;
	public static TextureRegion resume;
	public static TextureRegion exit;
	public static TextureRegion expanded, collapsed;
	public static TextureRegion spinArrows;
	public static TextureRegion rotationWidget;
	public static TextureRegion[] digits = new TextureRegion[12];
	public static TextureRegion ng;
	public static TextureRegion gadgetButton;
	public static TextureRegion card;
	public static TextureRegion lipolyzer;
	public static TextureRegion drexlerSaw;
	public static TextureRegion scaffold;
	public static TextureRegion ionCatcher;
	public static TextureRegion none;
	public static Texture background;
	public static TextureRegion lungLevelBackground;
	public static Texture lungLevel;
	public static TextureRegion healthyLungTissue;
	public static TextureRegion tissueFilm;
	public static TextureRegion white;
	public static Texture modelAtlas;
	public static TextureRegion platform;
	public static TextureRegion slingIon;
	public static TextureRegion lipolase;
	public static Animation sparks;
	public static Texture throbberAtlas;
	public static TextureRegion throbber;
	public static TextureRegion infectingThrobber;
	public static TextureRegion throbberVapor;
	public static TextureRegion bigVapor;
	public static TextureRegion flake1;
	public static Animation vaporFading;
	public static Animation throbbing;
	public static Texture disintegration;
	public static Animation disintegrating;
	public static Texture help1;
	public static TextureRegion help1Region;
	public static Texture help2;
	public static TextureRegion help2Region;
	public static Texture help3;
	public static TextureRegion help3Region;
	public static Texture help4;
	public static TextureRegion help4Region;
	public static Texture more1;
	public static TextureRegion more1Region;
	public static Texture more2;
	public static TextureRegion more2Region;
	public static Music music;
	public static Sound spark;
	public static Sound shot;
	public static Sound stick;
	public static Sound cut;
	public static Sound click;
	public static Sound woosh;
	public static Sound clang;
	public static Sound loudClang;
	public static Sound airPowerTool;
	public static TintBatcher tintBatcher;
	public static final int LANDSCAPE = 0;
	public static final int PORTRAIT = 1;
	public static volatile Infection main;
	
	public static void load(GLApp app) {
		mainMenuBackground = new Texture(app, "gui/LungFungus.png");
		mainMenuBackgroundRegion = new TextureRegion(mainMenuBackground,
				0, 0, 675, 1024);
		menuAtlas = new Texture(app, "gui/MenuAtlas.png");
		logo = new TextureRegion(menuAtlas, 0, 0, 256, 128);
		mainMenu = new TextureRegion(menuAtlas, 256, 0, 160, 276);
		soundOn = new TextureRegion(menuAtlas, 0, 128, 128, 128);
		soundOff = new TextureRegion(menuAtlas, 0, 256, 128, 128);
		options = new TextureRegion(menuAtlas, 128, 128, 128, 58);
		resume = new TextureRegion(menuAtlas, 256, 276, 160, 92);
		exit = new TextureRegion(menuAtlas, 256, 276+92, 160, 92);
		expanded = new TextureRegion(menuAtlas, 420, 0, 200, 320);
		collapsed = new TextureRegion(menuAtlas, 420, 404, 200, 34);
		spinArrows = new TextureRegion(menuAtlas, 620, 0, 130, 130);
		rotationWidget = new TextureRegion(menuAtlas, 666, 178, 290, 290);
		
		int width = 8; int height = 9;
		for (int i=0, offset=0; i<12; i++, offset+=9)
			digits[i] = new TextureRegion(menuAtlas, 750+offset, 2, width, height);
		ng = new TextureRegion(menuAtlas, 355, 701, 312, 61);
		
		gadgetButton = new TextureRegion(menuAtlas, 38, 696, 294, 90);
		card = new TextureRegion(menuAtlas, 10, 489, 150, 150);
		lipolyzer = new TextureRegion(menuAtlas, 192-20, 509-20, 150, 150);
		drexlerSaw = new TextureRegion(menuAtlas, 507-20, 508-20, 150, 150);
		ionCatcher = new TextureRegion(menuAtlas, 629-20, 509-20, 150, 150);
		scaffold = new TextureRegion(menuAtlas, 751-20, 513-20, 150, 150);
		none = new TextureRegion(menuAtlas, 1024-10, 1024-10, 2, 2);
		
		background = new Texture(app, "gui/Background.png");
		lungLevelBackground = new TextureRegion(background, 0, 0, 1024, 1024*(2/3d));
		lungLevel = new Texture(app, "gui/LungLevelv3.png");
		healthyLungTissue = new TextureRegion(lungLevel, 0, 500, 1024, 300);
		tissueFilm = new TextureRegion(lungLevel, 0, 500-80-250, 1024, 300);
		white = new TextureRegion(lungLevel, 0, 0, 20, 20);
		modelAtlas = new Texture(app, "models/ModelAtlasv2.png");
		platform = new TextureRegion(modelAtlas, 353, 25, 431, 431);
		slingIon = new TextureRegion(modelAtlas, 44, 170, 117, 117);
		lipolase = new TextureRegion(modelAtlas, 193, 159, 138, 143);
		sparks = new Animation(.0125,
				new TextureRegion(modelAtlas, 14+306*2, 758, 306, 80),
				new TextureRegion(modelAtlas, 14+306, 758, 306, 80),
				new TextureRegion(modelAtlas, 14, 758, 306, 80),
				new TextureRegion(modelAtlas, 14+306*2, 568, 306, 80),
				new TextureRegion(modelAtlas, 14+306, 568, 306, 80),
				new TextureRegion(modelAtlas, 14, 568, 306, 80),
				new TextureRegion(modelAtlas, 14+306*2, 661, 306, 80),
				new TextureRegion(modelAtlas, 14+306, 661, 306, 80),
				new TextureRegion(modelAtlas, 14, 661, 306, 80),
				new TextureRegion(modelAtlas, 15+306*2, 472, 306, 80),
				new TextureRegion(modelAtlas, 15+306, 472, 306, 80),
				new TextureRegion(modelAtlas, 15, 472, 306, 80));
		throbberAtlas = new Texture(app, "models/ThrobberAtlas.png", GL10.GL_NEAREST);
		throbber = new TextureRegion(throbberAtlas, 30, 30, 282, 282);
		infectingThrobber = new TextureRegion(throbberAtlas, 30+300*2, 30+300*2, 282, 282);
		throbberVapor = new TextureRegion(throbberAtlas, 753, 1007, 6, 6);
		bigVapor = new TextureRegion(throbberAtlas, 768, 983, 36, 36);
		flake1 = new TextureRegion(throbberAtlas, 38, 936, 91, 78);
		vaporFading = new Animation(1,
				new TextureRegion(throbberAtlas, 768, 983, 36, 36),
				new TextureRegion(throbberAtlas, 768+36, 983, 36, 36),
				new TextureRegion(throbberAtlas, 768+36*2, 983, 36, 36),
				new TextureRegion(throbberAtlas, 768+36*3, 983, 36, 36),
				new TextureRegion(throbberAtlas, 768+36*4, 983, 36, 36));
		throbbing = new Animation(.05,
				new TextureRegion(throbberAtlas, 30, 30, 282, 282),
				new TextureRegion(throbberAtlas, 30+300, 30, 282, 282),
				new TextureRegion(throbberAtlas, 30+300*2, 30, 282, 282),
				new TextureRegion(throbberAtlas, 30, 30+300, 282, 282),
				new TextureRegion(throbberAtlas, 30+300, 30+300, 282, 282),
				new TextureRegion(throbberAtlas, 30+300*2, 30+300, 282, 282),
				new TextureRegion(throbberAtlas, 30, 30+300*2, 282, 282),
				new TextureRegion(throbberAtlas, 30+300, 30+300*2, 282, 282),
				new TextureRegion(throbberAtlas, 30+300*2, 30+300*2, 282, 282),
				new TextureRegion(throbberAtlas, 30+300, 30+300*2, 282, 282),
				new TextureRegion(throbberAtlas, 30, 30+300*2, 282, 282),
				new TextureRegion(throbberAtlas, 30+300*2, 30+300, 282, 282),
				new TextureRegion(throbberAtlas, 30+300, 30+300, 282, 282),
				new TextureRegion(throbberAtlas, 30, 30+300, 282, 282),
				new TextureRegion(throbberAtlas, 30+300*2, 30, 282, 282),
				new TextureRegion(throbberAtlas, 30+300, 30, 282, 282),
				new TextureRegion(throbberAtlas, 30, 30, 282, 282));
		disintegration = new Texture(app, "models/Disintegration.png");
		disintegrating = new Animation(.05,
				new TextureRegion(disintegration, 22, 22, 282, 282),
				new TextureRegion(disintegration, 22+300, 22, 282, 282),
				new TextureRegion(disintegration, 22+300*2, 22, 282, 282),
				new TextureRegion(disintegration, 22, 22+300, 282, 282),
				new TextureRegion(disintegration, 22+300, 22+300, 282, 282),
				new TextureRegion(disintegration, 22+300*2, 22+300, 282, 282),
				new TextureRegion(disintegration, 22, 22+300*2, 282, 282),
				new TextureRegion(disintegration, 22+300, 22+300*2, 282, 282),
				new TextureRegion(disintegration, 22+300*2, 22+300*2, 282, 282),
				new TextureRegion(disintegration, 0, 0, 2, 2));
		help1 = new Texture(app, "gui/Help1.png");
		help1Region = new TextureRegion(help1, 0, 0, 480, 720);
		help2 = new Texture(app, "gui/Help2.png");
		help2Region = new TextureRegion(help2, 0, 0, 480, 720);
		help3 = new Texture(app, "gui/Help3.png");
		help3Region = new TextureRegion(help3, 0, 0, 480, 720);
		help4 = new Texture(app, "gui/Help4.png");
		help4Region = new TextureRegion(help4, 0, 0, 480, 720);
		more1 = new Texture(app, "gui/More1.png");
		more1Region = new TextureRegion(more1, 0, 0, 480, 720);
		more2 = new Texture(app, "gui/More2.png");
		more2Region = new TextureRegion(more2, 0, 0, 480, 720);
		Audio audio = app.getAudio();
		music = audio.newMusic("sound/VysehradKemet.mp3");
		music.setLooping(true).setVolume(.25);
		if (Settings.soundEnabled) music.play();
		spark = audio.newSound("sound/Zap.ogg");
		shot = audio.newSound("sound/Shot.ogg");
		stick = audio.newSound("sound/Stick.ogg");
		cut = audio.newSound("sound/Cut.ogg");
		click = audio.newSound("sound/Click.ogg");
		woosh = audio.newSound("sound/Woosh.ogg");
		clang = audio.newSound("sound/CanClang.ogg");
		loudClang = audio.newSound("sound/LoudCanClang.ogg");
		airPowerTool = audio.newSound("sound/AirPowerTool.ogg");
		tintBatcher = new TintBatcher(app.getGLGraphics(), 50000);
	}
	public static void reload() {
		mainMenuBackground.reload(); menuAtlas.reload(); lungLevel.reload();
		modelAtlas.reload(); throbberAtlas.reload(); disintegration.reload();
		background.reload();
		help1.reload(); more1.reload(); help2.reload(); help3.reload();
		help4.reload(); more2.reload();
		if (Settings.soundEnabled) music.play();
	}
	public static void playSound(Sound sound) {if (Settings.soundEnabled) sound.play(1);}
	public static void setScreenOrientation(int orientation) {
		if (orientation==LANDSCAPE)
			main.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else if (orientation==PORTRAIT)
			main.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
