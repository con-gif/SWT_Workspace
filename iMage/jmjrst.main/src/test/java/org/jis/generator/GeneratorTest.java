package org.jis.generator;

import org.junit.*;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import static org.junit.Assert.*;

public class GeneratorTest {
  /**
   * Class under test.
   */
  private Generator generator;

  private int imageHeight, imageWidth;
  private static final File TEST_DIR = new File("target/test");
  private static final String IMAGE_FILE = "/image.jpg";
  private String imageName;

  private File input = new File("target/test");
  private File output = new File("target/test");


  /**
   * Input for test cases
   */
  private BufferedImage testImage;
  /**
   * Metadata for saving the image
   */
  private IIOMetadata imeta;
  /**
   * output from test cases
   */
  private BufferedImage rotatedImageTestResult;

  /**
   * Sicherstellen, dass das Ausgabeverzeichnis existiert und leer ist.
   */
  @BeforeClass
  public static void beforeClass() {
    if (TEST_DIR.exists()) {
      for (File f : TEST_DIR.listFiles()) {
        f.delete();
      }
    } else {
      TEST_DIR.mkdirs();
    }
  }

  @Before
  public void setUp() {
    this.generator = new Generator(null, 0);

    this.testImage = null;
    this.imeta = null;
    this.rotatedImageTestResult = null;
    
    final URL imageResource = this.getClass().getResource(IMAGE_FILE);
    imageName = extractFileNameWithoutExtension(new File(imageResource.getFile()));
   
    try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
      ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
      reader.setInput(iis, true);
      ImageReadParam params = reader.getDefaultReadParam();
      this.testImage = reader.read(0, params);
      this.imageHeight = this.testImage.getHeight();
      this.imageWidth = this.testImage.getWidth();
      this.imeta = reader.getImageMetadata(0);
      reader.dispose();
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  private String extractFileNameWithoutExtension(File file) {
    String fileName = file.getName();
    if (fileName.indexOf(".") > 0) {
      return fileName.substring(0, fileName.lastIndexOf("."));
    } else {
      return fileName;
    }
  }

  /**
   * Automatisches Speichern von testImage.
   */
  @After
  public void tearDown() {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH.mm.ss.SSS");
    String time = sdf.format(new Date());

    File outputFile = new File(
        MessageFormat.format("{0}/{1}_rotated_{2}.jpg", TEST_DIR, imageName, time));

    if (this.rotatedImageTestResult != null) {
      try (FileOutputStream fos = new FileOutputStream(outputFile);
           ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(ios);

        ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // mode explicit necessary

        // set JPEG Quality
        iwparam.setCompressionQuality(1f);
        writer.write(this.imeta, new IIOImage(this.rotatedImageTestResult, null, null), iwparam);
        writer.dispose();
      } catch (IOException e) {
        fail();
      }
    }
  }

  @Test
  public void testRotateImage_RotateImage0() {
    this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, 0);

    assertTrue(imageEquals(this.testImage, this.rotatedImageTestResult));
  }

  @Test
  public void testRotateImage_RotateNull0() {
    this.rotatedImageTestResult = this.generator.rotateImage(null, 0);

    assertNull(this.rotatedImageTestResult);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRotateImage_Rotate042() {
    this.generator.rotateImage(this.testImage, 0.42);
  }

  @Test
  public void testRotateImage_Rotate90() {
    this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Generator.ROTATE_90);

    assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
    assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

    for (int i = 0; i < this.imageHeight; i++) {
      for (int j = 0; j < this.imageWidth; j++) {
        assertEquals(this.testImage.getRGB(j, i), this.rotatedImageTestResult.getRGB(this.imageHeight - 1 - i, j));
      }
    }
  }

  @Test
  public void testRotateImage_Rotate270() {
    this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Generator.ROTATE_270);

    assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
    assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

    for (int i = 0; i < this.imageHeight; i++) {
      for (int j = 0; j < this.imageWidth; j++) {
        assertEquals(this.testImage.getRGB(j, i), this.rotatedImageTestResult.getRGB(i, this.imageWidth - 1 - j));
      }
    }
  }

  @Test
  public void testRotateImage_RotateM90() {
    this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Generator.ROTATE_M90);

    assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
    assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

    for (int i = 0; i < this.imageHeight; i++) {
      for (int j = 0; j < this.imageWidth; j++) {
        assertEquals(this.testImage.getRGB(j, i), this.rotatedImageTestResult.getRGB(i, this.imageWidth - 1 - j));
      }
    }
  }

  @Test
  public void testRotateImage_RotateM270() {
    this.rotatedImageTestResult = this.generator.rotateImage(this.testImage, Generator.ROTATE_M270);

    assertEquals(this.testImage.getHeight(), this.rotatedImageTestResult.getWidth());
    assertEquals(this.testImage.getWidth(), this.rotatedImageTestResult.getHeight());

    for (int i = 0; i < this.imageHeight; i++) {
      for (int j = 0; j < this.imageWidth; j++) {
        assertEquals(this.testImage.getRGB(j, i), this.rotatedImageTestResult.getRGB(this.imageHeight - 1 - i, j));
      }
    }
  }

  @Ignore
  public void testCreateZIP() {
    File zipFile = new File("target/test");
    File imageFile = new File("target/test");
    try {
      Vector<File> fileVector = new Vector<>();
      ImageIO.write(testImage, "jpg", imageFile);
      fileVector.add(imageFile);
      this.generator.createZip(zipFile, fileVector);
      assertEquals(zipFile.getAbsolutePath().substring(zipFile.getAbsolutePath().length() - 4), "zip");
    } catch (IOException e) {
      imageFile.delete();
      zipFile.delete();
      fail();
    }
  }

  @Test
  public void testGenerateTrue() {
    this.generator.generate(true);
    assertTrue(TEST_DIR.isDirectory());
  }

  @Test
  public void testGenerateFalse() {
    this.generator.generate(false);
    assertTrue(TEST_DIR.isDirectory());
  }

  @Ignore
  public void testRotate() {
    this.generator.rotate(input);
    assertTrue(input.isDirectory());
  }

  @Ignore
  public void testGenerateSingle() {
    File imageFile = new File("target/test");
    try {
      ImageIO.write(testImage, "jpg", imageFile);
      this.generator.generateSingle(imageFile, testImage);
    } catch (IOException e) {
      imageFile.delete();
      fail();
    }
  }

  /**
   * Check if two images are identical - pixel wise.
   *
   * @param expected
   *          the expected image
   * @param actual
   *          the actual image
   * @return true if images are equal, false otherwise.
   */
  protected static boolean imageEquals(BufferedImage expected, BufferedImage actual) {
    if (expected == null || actual == null) {
      return false;
    }

    if (expected.getHeight() != actual.getHeight()) {
      return false;
    }

    if (expected.getWidth() != actual.getWidth()) {
      return false;
    }

    for (int i = 0; i < expected.getHeight(); i++) {
      for (int j = 0; j < expected.getWidth(); j++) {
        if (expected.getRGB(j, i) != actual.getRGB(j, i)) {
          return false;
        }
      }
    }

    return true;
  }
}
