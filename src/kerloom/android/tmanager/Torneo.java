package kerloom.android.tmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Torneo extends Activity implements OnClickListener{

	int tournamentType; //Tipo de Torneo (ej. Direct Elimination)
	int numEquipos; //N�mero de equipos que siguen vivos
	int equipo; //Variable global para pasar datos al cambio de score
	int[] equiposId; //Arreglo con los id de equipo para los partidos
	int[] nuevosEquipos;
	boolean randomize; //Bool para decir si el usuario escogi� hacer al azar los partidos
	ArrayList<PartidoDetalles> partidos; //los partidos para esta "jornada"
	ArrayList<PartidoDetalles> historialPartidos; //Variable que almacena todos los partidos que se van jugando
	PartidoDetalles partido;
	ListView lvListaPartidos;
	CustomAdapter adapter;
	ArrayList<Equipo> equipos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		Log.v("Torneo", "Empezando clase torneo");
		
		lvListaPartidos = (ListView) findViewById(R.id.lvListaPrincipal);
		
		partidos = new ArrayList();
		randomize = false;
		
		initButtons(); //Iniciar Listeners en Botones
		
		equipos = getIntent().getParcelableArrayListExtra("kerloom.android.tmanager.equipos");
		historialPartidos = getIntent().getParcelableArrayListExtra("kerloom.android.tmanager.historial");
		
		Bundle passedon =  this.getIntent().getExtras();				//Tomar valores puestos en Setup
		if (passedon!= null){
			tournamentType = passedon.getInt("tournamentType");
			randomize = passedon.getBoolean("randomize");
		}
		
		numEquipos = getEquiposVivos();
		
		equiposId = new int[numEquipos];
		
		if(numEquipos == equipos.size()){ //Inicializar si es la primera ronda
			
			for(int i = 0; i < numEquipos; i++){
				equiposId[i] = i;
			}
			
			if(randomize){ //Revolver con el Algoritmo Fisher-Yates si se escogi� aleatorio
				Random r = new Random();
				for (int i = numEquipos -1; i > 0; i--){
					int shuff = r.nextInt(i);
					int tmp = equiposId[shuff];
					equiposId[shuff] = equiposId[i];
					equiposId[i] = tmp;
					
				} 
			} 
		} else { //Else de if numEquipos igual al ArrayList
			
			int n = 0; 
			for(int i = 0; i < equipos.size(); i++){
				Equipo equipo = equipos.get(i);
				if (!equipo.isEliminado()){
					equiposId[n] = equipo.getId();
				//	Toast.makeText(getApplicationContext(), String.valueOf(equiposId[n]), Toast.LENGTH_SHORT).show();
					n++;
				}
			}
		}
		
		boolean nuevosPartidos = false;
		int posicion = 0;
		try{
			PartidoDetalles partido;
			String id = "E" + String.valueOf(numEquipos) + "P0";
			partido = historialPartidos.get(posicion);
			while(!partido.getIdPartido().equals(id)) {
				partido = historialPartidos.get(posicion);
				posicion++;
			}
			if(posicion > 0) posicion--;
			
		} catch(IndexOutOfBoundsException e) { //Si da la excepcion quiere decir que no hay historial o no se encontro la id
			
			nuevosPartidos = true;
			
		}
		
		if(savedInstanceState == null){ //Si no se guardaron los partidos inicializarlos
			if(nuevosPartidos){ //Revisar si se necesitan generar nuevos partidos o copiar de historialEquipo
			//Juntar partidos primero con segundo de arreglo en equiposId y guardar en objeto partido Equipo1 y 2
				
				try{
				for(int i = 0; i < numEquipos; i +=2){ 
					int id = equiposId[i];
					int id2 = equiposId[i+1]; 
					Equipo equipo = new Equipo("nuevo");
					partido = new PartidoDetalles();
					equipo = equipos.get(id);
					partido.setEquipo1(id, equipo.getNombre());
					equipo = equipos.get(id2);
					partido.setEquipo2(id2, equipo.getNombre());
					partido.setPuntos1("-");
					partido.setPuntos2("-");
					partido.setPartidoId("E" + String.valueOf(numEquipos) + "P" + String.valueOf(i/2));
					partidos.add(partido); //Agregar objeto partido a la lista de arreglos.
				}
				} catch(IndexOutOfBoundsException e){
			//	Toast.makeText(getApplicationContext(), "numEquipos " + String.valueOf(numEquipos), Toast.LENGTH_SHORT).show();
				for(int i = 0; i < numEquipos; i++) {
			//	Toast.makeText(getApplicationContext(), String.valueOf(equiposId[i]), Toast.LENGTH_SHORT).show();
				}
				}
			} else {
				//Se obtienen los partidos de historialEquipos.
				//Se necesitan clonar con el m�todo hecho abajo, de otro modo solo apunta al mismo lugar.
				partidos.addAll(clonar(historialPartidos.subList(posicion, posicion+ numEquipos/2)));
	
			}
		} else {
			partidos = savedInstanceState.getParcelableArrayList("partidosGuardados");
		}
		
		initSpinner(); //Iniciar la lista de Etapas de partidos (Jornadas/Semis)
		
		//Crear el adaptador para la ListView
		adapter = new CustomAdapter(partidos, this);
		lvListaPartidos.setAdapter(adapter);
		lvListaPartidos.setOnItemClickListener(mMessageClickedHandler);
		

		
	} // Fin de OnCreate
	
	private void initSpinner() {
		// TODO Auto-generated method stub
		/*
		 * M�todo para inicializar el Spinner con la lista de Etapas disponibles
		 * Al hacer click debe mandarte a la etapa adecuada
		 */
		Spinner spEtapas = (Spinner) findViewById(R.id.spEtapas);
	    ArrayList<String> listaEtapas = (ArrayList<String>) getListaSpinner();
	    
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaEtapas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEtapas.setAdapter(adapter);
        spEtapas.setSelection(getEtapaActual());
        
        spEtapas.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				// TODO Auto-generated method stub
				
				moverEtapas(deltaEtapas(position));
			}

			private void moverEtapas(int delta) {
				// TODO Auto-generated method stub
				/*
				 * Método para moverse tantas etapas hacia atrás o hacia adelante.
				 */
				if(delta < 0){
					
					actividadAtras(-1*delta);
					
				} else if (delta > 0){
					Log.v("gotoEtapa", "Delta mayor a cero");
					//Revisar si ha habido cambios en el score, si no, pasar directo.
					//Si si hay cambios, mostrar un letrero de error y la etapa a la
					//que debes ir.
					if(isEtapaLista()){
						for(int i = 0; i < delta; i++){
							
							jugarPartidos();
							if(guardarPartidos()){
								Toast.makeText(getApplicationContext(), getString(R.string.sEmpateOFaltaPartido), Toast.LENGTH_SHORT).show();
								break;
							}
							
							//Actualizar numero de equipos despues de jugar una vez
							numEquipos = getEquiposVivos();
							int posicion = 0;
							PartidoDetalles partido;
							String id = "E" + String.valueOf(numEquipos) + "P0";
							partido = historialPartidos.get(posicion);
							while(!partido.getIdPartido().equals(id)) {
								partido = historialPartidos.get(posicion);
								posicion++;
							}
							if(posicion > 0) posicion--;
							
							partidos.clear();
							partidos.addAll(clonar(historialPartidos.subList(posicion, posicion+ numEquipos/2)));
							
						} //fin del for
						
						siguienteEtapa();
					} else {
						Spinner spEtapas = (Spinner) findViewById(R.id.spEtapas);
						Toast.makeText(getApplicationContext(), getString(R.string.sEmpateOFaltaPartido), Toast.LENGTH_SHORT).show();
						spEtapas.setSelection(getEtapaActual());
					}
					
				} else {
					//No hacer nada si es 0.
					//Log.v("gotoEtapa", "Delta es igual a cero");
				}
			}

			private int deltaEtapas(int position) {

				int delta;
				
				delta = position - getEtapaActual();
				return delta;
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				// TODO Auto-generated method stub
				
			}
        	
        });
		
	} //Fin de initSpinner

	private int getEtapaActual() {
		/* Método para determinar el número de la etapa en la que está el torneo con el
		 * número de selección del Spinner. Cambia dependiendo cuantos Juegos haya
		 * abiertos. (0, 1, ... ) El 0 siendo la primera etapa jugada.
		 */
		int totalEquipos = equipos.size();
		int vivosEquipos = getEquiposVivos();
		int contador = 0;
		int aux = totalEquipos;
		
		while(aux != vivosEquipos){
			aux /= 2;
			contador++;
		}
		
		return contador;
	}

	private List<String> getListaSpinner() {
		/* M�todo para obtener las etapas disponibles y regresar una lista
		 */
		
		List<String> listaEtapas = new ArrayList<String>();
		
		if(historialPartidos.size() != 0){
			for(int i = 0; i < historialPartidos.size(); i++){
				PartidoDetalles par = historialPartidos.get(i);
				String etapa = "";
				
				etapa = leerEtapa(par);
				if(!listaEtapas.contains(etapa)){
					listaEtapas.add(etapa);
				}
			}
		}
		
		PartidoDetalles par = partidos.get(0);
		String etapa = leerEtapa(par);
		if(!listaEtapas.contains(etapa)) listaEtapas.add(etapa);
		
		return listaEtapas;
	} //Fin de getListaSpinner

	private String leerEtapa(PartidoDetalles par) {

		String etapa = par.getIdPartido();
		int equipos;
		
		etapa = etapa.substring(1, etapa.indexOf("P"));
		equipos = Integer.valueOf(etapa);
		
		if(tournamentType == R.id.rbDirectElimination){
			switch(equipos){
				case 2:
					return getString(R.string.sFinal);
				case 4:
					return getString(R.string.sSemiFinal);
				case 8:
					return getString(R.string.sCuartosFinal);
				case 16:
					return getString(R.string.sOctavosFinal);
				case 32:
					return getString(R.string.sDieciseisFinal);
				case 64:
					return getString(R.string.sTreintaydosFinal);
					
			}
		}
		
		return etapa;
	}

	private void initButtons() { 
		/* M�todo para inicializar los botones de navegacion (Flechas) para
		 * Escuchar los clicks, y tambi�n cambia la imagen de la flecha cuando
		 * esta presionada. 
		 */
		
		ImageView ivAtras = (ImageView) findViewById(R.id.ivAtras);
		ImageView ivSiguiente = (ImageView) findViewById(R.id.ivSiguiente);
		
		ivAtras.setOnClickListener(this);
		ivSiguiente.setOnClickListener(this);
		
		ivAtras.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
				
				int accion = event.getAction(); //Obtener la accion del toque
				ImageView iv = (ImageView) v;
				
				//Si el toque es presionar, cambiar la imagen. Si se sale de la imagen, cambiar otra vez
				if(accion == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.flecha_atras_bis);
				} else if(accion == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.flecha_atras);
				}
				
				return false;
			}

		});
		
		ivSiguiente.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
				
				int accion = event.getAction(); //Obtener la accion del toque
				ImageView iv = (ImageView) v;
				
				//Si el toque es presionar, cambiar la imagen. Si se sale de la imagen, cambiar otra vez
				if(accion == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.flecha_siguiente_bis);
				} else if(accion == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.flecha_siguiente);
				}
				
				return false;
			}

		});
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) { //Guardar partidos actuales en caso de que se voltee la pantalla / se salgan del programa
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("partidosGuardados", partidos);
	}

	@Override
	public void onBackPressed() {
	    actividadAtras(1);
	}

	private int getEquiposVivos() {

		int n = 0; 
		for(int i = 0; i < equipos.size(); i++){
			Equipo equipo = equipos.get(i);
			if (!equipo.isEliminado()){
				n++;
			}
		}
		
		return n;
	}

	//Metodo para manejar clicks en la ListView
	public OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	   
		public void onItemClick(AdapterView parent, View v, int position, long id) {
	    	
	    	partido = (PartidoDetalles) partidos.get(position);
	    	//Limpiar e inicializar puntaje
	    	
	    	if(partido.puntos1.equals("-") || partido.puntos2.equals("-")){
	    		partido.puntos1 = "0";
	    		partido.puntos2 = "0";
	    	} 
	        
	    	//Llamar metodo de popup para poner el puntaje
	    	cambiarScore(position);

	    }
	}; //Fin de OnItemClickListener
	
		
	//Metodo para poner el puntaje en un popup
	private void cambiarScore(final int position){
		
		//Inicializar el inflater para tener el layout de puntos_partido
	    LayoutInflater inflater = LayoutInflater.from(this);
	    View popup = inflater.inflate(R.layout.puntos_partido, null); //Vista del Popup
	    
	    //Declarar componentes del layout
	    TextView tvNombreE1 = (TextView) popup.findViewById(R.id.tvNombreE1);
        TextView tvNombreE2 = (TextView) popup.findViewById(R.id.tvNombreE2);
        final TextView tvPuntosE1 = (TextView) popup.findViewById(R.id.tvPuntosE1);
        final TextView tvPuntosE2 = (TextView) popup.findViewById(R.id.tvPuntosE2);
        ImageView ibMasE1 = (ImageView) popup.findViewById(R.id.ibMasE1);
        ImageView ibMasE2 = (ImageView) popup.findViewById(R.id.ibMasE2);
        ImageView ibMenosE1 = (ImageView) popup.findViewById(R.id.ibMenosE1);
        ImageView ibMenosE2 = (ImageView) popup.findViewById(R.id.ibMenosE2);
        
        //Inicializar textos
        tvNombreE1.setText(partido.Equipo1);
        tvNombreE2.setText(partido.Equipo2);
        tvPuntosE1.setText(partido.puntos1);
        tvPuntosE2.setText(partido.puntos2);
	    
        //Declarar los dos OnTouchListener para cambiar flechas al touch
	    OnTouchListener setFlechasRojas = new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				
				int accion = event.getAction(); //Obtener la accion del toque
				ImageView iv = (ImageView) v;
				
				//Si el toque es presionar, cambiar la imagen. Si se sale de la imagen, cambiar otra vez
				if(accion == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.abajo_rojo_bis);
				} else if(accion == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.abajo_rojo);
				}

				return false;
			}	
	    };
	    
	    OnTouchListener setFlechasVerdes = new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				
				int accion = event.getAction(); //Obtener la accion del toque
				ImageView iv = (ImageView) v;
				
				//Si el toque es presionar, cambiar la imagen. Si se sale de la imagen, cambiar otra vez
				if(accion == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.arriba_verde_bis);
				} else if(accion == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.arriba_verde);
				}

				return false;
			}	
	    };
        
        //ibMasE1 inicializar Click y Touch
	    ibMasE1.setOnTouchListener(setFlechasVerdes);
	    ibMasE1.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				partido.setPuntos1(Integer.valueOf(partido.puntos1) + 1);
				tvPuntosE1.setText(partido.puntos1);
			}
	    	
	    });
	    
	    //ibMasE2 inicializar Click y Touch
	    ibMasE2.setOnTouchListener(setFlechasVerdes);
	    ibMasE2.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				partido.setPuntos2(Integer.valueOf(partido.puntos2) + 1);
				tvPuntosE2.setText(partido.puntos2);
			}   	
	    }); 
	    
	    //ibMenosE1 inicializar Click y Touch
	    ibMenosE1.setOnTouchListener(setFlechasRojas);
	    ibMenosE1.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				partido.setPuntos1(Integer.valueOf(partido.puntos1) - 1);
				tvPuntosE1.setText(partido.puntos1);
			}
	    	
	    }); 
	    
	    //ibMenos2 Iniciar Touch y Click (Cambio de imagen al Touch)
		ibMenosE2.setOnTouchListener(setFlechasRojas);
	    ibMenosE2.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				partido.setPuntos2(Integer.valueOf(partido.puntos2) - 1);
				tvPuntosE2.setText(partido.puntos2);
			}
	    	
	    });
        
	    //Crear Dialogo de popup para cambiar el marcador
	    Builder dialog = new AlertDialog.Builder(this);
	    dialog.setTitle(getString(R.string.sPuntos));
	    dialog.setView(popup);
	    dialog.setPositiveButton(getString(R.string.sAceptar), new DialogInterface.OnClickListener() {
	        
	    	//Click del dialogo para cambiar marcador
			public void onClick(DialogInterface dialog, int whichButton) {

		        	partidos.set(position, partido);
		        	Toast.makeText(getApplicationContext(), getString(R.string.sMarcadorEstablecido) , Toast.LENGTH_SHORT).show();
					adapter.notifyDataSetChanged();
	        }
			
	    }); //Fin de OnClickListener dentro de setPositive Button

	    dialog.show();
	    
	} //Fin de cambiarScore()

	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.ivSiguiente:
				switch(tournamentType) {
					case R.id.rbDirectElimination:	
						eliminacionDirecta();	
					break;	
				}
			break;
			
			case R.id.ivAtras:
				actividadAtras(1);
			break;
		}
			
	}
	
	private void actividadAtras(int repetir) {
		
		
		if(equipos.size() != numEquipos){
			
			for(int i = 0; i < repetir; i++){
				desJugarUltimos();
				guardarPartidos();
			}
					
			Bundle b = new Bundle();
			Intent intent = new Intent(this, Torneo.class);
			
			b.putInt("tournamentType", tournamentType);
			b.putBoolean("randomize", randomize);
			intent.putParcelableArrayListExtra("kerloom.android.tmanager.equipos", equipos); 
			intent.putParcelableArrayListExtra("kerloom.android.tmanager.historial", historialPartidos);
			intent.putExtras(b); 
			startActivity(intent); 
		} else {

			Bundle b = new Bundle();
			Intent intent = new Intent(this, SetupEquipos.class);
			
			b.putInt("tournamentType", tournamentType);
			b.putInt("NumEquipos", equipos.size());
			b.putBoolean("randomize", randomize);
			intent.putParcelableArrayListExtra("kerloom.android.tmanager.equipos", equipos); 
			intent.putParcelableArrayListExtra("kerloom.android.tmanager.historial", historialPartidos);
			intent.putExtras(b); 
			startActivity(intent); 
		}
		
	}

	private void desJugarUltimos() {
		PartidoDetalles partidoviejo = null;
		Equipo equipo1, equipo2;
		int posicion = 0;
		
		//Primera parte para obtener la posicion donde se guardaron los ultimos partidos.
		
		PartidoDetalles ultimoPartido = null;
		int indicador = historialPartidos.size();
		String idPartido = "";
		
		try {
			do {
				indicador--;
				ultimoPartido = historialPartidos.get(indicador);
			} while (!ultimoPartido.isJugado());
			indicador++; //Corregir el indice en 0 de size contra numero real
		} catch (IndexOutOfBoundsException e){
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
		
		idPartido = ultimoPartido.getIdPartido();
		int numUltimosPartidos = (Integer.parseInt(idPartido.substring(1, idPartido.indexOf("P"))))/2; //el numero de equipos antes de "P" entre dos
		posicion = indicador - numUltimosPartidos;
				
		for(int i = posicion; i < posicion + numUltimosPartidos; i++){
			//Error aqui con indexout of bounds -5 con size 11. Revisar algoritmo.
			//De 8 equipos hacia atras si llegas a la final. No se sale, si no da error.
			//Probablemente por el finish() que viene de otro lado.
			partidoviejo = (PartidoDetalles) historialPartidos.get(i);
			equipo1 = equipos.get(partidoviejo.getIdEquipo1());
			equipo2 = equipos.get(partidoviejo.getIdEquipo2());
			
			if(!partidoviejo.getPuntos1().equals("-") || !partidoviejo.getPuntos1().equals("-")){
				//Reestablecer estad�sticas de juego jugado
				equipo1.desJugar(partidoviejo.getPuntos1(), partidoviejo.getPuntos2());
				equipo2.desJugar(partidoviejo.getPuntos2(), partidoviejo.getPuntos1());
				
				equipos.set(equipo1.getId(), equipo1);
				equipos.set(equipo2.getId(), equipo2);
				partidoviejo.setJugado(false);
				historialPartidos.set(i, partidoviejo); 

			}
		} //Fin del for
			
	} //Fin de desjugar ultimos

	private void eliminacionDirecta(){ //Checar antes del método.

	/*	boolean listo = true; //Bandera para verificar si hay empates y todos los partidos hechos
		
		for(int i = 0; i< partidos.size(); i++){//Verificar que no haya empates y esten puestos todos los partidos.
			partido = (PartidoDetalles) partidos.get(i);
			if(partido.faltaPartido()){
				listo = false;
			}  else if(partido.hayEmpate()){ //Revisar despues empate para que no haya error por puntos = "-"
				listo = false;
			} 
			
		} */
		
		if(isEtapaLista()){
		/*	for(int i = 0; i < partidos.size(); i++){
	
				partido = (PartidoDetalles) partidos.get(i);
				equipo1 = equipos.get(partido.getIdEquipo1());
				equipo2 = equipos.get(partido.getIdEquipo2());
				
				//Establecer estadísticas de juego jugado
				equipo1.juegoJugado(partido.getPuntos1(), partido.getPuntos2());
				equipo2.juegoJugado(partido.getPuntos2(), partido.getPuntos1());
				
				//Verificar al perdedor y eliminarlo
				int perdedor = partido.getIdPerdedor();
				if(perdedor == equipo1.getId()){
					equipo1.setEliminado(true);
					equipo2.setEliminado(false);
				} else {
					equipo2.setEliminado(true);
					equipo1.setEliminado(false);
				}
				
				equipos.set(equipo1.getId(), equipo1);
				equipos.set(equipo2.getId(), equipo2);
				partido.setJugado(true);
				partidos.set(i, partido);
				
			} //Fin del for */
			
			jugarPartidos();
			guardarPartidos();
			
			//Si es el ultimo equipo mandar a pantalla de ganador
			int numFinEquipos = getEquiposVivos();
			if(numFinEquipos == 1){
				
				int ganadorId = 0;
				Bundle b = new Bundle();
				Intent intent = new Intent(this, Ganador.class);
				
				for(int i = 0; i < equipos.size(); i++){
					Equipo equipo = equipos.get(i);
					if (!equipo.isEliminado()){
						ganadorId = equipo.getId();
					}
				}
				
				b.putInt("id", ganadorId);
				intent.putParcelableArrayListExtra("kerloom.android.tmanager.equipos", equipos); 
				intent.putExtras(b); 
				startActivity(intent); 
				
			} else { //Si no es el ultimo, reiniciar actividad tournament con los nuevos valores
			
				siguienteEtapa();

			} //FIn else if equipo==1
			
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.sEmpateOFaltaPartido), Toast.LENGTH_SHORT).show();
			
		} //Fin de if listo
		
	}//Fin de eliminacion directa
	
	private void siguienteEtapa() {
		
		Bundle b = new Bundle();
		Intent intent = new Intent(this, Torneo.class);
		
		b.putInt("tournamentType", tournamentType);
		b.putBoolean("randomize", randomize);
		intent.putParcelableArrayListExtra("kerloom.android.tmanager.equipos", equipos); 
		intent.putParcelableArrayListExtra("kerloom.android.tmanager.historial", historialPartidos);
		intent.putExtras(b); 
		startActivity(intent);
		
	}

	private void jugarPartidos() {
		// Establece estadísticas y elimina equipos
		PartidoDetalles partido;
		Equipo equipo1;
		Equipo equipo2;
		
		for(int i = 0; i < partidos.size(); i++){
			
			partido = (PartidoDetalles) partidos.get(i);
			equipo1 = equipos.get(partido.getIdEquipo1());
			equipo2 = equipos.get(partido.getIdEquipo2());
			
			//Establecer estadísticas de juego jugado
			equipo1.juegoJugado(partido.getPuntos1(), partido.getPuntos2());
			equipo2.juegoJugado(partido.getPuntos2(), partido.getPuntos1());
			
			//Verificar al perdedor y eliminarlo
			int perdedor = partido.getIdPerdedor();
			if(perdedor == equipo1.getId()){
				equipo1.setEliminado(true);
				equipo2.setEliminado(false);
			} else {
				equipo2.setEliminado(true);
				equipo1.setEliminado(false);
			}
			
			equipos.set(equipo1.getId(), equipo1);
			equipos.set(equipo2.getId(), equipo2);
			partido.setJugado(true);
			partidos.set(i, partido);
			
		} //Fin del for 
		
	} //fin jugarPartidos()

	private boolean isEtapaLista() {
		//Bandera para verificar si hay empates y todos los partidos hechos
		boolean listo = true;
		PartidoDetalles partido;
		
		for(int i = 0; i< partidos.size(); i++){//Verificar que no haya empates y esten puestos todos los partidos.
			partido = (PartidoDetalles) partidos.get(i);
			if(partido.faltaPartido()){
				listo = false;
			}  else if(partido.hayEmpate()){ //Revisar despues empate para que no haya error por puntos = "-"
				listo = false;
			} 
			
		}
		
		return listo;
	}

	private boolean guardarPartidos(){
		//Regresa true si hay partidos que cambian hacia adelante
		//Regresa false si los ganadores son iguales
		int posicion = 0;
		boolean isCambiado = false; //Si habilita es que algun partido cambió.
		PartidoDetalles partido;

		// Encontrar primero el numero de historialEquipos donde se va a trabajar.
		try {
			String id = "E" + String.valueOf(numEquipos) + "P0";
			partido = historialPartidos.get(posicion);
			while(!partido.getIdPartido().equals(id)) {
				partido = historialPartidos.get(posicion);
				posicion++;
			}
			if(posicion > 0) posicion--; //se decrementa cuando es mayor a cero porque cuando es 0 no entra al loop y no lo aumenta
			
			for(int i = 0; i < partidos.size() ; i++){ //pasar por todos los partidos
				PartidoDetalles partidoHistorial;
				partido = partidos.get(i); 
				partidoHistorial = historialPartidos.get(posicion + i); //Offsetear en posicion porque deben de ser iguales despues del offset de posicion
				int nuevoPerdedor = partido.getIdPerdedor();
				if(nuevoPerdedor ==-1){ //Excepcion porque -1 significa que el marcador esta en "-". El catch agrega los equipos
					Exception e = null;
					throw  e;
				}
				
				// Si es el mismo ganador, simplemente actualizar historialEquipos
				if(nuevoPerdedor == partidoHistorial.getIdPerdedor()) {
					historialPartidos.set(posicion + i, partido); 
				} else {
					//Buscar el equipo perdedor en todos los partidos de adelante de historialPartidos, 
					//cambiarlo por el nuevo ganador, y reestablecer los puntos a "-".
					//Se deben desjugar también todos los partidos guardados que se cambien.
					
					isCambiado = true; //Algún partido cambió de ganador.
					
					//Guardar el partido de la etapa actual.
					historialPartidos.set(posicion + i, partido);
					
					//Guardar los partidos en etapas despues de la actual (cambios de equipo)
					for(int j = posicion + i +1; j < historialPartidos.size(); j++){ //agregar posicion + i + 1 para saltarse el equipo actual
						partidoHistorial = historialPartidos.get(j);
						Equipo e1, e2;
						e1 = equipos.get(partidoHistorial.getIdEquipo1());
						e2 = equipos.get(partidoHistorial.getIdEquipo2());
						
							if(nuevoPerdedor == partidoHistorial.getIdEquipo1()){
								//Ajustar puntajes y estadisticas en equipos si es que ya se jugo
								if(partidoHistorial.isJugado()){
									e1.desJugar(partidoHistorial.getPuntos1(), partidoHistorial.getPuntos2());
									e2.desJugar(partidoHistorial.getPuntos2(), partidoHistorial.getPuntos1());
									equipos.set(e1.getId(), e1);
									equipos.set(e2.getId(), e2);
								}
								//Cambiar el partido
																
								partidoHistorial.setEquipo1(partido.getIdGanador(), partido.getGanador());
								partidoHistorial.setJugado(false);
								partidoHistorial.setPuntos1("-");
								partidoHistorial.setPuntos2("-");
								historialPartidos.set(j, partidoHistorial);
							} else if (nuevoPerdedor == partidoHistorial.getIdEquipo2()){
								//Ajustar puntajes y estadisticas en equipos
								if(partidoHistorial.isJugado()){
									e1.desJugar(partidoHistorial.getPuntos1(), partidoHistorial.getPuntos2());
									e2.desJugar(partidoHistorial.getPuntos2(), partidoHistorial.getPuntos1());
									equipos.set(e1.getId(), e1);
									equipos.set(e2.getId(), e2);
								}
								//Cambiar el partido
																
								partidoHistorial.setEquipo2(partido.getIdGanador(), partido.getGanador());
								partidoHistorial.setJugado(false);
								partidoHistorial.setPuntos1("-");
								partidoHistorial.setPuntos2("-");
								historialPartidos.set(j, partidoHistorial);
							}
						
					} //Fin historialPartidos.size

				}
			} //Fin for partidos.size

		} catch (IndexOutOfBoundsException e){ // Si no esta inicializado hisorialPartidos
			historialPartidos.addAll(partidos);
		//	Toast.makeText(getApplicationContext(), "AdAll: " + String.valueOf(historialPartidos.size()), Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			historialPartidos.addAll(partidos);
		}
		Log.v("guardarPartido", "isCambiado = " + String.valueOf(isCambiado));
		return isCambiado;
	}
	
	public static List<PartidoDetalles> clonar(List<PartidoDetalles> original){
		List<PartidoDetalles> clonado = new ArrayList<PartidoDetalles>(original.size());
		for(PartidoDetalles par : original) {
			clonado.add(new PartidoDetalles(par));
		}
		return clonado;
	}
	
}//Fin de Clase Torneo
