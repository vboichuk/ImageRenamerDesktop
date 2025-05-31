package exifreader;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import exception.NoExifDataException;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;


public class ExifReader {

    public static Optional<LocalDateTime> getDateTime(File imageFile) {
        try {
            Metadata metadata = getMetadata(imageFile);
            ExifDirectoryBase directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory == null)
                return Optional.empty();

            return Optional.ofNullable(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))
                    .map(date -> date.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime());
        } catch (NoExifDataException | ImageProcessingException | IOException ex) {
            System.out.println("Error: " + ex.getMessage() + " - " + imageFile.getName());
            return Optional.empty();
        }
    }

    public static Optional<String> getCameraModel(File imageFile) {
        try {
            Metadata metadata = getMetadata(imageFile);
            ExifDirectoryBase directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory == null)
                return Optional.empty();
            String camera = directory.getString(ExifIFD0Directory.TAG_MAKE);
            if (camera == null) {
                return Optional.empty();
            }
            return Optional.of(camera.trim());

        } catch (NoExifDataException | ImageProcessingException | IOException ex) {
            System.out.println("Failed to get camera name from " + imageFile.getName());
            return Optional.empty();
        }
    }

    public static void printAllTags(File imageFile) {
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(imageFile);
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }
        for (Directory directory : metadata.getDirectories()) {
            System.out.println(directory.getName());
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
    }

    protected static Metadata getMetadata(File imageFile)
            throws NoExifDataException, ImageProcessingException, IOException {
        if (imageFile == null)
            throw new RuntimeException("");

        if (!imageFile.exists() || !imageFile.isFile())
            throw new NoSuchFileException(imageFile.getAbsolutePath());

        return ImageMetadataReader.readMetadata(imageFile);
    }
}