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
	// - nada
	private char[][] tablero;
	private Vector2d destino;

	// Mapa
	private Vector2d escala;
	private Vector2d posicion;

	// Pathfinding
	private Nodo actual;

	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public myAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

		// DIBUJAMOS EL MAPA

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

		// Dibujamos el mapa
		for( int i=0; i<tablero.length; i++){
			for(int j=0; j<tablero[i].length; j++){
				System.out.print(tablero[i][j]);
			}
			System.out.print('\n');
		}


		// Obtenemos la posicion del avatar
		this.posicion = stateObs.getAvatarPosition().copy();
		this.posicion.x = Math.floor(this.posicion.x / this.escala.x);
		this.posicion.y = Math.floor(this.posicion.y / this.escala.y);

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
		if(!acciones.isEmpty()){
			ACTIONS a = acciones.poll();
			System.out.println(a);
			return a;
		}
		else{
			busqueda(stateObs, elapsedTimer);
			ACTIONS a = acciones.poll();
			System.out.println(a);
			return a;
		}
		// return Types.ACTIONS.ACTION_NIL;
	}
	
	private void busqueda(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Obtenemos el mejor nodos de abiertos
		actual = abiertos.poll();
		cerrados.add(actual);
		// Calculamos mientras no sea nodo objetivo o no se hayan superado los 50 ms
		while(tablero[actual.pos_y][actual.pos_x] != 'Y' && elapsedTimer.remainingTimeMillis() >= -8){

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
		if(actual.pos_x == destino.x && actual.pos_y == destino.y)
			acciones = actual.acts;
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
}