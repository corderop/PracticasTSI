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
	// private Vector2d destino;

	// Mapa
	private Vector2d escala;
	private Vector2d posicion;

	// Pathfinding
	private Nodo actual;
	private ArrayList<Vector2d> diamantes;
	
	// Distancias óptimas entre diamantes
	private int distancias[][];
	private double media;

	// Auxiliar
	private int nivel = 1;

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
		
		// Obtenemos la posicion del avatar
		this.posicion = stateObs.getAvatarPosition().copy();
		this.posicion.x = Math.floor(this.posicion.x / this.escala.x);
		this.posicion.y = Math.floor(this.posicion.y / this.escala.y);

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
		Vector2d destino = posiciones[0].get(0).position.copy();
        destino.x = Math.floor(destino.x / this.escala.x);
        destino.y = Math.floor(destino.y / this.escala.y);
		
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

		// Dibujamos el mapa
		for( int i=0; i<tablero.length; i++){
			for(int j=0; j<tablero[i].length; j++){
				System.out.print(tablero[i][j]);
			}
			System.out.print('\n');
		}

		// Calculamos el recorrido 
		int size = diamantes.size();
		if(size != 0){
			this.nivel = 1;
			this.distancias = new int[size+2][size];
			this.media = 0;
			for(int i=0; i<size; i++){
				for(int j=0; j<size; j++){
					if(i!=j){
						// Incializamos nodos para la búsqueda
						this.actual = new Nodo((int)diamantes.get(i).x,(int)diamantes.get(i).y,new LinkedList<ACTIONS>(), 0, new Vector2d(this.diamantes.get(j).x, this.diamantes.get(j).y));
						this.abiertos = new PriorityQueue<Nodo>();
						this.cerrados = new PriorityQueue<Nodo>();
						abiertos.add(actual);
						this.acciones = this.busqueda1(stateObs, elapsedTimer);
						distancias[i][j] = acciones.size();
						media += acciones.size();
					}
					else
						distancias[i][j] = 0;

					System.out.print(distancias[i][j]);
					System.out.print("  ");
				}
				System.out.println(" ");
			}
			for(int i=0; i<size; i++){
				this.actual = new Nodo((int)destino.x,(int)destino.y,new LinkedList<ACTIONS>(), 0, new Vector2d(this.diamantes.get(i).x, this.diamantes.get(i).y));
				this.abiertos = new PriorityQueue<Nodo>();
				this.cerrados = new PriorityQueue<Nodo>();
				abiertos.add(actual);
				this.acciones = this.busqueda1(stateObs, elapsedTimer);
				media += acciones.size();
				distancias[size][i] = acciones.size();
			}

			for(int i=0; i<size; i++){
				this.actual = new Nodo((int)posicion.x,(int)posicion.y,new LinkedList<ACTIONS>(), 0, new Vector2d(this.diamantes.get(i).x, this.diamantes.get(i).y));
				this.abiertos = new PriorityQueue<Nodo>();
				this.cerrados = new PriorityQueue<Nodo>();
				abiertos.add(actual);
				this.acciones = this.busqueda1(stateObs, elapsedTimer);
				media += acciones.size();
				distancias[size+1][i] = acciones.size();
			}

			media /= (size+1)*(size);
			System.out.println(media);
			this.nivel = 2;
		}
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
			if(nivel == 1)
				acciones = busqueda1(stateObs, elapsedTimer);
			else if(nivel == 2)
				acciones = busqueda2(stateObs, elapsedTimer);
			
			ACTIONS a;
			if(!acciones.isEmpty())
				a = acciones.poll();
			else 
				a = Types.ACTIONS.ACTION_NIL;
			
			System.out.println(a);
			return a;
		}
		// return Types.ACTIONS.ACTION_NIL;
	}
	
	private Queue<ACTIONS> busqueda1(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Obtenemos el mejor nodos de abiertos
		actual = abiertos.poll();
		cerrados.add(actual);
		// Calculamos mientras no sea nodo objetivo o no se hayan superado los 50 ms
		while( !actual.esDestino() && elapsedTimer.remainingTimeMillis() >= -8){

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
		if(actual.esDestino())
			return actual.acts;
		
		return new LinkedList<ACTIONS>();
	}

	private Queue<ACTIONS> busqueda2(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){


		// Obtenemos el mejor nodos de abiertos
		Nodo2 act = new Nodo2();
		PriorityQueue<Nodo2> ab = new PriorityQueue<Nodo2>(), 
							 ce = new PriorityQueue<Nodo2>();
		ce.add(act);
		
		while( !act.esDestino() && elapsedTimer.remainingTimeMillis() >= -8){

			int size = this.diamantes.size();

			if(act.diamantes.size() != size){
				for(int i=0; i<size; i++){
					if(!act.diamantes.contains(i)){
						Nodo2 h = act.siguiente(i);
						if(!ce.contains(h)){
							ab.add(h);
						}
					}
				}
			}
			else{
				Nodo2 h = act.siguiente(size);
				if(!ce.contains(h)){
					ab.add(h);
				}
			}

			// Toma el siguiente nodo a evaluar
			act = ab.poll();
			// Elimina todos los nodos iguales a actual (misma posición) en abiertos
			ab.remove(act);
			ce.add(act);
		}

		

		System.out.println(act.diamantes);

		return new LinkedList<ACTIONS>();
	}

	public class Nodo implements Comparable<Nodo>{

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
			this.o = _o;
			this.distancia();
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

		public Boolean esDestino(){
			return this.pos_x == destino.x && this.pos_y == destino.y;
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

	public class Nodo2 implements Comparable<Nodo2>{

        public ArrayList<Integer> diamantes;			// Acciones de un nodo
		public double g = 0,				// Variables de la función de evaluación
					  h = 0;
                     
        public Nodo2() {
			this.diamantes = new ArrayList<Integer>();
			this.distancia();
		}

		public Nodo2(Nodo2 copy){
			this.diamantes = new ArrayList<Integer>(copy.diamantes);
			this.g = copy.g;
			this.h = copy.h;
		}

		public Nodo2 siguiente(int i){
			Nodo2 salida;
			salida = new Nodo2(this);

			diamantes.add(i);
			
			if(i!=0)
				g += myAgent.this.distancias[i][diamantes.get(diamantes.size()-1)];
			else
				g += myAgent.this.distancias[diamantes.size()+1][i];
			
			this.distancia();

			return salida;
		}

		// Calcula la distancia manhattan
		private void distancia(){
			this.h = (myAgent.this.diamantes.size() - diamantes.size() + 1)*myAgent.this.media;
		}

		public Boolean esDestino(){
			return diamantes.size() == myAgent.this.diamantes.size()+1;
		}

        // Comparar para ordenar en la priority queue
		@Override
		public int compareTo(Nodo2 n) {
			return (int)((this.g + this.h) - (n.g + n.h));
        }

        // Comparar que sean iguales
        @Override
        public boolean equals(Object o) {
			Nodo2 n = (Nodo2) o;
            if(this.diamantes.size() == n.diamantes.size()){
				if(this.diamantes.size()>0){
					if(this.diamantes.get(this.diamantes.size()-1) == n.diamantes.get(this.diamantes.size()-1)){
						return this.diamantes.containsAll(n.diamantes);
					}
					else 
						return false;
				}
				else
					return true;
			}

			return false;
        }
        
        // @Override
        // public String toString() {
        //     return "Nodo [pos_x=" + pos_x + ", pos_y=" + pos_y + ", orientacion=" + o + ", f=" + (g+h) + "]\n";
        // }
	}
}