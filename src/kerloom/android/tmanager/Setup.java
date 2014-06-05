package kerloom.android.tmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;


public class Setup extends Activity implements OnClickListener{
	
	RadioGroup rgTournamentType;
	int NumEquipos;
	TextView tvNumEquipos;
	CheckBox chkRandom;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		Button bSelectType = (Button) findViewById(R.id.bSelectTournament);
		tvNumEquipos = (TextView) findViewById(R.id.tvNumEquipos);
		rgTournamentType = (RadioGroup) findViewById(R.id.rgType);
		chkRandom = (CheckBox) findViewById(R.id.chkRandom);
		
		initFlechas();
		bSelectType.setOnClickListener(this);
		
		NumEquipos = 4;
		
		tvNumEquipos.setText(String.valueOf(NumEquipos));
			
	}
	
	private void initFlechas() {
		
		ImageView ivMas = (ImageView) findViewById(R.id.ivMas);
		ImageView ivMenos = (ImageView) findViewById(R.id.ivMenos);
		
		OnTouchListener setFlechasRojas = new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
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
		
		ivMas.setOnClickListener(this);
		ivMenos.setOnClickListener(this);
		ivMas.setOnTouchListener(setFlechasVerdes);
		ivMenos.setOnTouchListener(setFlechasRojas);
	    
	} //Fin initFlechas

	public int getTournamentType (){
		int TournamentType = rgTournamentType.getCheckedRadioButtonId();
		return TournamentType;
	}
	
	

	public void onClick(View v) {
		switch(v.getId()){ 
			case R.id.bSelectTournament:
				Bundle b = new Bundle();
				Intent intent = new Intent(this, SetupEquipos.class);
								
				b.putInt("tournamentType", getTournamentType());
				b.putInt("NumEquipos", NumEquipos);
				b.putBoolean("randomize", chkRandom.isChecked());
				intent.putExtras(b);
				startActivity(intent);
			break;
			
			case R.id.ivMas:
				if (getTournamentType() == R.id.rbDirectElimination){
					if (NumEquipos < 64){
						NumEquipos *=2;
						tvNumEquipos.setText(String.valueOf(NumEquipos));
					}
				}
			break;
			
			case R.id.ivMenos:
				if (getTournamentType() == R.id.rbDirectElimination){
					if (NumEquipos > 2){
						NumEquipos /=2;
						tvNumEquipos.setText(String.valueOf(NumEquipos));
					}
				}
			break;
				
			
		}
	}
}
	

