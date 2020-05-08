package de.marvin.merkletree;

public interface MerkleNode {

    public byte[] getHash();
    public MerkleNode getLeftChild();
    public MerkleNode getRightChild();
    public MerkleNode getParent();
    public void setParent(MerkleNode parent);

}
