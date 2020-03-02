import java.util.*;
import java.io.*;
import ontology.Types.ACTIONS;

public class prueba{

    public static void main(String[] args){
        
        Nodo n1 = new Nodo(1, 2, 0, 1);
        Nodo n3 = new Nodo(4, 5, 0, 1.5);
        Nodo n2 = new Nodo(1, 3, 0, 1);

        PriorityQueue<Nodo> nd = new PriorityQueue<>();

        nd.add(n1);
        nd.add(n3);
        nd.add(n2);

        System.out.println(nd.contains(new Nodo(1, 2, 565, 324)));

        // Creating an iterator 
        Iterator value = nd.iterator(); 
  
        // Displaying the values after iterating through the queue 
        System.out.println("The iterator values are: "); 
        while (value.hasNext()) { 
            System.out.println(value.next()); 
        } 

    }

    static class Nodo implements Comparable<Nodo>{

        public int pos_x, pos_y;
        public Stack<ACTIONS> acts = new Stack<ACTIONS>();
		public double g = 0,
                     h = 0;
                     
        public Nodo(int _x, int _y, double _g, double _h) {
            this.pos_x = _x;
            this.pos_y = _y;
            this.g = _g;
            this.h = _h;
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
            return this.pos_x== n.pos_x && this.pos_y == n.pos_y;
        }
        
        @Override
        public String toString() {
            return "Nodo [pos_x=" + pos_x + ", pos_y=" + pos_y + ", g=" + g + ", h=" + h + "]";
        }
	}
}