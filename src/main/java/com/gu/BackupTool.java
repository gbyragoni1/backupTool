package com.gu;

import com.gu.persistence.Snapshot;
import com.gu.services.BackupService;
import com.gu.services.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class BackupTool {
    private static final String USAGE_MESSAGE = "Usage: backuptool snapshot|restore|list|prune";

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BackupTool.class, args);
        BackupService backupService = context.getBean(BackupService.class);
        FileService fileService = context.getBean(FileService.class);

//        Quick Testing From IDE
//        args = new String[]{"snapshot","--target-directory=~/testTargetDirectory"};
//        args = new String[]{"restore","--snapshot-number=1","--output-directory=~/outTargetDirectory"};
//        args = new String[]{"prune","--snapshot=34"};
//        args = new String[]{"list"};

        if (args.length == 0) {
            System.out.println(USAGE_MESSAGE);
            System.exit(1);
        }

        String operation = args[0];

        if ("snapshot".equalsIgnoreCase(operation)){
            snapshot(args, backupService, fileService);
        } else if ("restore".equalsIgnoreCase(operation)){
            restore(args, backupService);
        } else if ("list".equalsIgnoreCase(operation)){
            list(backupService);
        } else if ("prune".equalsIgnoreCase(operation)){
            prune(args, backupService);
        } else {
            System.out.println("Unknown operation " + operation + ". " + USAGE_MESSAGE);
            System.exit(1);
        }
    }

    public static void snapshot(String[] args, BackupService backupService, FileService fileService){
        String targetDirectory = fixTargetDirectory(args[1].split("=")[1]);
        File root = new File(targetDirectory);
        if (!root.exists()){
            System.out.println(USAGE_MESSAGE);
        }
        List<File> files = new LinkedList<>();
        fileService.collectFiles(files, root);
        backupService.createSnapshot(files, targetDirectory);
    }

    public static void restore(String[] args, BackupService backupService){
        String snapshotStr = args[1].split("=")[1];
        Integer snapshotId = Integer.valueOf(snapshotStr);
        String targetDirectory = fixTargetDirectory(args[2].split("=")[1]);
        backupService.restoreSnapshot(snapshotId, targetDirectory);
    }

    public static String fixTargetDirectory(String targetDirectory){
        if (targetDirectory.startsWith("~/")){
            String home = System.getProperty("user.home");
            targetDirectory = home + targetDirectory.substring(1);
        } else if (targetDirectory.startsWith("./")){
            String currentWorkingDirectory = System.getProperty("user.dir");
            targetDirectory = currentWorkingDirectory + targetDirectory.substring(1);
        }
        return targetDirectory;
    }

    public static void list(BackupService backupService){
        List<Snapshot> snapshots = backupService.listSnapshots();
        System.out.println("SNAPSHOT\tTIMESTAMP");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        snapshots.stream().forEach(s -> {
            System.out.println(s.getId() + "\t\t" + dateFormat.format(s.getCreateTime()));
        });
    }

    public static void prune(String[] args, BackupService backupService){
        Integer snapshotId = Integer.valueOf(args[1].split("=")[1]);
        backupService.pruneSnapshot(snapshotId);
    }
}