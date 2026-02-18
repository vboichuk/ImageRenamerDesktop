package filerenamer;

import fileprocessor.FileMetadata;
import junit.framework.TestCase;

public class TemplateNamingStrategyTest extends TestCase {

    public void testProcessOriginalName() {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");

        // no match
        assertEquals("{:original::}", TemplateNamingStrategy.processOriginalName("{:original::}", metadata));

        // multiple matches
        assertEquals("testimage-001_TestI", TemplateNamingStrategy.processOriginalName("{original:lower}_{original:5}", metadata));

        // invalid formats
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original:lowe10}", metadata));
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original::}", metadata));

        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original}", metadata));
        assertEquals("TESTIMAGE-001", TemplateNamingStrategy.processOriginalName("{original:upper}", metadata));
        assertEquals("testimage-001", TemplateNamingStrategy.processOriginalName("{original:lower}", metadata));
        assertEquals("TestImage-", TemplateNamingStrategy.processOriginalName("{original:10}", metadata));
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original:0}", metadata));
        assertEquals("Test", TemplateNamingStrategy.processOriginalName("{original:4}", metadata));
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original:100}", metadata));
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original:-1}", metadata));
        assertEquals("testi", TemplateNamingStrategy.processOriginalName("{original:5:lower}", metadata));
        assertEquals("TestI", TemplateNamingStrategy.processOriginalName("{original:5:10}", metadata));
        assertEquals("testimage-", TemplateNamingStrategy.processOriginalName("{original:lower:10}", metadata));
    }
}