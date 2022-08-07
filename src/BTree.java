import java.security.PublicKey;
import java.util.ArrayList;

/******************************************************************************
 *  Compilation:  javac BTree.java
 *  Execution:    java BTree
 *  Dependencies: StdOut.java
 *
 *  B-tree.
 *
 ******************************************************************************/
/**
 *  The {@code BTree} class represents an ordered symbol table of generic
 *  key-value pairs.
 *  It supports the <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>size</em>, and <em>is-empty</em> methods.
 *  A symbol table implements the <em>associative array</em> abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  Unlike {@link java.util.Map}, this class uses the convention that
 *  values cannot be {@code null}—setting the
 *  value associated with a key to {@code null} is equivalent to deleting the key
 *  from the symbol table.
 *  <p>
 *  This implementation uses a B-tree. It requires that
 *  the key type implements the {@code Comparable} interface and calls the
 *  {@code compareTo()} and method to compare two keys. It does not call either
 *  {@code equals()} or {@code hashCode()}. 
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/62btree">Section 6.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class BTree<Key extends Comparable<Key>, Value>  {

	//ORDEN M
	//Debe ser mayor que dos porque la cantidad m�xima de hijos por cada Nodo es M-1
    private static final int M = 4;

    public Node root;       // raiz
    private int height;      // altura
    private int n;           // cantidad de nodos (pares clave valor)
    public boolean bandera = true;
    // NODO
    private static final class Node {
        private int m;                             // numero de claves
        private Entry[] children = new Entry[M];   // arreglo de claves, fijense que se crea en base al tamano del orden

        // creacion de un nodo con k numero de hijos
        private Node(int k) {
            m = k;
        }


        public String toString(){
            String str = "";
            for(int i = 0 ; i < m ; i++){
                str += "HIJO " + (i+1) + " : Clave : " + this.children[i].getKey() + " Valor :" + this.children[i].val +  " |||| ";
            }
            return str;
        }
    }

    public static class Entry {
        private Comparable key;
        private Object val;
        private Node next;     // permite iterar sobre el arreglo de entradas
        public Entry(Comparable key, Object val, Node next) {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }

        public String toString (){
            return "[ Clave : " + this.key + " - Valor : " + this.val + " ]";
        }

        public Comparable getKey(){
            return this.key;
        }
        public Node getNext(){
            return this.next;
        }
        public Object getVal(){
            return this.val;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTree() {
        root = new Node(0);
    }
 
    /**
     * Returns true if this symbol table is empty.
     * @return {@code true} if this symbol table is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Returns the height of this B-tree (for debugging).
     *
     * @return the height of this B-tree
     */
    public int height() {
        return height;
    }


    /**
     * Returns the value associated with the given key.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and {@code null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public ArrayList<Object> get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        //System.out.println("En metodo get : " + root.m);
        //System.out.println("NODO RAIZ : " + root);
        //System.out.println("NODO REFERNCIA PRIMER HJIO: " + root.children[0].next);
        //System.out.println("NODO REFERNCIA SEGUNDDO HIJO : " + root.children[1].next);
        ArrayList<Object> myItems = new ArrayList<>();
        
        do{
            //Primera vez que encentra
            Entry item = search(root, key, height, myItems);
            //Si ya no encuentra sale del bucle y termina 
            if(item == null){
                bandera = false;
            }else{
                myItems.add(item);
            }
            
        }
        while(bandera);

        return myItems;
    }

    private Entry search(Node x, Key key, int ht, ArrayList<Object> items) {
        

        Entry[] children = x.children; //Es el arreglo de claves de un nodo

        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                    if(j >= items.size()){
                        if ( eq(key, children[j].key)){
                            System.out.println("Esto va al array : " + children[j]);
                            return children[j];
                        }
                    }else{
                        //System.out.println("==================================================");
                        //System.out.println("Se compara " + children[j] + " con " + key);
                        if ( items.get(j) != children[j] && eq(key, children[j].key)){
                            System.out.println("CON REFERENCIAS : Esto va al array : " + children[j]);
                            return children[j];
                        }
                    }
            }
        }

        else {
            for (int j = 0; j < x.m; j++) {
                if (j+1 == x.m || less(key, children[j+1].key)){
                    //System.out.println("El elemento al que va : " + children[j].next);
                    return search(children[j].next, key, ht-1, items);

                }
            }
        }
        return null;
    }


    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is {@code null}, this effectively deletes the key from the symbol table.
     *
     * @param  key the key
     * @param  val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(Key key, Value val) {
        if (key == null) 
        	throw new IllegalArgumentException("argument key to put() is null");
        Node u = insert(root, key, val, height); //Se crea nuevo nodo
        //System.out.println(u);
        n++;
        //System.out.println("LAS CALVES SON " + root.m);
        if (u == null) return;

        // fraccionar la raiz
        Node t = new Node(2);
        //System.out.println("EL VALOR QUE SE INSERTA ES: " + root.children[0].key);
        //System.out.println("EL VALOR QUE SE INSERTA ES : " + u.children[0].key);
        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);
        root = t;
        height++;
    }

    private Node insert(Node h, Key key, Value val, int ht) {
        int j;
        Entry t = new Entry(key, val, null);

        if (ht == 0) {
            for (j = 0; j < h.m; j++) {
                //System.out.println("Se compara: " + key + " y " + h.children[j].key);
                if (less(key, h.children[j].key)) 
                	break;
            }
        }

        else {
            for (j = 0; j < h.m; j++) {
                if ((j+1 == h.m) || less(key, h.children[j+1].key)) {
                    Node u = insert(h.children[j++].next, key, val, ht-1);
                    if (u == null) 
                    	return null;
                    t.key = u.children[0].key;
                    t.val = null;
                    t.next = u;
                    break;
                }
            }
        }

        
        for (int i = h.m; i > j; i--){
            h.children[i] = h.children[i-1];
        }
        h.children[j] = t; //Se asigna al arreglo
        h.m++; //Se aumenta el numero de clave
        if (h.m < M) //Numero de clavs?
        	return null;
        else         
        	return split(h);
    }

    // partir el nodo en la mitad
    private Node split(Node h) {
        Node t = new Node(M/2);
        h.m = M/2;
        //System.out.println("AHORA h.m es : " + h.m);
        for (int j = 0; j < M/2; j++){
            t.children[j] = h.children[M/2+j]; //Aqui formo el nodo que se forma a la derecha de la mediana
        }
        return t;    
    }

    /**
     * Returns a string representation of this B-tree (for debugging).
     *
     * @return a string representation of this B-tree.
     */
    public String toString() {
        return toString(root, height, "") + "\n";
    }

    private String toString(Node h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        Entry[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.m; j++) {
                s.append(indent + children[j].key + " " + children[j].val + "\n");
            }
        }
        else {
            for (int j = 0; j < h.m; j++) {
                if (j > 0) 
                	s.append(indent + "(" + children[j].key + ")\n");
                s.append(toString(children[j].next, ht-1, indent + "     "));
            }
        }
        return s.toString();
    }


    // comparadores
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }
}




