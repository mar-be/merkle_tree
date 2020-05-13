package de.marvin.merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * A MerkleTree stroes data in the LeafNodes and the hash value of the child nodes in the InnerNodes.
 * The Merkle tree also constructs proof that a single leaf is a part of the tree.
 */
public class MerkleTree {
    private ArrayList<LeafNode> leafs =  new ArrayList<>();
    private String hashAlg;
    private MessageDigest md;
    private IMerkleNode root;

    public MerkleTree() throws NoSuchAlgorithmException {
        this.hashAlg = "SHA-256";
        this.md = MessageDigest.getInstance(hashAlg);
        this.root = null;
    }


    public MerkleTree(String hashAlg) throws NoSuchAlgorithmException {
        this.hashAlg = hashAlg;
        this.md = MessageDigest.getInstance(hashAlg);
        this.root = null;
    }


    /**
     * Appends a value to the merkle tree. This is only possible if the tree has not been build before.
     * @param value bytes to append
     */
    public void append(byte[] value){
        if(root != null){
            throw new IllegalStateException();
        }
        leafs.add(new LeafNode(value, md));
    }

    /**
     * Builds up the merkle tree from the appended data. This is only possible if the tree has not been build before.
     */
    public void buildTree(){
        if(root != null){
            throw new IllegalStateException();
        }
        // at the beginning all leafs are roots
        ArrayList<IMerkleNode> roots = new ArrayList<>(leafs);

        // combine roots iteratively until there is only one root left
        while(roots.size() > 1){
            ArrayList<IMerkleNode> newRoots = new ArrayList<>();
            for(int i = 0; i < roots.size()/2; i++){
                final IMerkleNode left = roots.get(2 * i);
                final IMerkleNode right = roots.get(2 * i + 1);
                final InnerNode parent = new InnerNode(this.md, left, right);
                newRoots.add(parent);
                left.setParent(parent);
                right.setParent(parent);
            }

            // if the last element remains because it could not be combined with another node, take it also as a new root
            if(roots.size() % 2 == 1){
                newRoots.add(roots.get(roots.size() - 1));
            }
            roots = newRoots;
        }
        this.root = roots.get(0);
    }

    /**
     * This is only possible if the tree has been build before.
     * @return root node of the merkle tree
     */
    public IMerkleNode getRoot() {
        if(root == null){
            throw new IllegalStateException();
        }
        return root;
    }

    /**
     * @param leafIndex
     * @return leaf node at the given index
     */
    public LeafNode getLeaf(int leafIndex){
        if(leafIndex < 0 || leafIndex>=leafs.size()){
            throw new IndexOutOfBoundsException();
        }
        return leafs.get(leafIndex);
    }

    /**
     * @return the number of data elements which got appended to the merkle tree
     */
    public int getSize(){
        return leafs.size();
    }

    /**
     * @param leafIndex
     * @return merkle proof for the data element which is stored in the given leaf
     */
    public Proof getProof(int leafIndex) {
        if(root == null){
            throw new IllegalStateException();
        }

        if(leafIndex < 0 || leafIndex>=leafs.size()){
            throw new IndexOutOfBoundsException();
        }

        ArrayList<byte[]> proof = new ArrayList<>();

        IMerkleNode node = this.leafs.get(leafIndex);

        // go up the path from the leaf to the root
        while(node.getParent() != null){
            IMerkleNode parent = node.getParent();
            // check if the node is the right or left child of the parent and add the missing hash value to the proof set
            if(parent.getLeftChild() != node){
                proof.add(parent.getLeftChild().getHash());
            } else {
                proof.add(parent.getRightChild().getHash());
            }
            node = parent;
        }
        return new Proof(root.getHash(), proof, leafIndex, this.leafs.size(), this.hashAlg);

    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MerkleTree merkleTree = new MerkleTree();
        merkleTree.append("0".getBytes());
        merkleTree.append("1".getBytes());
        merkleTree.append("2".getBytes());
        merkleTree.append("3".getBytes());
        merkleTree.append("4".getBytes());
        merkleTree.buildTree();
        Proof proof = merkleTree.getProof(0);
        System.out.println(Proof.verify("0".getBytes(), proof));
        System.out.println(Proof.verify("1".getBytes(), proof));
        Proof proof_1 = merkleTree.getProof(1);
        System.out.println(Proof.verify("0".getBytes(), proof_1));
        System.out.println(Proof.verify("1".getBytes(), proof_1));
        Proof proof_2 = merkleTree.getProof(4);
        System.out.println(Proof.verify("0".getBytes(), proof_2));
        System.out.println(Proof.verify("4".getBytes(), proof_2));
        Proof fromBytes = Proof.getFromBytes(proof.asBytes());
        System.out.println(proof.toString());
        System.out.println(fromBytes.toString());
    }
}
