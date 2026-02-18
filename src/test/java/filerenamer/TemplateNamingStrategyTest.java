package filerenamer;

import fileprocessor.FileMetadata;
import junit.framework.TestCase;

import java.time.LocalDateTime;

public class TemplateNamingStrategyTest extends TestCase {

    private TemplateNamingStrategy strategy;
    private FileMetadata metadata;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        strategy = new TemplateNamingStrategy("dummy");
        metadata = new FileMetadata();
        metadata.setOriginalName("TestImage-001");
        metadata.setExtension("jpg");
    }

    public void testProcessOriginalName() {
        metadata.setOriginalName("TestImage-001");

        // no match
        assertEquals("{:original::}", strategy.processOriginalName("{:original::}", metadata));

        // multiple matches
        assertEquals("testimage-001_TestI", strategy.processOriginalName("{original:lower}_{original:5}", metadata));

        // invalid patterns
        assertEquals("TestImage-001", strategy.processOriginalName("{original:lowe10}", metadata));
        assertEquals("TestImage-001", strategy.processOriginalName("{original::}", metadata));

        // default value for originalName is not supported
        assertEquals("{original:lower|default}", strategy.processOriginalName("{original:lower|default}", metadata));

        assertEquals("TestImage-001", strategy.processOriginalName("{original}", metadata));
        assertEquals("TESTIMAGE-001", strategy.processOriginalName("{original:upper}", metadata));
        assertEquals("testimage-001", strategy.processOriginalName("{original:lower}", metadata));
        assertEquals("TestImage-", strategy.processOriginalName("{original:10}", metadata));
        assertEquals("TestImage-001", strategy.processOriginalName("{original:0}", metadata));
        assertEquals("Test", strategy.processOriginalName("{original:4}", metadata));
        assertEquals("TestImage-001", strategy.processOriginalName("{original:100}", metadata));
        assertEquals("TestImage-001", strategy.processOriginalName("{original:-1}", metadata));
        assertEquals("testi", strategy.processOriginalName("{original:5:lower}", metadata));
        assertEquals("TestI", strategy.processOriginalName("{original:5:10}", metadata));
        assertEquals("testimage-", strategy.processOriginalName("{original:lower:10}", metadata));
    }

    public void testProcessDateTime() {
        metadata.setDateTime(LocalDateTime.of(2025, 1, 20, 14,45, 0));

        assertEquals("2025.01.20_14-45", strategy.processDateTime("{date}", metadata));
        assertEquals("2025", strategy.processDateTime("{date:yyyy}", metadata));
        assertEquals("25", strategy.processDateTime("{date:yy}", metadata));
        assertEquals("2025-01-20", strategy.processDateTime("{date:yyyy-MM-DD}", metadata));
        assertEquals("2025-01-20", strategy.processDateTime("{date:yyyy-MM-dd}", metadata));
        assertEquals("20250120-024500", strategy.processDateTime("{date:yyyyMMdd-hhmmss}", metadata));
        assertEquals("20250120-144500", strategy.processDateTime("{date:yyyyMMdd-HHmmss}", metadata));
        assertEquals("date is 01/20/2025 and time is 14-45", strategy.processDateTime("date is {date:MM/dd/yyyy} and time is {date:HH-mm}", metadata));
    }

    public void testProcessDateTimeWhenNoExif() {
        metadata.setDateTime(null);

        assertEquals("nodate", strategy.processDateTime("{date}", metadata));
        assertEquals("nodate", strategy.processDateTime("{date:yyyy}", metadata));
        assertEquals("nodate", strategy.processDateTime("{date:yyyy:lower}", metadata));

        assertEquals("noexif", strategy.processDateTime("{date|noexif}", metadata));
        assertEquals("no exif", strategy.processDateTime("{date|no exif}", metadata));

        // empty default value
        assertEquals("", strategy.processDateTime("{date|}", metadata));

        // invalid patterns - empty format
        assertEquals("{date:}", strategy.processDateTime("{date:}", metadata));

        // invalid patterns - multiple default values
        assertEquals("{date:yyyyy|noexif1|noexif2}", strategy.processDateTime("{date:yyyyy|noexif1|noexif2}", metadata));
        assertEquals("{date|noexif1|noexif2}", strategy.processDateTime("{date|noexif1|noexif2}", metadata));

    }

    public void testProcessCamera() {
        metadata.setCameraModel("Canon");

        assertEquals("Canon", strategy.processCamera("{camera}", metadata));
        assertEquals("Canon", strategy.processCamera("{camera:invalidformat}", metadata));
        assertEquals("canon", strategy.processCamera("{camera:invalidformat:lower}", metadata));
        assertEquals("can", strategy.processCamera("{camera:invalidformat:lower:3}", metadata));
    }

    public void testProcessCameraNoInfo() {
        metadata.setCameraModel(null);

        assertEquals("undefined", strategy.processCamera("{camera}", metadata));
        assertEquals("undefined", strategy.processCamera("{camera:invalidformat}", metadata));
        assertEquals("test", strategy.processCamera("{camera:lower|test}", metadata));
        assertEquals("", strategy.processCamera("{camera|}", metadata));
        assertEquals("test:2", strategy.processCamera("{camera|test:2}", metadata));

        // invalid patterns - multiple default values
        assertEquals("{camera:lower|test1|test2}", strategy.processCamera("{camera:lower|test1|test2}", metadata));
        assertEquals("{camera:}", strategy.processCamera("{camera:}", metadata));
    }
}