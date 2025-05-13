package com.gu.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileService {
    public List<File> collectFiles(List<File> files, File root){
        if (!root.isDirectory()){
            files.add(root);
            return files;
        }

        for (File file : root.listFiles()) {
            collectFiles(files, file);
        }
        return files;
    }
}
