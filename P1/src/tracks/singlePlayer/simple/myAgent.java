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

	private PriorityQueue<Nodo> abiertos, cerrados;		// Lista de abiertos y cerrados
	private Nodo actual;
	private Vector2d destino;
	private ArrayList<Vector2d> obstaculos;
	private Queue<ACTIONS> acciones;
	private int hola = 0;

	// Auxiliares
	private Vector2d escala;
	private Vector2d posicion;

	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public myAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Calculamos la escala
		this.escala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

		// Obtenemos todos los portales
		ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos el portal mas proximo
        this.destino = posiciones[0].get(0).position;
        this.destino.x = Math.floor(this.destino.x / this.escala.x);
        this.destino.y = Math.floor(this.destino.y / this.escala.y);

		this.posicion = stateObs.getAvatarPosition();
		this.posicion.x = Math.floor(this.posicion.x / this.escala.x);
		this.posicion.y = Math.floor(this.posicion.y / this.escala.y);

		posiciones = stateObs.getImmovablePositions();
		this.obstaculos = new ArrayList<Vector2d>();
		for( Observation i : posiciones[0] ){
			this.obstaculos.add(new Vector2d(Math.floor(i.position.x / this.escala.x), Math.floor(i.position.y / this.escala.y))); 
		}

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
			ACTIONS aaa = acciones.poll();
			System.out.println(aaa);
			return aaa;
		}
		else if(hola == 0){
			busqueda(stateObs, elapsedTimer);
			ACTIONS aaa = acciones.poll();
			if(aaa != Types.ACTIONS.ACTION_NIL)
				hola = 1;
			System.out.println(aaa);
			return aaa;
		}
		else{
			return Types.ACTIONS.ACTION_NIL;
		}
	}
	
	private void busqueda(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Obtenemos el mejor nodos de abiertos
		actual = abiertos.poll();
		cerrados.add(actual);
		System.out.println("POSICión destino : " + destino + "\n");
		System.out.print(actual);
		// Calculamos mientras no sea nodo objetivo
		while(!(actual.pos_x == destino.x && actual.pos_y == destino.y) && elapsedTimer.remainingTimeMillis() > 5){

			Nodo 	h1 = actual.siguiente(Types.ACTIONS.ACTION_UP), 
					h2 = actual.siguiente(Types.ACTIONS.ACTION_DOWN), 
					h3 = actual.siguiente(Types.ACTIONS.ACTION_RIGHT), 
					h4 = actual.siguiente(Types.ACTIONS.ACTION_LEFT);

			// Procesamos el primer hijo
			Vector2d v = new Vector2d(h1.pos_x, h1.pos_y);
			if(!obstaculos.contains(v))
				if(!cerrados.contains(h1)){
					if(abiertos.contains(h1)){
						Iterator<Nodo> it = abiertos.iterator(); 
						Boolean igual = false;
		
						while (it.hasNext() && !igual){ 
							Nodo n = it.next();

							if(n.equals(h1)){
								igual = true;
								if(n.compareTo(h1) > 0){
									abiertos.remove(n);
									abiertos.add(h1);
								}

							}
						} 
					}
					else
						abiertos.add(h1);
				}

			// Procesamos el segundo hijo
			v = new Vector2d(h2.pos_x, h2.pos_y);
			if(!obstaculos.contains(v))
				if(!cerrados.contains(h2)){
					if(abiertos.contains(h2)){
						Iterator<Nodo> it = abiertos.iterator(); 
						Boolean igual = false;
		
						while (it.hasNext() && !igual){ 
							Nodo n = it.next();

							if(n.equals(h2)){
								igual = true;
								if(n.compareTo(h2) > 0){
									abiertos.remove(n);
									abiertos.add(h2);
								}

							}
						} 
					}
					else
						abiertos.add(h2);
				}

			// Procesamos el primer hijo
			v = new Vector2d(h3.pos_x, h3.pos_y);
			if(!obstaculos.contains(v))
				if(!cerrados.contains(h3)){
					if(abiertos.contains(h3)){
						Iterator<Nodo> it = abiertos.iterator(); 
						Boolean igual = false;
		
						while (it.hasNext() && !igual){ 
							Nodo n = it.next();

							if(n.equals(h3)){
								igual = true;
								if(n.compareTo(h3) > 0){
									abiertos.remove(n);
									abiertos.add(h3);
								}

							}
						} 
					}
					else
						abiertos.add(h3);
				}

			// Procesamos el cuarto hijo
			v = new Vector2d(h4.pos_x, h4.pos_y);
			if(!obstaculos.contains(v))
				if(!cerrados.contains(h4)){
					if(abiertos.contains(h4)){
						Iterator<Nodo> it = abiertos.iterator(); 
						Boolean igual = false;
		
						while (it.hasNext() && !igual){ 
							Nodo n = it.next();

							if(n.equals(h4)){
								igual = true;
								if(n.compareTo(h4) > 0){
									abiertos.remove(n);
									abiertos.add(h4);
								}

							}
						} 
					}
					else
						abiertos.add(h4);
				}

			// Toma el siguiente nodo a evaluar
			actual = abiertos.poll();
			cerrados.add(actual);
			System.out.print(actual);
		}

		// Cuando encuentra nodo objetivo toma su lista de acciones para realizarlas
		if(actual.pos_x == destino.x && actual.pos_y == destino.y)
			acciones = actual.acts;
		else
			acciones.add(Types.ACTIONS.ACTION_NIL);
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