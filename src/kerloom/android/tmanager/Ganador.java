package kerloom.android.tmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Ganador extends Activity {

	ArrayList<Equipo> equipos;
	int idGanador;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner);
		
		Equipo equipoGanador = new Equipo("equipo");
		Bundle passedon =  this.getIntent().getExtras();				//Tomar valores puestos en Setup
		if (passedon!= null){
			idGanador = passedon.getInt("id");
		}
		
		equipos = getIntent().getParcelableArrayListExtra("kerloom.android.tmanager.equipos");
		equipoGanador = equipos.get(idGanador);
		
		TextView tvEquipoGanador = (TextView) findViewById(R.id.tvEquipoGanador);
		TextView tvJuegosJugados = (TextView) findViewById(R.id.tvJuegosJugados);
		TextView tvJuegosGanados = (TextView) findViewById(R.id.tvJuegosGanados);
		TextView tvJuegosPerdidos = (TextView) findViewById(R.id.tvJuegosPerdidos);
		TextView tvPuntosFavor = (TextView) findViewById(R.id.tvPuntosFavor);
		TextView tvPuntosContra = (TextView) findViewById(R.id.tvPuntosContra);
		
		tvEquipoGanador.setText(equipoGanador.getNombre());
		tvJuegosJugados.setText(getString(R.string.sJuegosJugados) + String.valueOf(equipoGanador.getJuegosJugados()));
		tvJuegosGanados.setText(getString(R.string.sJuegosGanados) + String.valueOf(equipoGanador.getJuegosGanados()));
		tvJuegosPerdidos.setText(getString(R.string.sJuegosPerdidos) + String.valueOf(equipoGanador.getJuegosPerdidos()));
		tvPuntosFavor.setText(getString(R.string.sPuntosFavor) + String.valueOf(equipoGanador.getPuntosFavor()));
		tvPuntosContra.setText(getString(R.string.sPuntosContra) + String.valueOf(equipoGanador.getPuntosContra()));
		
		
	}

}
