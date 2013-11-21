package es.eucm.eadmockup.prototypes.camera.common;

public class InfoTh {

	int num;
	boolean picture;
	
	public InfoTh(int n, boolean p){
		num=n;
		picture=p;
	}
	
	public boolean isPicture(){
		return picture;
	}
	
	public int getNum(){
		return num;
	}
}
