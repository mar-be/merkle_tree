package de.marvin.merkletree;

import java.util.ArrayList;

/**
 * Node in the merkle tree
 */
public interface IMerkleNode {

    public byte[] getHash();
    public IMerkleNode getLeftChild();
    public IMerkleNode getRightChild();
    public IMerkleNode getParent();
    public boolean setParent(IMerkleNode parent);
    public ArrayList<byte[]> getProof();
    public boolean setProof(ArrayList<byte[]> proof);
}
