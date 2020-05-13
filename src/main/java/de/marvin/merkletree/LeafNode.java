package de.marvin.merkletree;

import java.security.MessageDigest;

public class LeafNode implements IMerkleNode {

    private byte[] value;
    private byte[] hash;
    private IMerkleNode parent;

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


}

