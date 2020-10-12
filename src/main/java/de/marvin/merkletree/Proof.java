package de.marvin.merkletree;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Stores all relevant information for a merkle proof and provides the functionality to verify a proof and to convert it to a byte array.
 */
public class Proof {
    private byte[] rootHash;

    /**
     * The proofSet contains all relevant hash values for the proof
     */
    private ArrayList<byte[]> proofSet;

    private int leafIndex;
    private int leafSize;
    private String hashAlg;

    public Proof(byte[] rootHash, ArrayList<byte[]> proofSet, int leafIndex, int leafSize, String hashAlg){
        this.rootHash = rootHash;
        this.proofSet = proofSet;
        this.leafIndex = leafIndex;
        this.leafSize = leafSize;
        this.hashAlg = hashAlg;
    }

    /**
     * Creates a byte array which contains all information of the proof.
     * The byte array is constructed in the following way:
     * hash length (int) | proofSet Size (int) | leafIndex (int) | leafSize (int) | rootHash (byte[]) | proofSet (byte[]) | hashAlgorithm (String)
     * @return byte array
     */
    public byte[] asBytes(){
        ByteBuffer bb = ByteBuffer.allocate((proofSet.size() + 1) * rootHash.length + 4 * Integer.BYTES + hashAlg.length());
        bb.putInt(rootHash.length);
        bb.putInt(proofSet.size());
        bb.putInt(leafIndex);
        bb.putInt(leafSize);
        bb.put(rootHash);
        for(byte[] hash: proofSet){
            bb.put(hash);
        }
        bb.put(hashAlg.getBytes(StandardCharsets.UTF_8));
        return bb.array();
    }

    public byte[] getRootHash() {
        return rootHash;
    }

    public String getHashAlg() {
        return hashAlg;
    }

    @Override
    public String toString() {
        return "Proof{" +
                "rootHash=" + Arrays.toString(rootHash) +
                ", proofSet=" + proofSet +
                ", leafIndex=" + leafIndex +
                ", leafSize=" + leafSize +
                ", hashAlg='" + hashAlg + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Proof proof = (Proof) o;
        if(this.proofSet.size() != proof.proofSet.size()){
            return false;
        }
        for(int i = 0; i<this.proofSet.size(); i++){
            if(!Arrays.equals(this.proofSet.get(i), proof.proofSet.get(i))){
                return false;
            }
        }
        return leafIndex == proof.leafIndex &&
                leafSize == proof.leafSize &&
                Arrays.equals(rootHash, proof.rootHash) &&
                hashAlg.equals(proof.hashAlg);
    }

    /**
     * Restores a proof from a byte array which was created with {@link #asBytes()}
     * @param proof byte array which contains the proof
     * @return proof
     */
    public static Proof getFromBytes(byte[] proof){
        ByteBuffer bb = ByteBuffer.wrap(proof);
        int hashLength = bb.getInt();
        int proofSetSize = bb.getInt();
        int leafIndex = bb.getInt();
        int leafSize = bb.getInt();
        int position = bb.position();
        byte[] rootHash = Arrays.copyOfRange(proof, position, position+hashLength);

        ArrayList<byte[]> proofSet = new ArrayList<>();
        for(int i = 0; i < proofSetSize; i++){
            position += hashLength;
            proofSet.add(Arrays.copyOfRange(proof, position, position+hashLength));
        }
        position += hashLength;
        String hashAlg = new String(Arrays.copyOfRange(proof, position, proof.length), StandardCharsets.UTF_8);

        return new Proof(rootHash, proofSet, leafIndex, leafSize, hashAlg);
    }

    /**
     * Evaluates a merkle proof with the given data and verifies if the data belongs to the corresponding merkle tree
     * @param data
     * @param proof
     * @return true iff data fulfills the merkle proof
     * @throws NoSuchAlgorithmException
     */
    public static boolean verify(byte[] data, Proof proof) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(proof.hashAlg);
        byte[] currentHash = md.digest(data);
        byte[] combinedChildHashes = new byte[2*md.getDigestLength()];
        int index = proof.leafIndex;
        int leafSize = proof.leafSize;
        for(byte[] hash: proof.proofSet){
            if(index == leafSize - 1 || index % 2 == 1){
                // currentHash is the right child and the hash from the proofSet is the left child
                System.arraycopy(hash, 0, combinedChildHashes, 0, hash.length);
                System.arraycopy(currentHash, 0, combinedChildHashes, md.getDigestLength(), currentHash.length);
            } else {
                // currentHash is the left child and the hash from the proofSet is the right child
                System.arraycopy(currentHash, 0, combinedChildHashes, 0, currentHash.length);
                System.arraycopy(hash, 0, combinedChildHashes, md.getDigestLength(), hash.length);
            }
            currentHash = md.digest(combinedChildHashes);
            leafSize = leafSize / 2 + leafSize % 2;
            index = index / 2;
        }
        return MessageDigest.isEqual(proof.rootHash, currentHash);
    }
}
