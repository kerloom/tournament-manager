package kerloom.android.tmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;


public class SetupEquipos extends Activity implements OnClickListener{

	int numEquipos;
	int tournamentType;
	boolean randomize;
	String[] nombresEquipos;
	ArrayAdapter<String> adapter;
	int[] equiposId; //Arreglo con id de equipos vivos en el torneo. Se inicializan todos.
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		Spinner spEtapas = (Spinner) findViewById(R.id.spEtapas);
		spEtapas.setVisibility(View.INVISIBLE);
		spEtapas.setEnabled(false);
		
		Bundle passedon =  getIntent().getExtras();				//Tomar valores puestos en Setup
		if (passedon!= null){
			tournamentType = passedon.getInt("tournamentType");
			numEquipos = passedon.getInt("NumEquipos");
			randomize = passedon.getBoolean("randomize");
		} 
		
		//Crear Todos los Equipos e Inicializarlos
		
		nombresEquipos = new String[numEquipos];
		equiposId = new int[numEquipos];
		for(int i=0; i < numEquipos; i++){
			String nombre = getString(R.string.sEquipo) + " " + String.valueOf(i + 1);
			nombresEquipos[i] = nombre; //Crear Equipo con Numero único sacando nombre del XML
			equiposId[i] = i;
		}
	
		//Llenar el ListView con los equipos
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombresEquipos); //Crear adaptador para llenar ListView
		ListView lvListaEquipos = (ListView) findViewById(R.id.lvListaPrincipal);
		lvListaEquipos.setAdapter(adapter);
		lvListaEquipos.setOnItemClickListener(mMessageClickedHandler);
		
		initButtons();
		
	} //Fin de OnCreate
	
	private void initButtons() {
		// TODO Auto-generated method stub
		/* Método para inicializar los botones de navegacion (Flechas) para
		 * Escuchar los clicks, y también cambia la imagen de la flecha cuando
		 * esta presionada. 
		 */
		
		ImageView ivAtras = (ImageView) findViewById(R.id.ivAtras);
		ImageView ivSiguiente = (ImageView) findViewById(R.id.ivSiguiente);
		
		ivAtras.setOnClickListener(this);
		ivSiguiente.setOnClickListener(this);
		
		ivAtras.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
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
	

	//Metodo para manejar clicks en la ListView
	private OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        cambiarNombre(position);

	    }
	};
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.ivSiguiente:
				iniciarTorneo();
			break;
			
			case R.id.ivAtras:
				finish();
			break;
		}
	
	}
	
	private void iniciarTorneo() {
		// TODO Auto-generated method stub
		/* Pasar el número de equipos y los nombres de equipo a la siguiente
		 * actividad e inicializarla.
		 */
		
		Bundle b = new Bundle();
		Intent intent = new Intent(this, Torneo.class);
		
		ArrayList<Equipo> tmp = new ArrayList<Equipo>(numEquipos);
		
		for(int i=0; i< numEquipos; i++){
			Equipo equipo = new Equipo(nombresEquipos[i]);
			equipo.setId(i);
			tmp.add(equipo);
		}
		
		b.putInt("tournamentType", tournamentType);
		b.putBoolean("randomize", randomize);
		intent.putParcelableArrayListExtra("kerloom.android.tmanager.equipos", tmp);
		intent.putParcelableArrayListExtra("kerloom.android.tmanager.historial", new ArrayList<PartidoDetalles>());
		intent.putExtras(b); 
		startActivity(intent);
		
	}

	void cambiarNombre (final int equipo){

		AlertDialog.Builder alert = new AlertDialog.Builder(this); //Crear PopUp

		alert.setTitle(getString(R.string.sCambiarNombre));
		alert.setMessage(getString(R.string.sNuevoNombre));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(nombresEquipos[equipo]);
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.sAceptar), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			nombresEquipos[equipo] = input.getText().toString();
			adapter.notifyDataSetChanged();
		  }
		});

		alert.setNegativeButton(getString(R.string.sCancelar), new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    dialog.cancel();
		  }
		});

		alert.show();
		
		
	}

}

