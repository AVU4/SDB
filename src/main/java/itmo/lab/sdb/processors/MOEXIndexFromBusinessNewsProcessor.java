package itmo.lab.sdb.processors;

import itmo.lab.sdb.entities.BusinessNews;
import itmo.lab.sdb.entities.MOEXIndexResult;
import org.springframework.batch.item.ItemProcessor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MOEXIndexFromBusinessNewsProcessor implements ItemProcessor<BusinessNews, MOEXIndexResult> {
    @Override
    public MOEXIndexResult process(BusinessNews item) throws Exception {
        MOEXIndexResult moexIndexResult = new MOEXIndexResult();
        moexIndexResult.setSummary(item.getSummary());
        moexIndexResult.setTitle(item.getTitle());
        ZonedDateTime zonedDateTime;
        try {
            zonedDateTime = ZonedDateTime.parse(item.getPublished().replace("GMT", "+0300"), DateTimeFormatter.RFC_1123_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
        moexIndexResult.setDate(zonedDateTime.toLocalDate());
        return moexIndexResult;
    }
}
