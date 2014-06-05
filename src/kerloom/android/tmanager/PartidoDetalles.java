package kerloom.android.tmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class PartidoDetalles implements Parcelable{

	String Equipo1;
	String Equipo2;
	String puntos1;
	String puntos2;
	String partidoId;
	int idEquipo1;
	int idEquipo2;
	boolean empate;
	boolean jugado;
	
	public void setPuntos1(int puntos){
		puntos1 = String.valueOf(puntos);
	}
	
	public void setPuntos1(String puntos){
		puntos1 = puntos;
	}
	
	public String getPuntos1(){
		return puntos1;
	}
	
	public void setPuntos2(int puntos){
		puntos2 = String.valueOf(puntos);
	}
	
	public void setPuntos2(String puntos){
		puntos2 = puntos;
	}
	
	public String getPuntos2(){
		return puntos2;
	}
	
	public void setEquipo1(int id, String strEquipo){
		Equipo1 = strEquipo;
		idEquipo1 = id;
	}
	
	public String getEquipo1(){
		return Equipo1;
	}
	
	public int getIdEquipo1(){
		return idEquipo1;
	}
	
	public void setEquipo2(int id, String strEquipo){
		Equipo2 = strEquipo;
		idEquipo2 = id;
	}
	
	public String getEquipo2(){
		return Equipo2;
	}
	
	public int getIdEquipo2(){
		return idEquipo2;
	}
	
	public void setPartidoId(String id){
		partidoId = id;
	}
	
	public String getIdPartido(){
		return partidoId;
	}
	
	public boolean isEmpate(){
		return empate;
	}
	
	public boolean hayEmpate(){
		boolean hayEmpate;
		if (Integer.valueOf(puntos1) == Integer.valueOf(puntos2)){
			hayEmpate = true;
		} else hayEmpate = false;
		return hayEmpate;
	}
	
	public boolean faltaPartido(){
		if(puntos1.equals("-") || puntos2.equals("-")) return true;
		else return false;
	}
	
	public int getIdPerdedor(){ //Regresa -1 si el partido no está hecho
		if(puntos1.equals("-") || puntos2.equals("-")){
			return -1;
		} else {
			return Integer.valueOf(puntos1) < Integer.valueOf(puntos2) ? idEquipo1 : idEquipo2;
		}
	}
	
	public String getPerdedor(){ //Regresa error si el partido no está hecho
		if(puntos1.equals("-") || puntos2.equals("-")){
			return "error";
		} else {
		return Integer.valueOf(puntos1) < Integer.valueOf(puntos2) ? Equipo1 : Equipo2;
		}
	}
	
	public int getIdGanador(){ //Regresa -1 si el partido no está hecho
		if(puntos1.equals("-") || puntos2.equals("-")){
			return -1;
		} else {
		return Integer.valueOf(puntos1) > Integer.valueOf(puntos2) ? idEquipo1 : idEquipo2;
		}
	}
	
	public String getGanador(){ //Regresa error si el partido no está hecho
		if(puntos1.equals("-") || puntos2.equals("-")){
			return "error";
		} else {
		return Integer.valueOf(puntos1) > Integer.valueOf(puntos2) ? Equipo1 : Equipo2;
		}
	}
	
	public boolean isJugado(){
		return jugado;
	}
	
	public void setJugado(boolean yesno){
		jugado = yesno;
	}
	
	//Constructor para Parcel
	public PartidoDetalles(Parcel in){
		String[] strings = new String[5];
		int[] ints = new int[2];
		
		in.readStringArray(strings);
		in.readIntArray(ints);
		empate = in.readByte() == 1;
		jugado = in.readByte() == 1;
		
		Equipo1 = strings[0];
		Equipo2 = strings[1];
		puntos1 = strings[2];
		puntos2 = strings[3];
		partidoId = strings[4];
		idEquipo1 = ints[0];
		idEquipo2 = ints[1];
		
	}
	
	public PartidoDetalles() {
		// TODO Auto-generated constructor stub
	}

	public PartidoDetalles(PartidoDetalles par){
		this.Equipo1 = par.getEquipo1();
		this.Equipo2 = par.getEquipo2();
		this.puntos1 = par.getPuntos1();
		this.puntos2 = par.getPuntos2();
		this.partidoId = par.getIdPartido();
		this.idEquipo1 = par.getIdEquipo1();
		this.idEquipo2 = par.getIdEquipo2();
		this.empate = par.isEmpate();
		this.jugado = par.isJugado();
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		String[] strings = {Equipo1, Equipo2, puntos1, puntos2, partidoId};
		int[] ints = {idEquipo1, idEquipo2};
		
		dest.writeStringArray(strings);
		dest.writeIntArray(ints);
		dest.writeByte((byte)(empate ? 1 : 0));
		dest.writeByte((byte)(jugado ? 1 : 0));
	}
	
	public static final Parcelable.Creator<PartidoDetalles> CREATOR = new Parcelable.Creator<PartidoDetalles>() {

		public PartidoDetalles createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new PartidoDetalles(source);
		}

		public PartidoDetalles[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PartidoDetalles[size];
		}
	
	
	};
	
}
