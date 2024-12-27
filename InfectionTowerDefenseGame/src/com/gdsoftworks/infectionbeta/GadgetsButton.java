package com.gdsoftworks.infectionbeta;

import java.util.List;

import gdsoftworks.geometry.Vector;

public class GadgetsButton extends Widget {
	double speed = 180;
	List<Card> cards;
	public GadgetsButton(List<Card> cards) {
		super(55+9, 320-17-6, 110, 34);
		this.cards = cards;
		state = EXPANDED;
		sound = Assets.woosh;
	}
	public void press(Vector point) {
		switch (state) {
		case EXPANDED:
			changeTime = 0;
			Assets.playSound(sound);
			state = MOVING;
			velocity.set(0, speed);
			for (Card card: cards) card.recall();
			break;
		}
	}
	public void recall() {
		if (state==COLLAPSED) {
			changeTime = 0;
			Assets.playSound(sound);
			state = RECALLED;
			for (Card card: cards) card.collapse();
		}
	}
	public void update(double deltaTime) {
		super.update(deltaTime);
		int len = cards.size();
		for (int i=0; i<len; i++) cards.get(i).update(deltaTime);
		switch (state) {
		case MOVING:
			if (velocity.y>0) {
				if (changeTime>.25) {
					changeTime = 0;
					velocity.set(0, 0);
					position.set(55+9, 320-17-6+speed*.25);
					bounds.lowerLeft.set(position.x-bounds.width/2, position.y-bounds.height/2);
					state = COLLAPSED;
				}
			}
			else {
				if (changeTime>.25) {
					changeTime = 0;
					velocity.set(0, 0);
					position.set(55+9, 320-17-6);
					bounds.lowerLeft.set(position.x-bounds.width/2, position.y-bounds.height/2);
					state = EXPANDED;
				}
			}
			break;
		case RECALLED:
			changeTime = 0;
			state = MOVING;
			velocity.set(0, -speed);
			break;
		}
	}
}
