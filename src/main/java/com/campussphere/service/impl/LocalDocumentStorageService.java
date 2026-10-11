package com.campussphere.service.impl;

import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.service.DocumentStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.UUID;

@Service
public class LocalDocumentStorageService implements DocumentStorageService {
    private static final long MAX_SIZE = 20L * 1024 * 1024;
    private final Path root;

    public LocalDocumentStorageService(@Value("${campussphere.storage.documents-dir:${user.home}/campussphere-data/documents}") String directory) {
        this.root = Paths.get(directory).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("A non-empty document file is required");
        if (file.getSize() > MAX_SIZE) throw new BadRequestException("Document exceeds the 20 MB limit");
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank() || name.length() > 255 || name.contains("\r") || name.contains("\n")) throw new BadRequestException("Invalid filename");
        String safeName = name.replace('\\', '/');
        safeName = safeName.substring(safeName.lastIndexOf('/') + 1);
        int dot = safeName.lastIndexOf('.');
        if (dot < 1) throw new BadRequestException("File extension is required");
        String ext = safeName.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!java.util.Set.of("pdf", "txt", "csv", "png", "jpg", "jpeg").contains(ext)) throw new BadRequestException("File type is not permitted");
        try {
            byte[] bytes = file.getBytes();
            validateContent(ext, bytes, file.getContentType());
            Files.createDirectories(root);
            String key = UUID.randomUUID() + "." + ext;
            Path target = resolve(key);
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return key;
        } catch (IOException e) {
            throw new BadRequestException("Unable to store document");
        }
    }

    @Override
    public Resource load(String storageKey) {
        Path path = resolve(storageKey);
        if (!Files.isRegularFile(path)) throw new ResourceNotFoundException("Document file not found");
        return new FileSystemResource(path);
    }

    @Override
    public void delete(String storageKey) {
        try { Files.deleteIfExists(resolve(storageKey)); }
        catch (IOException e) { throw new BadRequestException("Unable to delete stored document"); }
    }

    private Path resolve(String key) {
        if (key == null || !key.matches("[0-9a-fA-F-]{36}\\.(pdf|txt|csv|png|jpg|jpeg)")) throw new BadRequestException("Invalid storage reference");
        Path path = root.resolve(key).normalize();
        if (!path.startsWith(root)) throw new BadRequestException("Invalid storage reference");
        return path;
    }

    private void validateContent(String ext, byte[] b, String mime) {
        boolean valid = switch (ext) {
            case "pdf" -> starts(b, new byte[]{'%', 'P', 'D', 'F', '-'}) && (mime == null || mime.equals("application/pdf") || mime.equals("application/octet-stream"));
            case "png" -> starts(b, new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10}) && (mime == null || mime.equals("image/png") || mime.equals("application/octet-stream"));
            case "jpg", "jpeg" -> b.length > 3 && (b[0] & 255) == 255 && (b[1] & 255) == 216 && (mime == null || mime.equals("image/jpeg") || mime.equals("application/octet-stream"));
            case "txt", "csv" -> (mime == null || mime.startsWith("text/") || mime.equals("application/octet-stream")) && textBytes(b);
            default -> false;
        };
        if (!valid) throw new BadRequestException("File content does not match a permitted file type");
    }
    private boolean starts(byte[] data, byte[] signature) { if (data.length < signature.length) return false; for (int i=0;i<signature.length;i++) if (data[i] != signature[i]) return false; return true; }
    private boolean textBytes(byte[] data) { for (byte value : data) if (value == 0) return false; return true; }
}
