package kerloom.android.tmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class Equipo implements Parcelable{

	String nombre;
	boolean eliminado = false;
	int jganados = 0;
	int jperdidos = 0;
	int jjugados = 0;
	int puntosfavor = 0;
	int puntoscontra = 0;
	int jempatados = 0;
	int id;
	
	void juegoJugado(int puntosFavor, int puntosContra){
		if(puntosFavor > puntosContra){
			jganados++;
		} else if(puntosFavor < puntosContra) {
			jperdidos++;
		} else jempatados++;
		
		jjugados++;
		puntosfavor += puntosFavor;
		puntoscontra += puntosContra;
	}
	
	void juegoJugado(String pFavor, String pContra){
		int puntosFavor = Integer.valueOf(pFavor);
		int puntosContra = Integer.valueOf(pContra);
		
		if(puntosFavor > puntosContra){
			jganados++;
		} else if(puntosFavor < puntosContra) {
			jperdidos++;
		} else jempatados++;
		
		jjugados++;
		puntosfavor += puntosFavor;
		puntoscontra += puntosContra;
	}
	
	void desJugar(int puntosFavor, int puntosContra){
		if(puntosFavor > puntosContra){
			jganados--;
		} else if(puntosFavor < puntosContra) {
			jperdidos--;
		} else jempatados--;
		
		jjugados--;
		puntosfavor -= puntosFavor;
		puntoscontra -= puntosContra;
		
		eliminado = false;
	}
	
	void desJugar(String pFavor, String pContra){
		int puntosFavor = Integer.valueOf(pFavor);
		int puntosContra = Integer.valueOf(pContra);
		
		if(puntosFavor > puntosContra){
			jganados--;
		} else if(puntosFavor < puntosContra) {
			jperdidos--;
		} else jempatados--;
		
		jjugados--;
		puntosfavor -= puntosFavor;
		puntoscontra -= puntosContra;
		
		eliminado = false;
	}
	
	void juegoGanado(int puntosFavor, int puntosContra){
		jganados++;
		jjugados++;
		puntosfavor += puntosFavor;
		puntoscontra += puntosContra;
	}
	
	void juegoPerdido(int puntosFavor, int puntosContra){
		jperdidos++;
		jjugados++;
		puntoscontra += puntosContra;
		puntosfavor += puntosFavor;
	}
	
	int getPuntosFavor(){
		return puntosfavor;
	}
	
	int getPuntosContra(){
		return puntoscontra;
	}
	
	int getJuegosJugados(){
		return jjugados;
	}
	
	int getJuegosGanados(){
		return jganados;
	}
	
	int getJuegosPerdidos(){
		return jperdidos;
	}
	
	String getNombre(){
		return nombre;
	}
	
	void setNombre(String nuevoNombre){
		nombre = nuevoNombre;
	}
	
	void setId(int newId){
		id = newId;
	}
	
	int getId(){
		return id;
	}
	
	void setEliminado(boolean b){
		eliminado = b;
	}
	
	boolean isEliminado(){
		return eliminado;
	}
	
	//constructor
	Equipo(String nuevoNombre){

		nombre = nuevoNombre;
	}

	//Segundo Constructor con parcel
	public Equipo(Parcel in){
		
		int[] ints = new int[7];
		
		this.nombre = in.readString();
		this.eliminado = in.readByte() == 1;
		in.readIntArray(ints);
		this.jganados = ints[0];
		this.jperdidos = ints[1];
		this.jjugados = ints[2];
		this.puntosfavor = ints[3];
		this.puntoscontra = ints[4];
		this.jempatados = ints[5];
		this.id = ints[6];
		
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		int[] ints = new int[7];
		
		ints[0] = this.jganados;
		ints[1] = this.jperdidos;
		ints[2] = this.jjugados;
		ints[3] = this.puntosfavor;
		ints[4] = this.puntoscontra;
		ints[5] = this.jempatados;
		ints[6] = this.id; 
		
		dest.writeString(nombre);
		dest.writeByte((byte) (this.eliminado ? 1:0));
		dest.writeIntArray(ints);
	}
	
	public static final Parcelable.Creator<Equipo> CREATOR = new Parcelable.Creator<Equipo>() {

		public Equipo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Equipo(source);
		}

		public Equipo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Equipo[size];
		}
	
	
	};

	
}
