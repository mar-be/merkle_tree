package de.marvin.merkletree;

/**
 * Node in the merkle tree
 */
public interface IMerkleNode {

    public byte[] getHash();
    public IMerkleNode getLeftChild();
    public IMerkleNode getRightChild();
    public IMerkleNode getParent();
    public boolean setParent(IMerkleNode parent);

}
