package tracks.singlePlayer.simple;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import tools.pathfinder.*;

import java.util.*;

public class myAgent extends AbstractPlayer{

	private StateObservation estado;

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

	// Mapa
	private Vector2d escala;
	private Vector2d posicion;
	int orit;

	// Pathfinding
	private Nodo actual;

	// Auxiliares
	private int nivel = 1;
	private PathFinder pf;
	// Distancias óptimas entre diamantes
	private int distancias[][];
	private double media;


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
		this.diamantes.add(posicion);
		if(posiciones != null){
			nivel = 2;
			for(int i=0; i<posiciones[0].size(); i++){
				int x = (int) Math.floor(posiciones[0].get(i).position.x / this.escala.x);
				int y = (int) Math.floor(posiciones[0].get(i).position.y / this.escala.y);
				this.diamantes.add(new Vector2d(x, y));
				this.tablero[y][x] = 'D';
			}
		}
		this.diamantes.add(destino);

		int size = diamantes.size();
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
					contador++;
				}
			}
			media /= contador;
			System.out.println(media);
		}

		ArrayList<Integer> obstaculos = new ArrayList<Integer>();
		obstaculos.add((int)'-');
		obstaculos.add((int)'x');
		this.pf = new PathFinder(obstaculos);
		this.pf.run(stateObs);

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
				if(posiciones[0].size()>=1)
					nivel = 3;
				// else
				// 	nivel = 4;
			}
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
		if(!acciones.isEmpty()){	
			ACTIONS a = acciones.poll();
			System.out.println(a);
			return a;
		}
		else{
			if(nivel==1){
				acciones = busqueda(stateObs, elapsedTimer);
			}
			else if(nivel==2){
				acciones = busquedaDiamantes(stateObs, elapsedTimer);
			}
			else if(nivel==3){
				return escapar(stateObs);
			}

			if(!acciones.isEmpty()){
				ACTIONS a = acciones.poll();
				System.out.println(a);
				return a;
			}
			else{
				System.out.println("Camino no encontrado");
				return Types.ACTIONS.ACTION_NIL;
			}
		}
		// return Types.ACTIONS.ACTION_NIL;
	}

	private ACTIONS escapar(StateObservation stateObs){
		ArrayList<Observation>[] posicionNPC = stateObs.getNPCPositions();
		ArrayList<Vector2d> posN = new ArrayList<Vector2d>(0);
		ACTIONS salida = Types.ACTIONS.ACTION_NIL;
		ArrayList<Double> dists = new ArrayList<Double>(4);
		dists.add(0.0);
		dists.add(0.0);
		dists.add(0.0);
		dists.add(0.0);

		for(int i=0; i<posicionNPC[0].size(); i++){
			int x = (int) Math.floor(posicionNPC[0].get(i).position.x / this.escala.x);
			int y = (int) Math.floor(posicionNPC[0].get(i).position.y / this.escala.y);
			Vector2d a = new Vector2d(x,y);
			posN.add(a);
			double auxDist = distanciaMan(this.posicion, a);
			// Añadimos distancias con la posición actual
			for(int j=0; j<4 ; j++){
				if(auxDist < dists.get(j) && i!=0)
					dists.set(j, auxDist);
				else if(i==0){
					dists.set(j, auxDist);
				}
			}
		}

		// Orientación 0-Der, 1-Up, 2-Izq, 3-Down
		// DERECHA
		if(tablero[(int) this.posicion.y][(int) this.posicion.x+1] != 'X' && tablero[(int) this.posicion.y][(int) this.posicion.x+1] != 'Y'){
			Vector2d aux = new Vector2d(this.posicion.x+1, this.posicion.y);
			double sum = distanciaMan(aux, posN.get(0));
			for(int i=1; i<posN.size(); i++){
				double auxD = distanciaMan(aux, posN.get(i));
				if(auxD < sum){
					sum = auxD;
				}
			}
			
			if(this.orit == 0){
				dists.set(0, sum);
			}
			else{
				if(sum > dists.get(0) ){
					dists.set(0, dists.get(0)+0.5);
				}
				else if(sum < dists.get(0) ){
					dists.set(0, dists.get(0)-1.5);
				}
				else{
					dists.set(0, dists.get(0)-2);
				}
			}
		}
		else{
			dists.set(0, 0.0);
		}
		
		// ARRIBA
		if(tablero[(int) this.posicion.y-1][(int) this.posicion.x] != 'X' && tablero[(int) this.posicion.y-1][(int) this.posicion.x] != 'Y'){
			Vector2d aux = new Vector2d(this.posicion.x, this.posicion.y-1);
			double sum = distanciaMan(aux, posN.get(0));
			for(int i=1; i<posN.size(); i++){
				double auxD = distanciaMan(aux, posN.get(i));
				if(auxD < sum){
					sum = auxD;
				}
			}
			
			if(this.orit == 1){
				dists.set(1, sum);
			}
			else{
				if(sum > dists.get(1) ){
					dists.set(1, dists.get(1)+0.5);
				}
				else if(sum < dists.get(0) ){
					dists.set(1, dists.get(1)-1.5);
				}
				else{
					dists.set(1, dists.get(1)-2);
				}
			}
		}
		else{
			dists.set(1, 0.0);
		}

		// IZQUIERDA
		if(tablero[(int) this.posicion.y][(int) this.posicion.x-1] != 'X' && tablero[(int) this.posicion.y][(int) this.posicion.x-1] != 'Y'){
			Vector2d aux = new Vector2d(this.posicion.x-1, this.posicion.y);
			double sum = distanciaMan(aux, posN.get(0));
			for(int i=1; i<posN.size(); i++){
				double auxD = distanciaMan(aux, posN.get(i));
				if(auxD < sum){
					sum = auxD;
				}
			}
			
			if(this.orit == 2){
				dists.set(2, sum);
			}
			else{
				if(sum > dists.get(2) ){
					dists.set(2, dists.get(2)+0.5);
				}
				else if(sum < dists.get(0) ){
					dists.set(2, dists.get(2)-1.5);
				}
				else{
					dists.set(2, dists.get(2)-2);
				}
			}
		}
		else{
			dists.set(2, 0.0);
		}

		// ABAJO
		if(tablero[(int) this.posicion.y+1][(int) this.posicion.x] != 'X' && tablero[(int) this.posicion.y+1][(int) this.posicion.x] != 'Y'){
			Vector2d aux = new Vector2d(this.posicion.x, this.posicion.y+1);
			double sum = distanciaMan(aux, posN.get(0));
			for(int i=1; i<posN.size(); i++){
				double auxD = distanciaMan(aux, posN.get(i));
				if(auxD < sum){
					sum = auxD;
				}
			}
			
			if(this.orit == 3){
				dists.set(3, sum);
			}
			else{
				if(sum > dists.get(3) ){
					dists.set(3, dists.get(3)+0.5);
				}
				else if(sum < dists.get(0) ){
					dists.set(3, dists.get(3)-1.5);
				}
				else{
					dists.set(3, dists.get(3)-2);
				}
			}
		}
		else{
			dists.set(3, 0.0);
		}

		int pos = 0;
		double max = dists.get(0);
		for(int i=1; i<4; i++){
			if(max < dists.get(i)){
				max = dists.get(i);
				pos = i;
			}
		}

		switch (pos) {
			case 0:
				salida = Types.ACTIONS.ACTION_RIGHT;
				if(this.orit == 0)
					this.posicion.x++;
				break;
			case 1:
				salida = Types.ACTIONS.ACTION_UP;
				if(this.orit == 1)
					this.posicion.y--;
				break;
			case 2:
				salida = Types.ACTIONS.ACTION_LEFT;
				if(this.orit == 2)
					this.posicion.x--;
				break;
			case 3:
				salida = Types.ACTIONS.ACTION_DOWN;
				if(this.orit == 3)
					this.posicion.y++;
				break;
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

	private Queue<ACTIONS> busquedaDiamantes(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		PriorityQueue<Objetivo> ab = new PriorityQueue<Objetivo>();
		ArrayList<Integer> num = new ArrayList<Integer>(0);
		for(int i=0; i<this.diamantes.size(); i++){
			num.add(i);
		}

		Objetivo act = new Objetivo(num);

		while( !act.obj.isEmpty() && elapsedTimer.remainingTimeMillis() >= -8){

			int size = act.obj.size();
			if(size > 1){
				for(int i=0; i<size-1; i++){
					ab.add(act.siguiente(i));
				}
			}
			else{
				ab.add(act.siguiente(0));
			}

			act = ab.poll();
		}

		System.out.println(this.diamantes);
		System.out.println(act.sol);

		if(act.obj.isEmpty()){

			Vector2d orientacion = stateObs.getAvatarOrientation();
			int ori = 0;

			if(orientacion.x == 0){
				if(orientacion.y == 1) 		ori=3;
				if(orientacion.y == -1) 	ori=1;
			}
			else{
				if(orientacion.x == 1)		ori=0;
				if(orientacion.x == -1)		ori=2;
			}
	
			this.acciones = new LinkedList<ACTIONS>();
			
			for(int i=1; i<act.sol.size(); i++){
				this.actual = new Nodo((int)this.diamantes.get(act.sol.get(i-1)).x, (int)this.diamantes.get(act.sol.get(i-1)).y,  new LinkedList<ACTIONS>(), ori, this.diamantes.get(act.sol.get(i)));
				this.abiertos = new PriorityQueue<Nodo>();
				this.cerrados = new PriorityQueue<Nodo>();
				this.abiertos.add(this.actual);
				
				this.acciones.addAll(busqueda(stateObs, elapsedTimer));
				ori = this.actual.o;
			}

			return this.acciones;
		}

		return new LinkedList<ACTIONS>();
	}

	public class Objetivo implements Comparable<Objetivo>{
        public ArrayList<Integer> obj;		// Acciones de un nodo
		public double g = 0,				// Variables de la función de evaluación
					  h = 0;
		public ArrayList<Integer> sol;
		
		public Objetivo(ArrayList<Integer> _obj){
			this.obj = new ArrayList<Integer>(_obj);
			this.sol = new ArrayList<Integer>();
			sol.add( obj.remove(0) );
			this.g = 0;
			this.heuristica();
		}

		public Objetivo( Objetivo cp ){
			this.obj = new ArrayList<Integer>(cp.obj);
			this.sol = new ArrayList<Integer>(cp.sol);
			this.g = cp.g;
			this.h = cp.h;
		}

		public Objetivo siguiente(int i){
			Objetivo salida = new Objetivo(this);

			int pos = salida.obj.remove(i);
			int tam = myAgent.this.pf.getPath(myAgent.this.diamantes.get(salida.sol.get(salida.sol.size()-1)), myAgent.this.diamantes.get(pos)).size();
			salida.g += tam;
			salida.sol.add(pos);
			salida.heuristica();

			return salida;
		}

		private double distanciaMan(Vector2d a, Vector2d b){
			return Math.abs(a.x - b.x) + Math.abs(a.y-b.y);
		}

		private void heuristica(){
			this.h = myAgent.this.media*this.obj.size();
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
}