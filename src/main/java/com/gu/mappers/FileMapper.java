package com.gu.mappers;

import com.gu.persistence.SnapshotFile;
import com.gu.persistence.SnapshotFileContent;
import com.gu.repositories.FileContentRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class FileMapper {
    public static List<SnapshotFile> mapFilesToSnapshotFiles(List<File> files, Integer snapshotId, String targetDirectory,
                                                             List<SnapshotFile> previousSnapshotFiles, FileContentRepository fileContentRepository){
        List<SnapshotFile> snapshotFiles = new ArrayList<>();

        files.stream().forEach( f ->{
            SnapshotFile s = new SnapshotFile();
            s.setSnapshotId(snapshotId);
            s.setFileName(f.getName());
            s.setPath(f.getPath().replace(targetDirectory+"/", ""));
            String contentHash = "";
            try {
                contentHash = getContentHash(targetDirectory + "/" + s.getPath());
                if (!sameContent(contentHash, previousSnapshotFiles)) {
                    saveContent(targetDirectory, fileContentRepository, f, s, contentHash);
                } else {
                    Integer matchingFileContentId = findMatchingFileContentId(previousSnapshotFiles, s);
                    if (matchingFileContentId != -1){
                        s.setFileContentId(matchingFileContentId);
                    } else {
                        saveContent(targetDirectory, fileContentRepository, f, s, contentHash);
                    }
                    s.setContentHash(contentHash);
                }
                snapshotFiles.add(s);
            }catch(Exception e){
                System.out.println("Error getting content hash." + e.getMessage());
            }

        });
        return snapshotFiles;
    }

    private static void saveContent(String targetDirectory, FileContentRepository fileContentRepository, File f, SnapshotFile s, String contentHash) {
        SnapshotFileContent sfc = new SnapshotFileContent();
        sfc.setFileName(f.getName());

        try {
            Blob blob = new SerialBlob(convertFileToByteArray(targetDirectory +"/"+ s.getPath()));
            sfc.setFileContent(blob);
        } catch (Exception e){
            System.out.println("Error creating file content."+ e.getMessage());
        }

        SnapshotFileContent saved =  fileContentRepository.save(sfc);
        s.setFileContentId(saved.getId());
        s.setContentHash(contentHash);
    }

    public static byte[] convertFileToByteArray(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len; (len = fileInputStream.read(buffer)) != -1; ) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e){

        }
        return null;
    }

    public static String getContentHash(String filePath) throws NoSuchAlgorithmException {
        byte[] fileBytes = convertFileToByteArray(filePath);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        return HexFormat.of().formatHex(hashBytes);
    }

    public static boolean sameContent(String contentHash, List<SnapshotFile> previousSnapshotFiles){
        for (SnapshotFile sf: previousSnapshotFiles){
            if (contentHash.equals(sf.getContentHash())){
                return true;
            }
        }
        return false;
    }

    public static Integer findMatchingFileContentId(List<SnapshotFile> previousSnapshotFiles, SnapshotFile current){
        for (SnapshotFile sf: previousSnapshotFiles){
            if (current.getFileName().equals(sf.getFileName()) && current.getPath().equals(sf.getPath())){
                return sf.getFileContentId();
            }
        }
        return -1;
    }

}
