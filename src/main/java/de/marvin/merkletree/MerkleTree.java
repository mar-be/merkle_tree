package de.marvin.merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MerkleTree {
    private ArrayList<LeafNode> leafs =  new ArrayList<>();
    private MessageDigest md;
    private MerkleNode root;


    public MerkleTree(String hashAlg) throws NoSuchAlgorithmException {
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

    public ArrayList<byte[]> getProof(int leafIndex){
        ArrayList<byte[]> proof = new ArrayList<>();

        MerkleNode node = leafs.get(leafIndex);
        if(node.getParent() == null){
            proof.add(node.getHash());
        }
        while(node.getParent() != null){
            node = node.getParent();
            proof.add(node.getLeftChild().getHash());
            proof.add(node.getRightChild().getHash());
        }
        return proof;

    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MerkleTree merkleTree = new MerkleTree("SHA-256");
        merkleTree.append("0".getBytes());
        merkleTree.append("1".getBytes());
        merkleTree.append("2".getBytes());
        merkleTree.append("3".getBytes());
        merkleTree.append("4".getBytes());
        merkleTree.buildTree();
        System.out.println(merkleTree.getProof(0));
        System.out.println(merkleTree.getProof(2));
    }
}
