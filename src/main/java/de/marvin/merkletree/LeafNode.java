package de.marvin.merkletree;

import java.security.MessageDigest;
import java.util.ArrayList;

public class LeafNode implements IMerkleNode {

    private byte[] value;
    private byte[] hash;
    private IMerkleNode parent;
    private ArrayList<byte[]> proof;

    public LeafNode(byte[] value, MessageDigest md){
        this.value = value;
        this.hash = md.digest(value);
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public byte[] getHash() {
        return this.hash;
    }

    @Override
    public IMerkleNode getLeftChild() {
        return null;
    }

    @Override
    public IMerkleNode getRightChild() {
        return null;
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

