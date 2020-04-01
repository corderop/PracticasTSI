package tracks.singlePlayer.simple;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.*;

public class myAgent extends AbstractPlayer{

	// Lista de abiertos y cerrados
	private PriorityQueue<Nodo> abiertos, cerrados;
	private Queue<ACTIONS> acciones;
	// Tablero
	// X Obstaculos
	// Y Portales
	// D Diamantes
	// - nada
	private char[][] tablero;
	private Vector2d destino;
	private ArrayList<Vector2d> diamantes;
	private char[][] mapa_calor;

	// Mapa
	private Vector2d escala;
	private Vector2d posicion;
	int orit;

	// Pathfinding
	private Nodo actual;

	// Auxiliares
	private int nivel = 1;
	private int diamantes_pend = 10; // Diamantes por recoger
	private int distancias[][];	// Distancias óptimas entre diamantes
	private double media; // Media de distancias entre diamantes
	private int dist_personaje[];
	private int dist_final[];

	private ACTIONS[] actions;


	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public myAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

		// DIBUJAMOS EL MAPA-------------------------

		// Calculamos la escala para poder obtener una cuadricula
		this.escala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
		
		// Inicializamos tablero con todo a 0
		int columnas = stateObs.getObservationGrid().length;
		int filas = stateObs.getObservationGrid()[0].length;
		tablero = new char[filas][columnas];
		for( int i=0; i<tablero.length; i++){
			for(int j=0; j<tablero[i].length; j++){
				tablero[i][j] = '-';
			}
		}

		// Obtenemos todos los portales
		ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());

		// Obtenemos la posicion del avatar
		this.posicion = stateObs.getAvatarPosition().copy();
		this.posicion.x = Math.floor(this.posicion.x / this.escala.x);
		this.posicion.y = Math.floor(this.posicion.y / this.escala.y);

		//Seleccionamos el portal mas proximo como destino
		this.destino = posiciones[0].get(0).position.copy();
        this.destino.x = Math.floor(this.destino.x / this.escala.x);
        this.destino.y = Math.floor(this.destino.y / this.escala.y);
		
		// Dibujamos los portales
		for(int i=0; i<posiciones[0].size(); i++){
			int x = (int) Math.floor(posiciones[0].get(i).position.x / this.escala.x);
			int y = (int) Math.floor(posiciones[0].get(i).position.y / this.escala.y);
			tablero[y][x] = 'Y';
		}

		// Dibujamos los obstaculos
		posiciones = stateObs.getImmovablePositions();
		for(int i=0; i<posiciones[0].size(); i++){
			int x = (int) Math.floor(posiciones[0].get(i).position.x / this.escala.x);
			int y = (int) Math.floor(posiciones[0].get(i).position.y / this.escala.y);
			tablero[y][x] = 'X';
		}

		// Dibujamos los diamantes
		posiciones = stateObs.getResourcesPositions();
		this.diamantes = new ArrayList<Vector2d>(0);
		if(posiciones != null){
			nivel = 2;
			for(int i=0; i<posiciones[0].size(); i++){
				int x = (int) Math.floor(posiciones[0].get(i).position.x / this.escala.x);
				int y = (int) Math.floor(posiciones[0].get(i).position.y / this.escala.y);
				this.diamantes.add(new Vector2d(x, y));
				this.tablero[y][x] = 'D';
			}
		}

		int size = diamantes.size();
		this.distancias = new int[size][size];
		if(size != 0){
			this.media = 0;
			int contador = 0;
			for(int i=0; i<size-1; i++){
				for(int j=i+1; j<size; j++){
					// Incializamos nodos para la búsqueda
					this.actual = new Nodo((int)diamantes.get(i).x,(int)diamantes.get(i).y,new LinkedList<ACTIONS>(), 0, new Vector2d(this.diamantes.get(j).x, this.diamantes.get(j).y));
					this.abiertos = new PriorityQueue<Nodo>();
					this.cerrados = new PriorityQueue<Nodo>();
					abiertos.add(actual);
					this.acciones = this.busqueda(stateObs, elapsedTimer);
					media += acciones.size();
					this.distancias[i][j] = acciones.size();
					this.distancias[j][i] = acciones.size();
					contador++;
				}
			}
			media /= contador;
			System.out.println(media);
		}

		// Dibujamos el mapa
		for( int i=0; i<tablero.length; i++){
			for(int j=0; j<tablero[i].length; j++){
				System.out.print(tablero[i][j]);
			}
			System.out.print('\n');
		}

		posiciones = stateObs.getNPCPositions();
		if(posiciones != null){
			if(nivel==2)
				nivel = 5;
			else{
				if(posiciones[0].size()==1)
					nivel = 3;
				else if(posiciones[0].size()>=1)
					nivel = 4;
			}
		}

		actions = new ACTIONS[4];
		actions[0] = Types.ACTIONS.ACTION_RIGHT;
		actions[1] = Types.ACTIONS.ACTION_UP;
		actions[2] = Types.ACTIONS.ACTION_LEFT;
		actions[3] = Types.ACTIONS.ACTION_DOWN;

		// Indicamos la orientación del avatar
		int ori = 0;
		Vector2d orientacion = stateObs.getAvatarOrientation();

		if(orientacion.x == 0){
			if(orientacion.y == 1) 		ori=3;
			if(orientacion.y == -1) 	ori=1;
		}
		else{
			if(orientacion.x == 1)		ori=0;
			if(orientacion.x == -1)		ori=2;
		}

		this.orit = ori;

		// Incializamos nodos para la búsqueda
		this.actual = new Nodo((int)this.posicion.x,(int)this.posicion.y,new LinkedList<ACTIONS>(), ori, destino);

		this.abiertos = new PriorityQueue<Nodo>();
		this.cerrados = new PriorityQueue<Nodo>();

		abiertos.add(actual);

		this.acciones = new LinkedList<ACTIONS>();
	}
	
	/**
	 * Devuelve la acción
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 * @return 	ACTION_NIL all the time
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		Boolean peligro = false;
		// Si está encima de un diamante lo elimina del mapa
		if( this.tablero[(int)this.posicion.y][(int)this.posicion.x] == 'D' ){
			this.tablero[(int)this.posicion.y][(int)this.posicion.x] = '-';
			this.diamantes_pend--;
			int auxI = this.diamantes.indexOf(this.posicion);
			this.diamantes.set(auxI, new Vector2d(-1,-1));
		}

		if(nivel >= 3){
			calcularMapaCalor(stateObs);
			peligro = this.mapa_calor[(int)this.posicion.y][(int)this.posicion.x] != '-';
		}

		if(!acciones.isEmpty() && !peligro){	
			ACTIONS a = acciones.poll();
			cambiarPosicion(a);
			System.out.println(a);
			return a;
		}
		else{
			if(!peligro){
				if(nivel==1){
					acciones = busqueda(stateObs, elapsedTimer);
				}
				else if(nivel==2){
					acciones = busquedaDiamantes(stateObs, elapsedTimer);
				}
				else if(nivel == 5){
					if(this.diamantes_pend > 0)
						acciones = busquedaDiamantes(stateObs, elapsedTimer);
					else{
						this.actual = new Nodo((int)this.posicion.x,(int)this.posicion.y,new LinkedList<ACTIONS>(), this.orit, this.destino);

						this.abiertos = new PriorityQueue<Nodo>();
						this.cerrados = new PriorityQueue<Nodo>();

						this.abiertos.add(actual);

						this.acciones = busqueda(stateObs, elapsedTimer);
					}
				}
			}
			else{
				acciones = escapar(stateObs);
				if(acciones.isEmpty())
					acciones = escapar2(stateObs);
			}

			if(!acciones.isEmpty()){
				ACTIONS a = acciones.poll();
				cambiarPosicion(a);
				System.out.println(a);
				return a;
			}
			else{
				System.out.println(Types.ACTIONS.ACTION_NIL);
				return Types.ACTIONS.ACTION_NIL;
			}
		}
		// return Types.ACTIONS.ACTION_NIL;
	}

	private Queue<ACTIONS> escapar(StateObservation stateObs){
		Queue<ACTIONS> salida = new LinkedList<ACTIONS>();

		if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x] == '1'){
			System.out.println('1');
			if(this.orit == 0 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '-'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(this.orit == 1 && mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '-'){
				salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(this.orit == 2 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '-'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(this.orit == 3 && mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '-'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '-'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '-'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '-'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '-'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == 'X'){
				if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == 'X'){
					// Muro arriba y a la derecha
					if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-2] == '1'){
						salida.add(Types.ACTIONS.ACTION_LEFT);
						// salida.add(Types.ACTIONS.ACTION_LEFT);
						// salida.add(Types.ACTIONS.ACTION_LEFT);
					}
					else{
						salida.add(Types.ACTIONS.ACTION_DOWN);
						// salida.add(Types.ACTIONS.ACTION_DOWN);
						// salida.add(Types.ACTIONS.ACTION_DOWN);
					}
				}
				if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == 'X'){
					// Muro abajo y a la derecha
					if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-2] == '1'){
						salida.add(Types.ACTIONS.ACTION_LEFT);
						// salida.add(Types.ACTIONS.ACTION_LEFT);
						// salida.add(Types.ACTIONS.ACTION_LEFT);
					}
					else{
						salida.add(Types.ACTIONS.ACTION_UP);
						// salida.add(Types.ACTIONS.ACTION_UP);
						// salida.add(Types.ACTIONS.ACTION_UP);
					}
				}
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == 'X'){
				if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == 'X'){
					// Muro arriba y a la izquierda
					if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+2] == '1'){
						salida.add(Types.ACTIONS.ACTION_RIGHT);
						// salida.add(Types.ACTIONS.ACTION_RIGHT);
						// salida.add(Types.ACTIONS.ACTION_RIGHT);
					}
					else{
						salida.add(Types.ACTIONS.ACTION_DOWN);
						// salida.add(Types.ACTIONS.ACTION_DOWN);
						// salida.add(Types.ACTIONS.ACTION_DOWN);
					}
				}
				if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == 'X'){
					// Muro abajo y a la derecha
					if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+2] == '1'){
						salida.add(Types.ACTIONS.ACTION_RIGHT);
						// salida.add(Types.ACTIONS.ACTION_RIGHT);
						// salida.add(Types.ACTIONS.ACTION_RIGHT);
					}
					else{
						salida.add(Types.ACTIONS.ACTION_UP);
						// salida.add(Types.ACTIONS.ACTION_UP);
						// salida.add(Types.ACTIONS.ACTION_UP);
					}
				}
			}
		}
		else if( mapa_calor[(int)this.posicion.y][(int)this.posicion.x] == '2' ){
			System.out.println('2');
			if(this.orit == 0 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '1'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(this.orit == 1 && mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(this.orit == 2 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '1'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(this.orit == 3 && mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '1'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '1'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(this.orit != 0 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '3'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(this.orit != 1 && mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '3'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(this.orit != 2 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '3'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(this.orit != 3 && mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '3'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '3'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '3'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '3'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '3'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
		}
		else if( mapa_calor[(int)this.posicion.y][(int)this.posicion.x] == '3' ){
			System.out.println('3');
			if(this.orit == 0 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '2'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(this.orit == 1 && mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '2'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(this.orit == 2 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '2'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(this.orit == 3 && mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '2'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '2'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
				// salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '2'){
				salida.add(Types.ACTIONS.ACTION_UP);
				// salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '2'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
				// salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '2'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
				// salida.add(Types.ACTIONS.ACTION_DOWN);
			}
		}


		System.out.println(salida);
		return salida;
	}

	private Queue<ACTIONS> escapar2(StateObservation stateObs){
		Queue<ACTIONS> salida = new LinkedList<ACTIONS>();

		if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x] == '1'){
			System.out.println('1');
			if(this.orit == 0 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '1'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(this.orit == 1 && mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(this.orit == 2 && mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '1'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(this.orit == 3 && mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x+1] == '1'){
				salida.add(Types.ACTIONS.ACTION_RIGHT);
			}
			else if(mapa_calor[(int)this.posicion.y-1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_UP);
			}
			else if(mapa_calor[(int)this.posicion.y][(int)this.posicion.x-1] == '1'){
				salida.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(mapa_calor[(int)this.posicion.y+1][(int)this.posicion.x] == '1'){
				salida.add(Types.ACTIONS.ACTION_DOWN);
			}
		}
		else{
			salida.add(Types.ACTIONS.ACTION_NIL);
		}

		System.out.println(salida);
		return salida;
	}

	private Queue<ACTIONS> busqueda(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Obtenemos el mejor nodos de abiertos
		actual = abiertos.poll();
		cerrados.add(actual);
		// Calculamos mientras no sea nodo objetivo o no se hayan superado los 50 ms
		while( !actual.esObjetivo() && elapsedTimer.remainingTimeMillis() >= -8){

			// Genero los 4 hijos posibles

			if(tablero[actual.pos_y-1][actual.pos_x] != 'X'){
				Nodo h = actual.siguiente(Types.ACTIONS.ACTION_UP);
				if(!cerrados.contains(h)){
					abiertos.add(h);
				}
			}

			if(tablero[actual.pos_y+1][actual.pos_x] != 'X'){
				Nodo h = actual.siguiente(Types.ACTIONS.ACTION_DOWN);
				if(!cerrados.contains(h)){
					abiertos.add(h);
				}
			}

			if(tablero[actual.pos_y][actual.pos_x+1] != 'X'){
				Nodo h = actual.siguiente(Types.ACTIONS.ACTION_RIGHT);
				if(!cerrados.contains(h)){
					abiertos.add(h);
				}
			}

			if(tablero[actual.pos_y][actual.pos_x-1] != 'X'){
				Nodo h = actual.siguiente(Types.ACTIONS.ACTION_LEFT);
				if(!cerrados.contains(h)){
					abiertos.add(h);
				}
			}

			// Toma el siguiente nodo a evaluar
			actual = abiertos.poll();
			// Elimina todos los nodos iguales a actual (misma posición) en abiertos
			abiertos.remove(actual);
			cerrados.add(actual);
		}

		// Cuando encuentra nodo objetivo toma su lista de acciones para realizarlas
		if(actual.esObjetivo())
			return actual.acts;

		return new LinkedList<ACTIONS>();
	}

	static class Nodo implements Comparable<Nodo>{

		public int pos_x, pos_y;			// Posición en tablero 
        public Queue<ACTIONS> acts;			// Acciones de un nodo
		public double g = 0,				// Variables de la función de evaluación
					  h = 0;
		public int o;						// Orientación 0-Der, 1-Up, 2-Izq, 3-Down
		public Vector2d destino;			// Objetivo del nodo
                     
        public Nodo(int _x, int _y, Queue<ACTIONS> _acts, int _o, Vector2d _destino) {
            this.pos_x = _x;
			this.pos_y = _y;
			this.acts = _acts;
			this.destino = _destino;
			this.g = acts.size();
			this.distancia();
			this.o = _o;
		}

		public Nodo(Nodo copy){
			this.pos_x = copy.pos_x;
			this.pos_y = copy.pos_y;
			this.acts = new LinkedList<ACTIONS>(copy.acts);
			this.g = copy.g;
			this.h = copy.h;
			this.o = copy.o;
			this.destino = copy.destino;
		}

		public Nodo siguiente(ACTIONS a){
			Nodo salida;
			salida = new Nodo(this);

			if(a == Types.ACTIONS.ACTION_UP){
				if(salida.o==1){
					salida.pos_y--;
					salida.distancia();
				}
				else
					salida.o = 1;

				salida.g++;
				salida.acts.add(Types.ACTIONS.ACTION_UP);
			}
			else if(a == Types.ACTIONS.ACTION_DOWN){
				if(salida.o==3){
					salida.pos_y++;
					salida.distancia();
				}
				else
					salida.o = 3;

				salida.g++;
				salida.acts.add(Types.ACTIONS.ACTION_DOWN);
			}
			else if(a == Types.ACTIONS.ACTION_LEFT){
				if(salida.o==2){
					salida.pos_x--;
					salida.distancia();
				}
				else
					salida.o = 2;

				salida.g++;
				salida.acts.add(Types.ACTIONS.ACTION_LEFT);
			}
			else if(a == Types.ACTIONS.ACTION_RIGHT){
				if(salida.o==0){
					salida.pos_x++;
					salida.distancia();
				}
				else
					salida.o = 0;

				salida.g++;
				salida.acts.add(Types.ACTIONS.ACTION_RIGHT);
			}

			return salida;
		}

		// Calcula la distancia manhattan
		private void distancia(){
			this.h = Math.abs(this.pos_x - this.destino.x) + Math.abs(this.pos_y-this.destino.y);
		}

		public Boolean esObjetivo(){
			return this.pos_x==destino.x && this.pos_y == destino.y;
		}

        // Comparar para ordenar en la priority queue
		@Override
		public int compareTo(Nodo n) {
			return (int)((this.g + this.h) - (n.g + n.h));
        }

        // Comparar que sean iguales
        @Override
        public boolean equals(Object o) {
            Nodo n = (Nodo) o;
            return this.pos_x== n.pos_x && this.pos_y == n.pos_y && this.o == n.o;
        }
        
        @Override
        public String toString() {
            return "Nodo [pos_x=" + pos_x + ", pos_y=" + pos_y + ", orientacion=" + o + ", f=" + (g+h) + "]\n";
        }
	}
//
//
//
//
	private Queue<ACTIONS> busquedaDiamantes(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		Vector2d diamanteNoValido = new Vector2d(-1.0,-1.0);
		ArrayList<Integer> num = new ArrayList<Integer>(0);
		
		dist_personaje = new int [diamantes.size()];
		dist_final = new int [diamantes.size()];
		for(int i=0; i<diamantes.size(); i++){
			dist_personaje[i] = (int)distanciaMan(this.posicion, diamantes.get(i));
			dist_final[i] = (int)distanciaMan(this.destino, diamantes.get(i));
			if(diamantes.get(i).x != diamanteNoValido.x && diamantes.get(i).y != diamanteNoValido.y)
				num.add(i);
		}

		PriorityQueue<Objetivo> ab = new PriorityQueue<Objetivo>();
		Objetivo act = new Objetivo(num);

		while( act.obj > 0 && elapsedTimer.remainingTimeMillis() >= -8){

			if(act.obj > 1){
				int size = act.rest.size();
				for(int i=0; i<size; i++){
					ab.add(act.siguiente(act.rest.get(i)));
				}
			}
			else{
				ab.add(act.siguiente(-1));
			}

			act = ab.poll();
		}

		System.out.println(this.diamantes);
		System.out.println(act.d);
	
		Queue<ACTIONS> actions = new LinkedList<ACTIONS>();
		
		int ori = this.orit;

		// Nodo inicio con primer diamante
		this.actual = new Nodo((int)this.posicion.x, (int)this.posicion.y,  new LinkedList<ACTIONS>(), ori, this.diamantes.get(act.d.get(0)));
		this.abiertos = new PriorityQueue<Nodo>();
		this.cerrados = new PriorityQueue<Nodo>();
		this.abiertos.add(this.actual);
		actions.addAll(busqueda(stateObs, elapsedTimer));
		ori = this.actual.o;

		// Camino hacia diamantes
		for(int i=0; i<act.d.size()-1; i++){
			this.actual = new Nodo((int)this.diamantes.get(act.d.get(i)).x, (int)this.diamantes.get(act.d.get(i)).y,  new LinkedList<ACTIONS>(), ori, this.diamantes.get(act.d.get(i+1)));
			this.abiertos = new PriorityQueue<Nodo>();
			this.cerrados = new PriorityQueue<Nodo>();
			this.abiertos.add(this.actual);
			actions.addAll(busqueda(stateObs, elapsedTimer));
			ori = this.actual.o;
		}

		// Último diamante hasta puerta
		this.actual = new Nodo((int)this.diamantes.get(act.d.get(act.d.size()-1)).x, (int)this.diamantes.get(act.d.get(act.d.size()-1)).y,  new LinkedList<ACTIONS>(), ori, this.destino);
		this.abiertos = new PriorityQueue<Nodo>();
		this.cerrados = new PriorityQueue<Nodo>();
		this.abiertos.add(this.actual);
		actions.addAll(busqueda(stateObs, elapsedTimer));

		return actions;
	}

	public class Objetivo implements Comparable<Objetivo>{
		public ArrayList<Integer> d;		// Acciones de un nodo
		public ArrayList<Integer> rest;     // Diamantes restantes
		public int obj;
		public double g = 0,				// Variables de la función de evaluación
					  h = 0;
		// public ArrayList<Integer> sol;
		
		public Objetivo(ArrayList<Integer> dia){
			this.rest = new ArrayList<Integer>(dia);
			this.d = new ArrayList<Integer>();
			this.obj = myAgent.this.diamantes_pend+1;
			this.g = 0;
			this.heuristica();
		}

		public Objetivo( Objetivo cp ){
			this.obj = cp.obj;
			this.rest = new ArrayList<Integer>(cp.rest);
			this.d = new ArrayList<Integer>(cp.d);
			this.g = cp.g;
			this.h = cp.h;
		}

		public Objetivo siguiente(int i){
			Objetivo salida = new Objetivo(this);

			if(i >= 0){
				if(salida.d.size() > 0)
					salida.g += myAgent.this.distancias[d.get(d.size()-1)][i];
				else
					salida.g += myAgent.this.dist_personaje[i];

				salida.rest.remove(Integer.valueOf(i));
				salida.obj--;
				salida.heuristica();
				salida.d.add(i);
			}
			else{
				// Primer hijo
				salida.g += myAgent.this.dist_final[d.get(d.size()-1)];
				salida.h = 0;
				salida.obj--;
			}

			return salida;
		}

		private void heuristica(){
			this.h = myAgent.this.media*this.obj;
		}

		// Comparar para ordenar en la priority queue
		@Override
		public int compareTo(Objetivo n) {
			return (int)((this.g + this.h) - (n.g + n.h));
        }

        // Comparar que sean iguales
        @Override
        public boolean equals(Object o) {
            Objetivo n = (Objetivo) o;
            return this.equals(n);
        }
	}

	// Distancia Manhattan entre dos puntos
	private double distanciaMan(Vector2d a, Vector2d b){
		return Math.abs(a.x - b.x) + Math.abs(a.y-b.y);
	}

	private void cambiarPosicion(ACTIONS a){

		if( a == Types.ACTIONS.ACTION_UP ){
			if(this.orit == 1 )
				this.posicion.y--;
			else
				this.orit = 1;
		}
		else if( a == Types.ACTIONS.ACTION_DOWN ){
			if(this.orit == 3 )
				this.posicion.y++;
			else
				this.orit = 3;
		}
		else if( a == Types.ACTIONS.ACTION_LEFT ){
			if(this.orit == 2 )
				this.posicion.x--;
			else
				this.orit = 2;
		}
		else if( a == Types.ACTIONS.ACTION_RIGHT ){
			if(this.orit == 0 )
				this.posicion.x++;
			else
				this.orit = 0;
		}

	}

	private void calcularMapaCalor(StateObservation stateObs){
		ArrayList<Observation>[] posicionNPC = stateObs.getNPCPositions();
		ArrayList<Vector2d> posN = new ArrayList<Vector2d>(0);

		int filas = stateObs.getObservationGrid()[0].length;
		int columnas = stateObs.getObservationGrid().length;
		this.mapa_calor = new char[filas][columnas];
		for(int i=0; i<this.mapa_calor.length; i++){
			for(int j=0; j<this.mapa_calor[i].length; j++){
				if(tablero[i][j] == 'D' || tablero[i][j] == 'Y')
					this.mapa_calor[i][j] = '-';
				else
					this.mapa_calor[i][j] = tablero[i][j];
			}
		}
		// Auxiliares
		
		// Calculo el mapa de calor
		for(int i=0; i<posicionNPC[0].size(); i++){
			int x = (int) Math.floor(posicionNPC[0].get(i).position.x / this.escala.x);
			int y = (int) Math.floor(posicionNPC[0].get(i).position.y / this.escala.y);
			Vector2d a = new Vector2d(x,y);
			posN.add(a);
			this.mapa_calor[(int)a.y][(int)a.x] = '4';

			// Calculo zona de calor
			if(this.mapa_calor[(int)a.y-1][(int)a.x] != 'X'){
				this.mapa_calor[(int)a.y-1][(int)a.x] = '3';
					if(this.mapa_calor[(int)a.y-2][(int)a.x] != 'X'){
						if(this.mapa_calor[(int)a.y-2][(int)a.x] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-2][(int)a.x])) < 2 )
							this.mapa_calor[(int)a.y-2][(int)a.x] = '2';
						if(this.mapa_calor[(int)a.y-3][(int)a.x] != 'X'){
							if(this.mapa_calor[(int)a.y-3][(int)a.x] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-3][(int)a.x])) < 1 )
								this.mapa_calor[(int)a.y-3][(int)a.x] = '1';
						}
					}
			}

			if(this.mapa_calor[(int)a.y+1][(int)a.x] != 'X'){
				this.mapa_calor[(int)a.y+1][(int)a.x] = '3';
					if(this.mapa_calor[(int)a.y+2][(int)a.x] != 'X'){
						if(this.mapa_calor[(int)a.y+2][(int)a.x] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+2][(int)a.x])) < 2 )
							this.mapa_calor[(int)a.y+2][(int)a.x] = '2';
						if(this.mapa_calor[(int)a.y+3][(int)a.x] != 'X'){
							if(this.mapa_calor[(int)a.y+3][(int)a.x] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+3][(int)a.x])) < 1 )
								this.mapa_calor[(int)a.y+3][(int)a.x] = '1';
						}
					}
			}

			if(this.mapa_calor[(int)a.y][(int)a.x-1] != 'X'){
				this.mapa_calor[(int)a.y][(int)a.x-1] = '3';
					if(this.mapa_calor[(int)a.y][(int)a.x-2] != 'X'){
						if(this.mapa_calor[(int)a.y][(int)a.x-2] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y][(int)a.x-2])) < 2 )
							this.mapa_calor[(int)a.y][(int)a.x-2] = '2';
						if(this.mapa_calor[(int)a.y][(int)a.x-3] != 'X'){
							if(this.mapa_calor[(int)a.y][(int)a.x-3] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y][(int)a.x-3])) < 1 )
								this.mapa_calor[(int)a.y][(int)a.x-3] = '1';
						}
					}
			}

			if(this.mapa_calor[(int)a.y][(int)a.x+1] != 'X'){
				this.mapa_calor[(int)a.y][(int)a.x+1] = '3';
					if(this.mapa_calor[(int)a.y][(int)a.x+2] != 'X'){
						if(this.mapa_calor[(int)a.y][(int)a.x+2] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y][(int)a.x+2])) < 2 )
							this.mapa_calor[(int)a.y][(int)a.x+2] = '2';
						if(this.mapa_calor[(int)a.y][(int)a.x+3] != 'X'){
							if(this.mapa_calor[(int)a.y][(int)a.x+3] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y][(int)a.x+3])) < 1 )
								this.mapa_calor[(int)a.y][(int)a.x+3] = '1';
						}
					}
			}

			if(this.mapa_calor[(int)a.y-1][(int)a.x-1] != 'X'){
				if(this.mapa_calor[(int)a.y-1][(int)a.x-1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-1][(int)a.x-1])) < 2 )
					this.mapa_calor[(int)a.y-1][(int)a.x-1] = '2';

				if(this.mapa_calor[(int)a.y-1][(int)a.x-2] != 'X'){
					if(this.mapa_calor[(int)a.y-1][(int)a.x-2] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-1][(int)a.x-2])) < 1 )
						this.mapa_calor[(int)a.y-1][(int)a.x-2] = '1';
				}
				if(this.mapa_calor[(int)a.y-2][(int)a.x-1] != 'X'){
					if(this.mapa_calor[(int)a.y-2][(int)a.x-1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-2][(int)a.x-1])) < 1 )
						this.mapa_calor[(int)a.y-2][(int)a.x-1] = '1';
				}

			}
			if(this.mapa_calor[(int)a.y+1][(int)a.x-1] != 'X'){
				if(this.mapa_calor[(int)a.y+1][(int)a.x-1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+1][(int)a.x-1])) < 2 )
					this.mapa_calor[(int)a.y+1][(int)a.x-1] = '2';

				if(this.mapa_calor[(int)a.y+2][(int)a.x-1] != 'X'){
					if(this.mapa_calor[(int)a.y+2][(int)a.x-1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+2][(int)a.x-1])) < 1 )
						this.mapa_calor[(int)a.y+2][(int)a.x-1] = '1';
				}
				if(this.mapa_calor[(int)a.y+1][(int)a.x-2] != 'X'){
					if(this.mapa_calor[(int)a.y+1][(int)a.x-2] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+1][(int)a.x-2])) < 1 )
						this.mapa_calor[(int)a.y+1][(int)a.x-2] = '1';
				}
	
			}
			if(this.mapa_calor[(int)a.y-1][(int)a.x+1] != 'X'){
				if(this.mapa_calor[(int)a.y-1][(int)a.x+1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-1][(int)a.x+1])) < 2 )
					this.mapa_calor[(int)a.y-1][(int)a.x+1] = '2';

				if(this.mapa_calor[(int)a.y-1][(int)a.x+2] != 'X'){
					if(this.mapa_calor[(int)a.y-1][(int)a.x+2] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-1][(int)a.x+2])) < 1 )
						this.mapa_calor[(int)a.y-1][(int)a.x+2] = '1';
				}
				if(this.mapa_calor[(int)a.y-2][(int)a.x+1] != 'X'){
					if(this.mapa_calor[(int)a.y-2][(int)a.x+1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y-2][(int)a.x+1])) < 1 )
						this.mapa_calor[(int)a.y-2][(int)a.x+1] = '1';
				}

			}
			if(this.mapa_calor[(int)a.y+1][(int)a.x+1] != 'X'){
				if(this.mapa_calor[(int)a.y+1][(int)a.x+1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+1][(int)a.x+1])) < 2 )
					this.mapa_calor[(int)a.y+1][(int)a.x+1] = '2';

				if(this.mapa_calor[(int)a.y+2][(int)a.x+1] != 'X'){
					if(this.mapa_calor[(int)a.y+2][(int)a.x+1] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+2][(int)a.x+1])) < 1 )
						this.mapa_calor[(int)a.y+2][(int)a.x+1] = '1';
				}
				if(this.mapa_calor[(int)a.y+1][(int)a.x+2] != 'X'){
					if(this.mapa_calor[(int)a.y+1][(int)a.x+2] == '-' || Integer.parseInt(String.valueOf(this.mapa_calor[(int)a.y+1][(int)a.x+2])) < 1 )
						this.mapa_calor[(int)a.y+1][(int)a.x+2] = '1';
				}
			}

		}

		for( int i=0; i<this.mapa_calor.length; i++){
			for(int j=0; j<this.mapa_calor[i].length; j++){
				System.out.print(this.mapa_calor[i][j]);
			}
			System.out.print('\n');
		}

	}
}
