package io.swagger.service;

import io.swagger.postgres.model.enums.ImportType;
import io.swagger.response.exception.DataNotFoundException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;

public interface DictionaryService {
    void importServiceWorksDictionaries(ImportType importType, File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException;

    void importServiceAddonsDictionaries(ImportType importType, File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException;

    void importVehicleDictionaries(ImportType importType, File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException;
}
