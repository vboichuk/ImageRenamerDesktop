package exifEditor;

import exception.NoExifDataException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExifEditor {
    public static void updateExifDateTimeOriginal(File jpegFile, LocalDateTime newDateTime)
            throws IOException, ImageReadException, ImageWriteException, NoExifDataException {

        // Получаем текущие EXIF-данные
        TiffImageMetadata exifMetadata = getExifMetadata(jpegFile);
        if (exifMetadata == null) {
            throw new NoExifDataException();
        }

        // Создаём TiffOutputSet для записи
        TiffOutputSet outputSet = exifMetadata.getOutputSet();

        // Получаем директорию EXIF (или создаём, если её нет)
        TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

        // Устанавливаем новое значение DateTimeOriginal (формат: "YYYY:MM:DD HH:MM:SS")
        String newDateTimeStr = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").format(newDateTime);
        // System.out.println("newDateTimeStr = " + newDateTimeStr);
        exifDirectory.removeField(org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        exifDirectory.add(org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, newDateTimeStr);

        // Записываем изменения во временный файл
        File tempFile = File.createTempFile("temp", ".jpg");
        // System.out.println("tempFile = " + tempFile.getName());
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            new ExifRewriter().updateExifMetadataLossless(jpegFile, bos, outputSet);
        }

        // Заменяем исходный файл
        Files.move(tempFile.toPath(), jpegFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static TiffImageMetadata getExifMetadata(File jpegFile) throws IOException, ImageReadException {
        ImageMetadata metadata = Imaging.getMetadata(jpegFile);
        if (metadata instanceof JpegImageMetadata) {
            return ((JpegImageMetadata) metadata).getExif();
        }
        return null;
    }
}
