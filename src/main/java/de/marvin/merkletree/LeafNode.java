package de.marvin.merkletree;

import java.security.MessageDigest;

public class LeafNode implements MerkleNode {

    private byte[] value;
    private byte[] hash;
    private MessageDigest md;
    private MerkleNode parent;

    public LeafNode(byte[] value, MessageDigest md){
        this.value = value;
        this.md = md;
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
    public MerkleNode getLeftChild() {
        return null;
    }

    @Override
    public MerkleNode getRightChild() {
        return null;
    }

    @Override
    public MerkleNode getParent() {
        return this.parent;
    }

    @Override
    public void setParent(MerkleNode parent) {
        this.parent = parent;
    }


}

