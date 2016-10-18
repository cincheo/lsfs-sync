# lsfs-sync

This project provides a utility to sync a LocalStorage file system (lsfs) with the OS file system.

LSFS (for LocalStorage File System) was created to support the ``java.io`` file API in the scope of the J4TS project. LSFS simulates a regular file system (with files and directories) within a WEB browser local storage.

With lsfs-sync, you can create images of your regular file system that will be loaded in your browser's local storage, so that you can access the content of your file system within your browser (without requiring any server). In future development lsfs-fs will provide a way to export modifications done in the browser back to your file system.

# How to use

lsfs-sync is build and executed with Maven (you need it in your path).

First, you need to create a ``lsfs.json`` configuration file, that tells lsfs-sync what files need to be bundled into an JavaScript image file (``.lsfs.js`` extension).

Then you run lsfs-sync with Maven, providing the directory where the ``lsfs.json`` configuration file is located.

```bash
mvn exec:exec -Ddir=DIRECTORY
```


