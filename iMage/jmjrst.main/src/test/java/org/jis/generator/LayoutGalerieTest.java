package org.jis.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

public class LayoutGalerieTest {
	
	private LayoutGalerie galerieUnderTest;
	private File resourceFolder;
	private File fromFile;
	private File toFile;

	private Path fromPath;
	private Path toPath;

	/**
	 * Initializes test object.
	 * @throws URISyntaxException
	 */
	@Before
	public final void prepareTestResources() throws URISyntaxException {
		galerieUnderTest = new LayoutGalerie(null, null);
		resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
		fromFile = new File(resourceFolder, "from");
		toFile = new File(resourceFolder, "to");

		fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
		toPath = FileSystems.getDefault().getPath(toFile.getPath());

	}
		
	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyFile() throws URISyntaxException {

		try {
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);

			Files.writeString(fromPath, randomString);
			galerieUnderTest.copyFile(fromFile, toFile);

			assertTrue(toFile.exists());

			String contents = Files.readString(toPath);
			 		 
			assertEquals(randomString, contents);

		 }
		 catch (IOException e) {
			fail();
		 }
		
	}

	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyToNonExistentFile() throws URISyntaxException, FileNotFoundException {

		try {

			toFile.delete();
			galerieUnderTest.copyFile(fromFile, toFile);


		}
		catch (IOException e) {
			//throw new FileNotFoundException();
		}

	}

	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyFromNonExistentFile() throws URISyntaxException, FileNotFoundException {

		try {

			fromFile.delete();
			galerieUnderTest.copyFile(fromFile, toFile);

		}
		catch (IOException e) {
			//throw new FileNotFoundException();
		}

	}

	/**
	 * Frees test object reference pointer.
	 */
	@After
	public final void destroyTestResources(){
		galerieUnderTest = null;
		resourceFolder = null;

		fromFile.delete();
		toFile.delete();
		fromFile = null;
		toFile = null;

		fromPath = null;
		toPath = null;
	}

}
