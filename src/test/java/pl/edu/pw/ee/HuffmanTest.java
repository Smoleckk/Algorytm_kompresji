package pl.edu.pw.ee;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class HuffmanTest {
  Huffman h = new Huffman();

  @Test
  public void check_Huffman() throws IOException {
    Huffman h = new Huffman();
    h.huffman("pathToRoot/file", false);
  }

  @Test
  public void should_throwException_WhenCatalogIsNull() throws IOException {
    try {
      h.huffman(null, true);
    } catch (Exception e) {
      assertEquals("Path to file is null", e.getMessage());
    }
  }

  @Test
  public void should_throwException_WhenFileContainPolishLetters() throws IOException {

    try {
      h.huffman("pathToRoot/fileUsedInTests", true);

    } catch (Exception e) {
      assertEquals("File should not contain Polish letters", e.getMessage());
    }
  }

  @Test
  public void should_throwException_WhenFileToCompressIsIncorrect() throws IOException {

    try {
      h.huffman("pathToRoot/fileUsedInTests/notExistFileToCodeInside", true);

    } catch (Exception e) {
      assertEquals("File to compress is incorrect", e.getMessage());
    }
  }

  @Test
  public void should_DecompressedTextHaveCorrectlyLength() throws IOException {
    String pathToRootDir = "pathToRoor";

    File file = new File(pathToRootDir + "/niemanie.txt");

    File filee = new File(pathToRootDir + "/decompresedFile.txt");
    assertEquals(file.length(), filee.length());
  }


}
