package de.marvin.merkletree;

import java.security.MessageDigest;

public class InnerNode implements MerkleNode {

    private MessageDigest md;
    private byte[] hash;
    private MerkleNode leftChild;
    private MerkleNode rightChild;
    private MerkleNode parent;

    public InnerNode(MessageDigest md, MerkleNode leftChild, MerkleNode rightChild) {
        this.md =  md;
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
    public MerkleNode getLeftChild() {
        return this.leftChild;
    }

    @Override
    public MerkleNode getRightChild() {
        return this.rightChild;
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
