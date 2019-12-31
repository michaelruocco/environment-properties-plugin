package uk.co.mruoc.plugin.environment.properties

import org.slf4j.LoggerFactory

class FileCreator {

    private static def log = LoggerFactory.getLogger(FileCreator.class)

    private FileCreator() {
        // utility class
    }

    static def createFileIfDoesNotExist(File file) {
        if (file.exists()) {
            log.info("file ${file.absolutePath} already exists")
            return
        }

        createParentDirectoryIfDoesNotExist(file)

        if (!file.createNewFile()) {
            log.error("failed to create file ${file.absolutePath}")
            return
        }

        log.info("created file ${file.absolutePath}")
    }


    static def createParentDirectoryIfDoesNotExist(File file) {
        def parentDirectory = file.getParentFile()
        log.info("checking if parent directory ${parentDirectory.absolutePath} exists")

        if (parentDirectory.exists()) {
            log.info("parent directory ${parentDirectory.absolutePath} already exists")
            return
        }

        log.info("creating parent directory ${parentDirectory.absolutePath}")
        if (!parentDirectory.mkdirs()) {
            log.error("failed to create parent directory ${parentDirectory.absolutePath}")
        }
    }

}
