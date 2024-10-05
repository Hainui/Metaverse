package com.metaverse.common.Utils;

import lombok.Data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

public class SimpleBlockchain {

    // 区块类
    @Data
    static class Block {
        private String hash;
        private String previousHash;
        private long timestamp;
        private String data;
        private int nonce;

        public Block(String previousHash, String data) {
            this.previousHash = previousHash;
            this.data = data;
            this.timestamp = new Date().getTime();
            this.hash = calculateHash();
            mineBlock(4);
        }

        private void mineBlock(int difficulty) {
            String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty * '0'
            while (!hash.substring(0, difficulty).equals(target)) {
                nonce++;
                hash = calculateHash();
            }
            System.out.println("Block mined: " + hash);
        }

        private String calculateHash() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String dataToHash = previousHash + Long.toString(timestamp) + Integer.toString(nonce) + data;
                byte[] hash = digest.digest(dataToHash.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public String getHash() {
            return hash;
        }

        public String getData() {
            return data;
        }
    }

    // 区块链类
    static class Blockchain {
        private List<Block> chain;
        private int difficulty;

        public Blockchain(int difficulty) {
            this.chain = initializeChain();
            this.difficulty = difficulty;
        }

        private List<Block> initializeChain() {
            // 创建创世区块
            Block genesisBlock = new Block("", "Genesis Block");
            List<Block> chain = new java.util.ArrayList<>();
            chain.add(genesisBlock);
            return chain;
        }

        public void addBlock(Block block) {
            block.setPreviousHash(chain.get(chain.size() - 1).getHash());
            block.mineBlock(difficulty);
            chain.add(block);
        }

        public boolean isChainValid() {
            for (int i = 1; i < chain.size(); i++) {
                Block currentBlock = chain.get(i);
                Block previousBlock = chain.get(i - 1);

                if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                    System.out.println("Current Hashes not equal");
                    return false;
                }
                if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                    System.out.println("Previous Hashes not equal");
                    return false;
                }
            }
            return true;
        }

        public void printBlocks() {
            for (Block block : chain) {
                System.out.println("Timestamp : " + block.timestamp);
                System.out.println("Data : " + block.data);
                System.out.println("Hash : " + block.hash);
                System.out.println("Nonce : " + block.nonce);
                System.out.println("Previous Hash : " + block.previousHash);
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain(4);
        blockchain.addBlock(new Block(blockchain.chain.get(blockchain.chain.size() - 1).getHash(), "Second Block"));
        blockchain.addBlock(new Block(blockchain.chain.get(blockchain.chain.size() - 1).getHash(), "Third Block"));

        blockchain.printBlocks();
        System.out.println("Is the blockchain valid? " + blockchain.isChainValid());
    }
}