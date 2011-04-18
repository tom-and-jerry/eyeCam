package ch.hsr.eyecam;

import android.view.OrientationEventListener;

public enum Orientation {
	UNKNOW(OrientationEventListener.ORIENTATION_UNKNOWN){
		@Override
		public String toString(){
			return "unknow ("+getDegress()+")";
		}
	},
	PORTRAIT(0){
		@Override
		public String toString(){
			return "portrait ("+getDegress()+")";
		}
	},
	LANDSCAPE_RIGHT (90){
		@Override
		public String toString(){
			return "lanscape right ("+getDegress()+")";
		}
	},
	LANDSCAPE_LEFT(270){
		@Override
		public String toString(){
			return "lanscape left ("+getDegress()+")";
		}
	};
	
	
	private final int orientationInDegrees;
	Orientation(int orientation){
		this.orientationInDegrees = orientation;
	}
	
	public int getDegress(){
		return orientationInDegrees;
	}
	
}
