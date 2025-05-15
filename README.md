# backupTool
File backup and restore tool

## Features
- Take a snapshot of a specified directory
- Restore files from a snapshot to a specified directory
- List snapshots
- Prune old snapshots

## Requirements
- Java 17 or higher
- Maven
- MySQL

## Installation
1.  Java 17 or higher is required to run the application. You can download it from the [Oracle website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or use a package manager like `apt` or `brew`.
2. Maven is required to build the application. You can download it from the [Maven website](https://maven.apache.org/download.cgi) or use a package manager like `apt` or `brew`.
3. MySQL is required to store the snapshots. You can download it from the [MySQL website](https://dev.mysql.com/downloads/mysql/) or use a package manager like `apt` or `brew`.
4. Clone the repository:
   ```bash
   git clone https://github.com/gbyragoni1/backupTool.git
   cd  backupTool 
   ```
5. Run the following command for mysql cli (once it is installed from step 3):
    ```bash
   mysql -u root -p
   ```
6. Create a database for the application from mysql cli:
   ```sql
   source <project_root>/sql/backuptool.sql
   ```
7. Build the application usingMaven:
   ```bash
   mvn clean install
   mvn package
   ```  
8. After the build is complete, a JAR file will be created in the `target` directory (target/backupTool-1.0-SNAPSHOT.jar). 
   This is referenced in the shell script `backupTool.sh` (mac) and the batch file `backupTool.bat` (windows).

9. On mac os, you can create an alias by adding the following line to your `~/.bash_profile` or `~/.zshrc` file:
   ```bash
   alias backuptool='<PROJECT_ROOT>/backupTool/bin/backuptool.sh'
   ```
   where `<PROJECT_ROOT>` is the path to the project root directory.
   Then, run the following command to apply the changes:
   ```bash
   source ~/.bash_profile
   ```
   or 
   ```bash
   source ~/.zshrc
   ```
 10. Now you are ready to use the tool from project root:  
   ```bash
   backupTool
   backuptool snapshot --target-directory=~/my_important_files
   backuptool list
   backuptool restore --snapshot-number=42 --output-directory=./out
   backuptool prune --snapshot=42
   ```

## Usage
### Taking a snapshot
To take a snapshot of a specified directory, use the following command:
```bash 
backuptool snapshot --target-directory=~/my_important_files     
```
### Listing snapshots
To list all snapshots, use the following command:
```bash
backuptool list
```
### Restoring files
To restore files from a snapshot to a specified directory, use the following command:
```bash
backuptool restore --snapshot-number=42 --output-directory=./out
```
### Pruning old snapshots
To prune old snapshots, use the following command:
```bash
backuptool prune --snapshot=42
```
