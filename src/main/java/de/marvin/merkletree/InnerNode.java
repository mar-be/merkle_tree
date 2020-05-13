package de.marvin.merkletree;

import java.security.MessageDigest;
import java.util.ArrayList;

public class InnerNode implements IMerkleNode {

    private byte[] hash;
    private IMerkleNode leftChild;
    private IMerkleNode rightChild;
    private IMerkleNode parent;
    private ArrayList<byte[]> proof;

    public InnerNode(MessageDigest md, IMerkleNode leftChild, IMerkleNode rightChild) {
        byte[] combinedChildHashes = new byte[2*md.getDigestLength()];
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        byte[] leftHash = this.leftChild.getHash();
        byte[] rightHash = this.rightChild.getHash();

        System.arraycopy(leftHash, 0, combinedChildHashes, 0, leftHash.length);
        System.arraycopy(rightHash, 0, combinedChildHashes, md.getDigestLength(), rightHash.length);

        this.hash = md.digest(combinedChildHashes);
    }

    @Override
    public byte[] getHash() {
        return this.hash;
    }

    @Override
    public IMerkleNode getLeftChild() {
        return this.leftChild;
    }

    @Override
    public IMerkleNode getRightChild() {
        return this.rightChild;
    }

    @Override
    public IMerkleNode getParent() {
        return this.parent;
    }

    @Override
    public boolean setParent(IMerkleNode parent) {
        if(this.parent == null) {
            this.parent = parent;
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<byte[]> getProof() {
        return proof;
    }

    @Override
    public boolean setProof(ArrayList<byte[]> proof) {
        if(this.proof == null){
            this.proof = proof;
            return true;
        }
        return false;
    }
}
