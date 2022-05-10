package pl.edu.pw.ee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Huffman {
    private char[] code = new char[1000];
    private int last = 0;

    private void buildCodes(Node t, int level, char[][] codes) {
        if (t == null) {
            return;
        }
        if (t.getLeft() == null && t.getRight() == null) {
            code[level + 1] = '\0';
            int bite = t.getChar();
            for (int i = 0; i <= level; i++) {
                codes[bite][i] = code[i];
            }
            code[level] = '\0';
            codes[bite][level] = code[level];
            codes[bite][level + 1] = '\0';
        }
        code[level] = '0';
        buildCodes(t.getLeft(), level + 1, codes);
        code[level] = '1';
        buildCodes(t.getRight(), level + 1, codes);
    }

    private void heapUp(ArrayList<Node> q, int n) {
        int i = n - 1;
        int p = (i - 1) / 2;
        while (i > 0) {
            if (q.get(p).getFreq() > q.get(i).getFreq()) {
                Node tmp = q.get(p);
                q.set(p, q.get(i));
                q.set(i, tmp);
                i = p;
                p = (i - 1) / 2;
            } else {
                return;
            }
        }
    }

    private void heapDown(ArrayList<Node> q, int n) {
        int i = 0;
        int p = 2 * i + 1;

        while (p < n) {
            if (p + 1 < n && q.get(p + 1).getFreq() < q.get(p).getFreq())
                p++;
            if (q.get(i).getFreq() < q.get(p).getFreq())
                return;
            else {
                Node tmp = q.get(i);
                q.set(i, q.get(p));
                q.set(p, tmp);
                i = p;
                p = 2 * i + 1;
            }
        }
    }

    private int codeFile(char[] characters, char[][] codes, BufferedWriter writeCompresedFile) throws IOException {
        char buffer = 0x0;
        int actualLength = 0;
        int countOfBits = 0;
        for (int i = 0; i < characters.length; i++) {
            char z = characters[i];
            for (int j = 0; j < codes[z].length; j++) {
                if (codes[z][j] == '0') {
                    buffer <<= 1;
                    actualLength++;
                    countOfBits++;
                    countOfBits = checkToHighIntValue(countOfBits);
                }
                if (codes[z][j] == '1') {
                    buffer <<= 1;
                    buffer |= 1;
                    actualLength++;
                    countOfBits++;
                    countOfBits = checkToHighIntValue(countOfBits);
                }
                if (actualLength == 15) {
                    writeCompresedFile.write(buffer);
                    actualLength = 0;
                    buffer = 0x0;
                }
            }
        }
        writeCompresedFile.write(buffer);
        last = actualLength;
        return countOfBits;
    }

    private int decodeFile(BufferedReader reader, Node root, BufferedWriter writeDecompresedFile, File file,
            int lastChar)
            throws IOException {
        Node node = root;
        char iter = 0x4000;
        int decodeChars = 0;

        int a;
        ArrayList<Character> arrayOfChar = new ArrayList<>();
        while ((a = reader.read()) != -1) {
            arrayOfChar.add((char) a);
        }

        for (int t = 0; t < arrayOfChar.size() - 1; t++) {
            for (int j = 0; j < 15; j++) {
                boolean goRight = (((char) arrayOfChar.get(t)) & iter) != 0 ? true : false;
                iter = (char) (iter / 2);
                if (!goRight) {
                    node = node.getLeft();
                } else {
                    node = node.getRight();
                }
                if (node.getLeft() == null && node.getRight() == null) {
                    writeDecompresedFile.write((char) node.getChar());
                    node = root;
                    decodeChars++;
                    decodeChars = checkToHighIntValue(decodeChars);
                }
            }

            iter = 0x4000;
        }

        for (int i = 0; i < 15 - lastChar; i++) {
            iter = (char) (iter / 2);
        }
        for (int j = 0; j < lastChar; j++) {
            boolean goRight = (((char) arrayOfChar.get(arrayOfChar.size() - 1)) & iter) != 0 ? true : false;
            iter = (char) (iter / 2);
            if (!goRight) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
            if (node.getLeft() == null && node.getRight() == null) {
                writeDecompresedFile.write((char) node.getChar());
                node = root;
                decodeChars++;
                decodeChars = checkToHighIntValue(decodeChars);
            }
        }
        return decodeChars;
    }

    private void buildHuffmanTree(int n, ArrayList<Node> pq) {
        while (n > 1) {
            Node w1 = pq.get(0);
            pq.set(0, pq.get(--n));
            heapDown(pq, n);
            Node w2 = pq.get(0);
            pq.set(0, pq.get(--n));
            heapDown(pq, n);
            Node nowy = new Node(-1, w1.getFreq() + w2.getFreq(), w1, w2);
            pq.add(n++, nowy);
            heapUp(pq, n);
        }
    }

    private int checkToHighIntValue(int value) {
        if (value == Integer.MAX_VALUE) {
            value = value % 1000000000;
            System.out.println("Your value was too high and has been modulo by 1000000000");
        }
        return value;
    }

    public int huffman(String pathToRootDir, boolean compress) throws IOException {
        if (pathToRootDir == null)
            throw new IllegalArgumentException("Path to file is null");

        int[] occurencyArray = new int[256];
        int countOfBits = 0;
        int countOfDecodeChars = 0;

        File file = new File(pathToRootDir + "/niemanie.txt");

        File compressFile = new File(pathToRootDir + "/compresedFile.txt");

        if (compress) {
            File fileDicionary = new File(pathToRootDir + "/dicionary.txt");
            BufferedWriter writeCompresedFile = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(compressFile), StandardCharsets.UTF_8));
            BufferedWriter writeDicionary = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileDicionary), StandardCharsets.UTF_8));

            ArrayList<Node> pq = new ArrayList<>();
            int n = 0;
            char[] characters = null;
            FileReader fileReader;
            try {
                fileReader = new FileReader(file);
                characters = new char[(int) file.length()];
                fileReader.read(characters);
            } catch (Exception e) {
                e.printStackTrace();
                writeCompresedFile.close();
                writeDicionary.close();
                throw new IllegalArgumentException("File to compress is incorrect");

            }
            for (int i = 0; i < characters.length; i++) {
                int z = characters[i];
                if (z > 255) {
                    writeCompresedFile.close();
                    writeDicionary.close();
                    fileReader.close();
                    throw new IllegalArgumentException("File should not contain Polish letters");
                }
                occurencyArray[z]++;
            }

            fileReader.close();

            for (int i = 0; i < 256; i++) {
                if (occurencyArray[i] > 0) {
                    writeDicionary.write(i + ":" + occurencyArray[i] + ":\n");
                    Node nowy = new Node(i, occurencyArray[i]);
                    pq.add(n++, nowy);
                    heapUp(pq, n);
                }
            }
            buildHuffmanTree(n, pq);

            char[][] codes = new char[256][20];
            buildCodes(pq.get(0), 0, codes);

            countOfBits = codeFile(characters, codes, writeCompresedFile);
            writeDicionary.write(last + "");
            writeDicionary.close();
            writeCompresedFile.close();
        } else {
            BufferedReader readCompresedFile = new BufferedReader(
                    new InputStreamReader(new FileInputStream(compressFile), StandardCharsets.UTF_8));
            BufferedWriter writeDecompresedFile = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(pathToRootDir + "/decompresedFile.txt"),
                            StandardCharsets.UTF_8));
            BufferedReader readDicionary = new BufferedReader(new FileReader(pathToRootDir + "/dicionary.txt"));

            int lastChar = 0;

            for (String line = readDicionary.readLine(); line != null; line = readDicionary.readLine()) {
                if (line.contains(":")) {
                    int c = Integer.parseInt(line.split(":")[0]);
                    int val = Integer.parseInt(line.split(":")[1]);
                    occurencyArray[c] = val;
                } else {
                    lastChar = Integer.parseInt(line);
                }
            }
            readDicionary.close();

            ArrayList<Node> pq = new ArrayList<>();
            int n = 0;

            for (int i = 0; i < 256; i++) {
                if (occurencyArray[i] > 0) {
                    Node nowy = new Node(i, occurencyArray[i]);
                    pq.add(n++, nowy);
                    heapUp(pq, n);
                }

            }
            buildHuffmanTree(n, pq);

            countOfDecodeChars = decodeFile(readCompresedFile, pq.get(0), writeDecompresedFile, compressFile, lastChar);
            readCompresedFile.close();
            writeDecompresedFile.close();
        }
        return countOfBits == 0 ? countOfDecodeChars : countOfBits;
    }
}
