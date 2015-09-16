/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 */
public class Utils
{
	@SuppressWarnings("unchecked")
	public static <T> T loadObj(String path, Class<T> cls)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		try (InputStream file = new FileInputStream(path);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);)
		{
			return (T) input.readObject();
		}
	}

	public static void saveObj(String path, Object object) throws IOException
	{

		try (OutputStream file = new FileOutputStream(path);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);)
		{
			output.writeObject(object);
		}
	}

	public static String loadFileAsString(Path path) throws IOException
	{
		long size = Files.size(path);
		char[] fileContents = new char[(int) size];
		try (BufferedReader reader = Files.newBufferedReader(path,
				Charset.forName("UTF-8")))
		{
			reader.read(fileContents);
		}
		return String.valueOf(fileContents);
	}

	/**
	 * Actively destroys a direct buffer. Calling this guarantees that memory is
	 * freed immediately.
	 */
	public static void deallocateDirectByteBuffer(ByteBuffer directByteBuffer)
	{
		Method cleanerMethod;
		try
		{
			cleanerMethod = directByteBuffer.getClass().getMethod("cleaner");
			cleanerMethod.setAccessible(true);
			Object cleaner = cleanerMethod.invoke(directByteBuffer);
			Method cleanMethod = cleaner.getClass().getMethod("clean");
			cleanMethod.setAccessible(true);
			cleanMethod.invoke(cleaner);
		}
		catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException exception)
		{
			throw new RuntimeException("Cannot clean byte buffer");
		}
	}
}
