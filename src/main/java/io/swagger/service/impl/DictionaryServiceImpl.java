package io.swagger.service.impl;

import io.swagger.postgres.model.BaseDictionaryEntity;
import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.ServiceWorkDictionary;
import io.swagger.postgres.model.VehicleDictionary;
import io.swagger.postgres.model.enums.ImportType;
import io.swagger.postgres.repository.ServiceAddonDictionaryRepository;
import io.swagger.postgres.repository.ServiceWorkDictionaryRepository;
import io.swagger.postgres.repository.VehicleDictionaryRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.service.DictionaryService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    private final static Logger logger = LoggerFactory.getLogger(DictionaryService.class);

    @Autowired
    private ServiceWorkDictionaryRepository serviceWorkDictionaryRepository;
    @Autowired
    private ServiceAddonDictionaryRepository serviceAddonDictionaryRepository;
    @Autowired
    private VehicleDictionaryRepository vehicleDictionaryRepository;

    @Override
    public void importServiceWorksDictionaries(ImportType importType, File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException {
        List<String> dictionaries = getDictionaries(xlsxFile, sheetNumber, colNumber, startRow);

        if ( dictionaries.size() == 0 ) throw new DataNotFoundException("Данные не найдены!");

        switch ( importType ) {
            case REPLACE_ALL: {
                serviceWorkDictionaryRepository.deleteAll();

                List<ServiceWorkDictionary> newServiceWorkDictionaries = new ArrayList<>();
                for ( String dictionary : dictionaries ) {
                    ServiceWorkDictionary serviceWorkDictionary = new ServiceWorkDictionary();
                    serviceWorkDictionary.setDeleted(false);
                    serviceWorkDictionary.setIsInitial(true);
                    serviceWorkDictionary.setName(dictionary);

                    newServiceWorkDictionaries.add( serviceWorkDictionary );
                }

                serviceWorkDictionaryRepository.saveAll( newServiceWorkDictionaries );
                break;
            }
            case ADD_NEW: {
                List<ServiceWorkDictionary> newServiceWorkDictionaries = new ArrayList<>();
                for ( String dictionary: dictionaries ) {
                    if ( serviceWorkDictionaryRepository.isExistsByName( dictionary ) )
                        continue;

                    ServiceWorkDictionary serviceWorkDictionary = new ServiceWorkDictionary();
                    serviceWorkDictionary.setDeleted(false);
                    serviceWorkDictionary.setIsInitial(true);
                    serviceWorkDictionary.setName(dictionary);

                    newServiceWorkDictionaries.add( serviceWorkDictionary );
                }
                if ( newServiceWorkDictionaries.size() > 0 )
                    serviceWorkDictionaryRepository.saveAll( newServiceWorkDictionaries );

                break;
            }
        }
    }

    @Override
    public void importServiceAddonsDictionaries(ImportType importType, File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException {
        List<String> dictionaries = getDictionaries(xlsxFile, sheetNumber, colNumber, startRow);

        if ( dictionaries.size() == 0 ) throw new DataNotFoundException("Данные не найдены!");

        switch ( importType ) {
            case REPLACE_ALL: {
                serviceAddonDictionaryRepository.deleteAll();

                List<ServiceAddonDictionary> newServiceAddonDictionaries = new ArrayList<>();
                for ( String dictionary : dictionaries ) {
                    ServiceAddonDictionary serviceAddonDictionary = new ServiceAddonDictionary();
                    serviceAddonDictionary.setDeleted(false);
                    serviceAddonDictionary.setIsInitial(true);
                    serviceAddonDictionary.setName(dictionary);

                    newServiceAddonDictionaries.add( serviceAddonDictionary );
                }

                serviceAddonDictionaryRepository.saveAll( newServiceAddonDictionaries );
                break;
            }
            case ADD_NEW: {
                List<ServiceAddonDictionary> newServiceAddonDictionaries = new ArrayList<>();
                for ( String dictionary: dictionaries ) {
                    if ( serviceAddonDictionaryRepository.isExistsByName( dictionary ) )
                        continue;

                    ServiceAddonDictionary serviceAddonDictionary = new ServiceAddonDictionary();
                    serviceAddonDictionary.setDeleted(false);
                    serviceAddonDictionary.setIsInitial(true);
                    serviceAddonDictionary.setName(dictionary);

                    newServiceAddonDictionaries.add( serviceAddonDictionary );
                }
                if ( newServiceAddonDictionaries.size() > 0 )
                    serviceAddonDictionaryRepository.saveAll( newServiceAddonDictionaries );

                break;
            }
        }
    }

    @Override
    public void importVehicleDictionaries(ImportType importType, File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException {
        List<String> dictionaries = getDictionaries(xlsxFile, sheetNumber, colNumber, startRow);

        if ( dictionaries.size() == 0 ) throw new DataNotFoundException("Данные не найдены!");

        switch ( importType ) {
            case REPLACE_ALL: {
                vehicleDictionaryRepository.deleteAll();

                List<VehicleDictionary> newVehicleDictionaries = new ArrayList<>();
                for ( String dictionary : dictionaries ) {
                    VehicleDictionary vehicleDictionary = new VehicleDictionary();
                    vehicleDictionary.setDeleted(false);
                    vehicleDictionary.setIsInitial(true);
                    vehicleDictionary.setName(dictionary);

                    newVehicleDictionaries.add( vehicleDictionary );
                }

                vehicleDictionaryRepository.saveAll( newVehicleDictionaries );
                break;
            }
            case ADD_NEW: {
                List<VehicleDictionary> newVehicleDictionaries = new ArrayList<>();
                for ( String dictionary: dictionaries ) {
                    if ( vehicleDictionaryRepository.isExistsByName( dictionary ) )
                        continue;

                    VehicleDictionary vehicleDictionary = new VehicleDictionary();
                    vehicleDictionary.setDeleted(false);
                    vehicleDictionary.setIsInitial(true);
                    vehicleDictionary.setName(dictionary);

                    newVehicleDictionaries.add( vehicleDictionary );
                }
                if ( newVehicleDictionaries.size() > 0 )
                    vehicleDictionaryRepository.saveAll( newVehicleDictionaries );

                break;
            }
        }
    }

    private List<String> getDictionaries(File xlsxFile, Integer sheetNumber, Integer colNumber, Integer startRow) throws IOException, InvalidFormatException, DataNotFoundException {
        XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile);

        XSSFSheet sheet = workbook.getSheetAt( sheetNumber - 1 );
        if ( sheet == null ) throw new DataNotFoundException("Указанный лист не найден!");

        int totalRows = sheet.getPhysicalNumberOfRows();
        if ( totalRows == 0 ) throw new DataNotFoundException("Данные не найдены!");
        if ( startRow > totalRows ) throw new DataNotFoundException("Указанная строка находится за пределами данных!");

        List<String> dictionaries = new ArrayList<>();

        for ( int rowNumber = startRow - 1; rowNumber < totalRows; rowNumber++ ) {
            XSSFRow row = sheet.getRow(rowNumber);
            if ( row == null ) continue;

            XSSFCell cell = row.getCell(colNumber - 1);
            if ( cell == null ) continue;

            String cellValue = cell.getStringCellValue();
            if ( cellValue == null || cellValue.length() == 0 ) continue;

            dictionaries.add( cellValue );
        }

        return dictionaries;
    }
}
