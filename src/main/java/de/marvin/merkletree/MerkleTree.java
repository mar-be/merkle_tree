package de.marvin.merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MerkleTree {
    private ArrayList<LeafNode> leafs =  new ArrayList<>();
    private String hashAlg;
    private MessageDigest md;
    private MerkleNode root;

    public MerkleTree() throws NoSuchAlgorithmException {
        this.hashAlg = "SHA-256";
        this.md = md.getInstance(hashAlg);
        this.root = null;
    }


    public MerkleTree(String hashAlg) throws NoSuchAlgorithmException {
        this.hashAlg = hashAlg;
        this.md = md.getInstance(hashAlg);
        this.root = null;
    }



    public void append(byte[] value){
        if(root != null){
            throw new IllegalStateException();
        }
        leafs.add(new LeafNode(value, md));
    }

    public void buildTree(){
        if(root != null){
            throw new IllegalStateException();
        }
        ArrayList<MerkleNode> roots = new ArrayList<>();
        roots.addAll(leafs);
        while(roots.size() > 1){
            ArrayList<MerkleNode> newRoots = new ArrayList<>();
            for(int i = 0; i < roots.size()/2; i++){
                final MerkleNode left = roots.get(2 * i);
                final MerkleNode right = roots.get(2 * i + 1);
                final InnerNode parent = new InnerNode(this.md, left, right);
                newRoots.add(parent);
                left.setParent(parent);
                right.setParent(parent);
            }
            if(roots.size() % 2 == 1){
                newRoots.add(roots.get(roots.size() - 1));
            }
            roots = newRoots;
        }
        this.root = roots.get(0);
    }

    public MerkleNode getRoot() {
        return root;
    }


    public Proof getProof(int leafIndex) throws NoSuchAlgorithmException {
        ArrayList<byte[]> proof = new ArrayList<>();

        MerkleNode node = this.leafs.get(leafIndex);
        while(node.getParent() != null){
            MerkleNode parent = node.getParent();
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
