package utils;

import junit.framework.TestCase;

import java.nio.file.Paths;

public class FileUtilsTest extends TestCase {

    public void testGetFilenameWithoutExt() {
        // assertEquals("", FileUtils.PathUtils.getFilenameWithoutExt("folder.txt/awesome"));
        assertEquals("", FileUtils.PathUtils.getFilenameWithoutExt(".txt"));
        assertEquals("", FileUtils.PathUtils.getFilenameWithoutExt("."));
        assertEquals("test", FileUtils.PathUtils.getFilenameWithoutExt("test."));
        assertEquals("document", FileUtils.PathUtils.getFilenameWithoutExt("document.txt"));
        assertEquals("document.awesome", FileUtils.PathUtils.getFilenameWithoutExt("document.awesome.txt"));
        assertEquals("document.awesome.txt", FileUtils.PathUtils.getFilenameWithoutExt("document.awesome.txt."));
    }

    public void testGetDirectory() {
    }
}