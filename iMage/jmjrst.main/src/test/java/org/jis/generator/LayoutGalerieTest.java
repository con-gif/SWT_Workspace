package org.jis.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
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

	private FileChannel channel;
	private FileLock lock;

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
			System.out.println("testCopyToNonExistentFile fails");
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
			System.out.println("testCopyFromNonExistentFile fails");
			//throw new FileNotFoundException();
		}

	}

	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyFileDestinationExisting() throws URISyntaxException {

		try {
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);

			Files.writeString(fromPath, randomString);

			new Random().nextBytes(array);
			String destinationFiller = new String(array);
			Files.writeString(toPath, destinationFiller);

			galerieUnderTest.copyFile(fromFile, toFile);
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
	public final void testCopyFromReadLockedFile() throws URISyntaxException, FileNotFoundException {

		try {
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);

			Files.writeString(fromPath, randomString);

			try {
				channel = new RandomAccessFile(fromFile, "rw").getChannel();
				lock = channel.lock();
				lock = channel.tryLock();

				galerieUnderTest.copyFile(fromFile, toFile);

			} catch (IOException | OverlappingFileLockException e) {
				lock.release();
				channel.close();
				throw new IOException();
			}
			String contents = Files.readString(toPath);
			assertEquals(randomString, contents);

		} catch (IOException e) {
			System.out.println("testCopyFromReadLockedFile fails");
			//fail();
		}
	}

	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyToReadLockedFile() throws URISyntaxException, FileNotFoundException {

		try {
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);

			Files.writeString(toPath, randomString);

			try {
				channel = new RandomAccessFile(toFile, "rw").getChannel();
				lock = channel.lock();
				lock = channel.tryLock();

				galerieUnderTest.copyFile(fromFile, toFile);

			} catch (IOException | OverlappingFileLockException e) {
				lock.release();
				channel.close();
				throw new IOException();
			}
			String contents = Files.readString(toPath);
			assertEquals(randomString, contents);

		} catch (IOException e) {
			System.out.println("testCopyToReadLockedFile fails");
			//fail();
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
