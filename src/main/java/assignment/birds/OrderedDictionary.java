package assignment.birds;

public class OrderedDictionary implements OrderedDictionaryADT {

    Node root;

    OrderedDictionary() {
        root = new Node();
    }

    /**
     * Returns the Record object with key k, or it returns null if such a record
     * is not in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment/birds/DictionaryException.java
     */
    @Override
    public BirdRecord find(DataKey k) throws DictionaryException {
        Node current = root;
        int comparison;
        if (root.isEmpty()) {         
            throw new DictionaryException("There is no record matches the given key");
        }

        while (true) {
            comparison = current.getData().getDataKey().compareTo(k);
            if (comparison == 0) { // key found
                return current.getData();
            }
            if (comparison == 1) {
                if (current.getLeftChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getLeftChild();
            } else if (comparison == -1) {
                if (current.getRightChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getRightChild();
            }
        }

    }

    /**
     * Inserts r into the ordered dictionary. It throws a DictionaryException if
     * a record with the same key as r is already in the dictionary.
     *
     * @param r
     * @throws birds.DictionaryException
     */
    @Override
    public void insert(BirdRecord r) throws DictionaryException {
        // Write this method

        if(root.isEmpty()){
            root = new Node(r); //If the tree is empty, the new record becomes the root
        } else {
            insertRec(root, r);
        }
    }

    public void insertRec(Node current, BirdRecord r) throws DictionaryException {

        DataKey newKey = r.getDataKey();
        DataKey currentKey = current.getData().getDataKey();

        //Compare the new record's key with the current node's key
        int comparison = newKey.compareTo(currentKey);
        if(comparison < 0){

            //New record goes in left subtree
            if(!current.hasLeftChild()){

                current.setLeftChild(new Node(r));
            } else {
                insertRec(current.getLeftChild(), r);
            }
        } else if(comparison > 0){

            //New record goes in right subtree
            if(!current.hasRightChild()){
                current.setRightChild(new Node(r));
            } else {
                insertRec(current.getRightChild(), r);
            }
        } else {
            throw new DictionaryException("A record with the same key already exists.");
        }
    }

    /**
     * Removes the record with Key k from the dictionary. It throws a
     * DictionaryException if the record is not in the dictionary.
     *
     * @param k
     * @throws birds.DictionaryException
     */
    @Override
    public void remove(DataKey k) throws DictionaryException {
        // Write this method
        root = removeRec(root, k);
    }
    
    private Node removeRec(Node current, DataKey k) throws DictionaryException {
        if (current == null) {
            throw new DictionaryException("No record found with the given key to remove.");
        }
    
        int comparison = k.compareTo(current.getData().getDataKey());
        if (comparison < 0) {
            Node newLeftChild = removeRec(current.getLeftChild(), k);
            current.setLeftChild(newLeftChild);
        } else if (comparison > 0) {
            Node newRightChild = removeRec(current.getRightChild(), k);
            current.setRightChild(newRightChild);
        } else {
            // Node to be deleted is found.
            if (!current.hasLeftChild() && !current.hasRightChild()) {
                // Case 1: No children
                return null;
            } else if (!current.hasLeftChild()) {
                // Case 2: One child (right)
                return current.getRightChild();
            } else if (!current.hasRightChild()) {
                // Case 2: One child (left)
                return current.getLeftChild();
            } else {
                // Case 3: Two children
                // Find smallest node in the right subtree
                Node smallestNode = findSmallest(current.getRightChild());
                current.setData(smallestNode.getData());
                Node newRightChild = removeRec(current.getRightChild(), smallestNode.getData().getDataKey());
                current.setRightChild(newRightChild);
            }
        }
        return current;
    }

    private Node findSmallest(Node root) {
        while (root.getLeftChild() != null) {
            root = root.getLeftChild();
        }
        return root;
    }

    /**
     * Returns the successor of k (the record from the ordered dictionary with
     * smallest key larger than k); it returns null if the given key has no
     * successor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws birds.DictionaryException
     */
    @Override
    public BirdRecord successor(DataKey k) throws DictionaryException{
        Node successor = null;
        Node current = root;
        while(current != null){
            if(k.compareTo(current.getData().getDataKey())<0){
                //Current key is larger than k; go left and find a smaller key
                successor = current;
                current = current.getLeftChild();
            }else{
                //Current key is smaller than or equal to k; go right and find a larger key
                current = current.getRightChild();
            }
        }
        return successor == null ? null : successor.getData();
    }

   
    /**
     * Returns the predecessor of k (the record from the ordered dictionary with
     * largest key smaller than k; it returns null if the given key has no
     * predecessor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws birds.DictionaryException
     */
    @Override
    public BirdRecord predecessor(DataKey k) throws DictionaryException{
        Node predecessor = null;
        Node current = root;
        while(current != null){
            if(k.compareTo(current.getData().getDataKey())>0){
                //Current key is smaller than k; this is a potential predecessor but there may be a larger one
                predecessor = current;
                current = current.getRightChild();
            }else{
                //Current key is greater than or equal to k; need to find a smaller key
                current = current.getLeftChild();
            }
        }
        return predecessor == null ? null : predecessor.getData();
    }

    /**
     * Returns the record with smallest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     *
     * @return
     */
    @Override
    public BirdRecord smallest() throws DictionaryException{
        if(root.isEmpty() || root == null){
            return null; //Dictionary is empty
        }
        //Navigate to the leftmost node
        Node current = root;
        while(current.getLeftChild() != null){
            current = current.getLeftChild();
        }
        return current.getData();
    }

    /*
	 * Returns the record with largest key in the ordered dictionary. Returns
	 * null if the dictionary is empty.
     */
    @Override
    public BirdRecord largest() throws DictionaryException{
        if(root.isEmpty() || root == null){
            return null; //Dictionary is empty
        }

        //Navigate to the rightmost node
        Node current = root;
        while(current.getRightChild() != null){
            current = current.getRightChild();
        }
        return current.getData();
    }
      
    /* Returns true if the dictionary is empty, and true otherwise. */
    @Override
    public boolean isEmpty (){
        return root.isEmpty();
    }
}
