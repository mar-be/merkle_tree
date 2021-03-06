package de.marvin.merkletree;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import static org.junit.Assert.*;

public class MerkleTreeTest {

    @Test
    public void randomTreeDataSize1() throws NoSuchAlgorithmException {
        for(int numOfLeafs = 1; numOfLeafs <= 1024; numOfLeafs++){
            randomTreeProofs(numOfLeafs, 1);
        }
    }

    @Test
    public void randomTreeDataSize8() throws NoSuchAlgorithmException {
        for(int numOfLeafs = 1; numOfLeafs <= 1024; numOfLeafs++){
            randomTreeProofs(numOfLeafs, 8);
        }
    }

    @Test
    public void randomTreeDataSize64() throws NoSuchAlgorithmException {
        for(int numOfLeafs = 1; numOfLeafs <= 1024; numOfLeafs++){
            randomTreeProofs(numOfLeafs, 64);
        }
    }

    @Test
    public void randomTreeDataSize512() throws NoSuchAlgorithmException {
        for(int numOfLeafs = 1; numOfLeafs <= 1024; numOfLeafs++){
            randomTreeProofs(numOfLeafs, 512);
        }
    }

    @Test
    public void byteSerializationTest() throws NoSuchAlgorithmException {
        MerkleTree merkleTree = new MerkleTree();
        Random random = new Random();
        int leafs = 64;
        for(int i = 0; i < leafs; i++){
            byte[] randomBytes = new byte[64];
            random.nextBytes(randomBytes);
            merkleTree.append(randomBytes);
        }
        merkleTree.buildTree();
        for(int i = 0; i < leafs; i++){
            Proof proof = merkleTree.getProof(i);
            Proof proofFromBytes = Proof.getFromBytes(proof.asBytes());
            assertEquals(proof, proofFromBytes);
        }

    }


    private void randomTreeProofs(int leafs, int dataSize) throws NoSuchAlgorithmException{
        MerkleTree merkleTree = new MerkleTree();
        ArrayList<byte[]> data = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < leafs; i++){
            byte[] randomBytes = new byte[dataSize];
            random.nextBytes(randomBytes);
            merkleTree.append(randomBytes);
            data.add(randomBytes);
        }
        merkleTree.buildTree();
        for(int i = 0; i < leafs; i++){
            assertTrue(Proof.verify(data.get(i), merkleTree.getProof(i)));
            byte[] flipped = flipBits(data.get(i));
            assertFalse(Proof.verify(flipped, merkleTree.getProof(i)));
        }
    }

    private byte[] flipBits(byte[] bytes){
        BitSet set = BitSet.valueOf(bytes);
        set.flip(0, set.length());
        return set.toByteArray();
    }
}