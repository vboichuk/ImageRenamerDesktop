package utils;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

    public void testBuildPath() {
    }

    public void testNormalizePath() {
    }

    public void testGetFilenameWithoutExt() {
        // assertEquals("", FileUtils.PathUtils.getFilenameWithoutExt("folder.txt/awesome"));
        assertEquals("", FileUtils.PathUtils.getFilenameWithoutExt(".txt"));
        assertEquals("", FileUtils.PathUtils.getFilenameWithoutExt("."));
        assertEquals("test", FileUtils.PathUtils.getFilenameWithoutExt("test."));
        assertEquals("document", FileUtils.PathUtils.getFilenameWithoutExt("document.txt"));
        assertEquals("document.awesome", FileUtils.PathUtils.getFilenameWithoutExt("document.awesome.txt"));
        assertEquals("document.awesome.txt", FileUtils.PathUtils.getFilenameWithoutExt("document.awesome.txt."));
    }
}