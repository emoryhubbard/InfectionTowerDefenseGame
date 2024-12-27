package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.kinematics.Particle;

import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.opengl.Animation;
import com.gdsoftworks.androidapp.opengl.Camera2D;
import com.gdsoftworks.androidapp.opengl.GLGraphics;
import com.gdsoftworks.androidapp.opengl.SpriteBatcher;
import com.gdsoftworks.androidapp.opengl.TextureRegion;
import com.gdsoftworks.androidapp.opengl.TintBatcher;
import com.gdsoftworks.androidapp.opengl.Vertices;

public class LungLevelRenderer {
	static final int WIDTH = 15; static final int HEIGHT = 10;
	Camera2D camera;
	SpriteBatcher batcher;
	TintBatcher tintBatcher;
	GLGraphics glGraphics;
	LungLevel lungLevel;
	Vertices platformModel;
	Vertices antennaModel;
	Vertices lyzerModel;
	float[] platformVertices = new float[20]; {
		platformVertices[2] = 362/1024f; platformVertices[3] = 194/1024f;
		platformVertices[6] = 553/1024f; platformVertices[7] = 7/1024f;
		platformVertices[10] = 740/1024f; platformVertices[11] = 197/1024f;
		platformVertices[14] = 549/1024f; platformVertices[15] = 384/1024f;
	}
	float[] scaffoldBaseVertices = new float[20]; {
		scaffoldBaseVertices[2] = 166/1024f; scaffoldBaseVertices[3] = 125/1024f;
		scaffoldBaseVertices[6] = 166/1024f; scaffoldBaseVertices[7] = 94/1024f;
		scaffoldBaseVertices[10] = 196/1024f; scaffoldBaseVertices[11] = 94/1024f;
		scaffoldBaseVertices[14] = 196/1024f; scaffoldBaseVertices[15] = 125/1024f;
	}
	float[] scaffoldExtensionVertices = new float[20]; {
		scaffoldExtensionVertices[2] = 115/1024f; scaffoldExtensionVertices[3] = 113/1024f;
		scaffoldExtensionVertices[6] = 115/1024f; scaffoldExtensionVertices[7] = 106/1024f;
		scaffoldExtensionVertices[10] = 167/1024f; scaffoldExtensionVertices[11] = 106/1024f;
		scaffoldExtensionVertices[14] = 167/1024f; scaffoldExtensionVertices[15] = 113/1024f;
	}
	float[] scaffoldSliderVertices = new float[20]; {
		scaffoldSliderVertices[2] = 6/1024f; scaffoldSliderVertices[3] = 125/1024f;
		scaffoldSliderVertices[6] = 6/1024f; scaffoldSliderVertices[7] = 94/1024f;
		scaffoldSliderVertices[10] = 115/1024f; scaffoldSliderVertices[11] = 94/1024f;
		scaffoldSliderVertices[14] = 115/1024f; scaffoldSliderVertices[15] = 125/1024f;
	}
	float[] antennaVertices = new float[28]; {
		antennaVertices[2] = 10/1024f; antennaVertices[3] = 464/1024f;
		antennaVertices[6] = 34/1024f; antennaVertices[7] = 440/1024f;
		antennaVertices[10] = 39/1024f; antennaVertices[11] = 332/1024f;
		antennaVertices[14] = 45/1024f; antennaVertices[15] = 323/1024f;
		antennaVertices[18] = 66/1024f; antennaVertices[19] = 323/1024f;
		antennaVertices[22] = 62/1024f; antennaVertices[23] = 443/1024f;
		antennaVertices[26] = 84/1024f; antennaVertices[27] = 464/1024f;
	}
	float[] sphereVertices = new float[20]; {
		sphereVertices[2] = 43/1024f; sphereVertices[3] = 323/1024f;
		sphereVertices[6] = 43/1024f; sphereVertices[7] = 301/1024f;
		sphereVertices[10] = 65/1024f; sphereVertices[11] = 301/1024f;
		sphereVertices[14] = 65/1024f; sphereVertices[15] = 323/1024f;
	}
	float[] lyzerVertices = new float[24]; {
		lyzerVertices[2] = 238/1024f; lyzerVertices[3] = 148/1024f;
		lyzerVertices[6] = 217/1024f; lyzerVertices[7] = 114/1024f;
		lyzerVertices[10] = 236/1024f; lyzerVertices[11] = 24/1024f;
		lyzerVertices[14] = 284/1024f; lyzerVertices[15] = 24/1024f;
		lyzerVertices[18] = 308/1024f; lyzerVertices[19] = 112/1024f;
		lyzerVertices[22] = 288/1024f; lyzerVertices[23] = 148/1024f;
	}
	float[] firstSegmentVertices = new float[20]; {
		firstSegmentVertices[2] = 127/1024f; firstSegmentVertices[3] = 980/1024f;
		firstSegmentVertices[6] = 99/1024f; firstSegmentVertices[7] = 896/1024f;
		firstSegmentVertices[10] = 484/1024f; firstSegmentVertices[11] = 869/1024f;
		firstSegmentVertices[14] = 489/1024f; firstSegmentVertices[15] = 902/1024f;
	}
	float[] secondSegmentVertices = new float[20]; {
		secondSegmentVertices[2] = 499/1024f; secondSegmentVertices[3] = 900/1024f;
		secondSegmentVertices[6] = 501/1024f; secondSegmentVertices[7] = 850/1024f;
		secondSegmentVertices[10] = 830/1024f; secondSegmentVertices[11] = 892/1024f;
		secondSegmentVertices[14] = 829/1024f; secondSegmentVertices[15] = 925/1024f;
	}
	float[] bladeVertices = new float[20]; {
		bladeVertices[2] = 848/1024f; bladeVertices[3] = 992/1024f;
		bladeVertices[6] = 848/1024f; bladeVertices[7] = 844/1024f;
		bladeVertices[10] = 998/1024f; bladeVertices[11] = 844/1024f;
		bladeVertices[14] = 998/1024f; bladeVertices[15] = 992/1024f;
	}
	public LungLevelRenderer(GLGraphics graphics, SpriteBatcher batcher, LungLevel level) {
		glGraphics = graphics;
		camera = new Camera2D(graphics, WIDTH, HEIGHT);
		camera.zoom = 1;
		this.batcher = batcher;
		lungLevel = level;
		platformModel = new Vertices(glGraphics, 4, 0, false, true);
		antennaModel = new Vertices(glGraphics, 7, 0, false, true);
		lyzerModel = new Vertices(glGraphics, 6, 0, false, true);
	}
	public void render() {
		GL10 gl = glGraphics.getGL();
		camera.setViewportAndMatrices();
		renderBackground();
		batcher.beginBatch(Assets.modelAtlas);
		batcher.drawSprite(lungLevel.platform.x(), HEIGHT-lungLevel.platform.y(),
				2.25, 2.25, Assets.platform);
		batcher.endBatch();
		renderScaffolds();
		renderIonCatchers();
		renderLipolyzers();
		renderDrexlerSaws();
		if (lungLevel.lipolaseProjectiles.size()
				+lungLevel.ions.size()+lungLevel.ionCatchers.size()>0) {
			batcher.beginBatch(Assets.modelAtlas);
			renderIons();
			renderSparks();
			renderLipolase();
			batcher.endBatch();
		}
		renderThrobbers();
		gl.glDisable(GL10.GL_BLEND);
	}
	public void renderBackground() {
		GL10 gl = glGraphics.getGL();
		batcher.beginBatch(Assets.background);
		batcher.drawSprite(camera.position.x, camera.position.y, 15, 10,
				Assets.lungLevelBackground);
		batcher.endBatch();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batcher.beginBatch(Assets.lungLevel);
		batcher.drawSprite(camera.position.x, 1.3343, 15, 3.4, Assets.healthyLungTissue);
		batcher.endBatch();
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batcher.beginBatch(Assets.lungLevel);
		gl.glColor4f(1, 1, 1, .75f);
		batcher.drawSprite(camera.position.x, 1.3343, 15, 3.4, Assets.tissueFilm);
		batcher.endBatch();
		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
	/**private void renderPlatform() {
		Assets.modelAtlas.bind();
		Polygon gon = lungLevel.platform.base;
		double[] abscissae = gon.abscissae(); double[] ordinates = gon.ordinates();
		platformVertices[0] = (float)abscissae[0];
		platformVertices[1] = HEIGHT-(float)ordinates[0];
		platformVertices[4] = (float)abscissae[1];
		platformVertices[5] = HEIGHT-(float)ordinates[1];
		platformVertices[8] = (float)abscissae[2];
		platformVertices[9] = HEIGHT-(float)ordinates[2];
		platformVertices[12] = (float)abscissae[3];
		platformVertices[13] = HEIGHT-(float)ordinates[3];
		platformModel.setVertices(platformVertices, 0, 16);
		platformModel.bind();
		platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
		platformModel.unbind();
	}**/
	private void renderScaffolds() {
		GL10 gl = glGraphics.getGL();
		Assets.modelAtlas.bind();
		double[] abscissae; double[] ordinates;
		Polygon gon;
		int len = lungLevel.scaffolds.size();
		for (int i=0; i<len; i++) {
			Scaffold scaffold = lungLevel.scaffolds.get(i);
			if (lungLevel.template==scaffold && lungLevel.templateAttachable)
				gl.glColor4f(1,1,1,.5f);
			else if (lungLevel.template==scaffold)
				gl.glColor4f(1,0,0,.5f);
			gon = scaffold.base;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			scaffoldBaseVertices[0] = (float)abscissae[0];
			scaffoldBaseVertices[1] = HEIGHT-(float)ordinates[0];
			scaffoldBaseVertices[4] = (float)abscissae[1];
			scaffoldBaseVertices[5] = HEIGHT-(float)ordinates[1];
			scaffoldBaseVertices[8] = (float)abscissae[2];
			scaffoldBaseVertices[9] = HEIGHT-(float)ordinates[2];
			scaffoldBaseVertices[12] = (float)abscissae[3];
			scaffoldBaseVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(scaffoldBaseVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			
			gon = scaffold.extension;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			scaffoldExtensionVertices[0] = (float)abscissae[0];
			scaffoldExtensionVertices[1] = HEIGHT-(float)ordinates[0];
			scaffoldExtensionVertices[4] = (float)abscissae[1];
			scaffoldExtensionVertices[5] = HEIGHT-(float)ordinates[1];
			scaffoldExtensionVertices[8] = (float)abscissae[2];
			scaffoldExtensionVertices[9] = HEIGHT-(float)ordinates[2];
			scaffoldExtensionVertices[12] = (float)abscissae[3];
			scaffoldExtensionVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(scaffoldExtensionVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			
			gon = scaffold.slider;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			scaffoldSliderVertices[0] = (float)abscissae[0];
			scaffoldSliderVertices[1] = HEIGHT-(float)ordinates[0];
			scaffoldSliderVertices[4] = (float)abscissae[1];
			scaffoldSliderVertices[5] = HEIGHT-(float)ordinates[1];
			scaffoldSliderVertices[8] = (float)abscissae[2];
			scaffoldSliderVertices[9] = HEIGHT-(float)ordinates[2];
			scaffoldSliderVertices[12] = (float)abscissae[3];
			scaffoldSliderVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(scaffoldSliderVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			if (lungLevel.template==scaffold) gl.glColor4f(1,1,1,1);
		}
	}
	private void renderIonCatchers() {
		GL10 gl = glGraphics.getGL();
		Assets.modelAtlas.bind();
		double[] abscissae; double[] ordinates;
		Polygon gon;
		int len = lungLevel.ionCatchers.size();
		for (int i=0; i<len; i++) {
			IonCatcher ionCatcher = lungLevel.ionCatchers.get(i);
			if (lungLevel.template==ionCatcher && lungLevel.templateAttachable)
				gl.glColor4f(1,1,1,.5f);
			else if (lungLevel.template==ionCatcher)
				gl.glColor4f(1,0,0,.5f);
			gon = ionCatcher.antenna;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			antennaVertices[0] = (float)abscissae[0];
			antennaVertices[1] = HEIGHT-(float)ordinates[0];
			antennaVertices[4] = (float)abscissae[1];
			antennaVertices[5] = HEIGHT-(float)ordinates[1];
			antennaVertices[8] = (float)abscissae[2];
			antennaVertices[9] = HEIGHT-(float)ordinates[2];
			antennaVertices[12] = (float)abscissae[3];
			antennaVertices[13] = HEIGHT-(float)ordinates[3];
			antennaVertices[16] = (float)abscissae[4];
			antennaVertices[17] = HEIGHT-(float)ordinates[4];
			antennaVertices[20] = (float)abscissae[5];
			antennaVertices[21] = HEIGHT-(float)ordinates[5];
			antennaVertices[24] = (float)abscissae[6];
			antennaVertices[25] = HEIGHT-(float)ordinates[6];
			antennaModel.setVertices(antennaVertices, 0, 28);
			antennaModel.bind();
			antennaModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			antennaModel.unbind();
			
			gon = ionCatcher.sphere;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			sphereVertices[0] = (float)abscissae[0];
			sphereVertices[1] = HEIGHT-(float)ordinates[0];
			sphereVertices[4] = (float)abscissae[1];
			sphereVertices[5] = HEIGHT-(float)ordinates[1];
			sphereVertices[8] = (float)abscissae[2];
			sphereVertices[9] = HEIGHT-(float)ordinates[2];
			sphereVertices[12] = (float)abscissae[3];
			sphereVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(sphereVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			if (lungLevel.template==ionCatcher) gl.glColor4f(1,1,1,1);
		}
	}
	private void renderLipolyzers() {
		GL10 gl = glGraphics.getGL();
		Assets.modelAtlas.bind();
		double[] abscissae; double[] ordinates;
		Polygon gon;
		int len = lungLevel.lipolyzers.size();
		for (int i=0; i<len; i++) {
			Lipolyzer lipolyzer = lungLevel.lipolyzers.get(i);
			if (lungLevel.template==lipolyzer && lungLevel.templateAttachable)
				gl.glColor4f(1,1,1,.5f);
			else if (lungLevel.template==lipolyzer)
				gl.glColor4f(1,0,0,.5f);
			gon = lipolyzer.body;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			lyzerVertices[0] = (float)abscissae[0];
			lyzerVertices[1] = HEIGHT-(float)ordinates[0];
			lyzerVertices[4] = (float)abscissae[1];
			lyzerVertices[5] = HEIGHT-(float)ordinates[1];
			lyzerVertices[8] = (float)abscissae[2];
			lyzerVertices[9] = HEIGHT-(float)ordinates[2];
			lyzerVertices[12] = (float)abscissae[3];
			lyzerVertices[13] = HEIGHT-(float)ordinates[3];
			lyzerVertices[16] = (float)abscissae[4];
			lyzerVertices[17] = HEIGHT-(float)ordinates[4];
			lyzerVertices[20] = (float)abscissae[5];
			lyzerVertices[21] = HEIGHT-(float)ordinates[5];
			lyzerModel.setVertices(lyzerVertices, 0, 24);
			lyzerModel.bind();
			lyzerModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			lyzerModel.unbind();
			if (lungLevel.template==lipolyzer) gl.glColor4f(1,1,1,1);
		}
	}
	private void renderDrexlerSaws() {
		GL10 gl = glGraphics.getGL();
		double[] abscissae; double[] ordinates;
		Polygon gon;
		int len = lungLevel.drexlerSaws.size();
		for (int i=0; i<len; i++) {
			DrexlerSaw drexlerSaw = lungLevel.drexlerSaws.get(i);
			Assets.modelAtlas.bind();
			if (lungLevel.template==drexlerSaw && lungLevel.templateAttachable)
				gl.glColor4f(1,1,1,.5f);
			else if (lungLevel.template==drexlerSaw)
				gl.glColor4f(1,0,0,.5f);
			gon = drexlerSaw.firstSegment;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			firstSegmentVertices[0] = (float)abscissae[0];
			firstSegmentVertices[1] = HEIGHT-(float)ordinates[0];
			firstSegmentVertices[4] = (float)abscissae[1];
			firstSegmentVertices[5] = HEIGHT-(float)ordinates[1];
			firstSegmentVertices[8] = (float)abscissae[2];
			firstSegmentVertices[9] = HEIGHT-(float)ordinates[2];
			firstSegmentVertices[12] = (float)abscissae[3];
			firstSegmentVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(firstSegmentVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			
			gon = drexlerSaw.secondSegment;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			secondSegmentVertices[0] = (float)abscissae[0];
			secondSegmentVertices[1] = HEIGHT-(float)ordinates[0];
			secondSegmentVertices[4] = (float)abscissae[1];
			secondSegmentVertices[5] = HEIGHT-(float)ordinates[1];
			secondSegmentVertices[8] = (float)abscissae[2];
			secondSegmentVertices[9] = HEIGHT-(float)ordinates[2];
			secondSegmentVertices[12] = (float)abscissae[3];
			secondSegmentVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(secondSegmentVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			
			gon = drexlerSaw.blade;
			abscissae = gon.abscissae(); ordinates = gon.ordinates();
			bladeVertices[0] = (float)abscissae[0];
			bladeVertices[1] = HEIGHT-(float)ordinates[0];
			bladeVertices[4] = (float)abscissae[1];
			bladeVertices[5] = HEIGHT-(float)ordinates[1];
			bladeVertices[8] = (float)abscissae[2];
			bladeVertices[9] = HEIGHT-(float)ordinates[2];
			bladeVertices[12] = (float)abscissae[3];
			bladeVertices[13] = HEIGHT-(float)ordinates[3];
			platformModel.setVertices(bladeVertices, 0, 16);
			platformModel.bind();
			platformModel.draw(GL10.GL_TRIANGLE_FAN, 0, abscissae.length);
			platformModel.unbind();
			if (lungLevel.template==drexlerSaw) gl.glColor4f(1,1,1,1);
			Assets.throbberAtlas.bind();
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
			Assets.tintBatcher.beginBatch(Assets.throbberAtlas);
			boolean isEmpty = true;
			int len2 = drexlerSaw.vapor.particles.size();
			for (int j=0; j<len2; j++) {
				Particle particle = drexlerSaw.vapor.particles.get(j);
				isEmpty = false;
				float alpha = (float) (particle.alpha);
				Assets.tintBatcher.drawSprite(particle.position.x, particle.position.y,
						particle.bounds.width, particle.bounds.height, particle.orientation,
						Assets.flake1, alpha, alpha, alpha, alpha);
			}
			if (!isEmpty) Assets.tintBatcher.endBatch();
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	private void renderIons() {
		int len = lungLevel.ions.size();
		for (int i=0; i<len; i++) {
			Ion ion = lungLevel.ions.get(i);
			batcher.drawSprite(ion.position.x, ion.position.y, ion.bounds.width,
					ion.bounds.height, ion.orientation, Assets.slingIon);
		}
	}
	private void renderSparks() {
		int len = lungLevel.ionCatchers.size();
		for (int i=0; i<len; i++) {
			IonCatcher ionCatcher = lungLevel.ionCatchers.get(i);
			if (ionCatcher.sparking && ionCatcher.enabled) {
				TextureRegion keyFrame = Assets.sparks.getKeyFrame(ionCatcher.sparksTime,
						Animation.ANIMATION_LOOPING);
				batcher.drawSprite(ionCatcher.spherePoint.x(),
						HEIGHT-ionCatcher.spherePoint.y(), 1.5, .5,
						ionCatcher.sparkAngle, keyFrame);
			}
		}
		batcher.drawSprite(100, 100, 1.5, .5, Assets.sparks.getKeyFrame(0,
				Animation.ANIMATION_LOOPING));
	}
	private void renderLipolase() {
		int len = lungLevel.lipolaseProjectiles.size();
		for (int i=0; i<len; i++) {
			Lipolase lipolase = lungLevel.lipolaseProjectiles.get(i);
			batcher.drawSprite(lipolase.position.x, lipolase.position.y,
					lipolase.bounds.height, lipolase.bounds.width, lipolase.orientation,
					Assets.lipolase);
		}
	}
	private void renderThrobbers() {
		GL10 gl = glGraphics.getGL();
		if (lungLevel.throbbers.size()>0) {
			int len = lungLevel.throbbers.size();
			for (int i=0; i<len; i++) {
				Throbber throbber = lungLevel.throbbers.get(i);
				if (throbber.vapor!=null) {
					gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
					Assets.tintBatcher.beginBatch(Assets.throbberAtlas);
					boolean isEmpty = true;
					int len2 = throbber.vapor.particles.size();
					for (int j=0; j<len2; j++) {
						Particle particle = throbber.vapor.particles.get(j);
						isEmpty = false;
						float alpha = (float) (particle.alpha);
						Assets.tintBatcher.drawSprite(particle.position.x, particle.position.y,
								particle.bounds.width, particle.bounds.height,
								Assets.bigVapor, alpha, alpha, alpha, alpha);
					}
					if (!isEmpty) Assets.tintBatcher.endBatch();
					gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				}
				
				if (!throbber.disintegrating) {
					batcher.beginBatch(Assets.throbberAtlas);
					TextureRegion keyFrame = Assets.throbber;
					if (throbber.throbbing)
						keyFrame = Assets.throbbing.getKeyFrame(throbber.changeTime,
								Animation.ANIMATION_LOOPING);
					if (throbber.infecting)
						keyFrame = Assets.infectingThrobber;
					batcher.drawSprite(throbber.position.x, throbber.position.y,
							throbber.bounds.width, throbber.bounds.height,
							throbber.orientation, keyFrame);
					batcher.endBatch();
				}
				else {
					batcher.beginBatch(Assets.disintegration);
					TextureRegion keyFrame = Assets.disintegrating.getKeyFrame(
							throbber.changeTime, Animation.ANIMATION_NONLOOPING);
					batcher.drawSprite(throbber.position.x, throbber.position.y,
							throbber.bounds.width, throbber.bounds.height,
							throbber.orientation, keyFrame);
					batcher.endBatch();
				}
			}
		}
	}
	/**private void renderThrobberVapor() {
		Assets.tintBatcher.beginBatch(Assets.throbberAtlas);
		boolean isEmpty = true;
		for (ThrobberVapor vapor: lungLevel.throbberVapors) {
			for (Particle particle: vapor.particles) {
				isEmpty = false;
				float alpha = (float) (particle.alpha);
				Assets.tintBatcher.drawSprite(particle.position.x, particle.position.y,
						particle.bounds.width, particle.bounds.height,
						Assets.bigVapor, alpha, alpha, alpha, alpha);
			}
		}
		if (!isEmpty) Assets.tintBatcher.endBatch();
	}**/
}
