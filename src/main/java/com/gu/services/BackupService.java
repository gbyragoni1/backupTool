package com.gu.services;

import com.gu.mappers.FileMapper;
import com.gu.persistence.SnapshotFile;
import com.gu.persistence.Snapshot;
import com.gu.persistence.SnapshotFileContent;
import com.gu.repositories.FileContentRepository;
import com.gu.repositories.FileRepository;
import com.gu.repositories.SnapshotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BackupService {
    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileContentRepository fileContentRepository;

    @Autowired
    private FileService fileService;

    public List<Snapshot> listSnapshots(){
        return snapshotRepository.findAll();
    }

    @Transactional
    public void createSnapshot(List<File> files, String targetDirectory) {
        System.out.println("Creating snapshot....");
        Snapshot snapshot = new Snapshot();
        snapshot.setDirectory(targetDirectory);
        snapshot.setCreateTime(Timestamp.from(new Date().toInstant()));

        Snapshot latest = snapshotRepository.findLatest();
        Snapshot saved = snapshotRepository.save(snapshot);

        System.out.println("Saved snapshot Id: " + saved.getId());

        List<SnapshotFile> previousSnapshotFiles = new ArrayList<>();
        if (latest != null && latest.getId() != null){
            System.out.println("Latest snapshot Id before saving: " + latest.getId());
            previousSnapshotFiles = fileRepository.findBySnapshotId(latest.getId());
        }

        List<SnapshotFile> snapshotFiles = FileMapper.mapFilesToSnapshotFiles(files, saved.getId(), targetDirectory, previousSnapshotFiles, fileContentRepository);
        fileRepository.saveAll(snapshotFiles);

        System.out.println("Done!");
    }

    @Transactional
    public void pruneSnapshot(Integer snapshotId){
        System.out.println("Pruning snapshot: " + snapshotId);

        List<SnapshotFile> snapshotFiles = fileRepository.findBySnapshotId(snapshotId);

        List<SnapshotFileContent> snapshotFileContents = new ArrayList<>();

        snapshotFiles.stream().forEach(sf ->{
            Optional<SnapshotFileContent> sfco = fileContentRepository.findById(sf.getFileContentId());
            SnapshotFileContent sfc = sfco.get();
            List<SnapshotFile> references = fileRepository.findByFileContentId(sf.getFileContentId());
            if (references.size() == 1){
                snapshotFileContents.add(sfc);
            }
        });

        fileContentRepository.deleteAll(snapshotFileContents);
        fileRepository.deleteAll(snapshotFiles);
        snapshotRepository.deleteById(snapshotId);

        System.out.println("Done!");
    }

    public void restoreSnapshot(Integer snapshotId, String targetDirectory) {
        System.out.println("Restoring snapshot: "+ snapshotId + " to " + targetDirectory );
        File root = new File(targetDirectory);
        if (!root.exists()){
            if (root.mkdir()) {
//                System.out.println("Directory created successfully.");
            } else {
//                System.err.println("Failed to create directory.");
            }
        }
        List<SnapshotFile> snapshotFiles = fileRepository.findBySnapshotId(snapshotId);
        snapshotFiles.stream().forEach(s -> {
            Optional<SnapshotFileContent> sfco = fileContentRepository.findById(s.getFileContentId());
            SnapshotFileContent sfc = sfco.get();
            try {
                Blob blob = sfc.getFileContent();
                byte[] blobBytes = blob.getBytes(1, (int) blob.length());
                if (checkAndMakeDirectories(s.getPath(), blobBytes, targetDirectory)){

                }
                else {
                    File file = new File(targetDirectory + "/" + s.getPath());

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(blobBytes);
                    fos.close();
                }
            } catch (Exception e){
                System.out.println("Error creating file content." + e.getMessage());
            }
        });
        System.out.println("Done!");
    }

    private boolean checkAndMakeDirectories(String fileName, byte[] blobBytes, String targetDirectory){
        String[] parts = fileName.split(File.separator);
        boolean isDirectory = false;
        if (parts.length > 1) {
            int indexOfSlash = fileName.lastIndexOf("/");
            String dir = fileName.substring(0, indexOfSlash);
            String fname = fileName.substring(indexOfSlash);
            File file = new File(targetDirectory, dir);
            if (file.mkdir()) {
//                System.out.println("Directory created successfully.");
            } else {
//                System.err.println("Failed to create directory.");
            }
            File f = new File(file, fname);
            try {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(blobBytes);
                fos.close();
            } catch (Exception e){
                System.out.println("Error creating file content." + e.getMessage());
            }
            isDirectory = true;
        }

        return isDirectory;
    }

}
