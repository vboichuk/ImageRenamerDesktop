package filedata.datetime;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;


public class ExifReader implements DateTimeReader {
    // private static final Logger logger = LoggerFactory.getLogger(ExifReader.class);

    @Override
    public Optional<LocalDateTime> getDateTime(File imageFile) {
        if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
            return Optional.empty();
        }

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory != null) {
                return Optional.ofNullable(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))
                        .map(date -> date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());
            }
            return Optional.empty();

        } catch (ImageProcessingException | IOException e) {
            System.out.println("Failed to read EXIF data from " + imageFile + " " + e.getMessage());
            return Optional.empty();
        }
    }
}