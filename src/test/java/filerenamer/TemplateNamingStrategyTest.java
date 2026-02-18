package filerenamer;

import fileprocessor.FileMetadata;
import junit.framework.TestCase;

import java.time.LocalDateTime;

public class TemplateNamingStrategyTest extends TestCase {

    public void testProcessOriginalName() {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");

        // no match
        assertEquals("{:original::}", TemplateNamingStrategy.processOriginalName("{:original::}", metadata));

        // multiple matches
        assertEquals("testimage-001_TestI", TemplateNamingStrategy.processOriginalName("{original:lower}_{original:5}", metadata));

        // invalid patterns
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original:lowe10}", metadata));
        assertEquals("TestImage-001", TemplateNamingStrategy.processOriginalName("{original::}", metadata));

        // default value for originalName is not supported
        assertEquals("{original:lower|default}", TemplateNamingStrategy.processOriginalName("{original:lower|default}", metadata));

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

    public void testProcessDateTime() {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");
        metadata.setDateTime(LocalDateTime.of(2025, 1, 20, 14,45, 0));

        assertEquals("2025.01.20_14-45", TemplateNamingStrategy.processDateTime("{date}", metadata));
        assertEquals("2025", TemplateNamingStrategy.processDateTime("{date:yyyy}", metadata));
        assertEquals("25", TemplateNamingStrategy.processDateTime("{date:yy}", metadata));
        assertEquals("2025-01-20", TemplateNamingStrategy.processDateTime("{date:yyyy-MM-DD}", metadata));
        assertEquals("2025-01-20", TemplateNamingStrategy.processDateTime("{date:yyyy-MM-dd}", metadata));
        assertEquals("20250120-024500", TemplateNamingStrategy.processDateTime("{date:yyyyMMdd-hhmmss}", metadata));
        assertEquals("20250120-144500", TemplateNamingStrategy.processDateTime("{date:yyyyMMdd-HHmmss}", metadata));
        assertEquals("date is 01/20/2025 and time is 14-45", TemplateNamingStrategy.processDateTime("date is {date:MM/dd/yyyy} and time is {date:HH-mm}", metadata));
    }

    public void testProcessDateTimeWhenNoExif() {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");
        metadata.setDateTime(null);

        assertEquals("nodate", TemplateNamingStrategy.processDateTime("{date}", metadata));
        assertEquals("nodate", TemplateNamingStrategy.processDateTime("{date:yyyy}", metadata));
        assertEquals("nodate", TemplateNamingStrategy.processDateTime("{date:yyyy:lower}", metadata));

        assertEquals("noexif", TemplateNamingStrategy.processDateTime("{date|noexif}", metadata));
        assertEquals("no exif", TemplateNamingStrategy.processDateTime("{date|no exif}", metadata));

        // empty default value
        assertEquals("", TemplateNamingStrategy.processDateTime("{date|}", metadata));

        // invalid patterns - empty format
        assertEquals("{date:}", TemplateNamingStrategy.processDateTime("{date:}", metadata));

        // invalid patterns - multiple default values
        assertEquals("{date:yyyyy|noexif1|noexif2}", TemplateNamingStrategy.processDateTime("{date:yyyyy|noexif1|noexif2}", metadata));
        assertEquals("{date|noexif1|noexif2}", TemplateNamingStrategy.processDateTime("{date|noexif1|noexif2}", metadata));

    }

    public void testProcessCamera() {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");
        metadata.setCameraModel("Canon");

        assertEquals("Canon", TemplateNamingStrategy.processCamera("{camera}", metadata));
        assertEquals("Canon", TemplateNamingStrategy.processCamera("{camera:invalidformat}", metadata));
        assertEquals("canon", TemplateNamingStrategy.processCamera("{camera:invalidformat:lower}", metadata));
        assertEquals("can", TemplateNamingStrategy.processCamera("{camera:invalidformat:lower:3}", metadata));
    }

    public void testProcessCameraNoInfo() {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");
        metadata.setCameraModel(null);

        assertEquals("undefined", TemplateNamingStrategy.processCamera("{camera}", metadata));
        assertEquals("undefined", TemplateNamingStrategy.processCamera("{camera:invalidformat}", metadata));
        assertEquals("test", TemplateNamingStrategy.processCamera("{camera:lower|test}", metadata));
        assertEquals("", TemplateNamingStrategy.processCamera("{camera|}", metadata));
        assertEquals("test:2", TemplateNamingStrategy.processCamera("{camera|test:2}", metadata));

        // invalid patterns - multiple default values
        assertEquals("{camera:lower|test1|test2}", TemplateNamingStrategy.processCamera("{camera:lower|test1|test2}", metadata));
        assertEquals("{camera:}", TemplateNamingStrategy.processCamera("{camera:}", metadata));
    }
}