package com.sbrf.loyalist.dao;

import com.sbrf.loyalist.entity.BlackList;
import com.sbrf.loyalist.entity.BlackListEntity;
import com.sbrf.loyalist.entity.FileBlackList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlackListFileDao implements BlackListDao {

    // TODO Добавить маску файлов *.blk
    private static final String FILE_PATTERN = ".*.blk";
    
    private static final String END_LINE = "EOF";
    
    private static final String DELIMITER = ":";

    private String directory;

    public BlackListFileDao(String directory) {
        this.directory = directory;
    }

    @Override
    public BlackList get() {
        Path directoryPath = Paths.get(directory);
        BlackList blackList = null;
        try (Stream<Path> file =
                     Files.find(
                            directoryPath,
                            Integer.MAX_VALUE,
                            ((path, basicFileAttributes) -> path.toFile().getName().matches(FILE_PATTERN)))) {
            blackList = new FileBlackList(parseFiles(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return blackList;
    }

    private List<BlackListEntity> parseFiles(Stream<Path> fileStream) {
        List<BlackListEntity> result = new ArrayList<>();

        List<Path> paths = fileStream.filter(Files::isRegularFile).collect(Collectors.toList());
        for (Path path : paths) {
            List<BlackListEntity> blackListEntities = parseFile(path);
            if (!blackListEntities.isEmpty()) {
                result.addAll(blackListEntities);
            }
        }

        return result;
    }

    private List<BlackListEntity> parseFile(Path file) {
        List<BlackListEntity> blackListEntities = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                } else if (END_LINE.equals(line.trim())) {
                    break;
                } else {
                    String[] fields = line.split(DELIMITER);
                    String lastName = fields[0];
                    String firstName = fields[1];
                    String patronymic = fields[2];
                    String telephone = fields[3];
                    BlackListEntity blackListEntity =
                            new BlackListEntity(lastName, firstName, patronymic, telephone);
                    blackListEntities.add(blackListEntity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return blackListEntities;
    }

}
