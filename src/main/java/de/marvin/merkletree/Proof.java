package de.marvin.merkletree;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Proof {
    private byte[] rootHash;
    private ArrayList<byte[]> proofSet;
    private int leafIndex;
    private int leafSize;
    private String hashAlg;

    public Proof(byte[] rootHash, ArrayList<byte[]> proofSet, int leafIndex, int leafSize, String hashAlg) throws NoSuchAlgorithmException {
        this.rootHash = rootHash;
        this.proofSet = proofSet;
        this.leafIndex = leafIndex;
        this.leafSize = leafSize;
        this.hashAlg = hashAlg;
    }

    public byte[] asBytes(){
        ByteBuffer bb = ByteBuffer.allocate((proofSet.size() + 1) * rootHash.length + 3 * Integer.BYTES + hashAlg.length());
        bb.putInt(proofSet.size());
        bb.put(rootHash);
        for(byte[] hash: proofSet){
            bb.put(hash);
        }
        bb.putInt(leafIndex);
        bb.putInt(leafSize);
        bb.put(hashAlg.getBytes());
        return bb.array();
    }

    public static boolean verify(byte[] data, Proof proof) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(proof.hashAlg);
        byte[] currentHash = md.digest(data);
        byte[] combinedChildHashes = new byte[2*md.getDigestLength()];
        int index = proof.leafIndex;
        int leafSize = proof.leafSize;
        for(byte[] hash: proof.proofSet){
            if(index == leafSize - 1 || index % 2 == 1){
                System.arraycopy(hash, 0, combinedChildHashes, 0, hash.length);
                System.arraycopy(currentHash, 0, combinedChildHashes, md.getDigestLength(), currentHash.length);
            } else {
                System.arraycopy(currentHash, 0, combinedChildHashes, 0, currentHash.length);
                System.arraycopy(hash, 0, combinedChildHashes, md.getDigestLength(), hash.length);
            }
            currentHash = md.digest(combinedChildHashes);
            leafSize = leafSize/2 + leafSize % 2;
            index = index / 2;
        }
        return MessageDigest.isEqual(proof.rootHash, currentHash);
    }
}
